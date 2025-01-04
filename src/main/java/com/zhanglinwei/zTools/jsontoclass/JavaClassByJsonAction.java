package com.zhanglinwei.zTools.jsontoclass;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.zhanglinwei.zTools.jsontoclass.form.JavaClassByJsonDialog;
import com.zhanglinwei.zTools.jsontoclass.model.JavaClass;
import com.zhanglinwei.zTools.jsontoclass.model.Property;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.NotificationUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class JavaClassByJsonAction extends AnAction {

    private Project currentProject = null;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 4;
    private static final Random RANDOM = new Random();

    private static Map<String, JavaClass> objectMap = new HashMap<>();
    private static Map<String, JavaClass> tempMap = new HashMap<>();
    private static Set<String> classNameSet = new HashSet<>();
    private static Gson gson = new Gson();
    private static String folderPath = "";
    private static String packageName = "";

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (project == null) {
            return;
        }
        currentProject = project;
        VirtualFile actionFolder = actionEvent.getData(LangDataKeys.VIRTUAL_FILE);
        if (actionFolder == null) {
            NotificationUtil.warnNotify("Please select a folder!", project);
            return;
        }
        String path = actionFolder.getPath();
        folderPath = actionFolder.isDirectory() ? path : path.substring(0, path.lastIndexOf("/"));

        VirtualFile moduleSourceRoot = ProjectRootManager.getInstance(project).getFileIndex().getSourceRootForFile(actionFolder);
        if (moduleSourceRoot == null) {
            return;
        }

        packageName = getPackageName(actionFolder, moduleSourceRoot.getPath());
        if (AssertUtils.isBlank(packageName)) {
            NotificationUtil.warnNotify("Please select a valid location!", project);
            return;
        }

        objectMap.clear();
        tempMap.clear();
        classNameSet.clear();

        JavaClassByJsonDialog dialog = new JavaClassByJsonDialog(currentProject, (className, jsonString, useInnerClass) -> {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();

            if (jsonString.startsWith("[")) {
                jsonString = jsonString.substring(1);
            }
            if (jsonString.endsWith("]")) {
                jsonString = jsonString.substring(0, jsonString.length() - 1);
            }

            Map<String, Object> jsonMap = gson.fromJson(jsonString, type);

            JavaClass rootClass = createJavaClass(className, jsonMap);
            if (useInnerClass) {
                List<JavaClass> innerClassList = objectMap.values().stream().filter(item -> !item.getClassName().equals(className)).collect(Collectors.toList());
                rootClass.setInnerClassList(innerClassList);
                writeJavaInner(rootClass);
            } else {
                writeJavaFile();
            }

            try {
                Thread.sleep(100);
                ProjectView.getInstance(project).refresh();
                actionFolder.refresh(false, true);
                NotificationUtil.infoNotify("Generate successfully!", project);
            } catch (InterruptedException e) {
                NotificationUtil.infoNotify("If it does not display, please refresh and try again", project);
            }
        });
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);


    }

    private String getPackageName(VirtualFile actionFolder, String moduleSourcePath) {
        String folderPath = actionFolder.getPath();

        if (!actionFolder.isDirectory()) {
            folderPath =  actionFolder.getParent().getPath();
        }

        return folderPath.replace(moduleSourcePath + "/", "").replaceAll("/", ".");
    }

    private void writeJavaInner(JavaClass rootClass) {
        File writeFile = new File(folderPath + "/" + rootClass.getFullClassName());
        try (OutputStreamWriter java = new OutputStreamWriter(Files.newOutputStream(writeFile.toPath()), StandardCharsets.UTF_8)) {
            java.write(rootClass.toJavaInnerStr());
        } catch (IOException e) {
            NotificationUtil.errorNotify("Error writing file, Caused by: " + e.getMessage(), currentProject);
        }
    }

    private void writeJavaFile() {
        Collection<JavaClass> javaFileList = objectMap.values();
        if (AssertUtils.isNotEmpty(javaFileList)) {
            javaFileList.forEach(javaFile -> {
                File writeFile = new File(folderPath + "/" + javaFile.getFullClassName());
                try (OutputStreamWriter java = new OutputStreamWriter(Files.newOutputStream(writeFile.toPath()), StandardCharsets.UTF_8)) {
                    java.write(javaFile.toJavaFileStr());
                } catch (IOException e) {
                    NotificationUtil.errorNotify("Error writing file, Caused by: " + e.getMessage(), currentProject);
                }
            });
        }
    }

    private JavaClass createJavaClass(String className, Map<String, Object> jsonMap) {
        if (classNameSet.contains(className)) {
            className = appendRandomStr(className);
        }
        classNameSet.add(className);

        String uniqueCode = generateUniqueCode(jsonMap.keySet());
        if (objectMap.containsKey(uniqueCode)) {
            return objectMap.get(uniqueCode);
        } else if (tempMap.containsKey(uniqueCode)){
            return tempMap.get(uniqueCode);
        } else {
            tempMap.put(uniqueCode, new JavaClass(className, ""));
        }

        JavaClass javaClass = new JavaClass(className, packageName);

        Set<Map.Entry<String, Object>> entries = jsonMap.entrySet();

        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> valueMap = (Map<String, Object>) value;
                if (AssertUtils.isEmpty(valueMap)) {
                    javaClass.addProperty(new Property(key, "Map<String, Object>"));
                } else {
                    JavaClass innerClass = createJavaClass(firstUpper(key), valueMap);
                    javaClass.addProperty(new Property(key, innerClass.getClassName()));
                }
            } else if (value instanceof List) {
                List<?> valList = (List<?>) value;
                if (AssertUtils.isEmpty(valList)) {
                    javaClass.addProperty(new Property(key, "List"));
                } else {
                    Object firstValue = valList.get(0);
                    if (firstValue instanceof Map) {
                        @SuppressWarnings("unchecked")
                        JavaClass innerClass = createJavaClass(firstUpper(key), (Map<String, Object>) firstValue);
                        javaClass.addProperty(new Property(key, "List<" + innerClass.getClassName() + ">"));
                    } else {
                        javaClass.addProperty(new Property(key, "List<" + getTypeByValue(firstValue) + ">"));
                    }
                }
            } else {
                javaClass.addProperty(new Property(key, getTypeByValue(value)));
            }
        }

        objectMap.put(uniqueCode, javaClass);

        return javaClass;
    }

    private String appendRandomStr(String className) {
        StringBuilder builder = new StringBuilder();
        builder.append(className).append("_");
        for (int i = 0; i < LENGTH; i++) {
            int index = (int) (RANDOM.nextFloat() * CHARACTERS.length());
            builder.append(CHARACTERS.charAt(index));
        }
        return builder.toString();
    }

    private String firstUpper(String str) {
        return str.substring(0, 1).toUpperCase(Locale.getDefault()) + str.substring(1);
    }

    private String getTypeByValue(Object value) {
        if (value instanceof String) {
            return "String";
        }
        if (value instanceof Number) {
            return "Long";
        }
        if (value instanceof Boolean) {
            return "Boolean";
        }

        String valueStr = String.valueOf(value);
        if (valueStr.contains(".")) {
            return "BigDecimal";
        }

        return "LocalDateTime";
    }

//    public  Map<String, Object> convertToNestedMaps(Map<String, Object> map) {
//        Map<String, Object> nestedMap = new LinkedHashMap<>();
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            if (value instanceof Map) {
//                nestedMap.put(key, convertToNestedMaps((Map<String, Object>) value));
//            } else if (value instanceof List) {
//                List<Object> list = new ArrayList<>();
//                for (Object obj : (List<?>) value) {
//                    if (obj instanceof Map) {
//                        list.add(convertToNestedMaps((Map<String, Object>) obj));
//                    } else {
//                        list.add(obj);
//                    }
//                }
//                nestedMap.put(key, list);
//            } else {
//                nestedMap.put(key, value);
//            }
//        }
//        return nestedMap;
//    }

    public static String generateUniqueCode(Collection<String> collection) {
        if (AssertUtils.isEmpty(collection)) {
            return "";
        }
        String appendValue = String.join(",", collection);
        StringBuilder builder = null;
        try {
            builder = new StringBuilder();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(appendValue.getBytes(StandardCharsets.UTF_8));
            for (byte b : hashBytes) {
                builder.append(String.format("%02x", b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
//            log.info("generateUniqueCode failed, msg: {}", e.getMessage(), e);
        }

        return builder.toString();
    }

}
