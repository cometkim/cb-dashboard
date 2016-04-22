package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerProjectSelectWidget;
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
        return "Show all activities";
    }

    @Override
    public String getImgUrl() {
        return "/cb/images/xbeamerchart/activities.png";
    }

    private XBeamerWidget projectIdWidget;
    private XBeamerWidget titleWidget;
    private XBeamerWidget maxWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        projectIdWidget = new XBeamerProjectSelectWidget(context, true);
        projectIdWidget.setRequired(true);
        projectIdWidget.setSummary("");
        projectIdWidget.setShortDescription("");
        this.addWidgetForParameter("projectId", projectIdWidget);

        titleWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        titleWidget.setDefaultArgument("Activity Stream");
        this.addWidgetForParameter("title", titleWidget);

        maxWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.NUMBER);
        maxWidget.setDefaultArgument("10");
        this.addWidgetForParameter("max", maxWidget);
    }

    @Override
    protected Class<ActivityStreamPlugin> getOriginPlugin() {
        return ActivityStreamPlugin.class;
    }
}
