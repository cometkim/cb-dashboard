package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerSelectWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTrackerWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by comet on 2016-04-27.
 */
public class XBeamerIssueCountByFiledPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Issue Count by Field";
    }

    @Override
    public String getChartDescription() {
        return "Displays the issue distribution in the project by field";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/barchart.jpg";
    }

    public XBeamerIssueCountByFiledPlugin(){
        this.setShapeColspan(5);
        this.setShapeRowspan(3);
    }

    private XBeamerWidget trackerIdWidget, titleWidget, fieldWidget, displayWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        trackerIdWidget = new XBeamerTrackerWidget(context, true);
        trackerIdWidget.setRequired(true);
        trackerIdWidget.setLabel("Select Trackers");
        this.addWidgetForParameter("trackerId", trackerIdWidget);

        Map<String, String> displayTypes = new LinkedHashMap<>();
        displayTypes.put("chart", "Chart");
        displayTypes.put("table", "Table");
        displayTypes.put("both", "Both");

        displayWidget = new XBeamerSelectWidget(context, displayTypes, false);
        displayWidget.setDefaultValue("chart");
        displayWidget.setLabel("Display Type (default: Chart)");
        this.addWidgetForParameter("display", displayWidget);

        Map<String, String> supportedFields = new LinkedHashMap<>();
        supportedFields.put("assignedto", "Assignee");
        supportedFields.put("category", "Category");
        supportedFields.put("detected", "Detected");
        supportedFields.put("os", "OS");
        supportedFields.put("platform", "Platform");
        supportedFields.put("priority", "Priority");
        supportedFields.put("resolution", "Resolution");
        supportedFields.put("severity", "Severity");
        supportedFields.put("status", "Status");
        supportedFields.put("submitter", "Submitter");
        supportedFields.put("target", "Target");

        fieldWidget = new XBeamerSelectWidget(context, supportedFields, false);
        fieldWidget.setDefaultValue("status");
        fieldWidget.setLabel("Field");
        this.addWidgetForParameter("field", fieldWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);
    }

    @Override
    protected Class<IssueCountByFieldPlugin> getOriginPlugin() {
        return IssueCountByFieldPlugin.class;
    }
}
