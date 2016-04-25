package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerMyOpenTrackerItemsPlugin extends XBeamerWrapperPlugin {
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

    private XBeamerWidget titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        titleWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        this.addWidgetForParameter("title", titleWidget);
    }
    @Override
    protected Class<XBeamerMyOpenTrackerItemsPlugin> getOriginPlugin() {
        return XBeamerMyOpenTrackerItemsPlugin.class;
    }
}
