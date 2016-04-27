package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerSelectWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerTrackerWidget;
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
        return "";
    }

    @Override
    public String getImgUrl() {
        return "";
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
        displayWidget.setDefaultArgument("chart");
        displayWidget.setLabel("Display Type (default: Chart)");
        this.addWidgetForParameter("display", displayWidget);

        Map<String, String> supportedFields = new LinkedHashMap<>();
        displayTypes.put("assignedto", "Assignee");
        displayTypes.put("category", "Category");
        displayTypes.put("detected", "Detected");
        displayTypes.put("os", "OS");
        displayTypes.put("platform", "Platform");
        displayTypes.put("priority", "Priority");
        displayTypes.put("resolution", "Resolution");
        displayTypes.put("severity", "Severity");
        displayTypes.put("status", "Status");
        displayTypes.put("submitter", "Submitter");
        displayTypes.put("target", "Target");

        fieldWidget = new XBeamerSelectWidget(context, supportedFields, false);
        fieldWidget.setDefaultArgument("status");
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
