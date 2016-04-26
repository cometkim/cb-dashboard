package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerProjectSelectWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;

import com.intland.codebeamer.wiki.plugins.ProjectInfoPlugin;
import com.intland.codebeamer.wiki.plugins.recentactivities.ActivityStreamPlugin;

public class XBeamerProjectInfoPlugin extends XBeamerWrapperPlugin{

    @Override
    public String getChartName() {
        return null;
    }

    @Override
    public String getChartDescription() {
        return null;
    }

    @Override
    public String getImgUrl() {
        return null;
    }

    private XBeamerWidget projectIdWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        projectIdWidget = new XBeamerProjectSelectWidget(context, false);
        this.addWidgetForParameter("projectId", projectIdWidget);
    }

    @Override
    protected Class<ProjectInfoPlugin> getOriginPlugin() { return ProjectInfoPlugin.class; }
}