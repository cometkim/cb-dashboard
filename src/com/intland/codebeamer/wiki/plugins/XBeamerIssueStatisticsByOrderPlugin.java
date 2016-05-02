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
public class XBeamerIssueStatisticsByOrderPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Issues Statistics by Order";
    }

    @Override
    public String getChartDescription() {
        return "Display the number of issues by status, severity and one more field";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/landscape.jpg";
    }

    public XBeamerIssueStatisticsByOrderPlugin(){
        this.setShapeColspan(10);
        this.setShapeRowspan(3);
    }

    private XBeamerWidget trackerIdWidget, orderByWidget, filterWidget, titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        trackerIdWidget = new XBeamerTrackerWidget(context, false);
        trackerIdWidget.setLabel("Select a Tracker");
        trackerIdWidget.setRequired(true);
        this.addWidgetForParameter("trackerId", trackerIdWidget);

        orderByWidget = new XBeamerTextWidget(context);
        orderByWidget.setLabel("Order by Field");
        orderByWidget.setRequired(true);
        this.addWidgetForParameter("orderBy", orderByWidget);

        filterWidget = new XBeamerTextWidget(context);
        filterWidget.setLabel("Filter (field=value)");
        this.addWidgetForParameter("filter", filterWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);
    }

    @Override
    protected Class<IssueStatisticsByOrderPlugin> getOriginPlugin() {
        return IssueStatisticsByOrderPlugin.class;
    }
}
