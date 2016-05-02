package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.recentactivities.MostActiveProjectsPlugin;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerMostActiveProjectPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Most Active Projects";
    }

    @Override
    public String getChartDescription() {
        return "Shows the top most active projects";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/landscape.jpg";
    }

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();
    }

    @Override
    protected Class<MostActiveProjectsPlugin> getOriginPlugin() {
        return MostActiveProjectsPlugin.class;
    }
}
