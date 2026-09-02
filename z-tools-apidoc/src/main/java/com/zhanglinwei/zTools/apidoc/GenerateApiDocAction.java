package com.zhanglinwei.zTools.apidoc;

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
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.parse.SourceParser;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.apidoc.generator.ApiDocumentGenerator;
import com.zhanglinwei.zTools.common.util.NotificationUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 插件入口：编辑器右键「Generate Api Doc」。
 * <p>
 * 流程：取光标位置 → 解析为类 / 方法定义 → 校验 Controller / Mapping →
 * {@link ApiInfo#create} 抽出接口信息 → {@link ApiDocumentGenerator#write} 落盘。
 * <ul>
 *   <li>光标落在方法内：只生成该接口（方法需有 Mapping，类需是 Controller）</li>
 *   <li>光标落在类上、不在某个方法内：生成整个 Controller 的接口</li>
 * </ul>
 */
public class GenerateApiDocAction extends AnAction {

    /**
     * 从 AnActionEvent 取出 Editor / PsiFile，定位光标处的 PsiClass，再分流到方法或类生成。
     *
     * @param event IDEA 动作事件，需带 Editor 与 PsiFile
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            return;
        }

        PsiElement atCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(atCaret, PsiClass.class);
        if (selectedClass == null) {
            NotificationUtil.errorNotify("This operation only supports Java Class files!", project);
            return;
        }

        boolean success = false;
        try {
            PsiMethod selectedMethod = PsiTreeUtil.getContextOfType(atCaret, PsiMethod.class);
            success = selectedMethod != null
                    ? generateMethod(project, selectedClass, selectedMethod)
                    : generateClass(project, selectedClass);
        } catch (IOException e) {
            NotificationUtil.errorNotify("writer doc fail, Caused by: " + e.getMessage(), project);
        } catch (Exception e) {
            NotificationUtil.errorNotify("unknown exception, Caused by: " + e.getMessage(), project);
        }

        if (success) {
            NotificationUtil.infoNotify("Generate Api document successfully!", project);
            ProjectView.getInstance(project).refresh();
        }
    }

    /**
     * 单个接口：方法必须有 Mapping，所属类必须是 Controller。
     *
     * @param project  当前工程
     * @param psiClass 所在控制器
     * @param method   光标所在方法
     * @return 写出成功则为 {@code true}
     * @throws Exception 解析失败或写文档失败
     */
    private boolean generateMethod(Project project, PsiClass psiClass, PsiMethod method) throws Exception {
        ClassDefinition type = SourceParser.parseClass(psiClass, false);
        MethodDefinition methodDefinition = SourceParser.parseMethod(method);
        if (!WebAnnotationParser.isHandlerMethod(methodDefinition)) {
            NotificationUtil.warnNotify("The method is not a RestApi!", project);
            return false;
        }
        if (!WebAnnotationParser.isController(type)) {
            NotificationUtil.errorNotify("The file is not a Controller!", project);
            return false;
        }
        ApiInfo apiInfo = ApiInfo.create(type, methodDefinition);
        if (apiInfo == null) {
            NotificationUtil.warnNotify("The method is not a RestApi!", project);
            return false;
        }
        return ApiDocumentGenerator.write(
                Collections.singletonList(apiInfo),
                project,
                ApiFields.titleOf(methodDefinition)
        );
    }

    /**
     * 整个 Controller：类上要有 Controller 注解，文件名取自类注释或类名。
     *
     * @param project  当前工程
     * @param psiClass 控制器类
     * @return 写出成功则为 {@code true}
     * @throws Exception 解析失败或写文档失败
     */
    private boolean generateClass(Project project, PsiClass psiClass) throws Exception {
        ClassDefinition type = SourceParser.parseClass(psiClass, true);
        if (!WebAnnotationParser.isController(type)) {
            NotificationUtil.errorNotify("The file is not a Controller!", project);
            return false;
        }
        List<ApiInfo> apiInfos = ApiInfo.create(type);
        return ApiDocumentGenerator.write(apiInfos, project, ApiFields.titleOf(type));
    }
}
