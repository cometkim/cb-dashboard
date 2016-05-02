package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerProjectWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;

public class XBeamerProjectInfoPlugin extends XBeamerWrapperPlugin{

    @Override
    public String getChartName() {
        return "Project Info";
    }

    @Override
    public String getChartDescription() {
        return "Displays basic information about a specified project";
    }

    @Override
    public String getImgUrl() { return "/cb/xbeamerchart/images/landscape.jpg"; }

    private XBeamerWidget projectIdWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        projectIdWidget = new XBeamerProjectWidget(context, false);
        projectIdWidget.setRequired(true);
        projectIdWidget.setLabel("Select a Project");
        this.addWidgetForParameter("projectId", projectIdWidget);
    }

    @Override
    protected Class<ProjectInfoPlugin> getOriginPlugin() { return ProjectInfoPlugin.class; }
}