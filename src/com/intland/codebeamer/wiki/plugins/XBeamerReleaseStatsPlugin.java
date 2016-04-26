package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerWrapperPlugin;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerReleaseStatsPlugin extends XBeamerWrapperPlugin {
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

    @Override
    protected void initParameterWidgets() {

    }

    @Override
    protected Class<ReleaseStatsPlugin> getOriginPlugin() {
        return ReleaseStatsPlugin.class;
    }
}
