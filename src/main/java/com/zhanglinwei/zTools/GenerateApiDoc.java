package com.zhanglinwei.zTools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.zhanglinwei.zTools.constant.DocConstants;
import com.zhanglinwei.zTools.constant.MessageConstants;
import com.zhanglinwei.zTools.model.ClassInfo;
import com.zhanglinwei.zTools.model.MethodInfo;
import com.zhanglinwei.zTools.template.AbstractHtmlTemplate;
import com.zhanglinwei.zTools.template.AbstractMarkDownTemplate;
import com.zhanglinwei.zTools.template.AbstractWordTemplate;
import com.zhanglinwei.zTools.template.html.DefaultHtmlTemplate;
import com.zhanglinwei.zTools.template.md.DefaultMarkDownTemplate;
import com.zhanglinwei.zTools.template.word.DefaultWordTemplate;
import com.zhanglinwei.zTools.util.AnnotationUtil;
import com.zhanglinwei.zTools.util.ConfigUtils;
import com.zhanglinwei.zTools.util.FileUtils;
import com.zhanglinwei.zTools.util.NotificationUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GenerateApiDoc extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            return;
        }
        ConfigUtils.init(project);

        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        if (selectedClass == null) {
            NotificationUtil.errorNotify(MessageConstants.ONLY_CLASS, project);
            return;
        }

        executeGenerate(project, referenceAt, selectedClass);
    }

    private void executeGenerate(Project project, PsiElement referenceAt, PsiClass selectedClass) {
        boolean generateSuccess = false;

        try {

            PsiMethod selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
            generateSuccess = selectedMethod != null ? generateApiDocForSelectedMethod(project, selectedMethod) : generateApiDocForAllMethods(project, selectedClass);

        } catch (IOException e) {
            NotificationUtil.errorNotify("writer doc fail, Caused by: " + e.getMessage(), project);
        } catch (Exception e) {
            NotificationUtil.errorNotify("unknown exception, Caused by: " + e.getMessage(), project);
        }

        if (generateSuccess) {
            NotificationUtil.infoNotify(MessageConstants.API_DOC_SUCCESS, project);
        }

    }

    protected boolean generateApiDocForSelectedMethod(Project project, PsiMethod selectedMethod) throws IOException {
        PsiClass containingClass = selectedMethod.getContainingClass();

        if (!AnnotationUtil.hasMappingAnnotation(selectedMethod)) {
            NotificationUtil.warnNotify(MessageConstants.NOT_REST_API, project);
            return false;
        }

        ClassInfo classInfo = new ClassInfo(containingClass, new MethodInfo(selectedMethod));
        if (!classInfo.hasController()) {
            NotificationUtil.errorNotify(MessageConstants.NOT_CONTROLLER, project);
            return false;
        }

        return generateApiDoc(classInfo, project, true);
    }

    private boolean generateApiDocForAllMethods(Project project, PsiClass selectedClass) throws IOException {
        ClassInfo classInfo = new ClassInfo(selectedClass, true);
        if (!classInfo.hasController()) {
            NotificationUtil.errorNotify(MessageConstants.NOT_CONTROLLER, project);
            return false;
        }

        return generateApiDoc(classInfo, project, false);
    }

    private boolean generateApiDoc(ClassInfo classInfo, Project project, boolean forMethod) throws IOException {
        String dirPath = FileUtils.getDirPath(project);
        if (!FileUtils.mkDirectory(project, dirPath)) {
            return false;
        }
        String fileName = FileUtils.getFileName(classInfo, forMethod);

        switch (ConfigUtils.getDocType()) {
            case "MarkDown": return generateMdDoc(classInfo, dirPath + fileName + DocConstants.MD);
            case "Word": return generateWordDoc(classInfo, dirPath + fileName + DocConstants.DOCX);
            case "Html": return generateHtmlDoc(classInfo, dirPath + fileName + DocConstants.HTML);
            default: return false;
        }
    }

    private boolean generateHtmlDoc(ClassInfo classInfo, String pathName) throws IOException {
        File apiDoc = new File(pathName);
        try (OutputStreamWriter html = new OutputStreamWriter(Files.newOutputStream(apiDoc.toPath()), StandardCharsets.UTF_8)) {
            AbstractHtmlTemplate htmlTemplate = new DefaultHtmlTemplate();
            String content = htmlTemplate.buildApiContent(classInfo);
            html.write(content);
        }
        return true;
    }

    private boolean generateWordDoc(ClassInfo classInfo, String pathName) throws IOException {
        File apiDoc = new File(pathName);
        try (FileOutputStream fos = new FileOutputStream(apiDoc)) {
            AbstractWordTemplate wordTemplate = new DefaultWordTemplate();
            XWPFDocument word = wordTemplate.buildContent(classInfo);
            if (word == null) {
                return false;
            }

            word.write(fos);
        }
        return true;
    }

    private boolean generateMdDoc(ClassInfo classInfo, String pathName) throws IOException {
        File apiDoc = new File(pathName);
        try (OutputStreamWriter md = new OutputStreamWriter(Files.newOutputStream(apiDoc.toPath()), StandardCharsets.UTF_8)) {
            AbstractMarkDownTemplate markDownTemplate = new DefaultMarkDownTemplate();
            String content = markDownTemplate.buildContent(classInfo);
            md.write(content);
        }
        return true;
    }
}
