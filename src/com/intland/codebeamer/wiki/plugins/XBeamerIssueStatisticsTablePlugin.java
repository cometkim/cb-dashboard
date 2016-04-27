package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerTrackerWidget;
import com.ecyrd.jspwiki.WikiContext;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerIssueStatisticsTablePlugin extends XBeamerWrapperPlugin {
    public XBeamerIssueStatisticsTablePlugin(){
        this.setShapeColspan(5);
        this.setShapeRowspan(3);
        this.setFrame(true);
    }

    @Override
    public String getChartName() {
        return "Issues Statistics Table";
    }

    @Override
    public String getChartDescription() {
        return "";
    }

    @Override
    public String getImgUrl() {
        return "";
    }

    private XBeamerWidget trackerIdWidget, colWidget, rowWidget, titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext ctx = this.getWikiContext();

        trackerIdWidget = new XBeamerTrackerWidget(ctx, false);
        trackerIdWidget.setRequired(true);
        trackerIdWidget.setSummary("Select a tracker");

        colWidget = new XBeamerTextWidget(ctx);
        colWidget.setRequired(true);
        colWidget.setSummary("Field name 1 (Column)");

        rowWidget = new XBeamerTextWidget(ctx);
        rowWidget.setRequired(true);
        rowWidget.setSummary("Field name 2 (Row)");

        titleWidget = new XBeamerTextWidget(ctx);
    }

    @Override
    protected Class<IssueStatisticsTablePlugin> getOriginPlugin() {
        return IssueStatisticsTablePlugin.class;
    }
}
