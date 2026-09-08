package com.zhanglinwei.zTools.restful.component;

import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.zhanglinwei.zTools.common.util.StringUtils;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.Consumer;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SLASH;

/**
 * 在 Restful 搜索框上方放置全局前缀下拉，切换后回调刷新列表。
 */
public class IRestfulPrefixBar {

    /** 相对原 GoTo 弹窗额外加宽的像素（未缩放） */
    private static final int EXTRA_WIDTH = 120;

    /**
     * 把前缀下拉加到 GoTo 弹窗搜索框上方，默认选中第一项。
     * <p>
     * {@code createFilter} 时文本框尚未加入父容器，必须等弹窗 {@code initUI} 之后再挂载。
     *
     * @param popup    当前弹窗
     * @param prefixes 可选前缀，空串表示无前缀
     * @param onSelect 用户切换前缀时的回调
     */
    public static void install(ChooseByNamePopup popup, List<String> prefixes, Consumer<String> onSelect) {
        if (popup == null || prefixes == null || prefixes.isEmpty() || onSelect == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> attach(popup, prefixes, onSelect));
    }

    /**
     * 在搜索框所在面板顶部插入前缀行，并补足弹窗宽高，避免输入框被挤扁。
     *
     * @param popup    当前弹窗
     * @param prefixes 可选前缀
     * @param onSelect 切换回调
     */
    private static void attach(ChooseByNamePopup popup, List<String> prefixes, Consumer<String> onSelect) {
        JTextField textField = popup.getTextField();
        Container parent = textField.getParent();
        if (!(parent instanceof JComponent)) {
            return;
        }

        ComboBox<String> box = new ComboBox<String>(prefixes.toArray(new String[0]));
        box.setSelectedIndex(0);
        box.setRenderer(new PrefixRenderer());

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.add(new JBLabel("Global request prefix:"), BorderLayout.WEST);
        row.add(box, BorderLayout.CENTER);
        row.setOpaque(false);
        row.setAlignmentX(textField.getAlignmentX());
        lockRowHeight(row, box, textField);

        JComponent panel = (JComponent) parent;
        panel.add(row, 0);
        enlargePopup(textField, row);
        panel.revalidate();
        panel.repaint();

        box.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                SwingUtilities.invokeLater(() -> closeIfFocusLeftPopup(popup, textField, panel));
            }
        });
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        PropertyChangeListener focusOwnerListener = evt ->
                SwingUtilities.invokeLater(() -> closeIfFocusLeftPopup(popup, textField, panel));
        focusManager.addPropertyChangeListener("permanentFocusOwner", focusOwnerListener);
        Disposer.register(popup, () -> focusManager.removePropertyChangeListener("permanentFocusOwner", focusOwnerListener));
        box.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object selected = box.getSelectedItem();
                onSelect.accept(selected == null ? EMPTY : String.valueOf(selected));
                textField.requestFocusInWindow();
            }
        });
    }

    /**
     * 焦点已离开搜索弹窗（及其下拉）时关闭。点 Prefix 下拉本身不会关。
     *
     * @param popup     GoTo 弹窗
     * @param textField 搜索输入框
     * @param panel     搜索框所在面板
     */
    private static void closeIfFocusLeftPopup(ChooseByNamePopup popup, JTextField textField, JComponent panel) {
        if (popup == null || popup.checkDisposed()) {
            return;
        }
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner == null) {
            return;
        }
        if (shouldKeepOpen(focusOwner, textField, panel)) {
            return;
        }
        Window popupWindow = SwingUtilities.getWindowAncestor(textField);
        Window focusedWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
        if (focusedWindow != null && popupWindow != null
                && (focusedWindow == popupWindow || focusedWindow.getOwner() == popupWindow)) {
            return;
        }
        popup.close(false);
    }

    /**
     * 焦点仍在搜索框、前缀行或同一弹窗内时保持打开。
     *
     * @param focus     当前焦点组件
     * @param textField 搜索输入框
     * @param panel     搜索框所在面板
     * @return 应保持打开则为 {@code true}
     */
    private static boolean shouldKeepOpen(Component focus, JTextField textField, JComponent panel) {
        if (focus == textField || SwingUtilities.isDescendingFrom(focus, panel)) {
            return true;
        }
        Window focusWindow = SwingUtilities.getWindowAncestor(focus);
        return focusWindow != null && !(focusWindow instanceof JFrame)
                && SwingUtilities.getWindowAncestor(textField) != null
                && (focusWindow == SwingUtilities.getWindowAncestor(textField)
                || focusWindow.getOwner() == SwingUtilities.getWindowAncestor(textField));
    }

    /**
     * 把前缀行高度锁成与输入框接近，避免 BoxLayout 把输入框压扁。
     *
     * @param row       前缀行
     * @param box       前缀下拉
     * @param textField 搜索输入框
     */
    private static void lockRowHeight(JPanel row, ComboBox<String> box, JTextField textField) {
        int height = Math.max(textField.getPreferredSize().height, JBUI.scale(28));
        box.setPreferredSize(new Dimension(box.getPreferredSize().width, height));
        box.setMinimumSize(new Dimension(0, height));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        row.setPreferredSize(new Dimension(row.getPreferredSize().width, height));
        row.setMinimumSize(new Dimension(0, height));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    /**
     * 按前缀行高度加高弹窗，并加宽一点。只改弹窗窗口，不动 IDE 主窗口。
     *
     * @param textField 搜索输入框
     * @param row       前缀行
     */
    private static void enlargePopup(JTextField textField, JPanel row) {
        Window window = SwingUtilities.getWindowAncestor(textField);
        if (!(window instanceof JWindow) && !(window instanceof JDialog)) {
            return;
        }
        Dimension size = window.getSize();
        int extraHeight = row.getPreferredSize().height + JBUI.scale(6);
        int extraWidth = JBUI.scale(EXTRA_WIDTH);
        window.setSize(size.width + extraWidth, size.height + extraHeight);
        window.validate();
    }

    /**
     * 空前缀显示为 {@code /}，避免下拉看起来像没选项。
     */
    public static class PrefixRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (component instanceof JLabel) {
                String text = value == null ? SLASH : String.valueOf(value);
                ((JLabel) component).setText(StringUtils.isBlank(text) ? SLASH : text);
            }
            return component;
        }
    }
}
