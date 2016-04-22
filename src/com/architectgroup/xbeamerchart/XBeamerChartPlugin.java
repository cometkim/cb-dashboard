package com.architectgroup.xbeamerchart;

import com.architectgroup.xbeamerchart.controller.XBeamerChartController;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.taglib.UrlVersioned;
import com.intland.codebeamer.wiki.CodeBeamerWikiContext;
import com.intland.codebeamer.wiki.WikiMarkupProcessor;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-15.
 */
@Deprecated
public abstract class XBeamerChartPlugin extends AutoWiringCodeBeamerPlugin{
    /**
     * @return Exact plugin name for using in markup
     */
    public final String getPluginName(){
        return getClass().getSimpleName().replace("Plugin", "");
    }

    /**
     * @return Name to display to end-user
     */
    public abstract String getChartName();

    /**
     * @return Description to display to end-user
     */
    public abstract String getChartDescription();

    /**
     * @return URI of preview image to display to end-user
     */
    public abstract String getImgUrl();

    /**
     * If the plugin has been initialized, use this template instead widgets
     * @return      Path of template file(.vm), must placed under config/templates/wiki-plugin on host
     * @see         #getTemplateFilename()
     */
    public abstract String getChartTemplateFilePath();

    /**
     * @param   velocityContext
     * @param   params
     * @throws  PluginException
     *
     * @see #getTemplateFilename()
     */
    protected abstract void populateChartContext(VelocityContext velocityContext, Map params) throws PluginException ;

    protected VelocityContext getVelocityContext(){ return this.velocityContext; }

    /**
     * You can use widgets to input parameters
     * and each widgets must be initialized before execution.
     *
     * @see com.architectgroup.xbeamerchart.widget
     * @see #addWidgetForParameter(String, XBeamerWidget)
     * @see #addWidgetForParameter(String, String, XBeamerWidget)
     */
    protected abstract void initParameterWidgets();

    /**
     * Add widget for required parameter
     * @param param
     * @param widget
     *
     * @see com.architectgroup.xbeamerchart.widget
     */
    protected void addWidgetForParameter(String param, XBeamerWidget widget){
        this.widgets.put(param, widget);
    }

    /**
     * Add widget for parameter with default value
     * @param param
     * @param defaultValue
     * @param widget
     *
     * @see com.architectgroup.xbeamerchart.widget
     */
    protected void addWidgetForParameter(String param, @NotNull String defaultValue, XBeamerWidget widget){
        this.widgets.put(param, widget);

        if(defaultValue != null)
            this.defaultValues.put(param, defaultValue);
    }

    /**
     * @param param         Name of parameter
     * @param <T>           Type of target subject
     * @return              Target Subject of parameter
     */
    protected @Nullable <T extends Object> T getParameterSubject(@NotNull String param){
        XBeamerWidget widget = widgets.get(param);

        String arg = this.getParameterValue(param);
        if(arg == null) return null;

        return widget.getSubject(arg);
    }

    /**
     * @param param     Name of parameter
     * @return          Value of parameter
     */
    protected @Nullable String getParameterValue(@NotNull String param){
        Map<String, String> params = this.getPluginParams();

        String paramValue = null;

        if(params.containsKey(param) && !params.get(param).isEmpty()){
            paramValue = params.get(param);

        }else if(defaultValues.containsKey(param)){
            paramValue = defaultValues.get(param);
        }

        return paramValue;
    }

    /**
     * Check it is initialized.
     * The empty string can't be used for required parameter, but default value be able.
     * @return         boolean
     *
     * @see #addWidgetForParameter(String, XBeamerWidget)
     * @see #addWidgetForParameter(String, String, XBeamerWidget)
     */
    protected boolean checkRequireParams(){
        Map<String, String> params = this.getPluginParams();

        for(String param : widgets.keySet()){
            if(( !params.containsKey(param) || params.get(param).isEmpty() ) && !defaultValues.containsKey(param))
                return false;
        }

        return true;
    }

    /**
     * If all parameter is initialized
     * @return      chart template
     * or
     * @return      default template to display widgets.
     *
     * @see #checkRequireParams()
     */
    @Override
    protected final String getTemplateFilename(){
        if(this.checkRequireParams())
            return this.getChartTemplateFilePath();

        else return "xbeamerchart/includes/widget-box.vm";
    }

    /**
     * @param markup
     * @return transformed HTML
     */
    protected String renderMarkup(String markup){
        return wikiMarkupProcessor.transformToHtml(markup, "W", false, false, (CodeBeamerWikiContext)this.getWikiContext());
    }

    @Override
    protected final void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        this.velocityContext = velocityContext;

        String contextPath = this.getContextPath();

        String chartName = this.getPluginName();
        String chartId = params.containsKey("id") ? (String) params.get("id") : RandomStringUtils.randomAlphanumeric(10);

        this.widgets = new LinkedHashMap<>();
        this.defaultValues = new HashMap<>();
        this.initParameterWidgets();

        boolean initialized = this.checkRequireParams();

        if (initialized) {
            populateChartContext(velocityContext, params);

        } else {
            Map<String, String> widgetContents = new LinkedHashMap<>();

            for (String param : this.widgets.keySet()) {
                XBeamerWidget widget = this.widgets.get(param);

                VelocityContext widgetContext = new VelocityContext();
                widgetContext.put("param", param);
                widgetContext.put("chartName", chartName);
                widgetContext.put("chartId", chartId);

                widget.populateContext(widgetContext);

                widgetContents.put(param, renderPluginTemplate(widget.getFileName(), widgetContext));
            }

            velocityContext.put("widgets", widgetContents);
        }

        velocityContext.put("initialized", initialized);

        velocityContext.put("pageId", this.getPage().getId());

        velocityContext.put("contextPath", contextPath);
        velocityContext.put("versionedPath", UrlVersioned.buildUrl(""));

        velocityContext.put("initUrl", contextPath + XBeamerChartController.AJAX_CHART_INIT_URL);
        velocityContext.put("resetUrl", contextPath + XBeamerChartController.AJAX_CHART_RESET_URL);
        velocityContext.put("removeUrl", contextPath + XBeamerChartController.AJAX_CHART_REMOVE_URL);

        velocityContext.put("chartName", chartName);
        velocityContext.put("chartId", chartId);
    }

    private Map<String, XBeamerWidget> widgets;

    private Map<String, String> defaultValues;

    private VelocityContext velocityContext;

    @Autowired
    private WikiMarkupProcessor wikiMarkupProcessor;
}
