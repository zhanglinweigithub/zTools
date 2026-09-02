package com.zhanglinwei.zTools.common.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * 系统剪贴板工具：把文本写入剪贴板，供「复制 cURL / 复制 JSON」等功能使用。
 */
public final class ClipboardUtils {

    /** 工具类，禁止实例化 */
    private ClipboardUtils(){}

    /**
     * 将文本写入系统剪贴板。
     *
     * @param content 要复制的文本，调用方保证非 {@code null}
     */
    public static void copyToClipboard(String content) {
        StringSelection selection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

}
