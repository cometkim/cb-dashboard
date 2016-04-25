package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerGeneralSelectWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerTrackerItemStateHistoryPlugin extends XBeamerWrapperPlugin {
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


    private XBeamerWidget idWidget;
    private XBeamerWidget showHeaderWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();
        Map<String,String> showHeader=new LinkedHashMap<>();
        showHeader.put("true","True");
        showHeader.put("false","False");

        idWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.NUMBER);
        idWidget.setRequired(true);
        this.addWidgetForParameter("id", idWidget);

        showHeaderWidget=new XBeamerGeneralSelectWidget(context,showHeader,false);
        this.addWidgetForParameter("showHeader",showHeaderWidget);

    }

    @Override
    protected Class<XBeamerTrackerItemStateHistoryPlugin> getOriginPlugin() {
        return XBeamerTrackerItemStateHistoryPlugin.class
                ;
    }
}
