package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerGoogleMapsPlugin extends XBeamerWrapperPlugin {
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

    private XBeamerWidget latWidget;
    private XBeamerWidget lngWidget;
    private XBeamerWidget zoomWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();

        latWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        this.addWidgetForParameter("lat", latWidget);

        lngWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        this.addWidgetForParameter("lng", lngWidget);

        zoomWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.NUMBER);
        this.addWidgetForParameter("zoom", zoomWidget);

    }

    @Override
    protected Class<XBeamerGoogleMapsPlugin> getOriginPlugin() {
        return XBeamerGoogleMapsPlugin.class;
    }
}
