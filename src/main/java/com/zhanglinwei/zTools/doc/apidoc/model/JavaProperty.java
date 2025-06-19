package com.zhanglinwei.zTools.doc.apidoc.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.java.stubs.impl.PsiJavaFileStubImpl;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.util.*;

import java.util.*;
import java.util.stream.Collectors;

public class JavaProperty {

    private String name;
    private String typeName;
    private String comment;
    private String example;
    private boolean required;

    private JavaProperty parent;
    private List<JavaProperty> children;
    private boolean cycle;

    private PsiType psiType;
    private Project project;
    private PsiAnnotation[] psiAnnotations;
    private Map<PsiTypeParameter, PsiType> genericTypeMap;

    private JavaProperty(){}

    private JavaProperty(PsiType psiType, Project project, JavaProperty parent) {
        this.psiType = psiType;
        this.project = project;
        this.parent = parent;
    }

    public boolean hasAnnotation(String annotationName) {
        return AnnotationUtil.getAnnotationByName(psiAnnotations, annotationName) != null;
    }

    public boolean hasChildren() {
        return AssertUtils.isNotEmpty(this.children);
    }

    public static JavaProperty create(PsiParameter psiParameter, Map<String, String> paramDescMap) {
        PsiType parameterType = psiParameter.getType();
        PsiAnnotation[] annotations = psiParameter.getAnnotations();

        JavaProperty javaProperty = new JavaProperty();
        javaProperty.setPsiType(parameterType);
        javaProperty.setProject(psiParameter.getProject());
        javaProperty.setPsiAnnotations(annotations);
        javaProperty.setName(AnnotationUtil.extractParamName(psiParameter.getName(), annotations));
        javaProperty.setTypeName(parameterType.getPresentableText());
        javaProperty.setGenericTypeMap(resolveGenerics(parameterType));
        javaProperty.setComment(paramDescMap.get(psiParameter.getName()));
        javaProperty.setExample(FieldUtil.example(javaProperty));
        javaProperty.setRequired(AnnotationUtil.isRequired(annotations));
        javaProperty.setParent(null);
        javaProperty.setCycle(false);
        javaProperty.setChildren(resolveChildren(javaProperty));

        return javaProperty;
    }

    public static List<JavaProperty> create(PsiMethod psiMethod) {
        PsiDocComment docComment = psiMethod.getDocComment();
        Map<String, String> paranDescMap = DesUtil.paramDescMapForDocComment(docComment);
        return Arrays.stream(psiMethod.getParameterList().getParameters())
                .map(item -> create(item, paranDescMap))
                .collect(Collectors.toList());
    }

    public static JavaProperty createSimple(PsiType psiType, Project project, JavaProperty parent) {
        JavaProperty property = new JavaProperty(psiType, project, parent);
        property.setGenericTypeMap(resolveGenerics(psiType));
        property.setChildren(resolveChildren(property));
        return property;
    }

    public static JavaProperty createRealProperty(PsiType realType, JavaProperty originProperty) {
        JavaProperty property = new JavaProperty();
        property.setName(originProperty.getName());
        property.setComment(originProperty.getComment());
        property.setExample(originProperty.getExample());
        property.setRequired(originProperty.isRequired());
        property.setParent(originProperty.getParent());
        property.setCycle(originProperty.isCycle());
        property.setProject(originProperty.getProject());
        property.setPsiAnnotations(originProperty.getPsiAnnotations());

        property.setPsiType(realType);
        property.setTypeName(realType.getPresentableText());
        property.setGenericTypeMap(resolveGenerics(realType));
        property.setChildren(resolveChildren(property));
        return property;
    }

    private static boolean needResolveChildren(JavaProperty javaProperty) {
        JavaProperty propertyParent = javaProperty.getParent();
        PsiType psiType = javaProperty.getPsiType();
        String typeName = psiType.getPresentableText();

        if(FieldUtil.isNormalType(typeName)) {
            return false;
        }
        if(FieldUtil.isNormalCollectionType(typeName)) {
            return false;
        }
        if(FieldUtil.isNormalArrayType(typeName)) {
            return false;
        }
        if(FieldUtil.isEnum(psiType)) {
            return false;
        }
        if(FieldUtil.isMapType(typeName)) {
            return false;
        }
        if(FieldUtil.isMultipartType(psiType)) {
            return false;
        }
//        if (FieldUtil.isInterface(psiType)) {
//            return false;
//        }
        if (propertyParent != null) {
            Set<PsiType> resolvedTypeSet = new HashSet<>();
            JavaProperty dummyParent = propertyParent;
            while (dummyParent != null) {
                resolvedTypeSet.add(dummyParent.getPsiType());
                dummyParent = dummyParent.getParent();
            }

            if (psiType instanceof PsiArrayType) {
                psiType = ((PsiArrayType) psiType).getComponentType();
            }
            else if (FieldUtil.isCollectionType(psiType)) {
                psiType = PsiUtil.extractIterableTypeParameter(psiType, false);
            }

            if(resolvedTypeSet.contains(psiType)) {
                javaProperty.setCycle(true);
                return false;
            }
        }

        return true;
    }


    private static List<JavaProperty> resolveChildren(JavaProperty javaProperty) {
        Project currentProject = javaProperty.getProject();
        PsiType parameterType = javaProperty.getPsiType();

        if (!needResolveChildren(javaProperty)) {
            return Collections.emptyList();
        }

        DocConfig.ApiDocConfig apiDocConfig = DocConfig.getInstance(currentProject).getApiDocConfig();
        // 如果是数组
        if(parameterType instanceof PsiArrayType) {
            // 获取数组类型
            PsiType componentType = ((PsiArrayType) parameterType).getComponentType();
            JavaProperty property = new JavaProperty(componentType, currentProject, javaProperty.getParent());
            return resolveChildren(property);
        }

        // 如果是JavaClass
        if (parameterType instanceof PsiClassType) {
            // 如果是集合类型
            if (TypeUtils.isCollectionType(parameterType)) {
                TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(parameterType);
                PsiType realType = nestedInfo.getRealType();
                if (realType == null) {
                    return Collections.emptyList();
                }

                // 兼容泛型
                realType = resolveGeneric(realType, javaProperty);
                JavaProperty property = new JavaProperty(realType, currentProject, javaProperty.getParent());
                return resolveChildren(property);
            }

            // 兼容泛型
            PsiType realType = resolveGeneric(parameterType, javaProperty);
            PsiClass psiClass = PsiUtil.resolveClassInType(realType);

            // 兼容第三方jar包
            if (psiClass instanceof ClsClassImpl){
                psiClass = resolveJarPackage(psiClass, currentProject);
            }

            if (psiClass == null) {
                return Collections.emptyList();
            }

            // 兼容继承
            Set<String> fieldNameList = new HashSet<>(128);
            List<JavaProperty> theChildren = new ArrayList<>();
            for (PsiField psiField : psiClass.getAllFields()) {
                // 需要忽略的字段
                if (apiDocConfig.getExcludeFieldList().contains(psiField.getName())) {
                    continue;
                }
                // 忽略静态字段
                if(FieldUtil.isStaticField(psiField)) {
                    continue;
                }
                // 忽略标注了xxxIgnore注解的字段
                if(FieldUtil.isIgnoredField(psiField)) {
                    continue;
                }
                // 继承的情况下会出现子类与父类拥有相同名称的字段
                if(fieldNameList.contains(psiField.getName())) {
                    continue;
                }

                // 兼容泛型
                PsiType fieldRealType = resolveGeneric(psiField.getType(), javaProperty);

                JavaProperty property = new JavaProperty();
                property.setProject(currentProject);
                property.setPsiType(fieldRealType);
                property.setPsiAnnotations(psiField.getAnnotations());
                property.setParent(javaProperty);
                property.setName(psiField.getName());
                property.setComment(DesUtil.getDescription(psiField.getDocComment(), psiField.getAnnotations()));
                property.setGenericTypeMap(resolveGenerics(fieldRealType));
                property.setRequired(AnnotationUtil.isRequired(psiField.getAnnotations()));

                if (TypeUtils.isIterableType(fieldRealType)) {
                    TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(fieldRealType);
                    JavaProperty simple = JavaProperty.createSimple(nestedInfo.getRealType(), currentProject, javaProperty.getParent());
                    property.setChildren(simple.getChildren());
                } else {
                    property.setChildren(resolveChildren(property));
                }

                theChildren.add(property);
                fieldNameList.add(property.getName());
            }

            return theChildren;
        }


        return Collections.emptyList();
    }

    private static PsiClass resolveJarPackage(PsiClass psiClass, Project currentProject) {
        StubElement parentStub = ((ClsClassImpl) psiClass).getStub().getParentStub();
        if(parentStub instanceof PsiJavaFileStubImpl) {
            String sourcePath = ((PsiJavaFileStubImpl) parentStub)
                    .getPsi().getViewProvider().getVirtualFile().toString()
                    .replace(".jar!", "-sources.jar!");
            sourcePath = sourcePath.substring(0, sourcePath.length() - 5)+"java";
            VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(sourcePath);
            if(virtualFile != null) {
                FileViewProvider fileViewProvider = new SingleRootFileViewProvider(PsiManager.getInstance(currentProject), virtualFile);
                PsiFile psiFile = new PsiJavaFileImpl(fileViewProvider);
                return PsiTreeUtil.findChildOfAnyType(psiFile.getOriginalElement(), PsiClass.class);
            }
        }

        return psiClass;
    }

    private static Map<PsiTypeParameter, PsiType> resolveGenerics(PsiType psiType){
        if(psiType instanceof PsiArrayType) {
            return new HashMap<>();
        }
        if(psiType instanceof PsiClassType) {
            PsiClassType psiClassType = (PsiClassType) psiType;
            PsiType[] realParameters = psiClassType.getParameters();
            PsiClass psiClass = psiClassType.resolve();
            if (psiClass != null) {
                PsiTypeParameter[] formParameters = psiClass.getTypeParameters();
                int i = 0;
                Map<PsiTypeParameter, PsiType> map = new HashMap<>();
                for (PsiType realParameter : realParameters) {
                    map.put(formParameters[i], realParameter);
                    i ++;
                }
                return map;
            }

        }
        return new HashMap<>();
    }

    /**
     * 根据泛型获取对应的PsiType
     */
    private static PsiType resolveGeneric(PsiType psiType, JavaProperty javaProperty) {
        if(null == psiType){
            return null;
        }
        JavaProperty propertyParent = javaProperty.getParent();
        Map<PsiTypeParameter, PsiType> map = propertyParent != null ? propertyParent.getGenericTypeMap() : javaProperty.getGenericTypeMap();

        if(AssertUtils.isNotEmpty(map)){
            for (PsiTypeParameter psiTypeParameter : map.keySet()) {
                if(Objects.equals(psiTypeParameter.getName(), psiType.getPresentableText())){
                    return map.get(psiTypeParameter);
                }
            }
        }
        return psiType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public JavaProperty getParent() {
        return parent;
    }

    public void setParent(JavaProperty parent) {
        this.parent = parent;
    }

    public List<JavaProperty> getChildren() {
        return children;
    }

    public void setChildren(List<JavaProperty> children) {
        this.children = children;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    public void setPsiType(PsiType psiType) {
        this.psiType = psiType;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public PsiAnnotation[] getPsiAnnotations() {
        return psiAnnotations;
    }

    public void setPsiAnnotations(PsiAnnotation[] psiAnnotations) {
        this.psiAnnotations = psiAnnotations;
    }

    public Map<PsiTypeParameter, PsiType> getGenericTypeMap() {
        return genericTypeMap;
    }

    public void setGenericTypeMap(Map<PsiTypeParameter, PsiType> genericTypeMap) {
        this.genericTypeMap = genericTypeMap;
    }
}
