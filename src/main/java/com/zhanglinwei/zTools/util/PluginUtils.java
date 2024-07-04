package com.zhanglinwei.zTools.util;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;

import java.util.List;

public class PluginUtils {

    private PluginUtils (){}

    public static String getInstallPath() {
        List<? extends IdeaPluginDescriptor> loadedPlugins = PluginManager.getLoadedPlugins();
        if (AssertUtils.isNotEmpty(loadedPlugins)) {
            for (IdeaPluginDescriptor plugin : loadedPlugins) {
                PluginId pluginId = plugin.getPluginId();
                if("com.zhanglinwei.zTools".equals(pluginId.getIdString())) {
                    return plugin.getPluginPath().toString().replaceAll("\\\\", "/");
                }
            }
        }

        return "";
    }

    public static IdeaPluginDescriptor getPluginDescriptor() {
        List<? extends IdeaPluginDescriptor> loadedPlugins = PluginManager.getLoadedPlugins();
        if (AssertUtils.isNotEmpty(loadedPlugins)) {
            for (IdeaPluginDescriptor plugin : loadedPlugins) {
                PluginId pluginId = plugin.getPluginId();
                if("com.zhanglinwei.zTools".equals(pluginId.getIdString())) {
                    return plugin;
                }
            }
        }

        return null;
    }

    public static String getLibPath() {
        String installPath = getInstallPath();
        return installPath + "/lib/";
    }



}
