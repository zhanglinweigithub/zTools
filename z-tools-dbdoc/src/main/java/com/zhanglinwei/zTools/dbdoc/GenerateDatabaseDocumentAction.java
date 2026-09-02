package com.zhanglinwei.zTools.dbdoc;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.doc.DocOutput;
import com.zhanglinwei.zTools.configure.config.DocumentConfig;
import com.zhanglinwei.zTools.dbdoc.generator.DatabaseDocumentGenerator;
import com.zhanglinwei.zTools.dbdoc.config.DataSourceConfig;
import com.zhanglinwei.zTools.dbdoc.model.TableInfo;
import com.zhanglinwei.zTools.common.util.NotificationUtil;

import java.util.List;

public class GenerateDatabaseDocumentAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (project == null) {
            return;
        }

        DataSourceConfig dataSourceConfig = DataSourceConfig.from(project);
        if (dataSourceConfig.hasError()) {
            NotificationUtil.errorNotify(dataSourceConfig.getErrorMsg(), project);
            return;
        }

        try {
            List<TableInfo> tableInfoList = dataSourceConfig.getDialect().loadTables(dataSourceConfig);
            if (tableInfoList.isEmpty()) {
                NotificationUtil.infoNotify("There are no tables in the database", project);
                return;
            }

            DocumentConfig documentConfig = DocumentConfig.getInstance(project);
            String outputPath = DocOutput.resolveDir(project, documentConfig.getSaveDir())
                    + dataSourceConfig.getDatabaseName()
                    + DatabaseDocumentGenerator.suffixOf(documentConfig.getDocType());

            if (DatabaseDocumentGenerator.write(tableInfoList, outputPath, documentConfig.getDocType())) {
                NotificationUtil.infoNotify("Generate DataBase document successfully!", project);
            }
        } catch (Exception e) {
            NotificationUtil.errorNotify("unknown exception, Caused by: " + e.getMessage(), project);
        }
    }
}
