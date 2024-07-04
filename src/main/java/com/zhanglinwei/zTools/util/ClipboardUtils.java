package com.zhanglinwei.zTools.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * 剪贴板工具类
 */
public class ClipboardUtils {

    private ClipboardUtils(){}

    public static void copyToClipboard(String content) {
        StringSelection selection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

}
