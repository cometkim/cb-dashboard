package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerSelectWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTrackerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.issuecounttrends.IssueCountTrendsPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerIssueCountTrendsPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Open vs Closed Trends";
    }

    @Override
    public String getChartDescription() {
        return "Displays the number of issues created vs the number of issues resolved for a period of a time";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/created-vs-closed.jpg";
    }

    public XBeamerIssueCountTrendsPlugin(){
    }

    private XBeamerWidget trackerIdWidget, titleWidget, displayWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();

        trackerIdWidget = new XBeamerTrackerWidget(context, true);
        trackerIdWidget.setRequired(true);
        trackerIdWidget.setLabel("Select Trackers");
        this.addWidgetForParameter("trackerId", trackerIdWidget);

        Map<String, String> options = new LinkedHashMap<>();
        options.put("chart", "Chart");
        options.put("table", "Table");
        options.put("both", "Both");

        displayWidget = new XBeamerSelectWidget(context, options, false);
        displayWidget.setDefaultArgument("chart");
        displayWidget.setLabel("Display Type (default: Chart)");
        this.addWidgetForParameter("display", displayWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);
    }

    @Override
    protected Class<IssueCountTrendsPlugin> getOriginPlugin() {
        return IssueCountTrendsPlugin.class;
    }
}
