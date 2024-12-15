package com.zhanglinwei.zTools;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.zhanglinwei.zTools.actiongroup.AbstractGenerateSQLGroup;
import com.zhanglinwei.zTools.constant.MessageConstants;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.ClipboardUtils;
import com.zhanglinwei.zTools.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

public class CopySelectAction extends AbstractGenerateSQLGroup {

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        if (!init(actionEvent)) {
            return;
        }

        String insertSql = generateSQL(SQLTypeEnum.SELECT);

        if (AssertUtils.isNotBlank(insertSql)) {
            ClipboardUtils.copyToClipboard(insertSql);
            NotificationUtil.infoNotify(MessageConstants.COPY_SELECT_SUCCESS, getProject());
        }
    }

}
