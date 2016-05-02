package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerSelectWidget;
import com.ecyrd.jspwiki.WikiContext;
import java.util.LinkedHashMap;
import java.util.Map;

public class XBeamerProjectListPlugin extends XBeamerWrapperPlugin {
    @Override
    public String getChartName() {
        return "Project List";
    }

    @Override
    public String getChartDescription() {
        return "Displays the complete list of projects accessible by the current user";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/landscape.jpg";
    }

    private XBeamerWidget namewWidget, formatWidget, categoryWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        Map<String,String> formats = new LinkedHashMap<>();
        formats.put("name","Visible Only Column");
        formats.put("brief","'Name','Member' and 'Created'");
        formats.put("full","Most Details");

        formatWidget = new XBeamerSelectWidget(context, formats, false);
        formatWidget.setRequired(true);
        formatWidget.setLabel("Select a display format");
        this.addWidgetForParameter("format", formatWidget);

//        namewWidget = new XBeamerTextWidget(context);
//        namewWidget.setDefaultArgument(".*");
//        namewWidget.setLabel("Name Regex (default: .*)");
//        this.addWidgetForParameter("name", namewWidget);
//
//        categoryWidget = new XBeamerTextWidget(context);
//        categoryWidget.setDefaultArgument(".*");
//        categoryWidget.setLabel("Category Regex (default: .*)");
//        this.addWidgetForParameter("category", categoryWidget);
    }

    @Override
    protected Class<ProjectListPlugin> getOriginPlugin() {
        return ProjectListPlugin.class;
    }
}