package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerChartPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerColorWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerQueryConditionWidget;
import com.architectgroup.xbeamerchart.widget.foundation.XBeamerTextWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import org.apache.velocity.VelocityContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-25.
 */
public class XBeamerQueryTilePlugin extends XBeamerChartPlugin {
    @Override
    public String getChartName() {
        return "Query Tile";
    }

    @Override
    public String getChartDescription() {
        return "Display a number of result for query.";
    }

    @Override
    public String getImgUrl() {
        return "/cb/xbeamerchart/images/querytile.jpg";
    }

    @Override
    public String getTemplateFilename() {
        return "xbeamerchart/QueryTile-plugin.vm";
    }

    public XBeamerQueryTilePlugin(){
        this.setFrame(false);
    }

    private XBeamerWidget cbQLWidget, colorWidget, titleWidget, suffixWidget;

    @Override
    protected void initParameterWidgets() {
        this.setShapeColspan(5);
        this.setShapeRowspan(3);

        WikiContext context = this.getWikiContext();

        cbQLWidget = new XBeamerQueryConditionWidget(context);
        cbQLWidget.setRequired(true);
        cbQLWidget.setDefaultValue("project.id != 0");
        this.addWidgetForParameter("cbQL", cbQLWidget);

        colorWidget = new XBeamerColorWidget(context);
        colorWidget.setLabel("Color");
        colorWidget.setRequired(true);
        this.addWidgetForParameter("color", colorWidget);

        titleWidget = new XBeamerTextWidget(context);
        titleWidget.setRequired(true);
        titleWidget.setLabel("Title");
        this.addWidgetForParameter("title", titleWidget);

        suffixWidget = new XBeamerTextWidget(context);
        suffixWidget.setLabel("Suffix");
        this.addWidgetForParameter("suffix", suffixWidget);
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        this.setShapeColspan(1);
        this.setShapeRowspan(1);

        String cbQl = cbQLWidget.getValue().replace("^", "'");

        List<TrackerItemDto> items;
        try {
            items = (List<TrackerItemDto>)cbQLWidget.getObject();
        }catch (Exception e){
            e.printStackTrace();
            items = Collections.emptyList();
        }

        String title = titleWidget.getValue();
        String suffix = suffixWidget.getValue();
        String color = colorWidget.getValue();

        Integer count = items.size();

        String countText;
        Integer fontSize = 58;

        if(count < 1000){ // 0 ~ 999
            countText = String.valueOf(count);
        }else if(count < 10000){ // 1000 ~ 9999
            countText = String.valueOf(count);
            fontSize = 52;
        }else if(count < 100000){ // 10K ~ 99K
            countText = String.format("%dK", count/1000);
        }else if(count < 1000000){ // 100K ~ 999K
            countText = String.format("%dK", count/1000);
            fontSize = 52;
        }else{ // 1M ~
            countText = String.format("%dM", count/1000000);
        }

        velocityContext.put("count", count);
        velocityContext.put("countText", countText);
        velocityContext.put("fontSize", fontSize);

        velocityContext.put("queryUrl", this.getContextPath() + "/query?cbQl=" + cbQl);
        velocityContext.put("title", title);
        velocityContext.put("suffix", suffix);
        velocityContext.put("color", color);
    }
}
