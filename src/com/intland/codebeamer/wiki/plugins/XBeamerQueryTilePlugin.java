package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerChartPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerBasicInputWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerColorPickerWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerInputTextWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerQueryConditionWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import com.intland.codebeamer.search.query.antlr.CbQLQueryHandler;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-25.
 */
public class XBeamerQueryTilePlugin extends XBeamerChartPlugin {
    @Autowired
    @Qualifier("TRACKER_ITEM")
    private CbQLQueryHandler<TrackerItemDto> cbQLQueryHandler;

    @Autowired
    private TrackerItemDao trackerItemDao;

    @Override
    public String getChartName() {
        return "Query Tile";
    }

    @Override
    public String getChartDescription() {
        return "Display a big number that is result of the query.";
    }

    @Override
    public String getImgUrl() {
        return "/cb/images/xbeamerchart/querytile.png";
    }

    @Override
    public String getTemplateFilename() {
        return "xbeamerchart/QueryTile-plugin.vm";
    }

    public XBeamerQueryTilePlugin(){
        this.setFrame(false);
    }

    private XBeamerWidget cbQLWidget;
    private XBeamerWidget colorWidget;
    private XBeamerWidget titleWidget;
    private XBeamerWidget suffixWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context = this.getWikiContext();

        cbQLWidget = new XBeamerQueryConditionWidget(context);
        cbQLWidget.setRequired(true);
        cbQLWidget.setDefaultArgument("project.id != 0");
        this.addWidgetForParameter("cbQL", cbQLWidget);

        colorWidget = new XBeamerColorPickerWidget(context);
        colorWidget.setSummary("Color");
        colorWidget.setShortDescription("Select the background color");
        colorWidget.setDefaultArgument("#0093b8");
        this.addWidgetForParameter("color", colorWidget);

        titleWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        titleWidget.setRequired(true);
        titleWidget.setSummary("Title");
        this.addWidgetForParameter("title", titleWidget);

        suffixWidget = new XBeamerBasicInputWidget(context, XBeamerBasicInputWidget.Type.TEXT);
        suffixWidget.setSummary("Suffix");
        this.addWidgetForParameter("suffix", suffixWidget);
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        this.setShapeColspan(1);
        this.setShapeRowspan(1);

        String cbQl = cbQLWidget.getValue();

        List<TrackerItemDto> items;
        try {
            items = cbQLWidget.getSubject();
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

        if(count < 1000){
            countText = String.valueOf(count);
        }else if(count < 10000){
            countText = String.format("%.1fK", (float)count/1000);
        }else if(count < 100000){
            countText = String.format("%dK", count/1000);
        }else if(count < 1000000){
            countText = String.format("%dK", count/1000);
            fontSize = 52;
        }else{
            countText = String.format("%dM", count/1000000);
        }

        velocityContext.put("count", count);
        velocityContext.put("countText", countText);
        velocityContext.put("fontSize", fontSize);

        velocityContext.put("queryUrl", this.getContextPath() + "/proj/queries.spr?cbQl=" + cbQl);
        velocityContext.put("title", title);
        velocityContext.put("suffix", suffix);
        velocityContext.put("color", color);
    }
}
