package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.controller.XBeamerChartController;
import com.architectgroup.xbeamerchart.plugin.base.XBeamerPlugin;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.Config;
import com.intland.codebeamer.license.LicenseCode;
import com.intland.codebeamer.license.LicenseCodeImpl;
import com.intland.codebeamer.persistence.dto.WikiPageDto;
import com.intland.codebeamer.taglib.UrlVersioned;
import com.intland.codebeamer.wiki.CodeBeamerWikiContext;
import com.intland.codebeamer.wiki.WikiMarkupProcessor;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-11.
 */
public class XBeamerChartSupportPlugin extends AutoWiringCodeBeamerPlugin{
    @Autowired
    private WikiMarkupProcessor wikiMarkupProcessor;

    public static final String VERSION = "1.0.0";
    public static final String VERSION_STAT = "beta";

    public static final String TARGET_CB_VERSION = "7.9.1";

    public static final String getCBVersion(){
        LicenseCode licenseCode = LicenseCodeImpl.getInstance();
        return Config.getVersion(licenseCode);
    }

    public static final List<XBeamerPlugin> getSupportedPlugins(){
        List<XBeamerPlugin> plugins = new ArrayList<>();
        // TODO : Add implemented plugins here.
        plugins.add(new XBeamerActivityStreamPlugin());
        plugins.add(new XBeamerIssueCountByFiledPlugin());
        plugins.add(new XBeamerIssueCountTrendsPlugin());
        plugins.add(new XBeamerIssueStatisticPlugin());
        plugins.add(new XBeamerIssueStatisticsByOrderPlugin());
        plugins.add(new XBeamerIssueStatisticsTablePlugin());
        plugins.add(new XBeamerMembersPlugin());
        plugins.add(new XBeamerMostActiveProjectPlugin());
        plugins.add(new XBeamerProjectActivityTrendsPlugin());
        plugins.add(new XBeamerProjectInfoPlugin());
        plugins.add(new XBeamerProjectListPlugin());
        plugins.add(new XBeamerQueryTilePlugin());
        plugins.add(new XBeamerResourcingExtractionPlugin());
        plugins.add(new XBeamerTestCasesByRunResultPlugin());
        plugins.add(new XBeamerTestExecutionTrendsPlugin());
        plugins.add(new XBeamerWorkItemStatisticsTablePlugin());

        return plugins;
    }

    @Override
    protected String getTemplateFilename() {
        return "XBeamerChartSupport-plugin.vm";
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        WikiPageDto page = this.getPage();
        String contextPath = this.getContextPath();

        velocityContext.put("contextPath", contextPath);
        velocityContext.put("versionedPath", UrlVersioned.buildUrl(""));

        try {
            velocityContext.put("pageId", page.getId());
        }catch (NullPointerException e){
            throw new PluginException("Please try again after page saved");
        }

        velocityContext.put("plugins", this.getSupportedPlugins());
        velocityContext.put("createUrl", contextPath + XBeamerChartController.AJAX_CHART_CREATE_URL);
        velocityContext.put("reorderUrl", contextPath + XBeamerChartController.AJAX_CHART_REORDER_URL);

        velocityContext.put("width", 420);

        List<String> pluginContents = new ArrayList<>();
        String body = StringUtils.trimToEmpty((String) params.get("_body"));

        WikiContext context = this.getWikiContext();
        CodeBeamerWikiContext cbContext = (CodeBeamerWikiContext) context;
        cbContext.setWrapWikiRenderToMarkerClass(false);

        for (String line : body.split("\n")) {
            pluginContents.add(wikiMarkupProcessor.transformToHtml(line, "W", false, false, cbContext));
        }
        velocityContext.put("pluginContents", pluginContents);
    }
}
