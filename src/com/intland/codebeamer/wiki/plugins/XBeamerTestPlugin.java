package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.XBeamerChartPlugin;
import com.architectgroup.xbeamerchart.widget.XBeamerColorPickerWidget;
import com.architectgroup.xbeamerchart.widget.XBeamerQueryConditionWidget;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.Config;
import com.intland.codebeamer.persistence.dao.PaginatedDtoList;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.ProjectDto;
import com.intland.codebeamer.persistence.dto.TrackerDto;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.intland.codebeamer.search.query.antlr.CbQLQueryHandler;
import com.intland.codebeamer.search.query.antlr.QueryContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by comet on 2016-03-16.
 */
public class XBeamerTestPlugin extends XBeamerChartPlugin {
    @Autowired
    private TrackerItemDao trackerItemDao;

    @Override
    public String getChartName() {
        return "XBeamerTest";
    }

    @Override
    public String getChartDescription() { return "A plugin for test"; }

    @Override
    public String getImgUrl() {
        return "/cb/images/xbeamerchart/test.png";
    }

    @Override
    protected String getTemplateFilename() {
        return "xbeamerchart/XBeamerTest-plugin.vm";
    }

    @Override
    protected void populateChartContext(VelocityContext velocityContext, Map params) {

    }

    @Override
    protected boolean initializeWidgets() {
        return false;
    }


}
