package com.zhanglinwei.zTools.restful.component;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.restful.matcher.AntPathMatcher;
import com.zhanglinwei.zTools.restful.model.IRestful;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

import static com.zhanglinwei.zTools.common.constant.StringPool.*;

/**
 * GoTo 窗口的名称模型。
 * <p>
 * 提供提示文案、当前模块勾选状态，并用模糊匹配 + Ant 路径匹配过滤接口。
 */
public class IRestfulChooseByNameModel extends FilteringGotoByModel<HttpMethod> implements DumbAware, CustomMatcherModel {

    /** 记住“仅当前模块”勾选状态的持久化键 */
    private final String CURRENT_MODULE_PERSISTENT_KEY = "CURRENT_MODULE_PERSISTENT_KEY";
    /** 当前工程 */
    private final Project currentProject;

    /**
     * 绑定工程与名称贡献者。
     *
     * @param project      当前工程
     * @param contributors 提供接口名称的贡献者
     */
    public IRestfulChooseByNameModel(@NotNull Project project, @NotNull ChooseByNameContributor contributors) {
        super(project, Collections.singletonList(contributors));
        this.currentProject = project;
    }

    /**
     * 取出导航项对应的 HTTP 方法，供过滤器使用。
     *
     * @param navigationItem 导航项
     * @return HTTP 方法；非 {@link IRestful} 时为 {@code null}
     */
    @Override
    protected @Nullable HttpMethod filterValueFor(NavigationItem navigationItem) {
        if (navigationItem instanceof IRestful) {
            return ((IRestful) navigationItem).getRequestType();
        }

        return null;
    }

    /**
     * 输入框提示文本。
     *
     * @return 提示文案
     */
    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) String getPromptText() {
        return "Input URL Path";
    }

    /**
     * 未包含在范围内时的提示。
     *
     * @return 提示文案
     */
    @Override
    public @NotNull @NlsContexts.Label String getNotInMessage() {
        return "Not found";
    }

    /**
     * 未找到匹配项时的提示。
     *
     * @return 提示文案
     */
    @Override
    public @NotNull @NlsContexts.Label String getNotFoundMessage() {
        return "Not found";
    }

    /**
     * 复选框名称。
     *
     * @return “Current Module”
     */
    @Override
    public @Nullable @NlsContexts.Label String getCheckBoxName() {
        return "Current Module";
    }

    /**
     * 读取“仅当前模块”的上次勾选状态。
     *
     * @return 已勾选则为 {@code true}
     */
    @Override
    public boolean loadInitialCheckBoxState() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(currentProject);
        return propertiesComponent.isTrueValue(CURRENT_MODULE_PERSISTENT_KEY);
    }

    /**
     * 保存“仅当前模块”勾选状态。
     *
     * @param state 是否勾选
     */
    @Override
    public void saveInitialCheckBoxState(boolean state) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(currentProject);
        propertiesComponent.setValue(CURRENT_MODULE_PERSISTENT_KEY, state);
    }

    /**
     * 路径分隔符：斜杠与问号（忽略 query）。
     *
     * @return 分隔符数组
     */
    @Override
    public String @NotNull [] getSeparators() {
        return new String[]{SLASH, QUESTION_MARK};
    }

    /**
     * 元素全名，与展示名称相同。
     *
     * @param element 列表元素
     * @return 名称
     */
    @Override
    public @Nullable String getFullName(@NotNull Object element) {
        return getElementName(element);
    }

    /**
     * 选中后是否打开编辑器。
     *
     * @return 始终为 {@code true}
     */
    @Override
    public boolean willOpenEditor() {
        return true;
    }

    /**
     * 判断接口路径是否匹配用户输入。
     * <p>
     * 先用 IDE 模糊匹配，失败再用 {@link AntPathMatcher}：
     * {@code /user/{id}} 可匹配 {@code /user/123}。
     *
     * @param item  接口路径
     * @param input 用户输入
     * @return 匹配则为 {@code true}
     */
    @Override
    public boolean matches(@NotNull String item, @NotNull String input) {
        if(input.equals(SLASH)) {
            return true;
        }

        MinusculeMatcher matcher = new NameUtil.MatcherBuilder(STAR + input)
                .build();
        boolean matches = matcher.matches(item);
        if (!matches) {
            AntPathMatcher pathMatcher = new AntPathMatcher();
            matches = pathMatcher.match(item,input);
        }
        return matches;
    }

}
