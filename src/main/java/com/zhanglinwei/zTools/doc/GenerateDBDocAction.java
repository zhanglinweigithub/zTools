package com.zhanglinwei.zTools.doc;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.doc.dbdoc.model.DBTableInfo;
import com.zhanglinwei.zTools.doc.dbdoc.model.DSCfg;
import com.zhanglinwei.zTools.doc.facade.DocFacade;
import com.zhanglinwei.zTools.util.NotificationUtil;

import java.util.Collection;
import java.util.List;

public class GenerateDBDocAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (project == null) {
            return;
        }

        DSCfg dsCfg = DSCfg.newInstance(project);
        if (dsCfg.hasError()) {
            NotificationUtil.errorNotify(dsCfg.getErrorMsg(), project);
            return;
        }

        try {
            List<DBTableInfo> tableInfoList = DBTableInfo.create(dsCfg);
            if (tableInfoList.isEmpty()) {
                NotificationUtil.infoNotify("There are no tables in the database", project);
                return;
            }

            executeGenerate(tableInfoList, dsCfg);
        } catch (Exception e) {
            NotificationUtil.errorNotify("unknown exception, Caused by: " + e.getMessage(), project);
        }

    }

    private void executeGenerate(Collection<DBTableInfo> tableInfos, DSCfg dsCfg) throws Exception {
        Project project = dsCfg.getProject();

        if (DocFacade.generateDbDoc(tableInfos, dsCfg)) {
            NotificationUtil.infoNotify("Generate DB document successfully!", project);
        }
    }

}
