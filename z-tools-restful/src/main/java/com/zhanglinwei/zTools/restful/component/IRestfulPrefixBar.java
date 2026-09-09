package com.zhanglinwei.zTools.restful.component;

import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.configure.config.RestfulConfig;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SLASH;

/**
 * 在 Restful 搜索框上方放置前缀下拉，右侧放 HTTP 方法过滤按钮。
 */
public class IRestfulPrefixBar {

    /** 相对原 GoTo 弹窗额外加宽的像素（未缩放） */
    private static final int EXTRA_WIDTH = 120;

    /**
     * 把前缀行加到搜索框上方，默认选中第一项。
     * <p>
     * {@code createFilter} 时文本框尚未加入父容器，必须等弹窗 {@code initUI} 之后再挂载。
     *
     * @param popup    当前弹窗
     * @param prefixes 可选前缀，空串表示根路径 {@code /}
     * @param onSelect 用户切换前缀时的回调
     * @param model    名称模型，用于 HTTP 方法过滤
     * @param project  当前工程
     */
    public static void install(ChooseByNamePopup popup, List<String> prefixes, Consumer<String> onSelect,
                               IRestfulChooseByNameModel model, Project project) {
        if (popup == null || prefixes == null || prefixes.isEmpty() || onSelect == null || model == null || project == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> attach(popup, prefixes, onSelect, model, project));
    }

    /**
     * 在搜索框所在面板顶部插入前缀行，并补足弹窗高度。
     *
     * @param popup    当前弹窗
     * @param prefixes 可选前缀
     * @param onSelect 切换回调
     * @param model    名称模型
     * @param project  当前工程
     */
    private static void attach(ChooseByNamePopup popup, List<String> prefixes, Consumer<String> onSelect,
                               IRestfulChooseByNameModel model, Project project) {
        JTextField textField = popup.getTextField();
        if (textField == null) {
            return;
        }
        Container parent = textField.getParent();
        if (!(parent instanceof JComponent)) {
            return;
        }
        JComponent panel = (JComponent) parent;

        ComboBox<String> box = new ComboBox<String>(prefixes.toArray(new String[0]));
        box.setSelectedIndex(0);
        box.setRenderer(new PrefixRenderer());

        IRestfulMethodFilter methodFilter = new IRestfulMethodFilter(popup, model, RestfulConfig.getInstance(project));

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.add(new JBLabel("Prefix:"), BorderLayout.WEST);
        row.add(box, BorderLayout.CENTER);
        row.add(methodFilter.getButton(), BorderLayout.EAST);
        row.setOpaque(false);
        row.setAlignmentX(textField.getAlignmentX());
        lockRowHeight(row, box, textField);

        panel.add(row, 0);
        enlargePopup(textField, row);
        panel.revalidate();
        panel.repaint();

        box.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                SwingUtilities.invokeLater(() -> closeIfFocusLeftPopup(popup, textField, methodFilter));
            }
        });
        AWTEventListener clickOutside = event -> {
            if (event instanceof MouseEvent) {
                MouseEvent mouse = (MouseEvent) event;
                if (mouse.getID() == MouseEvent.MOUSE_PRESSED) {
                    closeIfClickOutside(popup, textField, methodFilter, mouse);
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(clickOutside, AWTEvent.MOUSE_EVENT_MASK);
        Disposer.register(popup, () -> Toolkit.getDefaultToolkit().removeAWTEventListener(clickOutside));
        box.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Object selected = box.getSelectedItem();
                onSelect.accept(selected == null ? EMPTY : String.valueOf(selected));
                textField.requestFocusInWindow();
            }
        });
    }

    /**
     * 点在搜索弹窗、结果列表、前缀下拉或方法过滤弹层之外时关闭。
     *
     * @param popup        GoTo 弹窗
     * @param textField    搜索输入框
     * @param methodFilter 方法过滤
     * @param mouse        鼠标按下事件
     */
    private static void closeIfClickOutside(ChooseByNamePopup popup, JTextField textField,
                                            IRestfulMethodFilter methodFilter, MouseEvent mouse) {
        if (popup == null || popup.checkDisposed()) {
            return;
        }
        Component target = mouse.getComponent();
        if (target == null) {
            return;
        }
        if (isInsideSearchUi(target, textField, methodFilter)) {
            return;
        }
        popup.close(false);
    }

    /**
     * 前缀下拉失焦且焦点已离开搜索 UI 时关闭。
     *
     * @param popup        GoTo 弹窗
     * @param textField    搜索输入框
     * @param methodFilter 方法过滤
     */
    private static void closeIfFocusLeftPopup(ChooseByNamePopup popup, JTextField textField,
                                              IRestfulMethodFilter methodFilter) {
        if (popup == null || popup.checkDisposed()) {
            return;
        }
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner == null || isInsideSearchUi(focusOwner, textField, methodFilter)) {
            return;
        }
        popup.close(false);
    }

    /**
     * 点击或焦点是否仍在搜索框、结果列表、前缀下拉或方法过滤弹层内。
     *
     * @param target       目标组件
     * @param textField    搜索输入框
     * @param methodFilter 方法过滤
     * @return 仍在搜索 UI 内则为 {@code true}
     */
    private static boolean isInsideSearchUi(Component target, JTextField textField, IRestfulMethodFilter methodFilter) {
        if (textField == null || target == null) {
            return false;
        }
        if (SwingUtilities.isDescendingFrom(target, textField.getParent())) {
            return true;
        }
        Component root = popupContentRoot(textField);
        if (root != null && SwingUtilities.isDescendingFrom(target, root)) {
            return true;
        }
        JBPopup methodPopup = methodFilter.getMethodPopup();
        if (methodPopup != null && !methodPopup.isDisposed() && methodPopup.getContent() != null
                && SwingUtilities.isDescendingFrom(target, methodPopup.getContent())) {
            return true;
        }
        Window popupWindow = SwingUtilities.getWindowAncestor(textField);
        Window targetWindow = SwingUtilities.getWindowAncestor(target);
        if (targetWindow != null && popupWindow != null) {
            if (targetWindow == popupWindow) {
                Container layered = root == null ? null : root.getParent();
                return layered instanceof JLayeredPane && SwingUtilities.isDescendingFrom(target, layered);
            }
            return targetWindow.getOwner() == popupWindow || !(targetWindow instanceof JFrame);
        }
        return false;
    }

    /**
     * 搜索弹窗内容根节点（其父级是分层窗格或窗口）。
     *
     * @param textField 搜索输入框
     * @return 内容根
     */
    private static Component popupContentRoot(Component textField) {
        Component current = textField;
        while (current.getParent() != null) {
            Container parent = current.getParent();
            if (parent instanceof Window || parent instanceof JLayeredPane) {
                return current;
            }
            current = parent;
        }
        return current;
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
        if (window instanceof JWindow || window instanceof JDialog) {
            Dimension size = window.getSize();
            int extraHeight = row.getPreferredSize().height + JBUI.scale(6);
            window.setSize(size.width + JBUI.scale(EXTRA_WIDTH), size.height + extraHeight);
            window.validate();
            return;
        }
        Component root = popupContentRoot(textField);
        if (root == null) {
            return;
        }
        int extraHeight = row.getPreferredSize().height + JBUI.scale(6);
        Dimension current = root.getSize();
        if (current.width <= 0 || current.height <= 0) {
            current = root.getPreferredSize();
        }
        root.setPreferredSize(new Dimension(current.width + JBUI.scale(EXTRA_WIDTH), current.height + extraHeight));
        root.setSize(root.getPreferredSize());
        Container parent = root.getParent();
        if (parent != null) {
            parent.invalidate();
            parent.validate();
            parent.repaint();
        }
    }

    /**
     * 空前缀显示为 {@code /}，避免下拉看起来像没选项。
     */
    public static class PrefixRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                String text = value == null ? SLASH : String.valueOf(value);
                label.setText(StringUtils.isBlank(text) ? SLASH : text);
                label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            }
            return component;
        }
    }
}
