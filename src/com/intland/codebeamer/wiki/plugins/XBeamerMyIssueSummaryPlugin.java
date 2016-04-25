package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerGeneralSelectWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;
import org.apache.ecs.storage.Hash;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerMyIssueSummaryPlugin extends XBeamerWrapperPlugin {
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


    private XBeamerWidget showWidget;
    private XBeamerWidget titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();
        Map<String,String> shows = new LinkedHashMap<>();
        shows.put("Open","Open");
        shows.put("Closed","Closed");
        shows.put("Resolved","Resolved");
        shows.put("Successful","Successful");
        shows.put("Unresolved","Unresolved");
        shows.put("Unsuccessful","Unsuccessful");

        showWidget=new XBeamerGeneralSelectWidget(context,shows,false);
        this.addWidgetForParameter("show",showWidget);

        titleWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        this.addWidgetForParameter("title", titleWidget);

    }

    @Override
    protected Class<XBeamerMyIssueSummaryPlugin> getOriginPlugin() {
        return XBeamerMyIssueSummaryPlugin.class;
    }
}
