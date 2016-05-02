package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.wiki.plugins.mycurrentissues.MyCurrentIssuesPlugin;

/**
 * Created by Administrator on 2016-04-18.
 */
public class XBeamerMyCurrentIssuesPlugin extends XBeamerWrapperPlugin {
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

    private XBeamerWidget projectIdWidget, trackerIdWidget, titleWidget;

    @Override
    protected void initParameterWidgets() {
        WikiContext context=this.getWikiContext();

    }

    @Override
    protected Class<MyCurrentIssuesPlugin> getOriginPlugin() {
        return MyCurrentIssuesPlugin.class;
    }
}
