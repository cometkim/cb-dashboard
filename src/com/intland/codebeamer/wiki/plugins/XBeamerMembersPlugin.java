package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerProjectWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;

public class XBeamerMembersPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Members";
    }

    @Override
    public String getChartDescription() {
        return "Display the members and administrators of the specified project";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/members.jpg";
    }

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
    protected Class<MembersPlugin> getOriginPlugin() {
        return MembersPlugin.class;
    }
}