package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerChartPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.architectgroup.xbeamerchart.widget.foundation.*;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.plugin.PluginException;
import org.apache.velocity.VelocityContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerTestPlugin extends XBeamerChartPlugin {
    @Override
    public String getChartName() {
        return "Test";
    }

    @Override
    public String getChartDescription() {
        return null;
    }

    @Override
    public String getImgUrl() {
        return null;
    }

    @Override
    protected String getTemplateFilename() {
        return null;
    }

    private XBeamerWidget booleanWidget, colorWidget, numberWidget, projectWidget, selectWidget, textareaWidget, textWidget, trackerWidget, userWidget;

    public XBeamerTestPlugin(){
        this.setShapeColspan(5);
        this.setShapeRowspan(3);
    }

    @Override
    protected void initParameterWidgets() {
        WikiContext ctx = this.getWikiContext();

        try {

            booleanWidget = new XBeamerBooleanWidget(ctx);
            booleanWidget.setLabel("Boolean");
            booleanWidget.setRequired(true);
            this.addWidgetForParameter("boolean", booleanWidget);

            colorWidget = new XBeamerColorWidget(ctx);
            colorWidget.setLabel("Color");
            this.addWidgetForParameter("color", colorWidget);

            numberWidget = new XBeamerNumberWidget(ctx);
            numberWidget.setLabel("Number");
            this.addWidgetForParameter("number", numberWidget);

            projectWidget = new XBeamerProjectWidget(ctx, true);
            projectWidget.setLabel("Project");
            this.addWidgetForParameter("project", projectWidget);

            Map<String, String> options = new LinkedHashMap<>();
            options.put("test1", "Test 1");
            options.put("test2", "Test 2");
            options.put("test3", "Test 3");

            selectWidget = new XBeamerSelectWidget(ctx, options, true);
            selectWidget.setLabel("Select");
            this.addWidgetForParameter("select", selectWidget);

            textareaWidget = new XBeamerTextareaWidget(ctx, 30, 5);
            textareaWidget.setLabel("Textarea");
            this.addWidgetForParameter("textarea", textareaWidget);

            textWidget = new XBeamerTextWidget(ctx);
            textWidget.setLabel("Text");
            this.addWidgetForParameter("text", textWidget);

            trackerWidget = new XBeamerTrackerWidget(ctx, true);
            trackerWidget.setLabel("Tracker");
            this.addWidgetForParameter("tracker", trackerWidget);

            userWidget = new XBeamerUserWidget(ctx, true);
            userWidget.setLabel("User");
            this.addWidgetForParameter("user", userWidget);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {

    }
}
