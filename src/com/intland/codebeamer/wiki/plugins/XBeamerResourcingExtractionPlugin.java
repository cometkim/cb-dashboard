package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerBooleanWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerProjectWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.resourcingextraction.ResourcingExtractionPlugin;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerResourcingExtractionPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Resourcing Extraction";
    }

    @Override
    public String getChartDescription() {
        return "Shows the open tracker items group by projects and assigned members";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/landscape.jpg";
    }

    private XBeamerWidget projectIdWidget, titleWidget, displayGroupsWidget, displayTeamWidget, displayReleasesWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();

        projectIdWidget = new XBeamerProjectWidget(context, true);
        projectIdWidget.setRequired(true);
        projectIdWidget.setLabel("Select Projects");
        this.addWidgetForParameter("projectIds", projectIdWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);

        displayGroupsWidget = new XBeamerBooleanWidget(context);
        displayGroupsWidget.setLabel("Display Groups");
        this.addWidgetForParameter("displayGroups", displayGroupsWidget);

        displayTeamWidget = new XBeamerBooleanWidget(context);
        displayTeamWidget.setLabel("Display Teams");
        this.addWidgetForParameter("displayTeams", displayTeamWidget);

        displayReleasesWidget = new XBeamerBooleanWidget(context);
        displayReleasesWidget.setLabel("Display Releases");
        this.addWidgetForParameter("displayReleases", displayReleasesWidget);
    }

    @Override
    protected Class<ResourcingExtractionPlugin> getOriginPlugin() {
        return ResourcingExtractionPlugin.class;
    }
}
