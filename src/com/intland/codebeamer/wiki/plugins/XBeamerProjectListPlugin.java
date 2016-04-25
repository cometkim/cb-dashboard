package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerGeneralSelectWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerProjectSelectWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.ProjectListPlugin;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;
import java.util.LinkedHashMap;
import java.util.Map;

public class XBeamerProjectListPlugin extends XBeamerWrapperPlugin {
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
    private XBeamerWidget namewWidget;
    private XBeamerWidget formatWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        Map<String,String> formats=new LinkedHashMap<>();
        formats.put("name","visible only column");
        formats.put("brief","'name','member' and 'created'");
        formats.put("full","most details");


        projectIdWidget=new XBeamerProjectSelectWidget(context,true);
        projectIdWidget.setRequired(true);
        this.addWidgetForParameter("id",projectIdWidget);

        namewWidget=new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        this.addWidgetForParameter("name",namewWidget);

        formatWidget=new XBeamerGeneralSelectWidget(context,formats,false);
        this.addWidgetForParameter("format",formatWidget);
    }

    @Override
    protected Class<ProjectListPlugin> getOriginPlugin() {
        return ProjectListPlugin.class;
    }
}