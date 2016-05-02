package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerTrackerWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;

/**
 * Created by comet on 2016-04-28.
 */
public class XBeamerWorkItemStatisticsTablePlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Work Item Statistics Table";
    }

    @Override
    public String getChartDescription() {
        return "Displays number of issue group by work item status for selected trackers";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/landscape.jpg";
    }

    private XBeamerWidget trackerIdWidget, fieldWidget, titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        trackerIdWidget = new XBeamerTrackerWidget(context, true);
        trackerIdWidget.setRequired(true);
        trackerIdWidget.setLabel("Select Trackers");
        this.addWidgetForParameter("trackerId", trackerIdWidget);

        fieldWidget = new XBeamerTextWidget(context);
        fieldWidget.setRequired(true);
        fieldWidget.setLabel("Field Name");
        this.addWidgetForParameter("field", fieldWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);
    }

    @Override
    protected Class<WorkItemStatisticsTablePlugin> getOriginPlugin() {
        return WorkItemStatisticsTablePlugin.class;
    }
}
