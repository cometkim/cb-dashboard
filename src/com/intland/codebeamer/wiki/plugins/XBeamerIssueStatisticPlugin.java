package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTrackerWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;

/**
 * Created by comet on 2016-04-28.
 */
public class XBeamerIssueStatisticPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Issue Statistic with Bar";
    }

    @Override
    public String getChartDescription() {
        return "Show number of issues and percentage by field name";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/landscape.jpg";
    }

    private XBeamerWidget trackerWidget, titleWidget, includeWidget, excludeWidget, fieldNameWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        trackerWidget = new XBeamerTrackerWidget(context, true);
        trackerWidget.setRequired(true);
        trackerWidget.setLabel("Select Trackers");
        this.addWidgetForParameter("tracker", trackerWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setLabel("Title");
        titleWidget.setDefaultValue("Issue Statistic");
        this.addWidgetForParameter("title", titleWidget);

        fieldNameWidget = new XBeamerTextWidget(context);
        fieldNameWidget.setRequired(true);
        fieldNameWidget.setLabel("Field Name");
        this.addWidgetForParameter("field", fieldNameWidget);

        includeWidget = new XBeamerTextWidget(context);
        includeWidget.setLabel("Includes");
        includeWidget.setDefaultValue("");
        this.addWidgetForParameter("include", includeWidget);

        excludeWidget = new XBeamerTextWidget(context);
        excludeWidget.setLabel("Excludes");
        excludeWidget.setDefaultValue("");
        this.addWidgetForParameter("exclude", excludeWidget);

    }

    @Override
    protected Class<IssueStatisticPlugin> getOriginPlugin() {
        return IssueStatisticPlugin.class;
    }
}
