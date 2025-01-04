package com.zhanglinwei.zTools.doc;

import com.intellij.ide.projectView.ProjectView;
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
import com.zhanglinwei.zTools.doc.apidoc.model.ClassInfo;
import com.zhanglinwei.zTools.doc.apidoc.model.MethodInfo;
import com.zhanglinwei.zTools.doc.facade.DocFacade;
import com.zhanglinwei.zTools.util.AnnotationUtil;
import com.zhanglinwei.zTools.util.NotificationUtil;

import java.io.IOException;

public class GenerateApiDocAction extends AnAction {

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

        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        if (selectedClass == null) {
            NotificationUtil.errorNotify("This operation only supports Java Class files!", project);
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
            NotificationUtil.infoNotify("Generate Api document successfully!", project);
            ProjectView.getInstance(project).refresh();
        }

    }

    protected boolean generateApiDocForSelectedMethod(Project project, PsiMethod selectedMethod) throws Exception {
        PsiClass containingClass = selectedMethod.getContainingClass();

        if (!AnnotationUtil.hasMappingAnnotation(selectedMethod)) {
            NotificationUtil.warnNotify("The method is not a RestApi!", project);
            return false;
        }

        ClassInfo classInfo = new ClassInfo(containingClass, new MethodInfo(selectedMethod));
        if (!classInfo.hasController()) {
            NotificationUtil.errorNotify("The file is not a Controller!", project);
            return false;
        }
        return DocFacade.generateApiDoc(classInfo, project, false);
    }

    private boolean generateApiDocForAllMethods(Project project, PsiClass selectedClass) throws Exception {
        ClassInfo classInfo = new ClassInfo(selectedClass, true);
        if (!classInfo.hasController()) {
            NotificationUtil.errorNotify("The file is not a Controller!", project);
            return false;
        }

        return DocFacade.generateApiDoc(classInfo, project, false);
    }

}
