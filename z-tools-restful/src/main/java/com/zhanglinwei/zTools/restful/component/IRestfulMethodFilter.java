package com.zhanglinwei.zTools.restful.component;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ui.JBUI;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.configure.config.RestfulConfig;
import org.jetbrains.annotations.NotNull;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Restful 搜索框的 HTTP 方法过滤：自绘漏斗按钮与勾选弹层，不依赖平台 ChooseByNameFilter。
 * <p>
 * 2026 里平台沙漏按钮经常点不出弹层，因此把过滤入口放在前缀行右侧自行弹出。
 */
public class IRestfulMethodFilter {

    private final ChooseByNamePopup popup;
    private final IRestfulChooseByNameModel model;
    private final RestfulConfig config;
    private final JButton button;
    private JBPopup methodPopup;

    /**
     * 创建漏斗按钮，并按已保存配置立刻过滤列表。
     *
     * @param popup 当前 GoTo 弹窗
     * @param model 名称模型
     * @param projectConfig 工程内保存的方法勾选
     */
    public IRestfulMethodFilter(ChooseByNamePopup popup, IRestfulChooseByNameModel model, RestfulConfig projectConfig) {
        this.popup = popup;
        this.model = model;
        this.config = projectConfig;
        this.button = createButton();
        apply(true);
        Disposer.register(popup, this::closePopup);
    }

    /**
     * 放在前缀行右侧的漏斗按钮。
     *
     * @return 按钮
     */
    public JButton getButton() {
        return button;
    }

    /**
     * 当前方法过滤弹层，点搜索框外关闭时用来判断点击是否落在弹层内。
     *
     * @return 弹层；未打开则为 {@code null}
     */
    public JBPopup getMethodPopup() {
        return methodPopup;
    }

    /**
     * 创建不抢焦点的漏斗按钮，避免搜索框失焦把整个 GoTo 弹窗关掉。
     *
     * @return 按钮
     */
    private JButton createButton() {
        JButton filterButton = new JButton(AllIcons.General.Filter);
        filterButton.setToolTipText("Filter by HTTP method");
        filterButton.setFocusable(false);
        filterButton.setOpaque(false);
        filterButton.setContentAreaFilled(false);
        filterButton.setBorder(JBUI.Borders.empty(2));
        int size = JBUI.scale(28);
        filterButton.setPreferredSize(new Dimension(size, size));
        filterButton.addActionListener(event -> togglePopup());
        return filterButton;
    }

    /**
     * 打开或关闭方法勾选弹层。延后到下一帧显示，避免同一次点击被当成“点在弹层外”而立刻关掉。
     */
    private void togglePopup() {
        if (methodPopup != null && methodPopup.isVisible()) {
            closePopup();
            return;
        }
        JPanel chooser = createChooserPanel();
        methodPopup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(chooser, chooser)
                .setFocusable(false)
                .setRequestFocus(false)
                .setModalContext(false)
                .setCancelOnClickOutside(true)
                .setCancelOnWindowDeactivation(false)
                .setMinSize(new Dimension(JBUI.scale(180), JBUI.scale(180)))
                .createPopup();
        methodPopup.addListener(new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                methodPopup = null;
            }
        });
        Disposer.register(popup, methodPopup);
        JButton filterButton = button;
        SwingUtilities.invokeLater(() -> {
            if (methodPopup != null && !methodPopup.isDisposed()) {
                methodPopup.showUnderneathOf(filterButton);
            }
        });
    }

    /**
     * 勾选列表与全选 / 全不选 / 反选。
     *
     * @return 弹层内容
     */
    private JPanel createChooserPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(JBUI.Borders.empty(8));
        Map<HttpMethod, JCheckBox> boxes = new LinkedHashMap<HttpMethod, JCheckBox>();
        HttpMethod[] methods = HttpMethod.values();
        for (HttpMethod method : methods) {
            JCheckBox checkBox = new JCheckBox(method.name(), config.accepts(method));
            checkBox.setFocusable(false);
            checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            checkBox.setHorizontalAlignment(SwingConstants.LEFT);
            checkBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, checkBox.getPreferredSize().height));
            checkBox.addItemListener(event -> {
                config.setAccepts(method, checkBox.isSelected());
                apply(false);
            });
            boxes.put(method, checkBox);
            panel.add(checkBox);
        }
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton all = new JButton("All");
        all.setFocusable(false);
        all.addActionListener(event -> setAll(boxes, true));
        JButton none = new JButton("None");
        none.setFocusable(false);
        none.addActionListener(event -> setAll(boxes, false));
//        JButton invert = new JButton("Invert");
//        invert.setFocusable(false);
//        invert.addActionListener(event -> invert(boxes));
        buttons.add(all);
        buttons.add(none);
//        buttons.add(invert);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttons.getPreferredSize().height));
        panel.add(Box.createVerticalStrut(JBUI.scale(6)));
        panel.add(buttons);
        return panel;
    }

    /**
     * 全选或全不选。
     *
     * @param boxes  各方法勾选框
     * @param marked 是否勾选
     */
    private void setAll(Map<HttpMethod, JCheckBox> boxes, boolean marked) {
        for (Map.Entry<HttpMethod, JCheckBox> entry : boxes.entrySet()) {
            entry.getValue().setSelected(marked);
        }
    }

    /**
     * 反选。
     *
     * @param boxes 各方法勾选框
     */
    private void invert(Map<HttpMethod, JCheckBox> boxes) {
        for (Map.Entry<HttpMethod, JCheckBox> entry : boxes.entrySet()) {
            JCheckBox checkBox = entry.getValue();
            checkBox.setSelected(!checkBox.isSelected());
        }
    }

    /**
     * 按当前勾选更新模型并刷新列表。
     *
     * @param initial 是否为弹窗刚打开时的首次过滤
     */
    private void apply(boolean initial) {
        List<HttpMethod> marked = new ArrayList<HttpMethod>();
        HttpMethod[] methods = HttpMethod.values();
        for (HttpMethod method : methods) {
            if (config.accepts(method)) {
                marked.add(method);
            }
        }
        model.setFilterItems(marked);
        popup.rebuildList(initial);
    }

    /**
     * 关闭方法勾选弹层。
     */
    private void closePopup() {
        if (methodPopup != null && !methodPopup.isDisposed()) {
            methodPopup.cancel();
        }
        methodPopup = null;
    }
}
