package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerProjectWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerSelectWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTextWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.recentactivities.ProjectActivityTrendsPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerProjectActivityTrendsPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Project Activity Trends";
    }

    @Override
    public String getChartDescription() {
        return "Shows the number of activities over time for selected projects";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/landscape.jpg";
    }

    private XBeamerWidget projectIdWidget, titleWidget, groupingWidget, displayWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();

        projectIdWidget = new XBeamerProjectWidget(context, true);
        projectIdWidget.setRequired(true);
        projectIdWidget.setLabel("Select Projects");
        this.addWidgetForParameter("projectId", projectIdWidget);

        Map<String, String> displayTypes = new LinkedHashMap<>();
        displayTypes.put("chart", "Chart");
        displayTypes.put("table", "Table");
        displayTypes.put("both", "Both");

        displayWidget = new XBeamerSelectWidget(context, displayTypes, false);
        displayWidget.setDefaultArgument("chart");
        displayWidget.setLabel("Display Type (default: Chart)");
        this.addWidgetForParameter("display", displayWidget);

        Map<String, String> groupingTypes = new LinkedHashMap<>();
        groupingTypes.put("daily", "Group by a Day");
        groupingTypes.put("weekly", "Group by a Week");
        groupingTypes.put("monthly", "Group by a Month");

        groupingWidget = new XBeamerSelectWidget(context, groupingTypes, false);
        groupingWidget.setDefaultArgument("daily");
        groupingWidget.setLabel("Grouping options (default: Daily)");
        this.addWidgetForParameter("grouping", groupingWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);
    }

    @Override
    protected Class<ProjectActivityTrendsPlugin> getOriginPlugin() {
        return ProjectActivityTrendsPlugin.class;
    }
}
