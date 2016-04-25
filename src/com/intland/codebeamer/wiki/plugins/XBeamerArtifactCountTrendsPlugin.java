package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerProjectSelectWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerArtifactCountTrendsPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Artifact Trends";
    }

    @Override
    public String getChartDescription() {
        return "Display the trends of Wiki/Documents.";
    }

    @Override
    public String getImgUrl() {
        return null;
    }

    private XBeamerWidget projectIdWidget;
    private XBeamerWidget titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();

        projectIdWidget = new XBeamerProjectSelectWidget(context, true);
        this.addWidgetForParameter("projectId", projectIdWidget);

        titleWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        this.addWidgetForParameter("title", titleWidget);
    }

    @Override
    protected Class<XBeamerArtifactCountTrendsPlugin> getOriginPlugin() {
        return XBeamerArtifactCountTrendsPlugin.class;
    }
}
