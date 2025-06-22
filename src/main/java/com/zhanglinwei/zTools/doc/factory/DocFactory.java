package com.zhanglinwei.zTools.doc.factory;

import com.zhanglinwei.zTools.doc.handler.DocHandler;
import com.zhanglinwei.zTools.doc.handler.HtmlDocHandler;
import com.zhanglinwei.zTools.doc.handler.MarkDownDocHandler;
import com.zhanglinwei.zTools.doc.handler.WordDocHandler;

import java.util.HashMap;
import java.util.Map;

public class DocFactory {

    private static final Map<String, DocHandler> docHandlerMap;

    static {
        HtmlDocHandler htmlDocHandler = new HtmlDocHandler();
        MarkDownDocHandler markDownDocHandler = new MarkDownDocHandler();
        WordDocHandler wordDocHandler = new WordDocHandler();

        docHandlerMap = new HashMap<>();
        docHandlerMap.put(htmlDocHandler.support().getType(), htmlDocHandler);
        docHandlerMap.put(markDownDocHandler.support().getType(), markDownDocHandler);
        docHandlerMap.put(wordDocHandler.support().getType(), wordDocHandler);
    }


    public static DocHandler getHandler(String fileFormat) {
        return docHandlerMap.get(fileFormat);
    }

}
