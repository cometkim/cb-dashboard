package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTrackerWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.persistence.dto.TrackerTypeDto;

import java.util.Collections;

/**
 * Created by comet on 2016-04-28.
 */
public class XBeamerTestExecutionTrendsPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Test Execution Trends";
    }

    @Override
    public String getChartDescription() {
        return "Displays the number of test run by test run result over a day";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/landscape.jpg";
    }

    private XBeamerWidget trackerIdWidget, titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        trackerIdWidget = new XBeamerTrackerWidget(context, false, Collections.singletonList(TrackerTypeDto.TESTRUN));
        trackerIdWidget.setRequired(true);
        trackerIdWidget.setLabel("Select a Tracker");
        this.addWidgetForParameter("trackerId", trackerIdWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);
    }

    @Override
    protected Class<TestExecutionTrendsPlugin> getOriginPlugin() {
        return TestExecutionTrendsPlugin.class;
    }
}
