package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.BaseCodeInsightAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * 插件入口：Java Generate 菜单里的 Builder（plugin.xml 注册本类）。
 * <p>
 * IDEA 会先问 {@link #isValidForFile} 是否显示菜单项，点下去后把工作交给
 * {@link GenerateBuilderHandler}：选字段、再生成内部 Builder。
 */
public class GenerateBuilderAction extends BaseCodeInsightAction {

    private final GenerateBuilderHandler handler = new GenerateBuilderHandler();

    /**
     * 返回实际处理逻辑。
     *
     * @return Generate Builder 的 Handler
     */
    @NotNull
    @Override
    protected CodeInsightActionHandler getHandler() {
        return handler;
    }

    /**
     * 仅当当前文件是带实例字段的 Java 类时，菜单里才出现 Builder。
     *
     * @param project 当前项目
     * @param editor  当前编辑器
     * @param file    当前文件
     * @return 菜单项是否可用
     */
    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return handler.isValidFor(editor, file);
    }
}
