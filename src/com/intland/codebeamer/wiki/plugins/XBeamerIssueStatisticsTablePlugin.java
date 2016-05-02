package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTrackerWidget;
import com.ecyrd.jspwiki.WikiContext;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerIssueStatisticsTablePlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Issues Statistics Table";
    }

    @Override
    public String getChartDescription() {
        return "Displays the number of issues by two specified field name";
    }

    @Override
    public String getImgUrl() { return "/cb/xbeamerchart/images/landscape.jpg"; }

    public XBeamerIssueStatisticsTablePlugin(){
        this.setShapeColspan(5);
        this.setShapeRowspan(3);
    }

    private XBeamerWidget trackerIdWidget, colWidget, rowWidget, titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext ctx = this.getWikiContext();

        trackerIdWidget = new XBeamerTrackerWidget(ctx, false);
        trackerIdWidget.setRequired(true);
        trackerIdWidget.setLabel("Select a tracker");
        this.addWidgetForParameter("trackerId", trackerIdWidget);

        titleWidget = new XBeamerTextWidget(ctx);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);

        colWidget = new XBeamerTextWidget(ctx);
        colWidget.setRequired(true);
        colWidget.setLabel("Field name 1 (Column)");
        this.addWidgetForParameter("col", colWidget);

        rowWidget = new XBeamerTextWidget(ctx);
        rowWidget.setRequired(true);
        rowWidget.setLabel("Field name 2 (Row)");
        this.addWidgetForParameter("row", rowWidget);
    }

    @Override
    protected Class<IssueStatisticsTablePlugin> getOriginPlugin() {
        return IssueStatisticsTablePlugin.class;
    }
}
