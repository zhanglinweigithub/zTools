package com.zhanglinwei.zTools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.zhanglinwei.zTools.actiongroup.AbstractGenerateSQLGroup;
import com.zhanglinwei.zTools.constant.MessageConstants;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.ClipboardUtils;
import com.zhanglinwei.zTools.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

public class CopyUpdateAction extends AbstractGenerateSQLGroup {

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        if (!init(actionEvent)) {
            return;
        }

        String insertSql = generateSQL(SQLTypeEnum.UPDATE);

        if (AssertUtils.isNotBlank(insertSql)) {
            ClipboardUtils.copyToClipboard(insertSql);
            NotificationUtil.infoNotify(MessageConstants.COPY_UPDATE_SUCCESS, getProject());
        }
    }
}
