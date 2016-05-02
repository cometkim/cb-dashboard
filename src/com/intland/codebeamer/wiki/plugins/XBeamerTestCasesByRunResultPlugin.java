package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerTrackerWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.persistence.dto.TrackerTypeDto;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;

import java.util.Collections;

/**
 * Created by comet on 2016-04-28.
 */
public class XBeamerTestCasesByRunResultPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Test cases by Test run results";
    }

    @Override
    public String getChartDescription() {
        return "Displays the number of completed test runs by result";
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
    protected Class<TestCasesByRunResultPlugin> getOriginPlugin() {
        return TestCasesByRunResultPlugin.class;
    }
}
