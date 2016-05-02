package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerProjectWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerTextWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.recentactivities.ActivityStreamPlugin;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-12.
 */
public class XBeamerActivityStreamPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Activity Stream";
    }

    @Override
    public String getChartDescription() {
        return "Shows all activities";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/activities.jpg";
    }

    public XBeamerActivityStreamPlugin(){
        this.setShapeColspan(5);
        this.setShapeRowspan(3);
    }

    private XBeamerWidget projectIdWidget, titleWidget, maxWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        projectIdWidget = new XBeamerProjectWidget(context, true);
        projectIdWidget.setLabel("select projects or all projects");
        projectIdWidget.setShortDescription("");
        this.addWidgetForParameter("projectId", projectIdWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setDefaultArgument("Activity Stream");
        this.addWidgetForParameter("title", titleWidget);

        maxWidget = new XBeamerTextWidget(context);
        maxWidget.setDefaultArgument("10");
        this.addWidgetForParameter("max", maxWidget);
    }

    @Override
    protected Class<ActivityStreamPlugin> getOriginPlugin() {
        return ActivityStreamPlugin.class;
    }
}
