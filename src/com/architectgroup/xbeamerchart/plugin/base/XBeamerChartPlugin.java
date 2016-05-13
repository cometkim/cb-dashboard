package com.architectgroup.xbeamerchart.plugin.base;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerPlugin;
import com.ecyrd.jspwiki.plugin.PluginException;
import org.apache.velocity.VelocityContext;

import java.util.Map;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-12.
 */
public abstract class XBeamerChartPlugin extends XBeamerPlugin {
    /**
     * If the plugin has been initialized, use this template instead widgets
     * @return      Path of template file(.vm), must placed under config/templates/wiki-plugin on host
     */
    protected abstract String getTemplateFilename();

    /**
     * @param   velocityContext
     * @param   params
     * @throws  PluginException
     *
     * @see #getTemplateFilename()
     */
    protected abstract void populateContext(VelocityContext velocityContext, Map params) throws PluginException;

    @Override
    protected final String execute() throws PluginException {
        Map params = this.getPluginParams();

        VelocityContext velocityContext = this.getDefaultVelocityContextFromContext(this.getWikiContext(), params);
        this.populateContextInfo(velocityContext);
        this.populateContext(velocityContext, params);

        String html = this.renderPluginTemplate(this.getTemplateFilename(), velocityContext);
        return html;
    }
}
