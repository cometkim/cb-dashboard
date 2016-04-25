package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerProjectSelectWidget;
import com.ecyrd.jspwiki.WikiContext;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerMostActiveProjectPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return null;
    }

    @Override
    public String getChartDescription() {
        return null;
    }

    @Override
    public String getImgUrl() {
        return null;
    }


    private XBeamerWidget projectIdWidget;
    private XBeamerWidget maxWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();

        projectIdWidget = new XBeamerProjectSelectWidget(context, true);
        this.addWidgetForParameter("projectId", projectIdWidget);

        maxWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.NUMBER);
        maxWidget.setDefaultArgument("10");
        this.addWidgetForParameter("max", maxWidget);

    }

    @Override
    protected Class<XBeamerMostActiveProjectPlugin> getOriginPlugin() {
        return XBeamerMostActiveProjectPlugin.class;
    }
}
