package com.architectgroup.xbeamerchart.plugin.base;

import com.architectgroup.xbeamerchart.controller.XBeamerChartController;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.controller.ControllerUtils;
import com.intland.codebeamer.controller.support.SimpleMessageResolver;
import com.intland.codebeamer.persistence.dto.ProjectDto;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.intland.codebeamer.persistence.dto.WikiPageDto;
import com.intland.codebeamer.taglib.UrlVersioned;
import com.intland.codebeamer.wiki.CodeBeamerWikiContext;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-12.
 */
public abstract class XBeamerPlugin extends AbstractCodeBeamerWikiPlugin {
    public static final String FRAME_TEMPLATE_FILENAME = "xbeamerchart/includes/frame.vm";

    private boolean autowire = true;

    private WikiContext wikiContext;

    protected Map pluginParams;

    protected Map<String, XBeamerWidget> widgets;

    private String pluginName;

    private String chartId;

    private boolean useFrame;

    private Integer shapeColspan;
    private Integer shapeRowspan;

    public XBeamerPlugin(){
        this.pluginName = this.getClass().getSimpleName();
        this.widgets = new LinkedHashMap<>();
        this.useFrame = true;
        this.shapeRowspan = 1;
        this.shapeColspan = 1;
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
     * @return Content HTML
     * @throws PluginException
     */
    protected abstract String execute() throws PluginException;

    public final String getPluginName(){ return this.pluginName; }

    public final WikiContext getWikiContext() { return this.wikiContext; }

    public final Map getPluginParams() { return this.pluginParams; }

    public final UserDto getUser() { return super.getUserFromContext(this.wikiContext); }

    public final WikiPageDto getPage() { return super.getPageFromContext(this.wikiContext); }

    public final ProjectDto getProject() { return ((CodeBeamerWikiContext)this.getWikiContext()).getProject(); }

    public final ApplicationContext getApplicationContext() { return super.getApplicationContext(this.wikiContext); }

    public final Integer getShapeColspan(){ return this.shapeColspan; }

    public final void setShapeColspan(Integer colspan){ this.shapeColspan = colspan; }

    public final Integer getShapeRowspan(){ return this.shapeRowspan; }

    public final void setShapeRowspan(Integer rowspan){ this.shapeRowspan = rowspan; }

    public final boolean useFrame(){ return this.useFrame; }

    public final void setFrame(boolean enable){ this.useFrame = enable; }

    public final boolean isAutowire() { return this.autowire; }

    protected final @Nullable String getContextPath() {
        WikiContext wikiContext = this.wikiContext;
        HttpServletRequest httpRequest = wikiContext != null ? wikiContext.getHttpRequest() : null;
        String contextPath = httpRequest != null ? httpRequest.getContextPath() : null;
        return contextPath;
    }

    protected final SimpleMessageResolver getSimpleMessageResolver() {
        HttpServletRequest request = this.getWikiContext().getHttpRequest();
        return SimpleMessageResolver.getInstance(request);
    }

    protected final String getChartId(){ return this.chartId; }

    /**
     * You can use widgets to input parameters for end-users
     * and each widgets must be initialized before execution.
     *
     * @see com.architectgroup.xbeamerchart.widget
     */
    protected abstract void initParameterWidgets();

    /**
     * Add widget for parameter
     * @param param
     * @param widget
     *
     * @see com.architectgroup.xbeamerchart.widget
     */
    protected void addWidgetForParameter(String param, XBeamerWidget widget){
        this.widgets.put(param, widget);
    }

    /**
     * @param wikiContext
     * @param params
     * @return HTML
     * @throws PluginException
     */
    @Override
    public final String execute(WikiContext wikiContext, Map params) throws PluginException{
        this.wikiContext = wikiContext;
        this.pluginParams = params;
        this.chartId = params.containsKey("id") ? (String) params.get("id") : RandomStringUtils.randomAlphanumeric(10);
        this.autowire();

        VelocityContext velocityContext = this.getDefaultVelocityContextFromContext(this.wikiContext);

        this.initParameterWidgets();
        boolean initialized = this.checkRequireParams();
        velocityContext.put("initialized", initialized);

        if(initialized){
            this.copyParametersToWidget();
            String content = this.execute();
            velocityContext.put("content", content);

        }else{
            this.populateWidgets(velocityContext);
        }

        this.populateContextInfo(velocityContext);
        this.populateChartInfo(velocityContext);

        return this.renderPluginTemplate(FRAME_TEMPLATE_FILENAME, velocityContext);
    }

    /**
     * populate all param-widget pairs.
     * @param velocityContext
     */
    private final void populateWidgets(VelocityContext velocityContext){
        Map<String, String> widgetContents = new LinkedHashMap<>();

        for (String param : this.widgets.keySet()) {
            XBeamerWidget widget = this.widgets.get(param);

            VelocityContext widgetContext = new VelocityContext();
            widgetContext.put("param", param);
            widgetContext.put("this", widget);
            widget.populateContext(widgetContext);

            try{
                this.populateContextInfo(widgetContext);
            } catch (PluginException e){}

            this.populateChartInfo(widgetContext);

            widgetContents.put(param, this.renderPluginTemplate(widget.getFileName(), widgetContext));
        }

        velocityContext.put("widgets", widgetContents);
    }

    /**
     * @param velocityContext
     * Populate basic context
     * - ID of page
     * - Base URL
     * - Ajax URL
     *
     * @throws PluginException if page has not been saved ever.
     */
    protected final void populateContextInfo(VelocityContext velocityContext) throws PluginException{
        try {
            velocityContext.put("pageId", this.getPage().getId());
        }catch (NullPointerException e){
            throw new PluginException("Please try again after initial saving");
        }

        String contextPath = this.getContextPath();

        velocityContext.put("contextPath", contextPath);
        velocityContext.put("versionedPath", UrlVersioned.buildUrl(""));

        velocityContext.put("initUrl", contextPath + XBeamerChartController.AJAX_CHART_INIT_URL);
        velocityContext.put("resetUrl", contextPath + XBeamerChartController.AJAX_CHART_RESET_URL);
    }

    /**
     * @param velocityContext
     */
    protected final void populateChartInfo(VelocityContext velocityContext){
        velocityContext.put("pluginName", this.getPluginName());
        velocityContext.put("chartName", this.getChartName());
        velocityContext.put("chartId", this.getChartId());
        velocityContext.put("useFrame", this.useFrame());
        velocityContext.put("colspan", this.getShapeColspan());
        velocityContext.put("rowspan", this.getShapeRowspan());
    }

    /**
     * Support @Autowired annotation
     */
    private void autowire() {
        ApplicationContext ctx = this.getApplicationContext();

        if(this.autowire) {
            ControllerUtils.autoWire(this, ctx);
            this.autowire = false;
        }
    }

    /**
     * Check it is initialized.
     * The empty string can't be used for required parameter, but default value be able.
     * @return         boolean
     *
     * @see #addWidgetForParameter(String, XBeamerWidget)
     */
    private boolean checkRequireParams(){
        Map<String, String> params = this.getPluginParams();

        for(String param : widgets.keySet()){
            XBeamerWidget widget = widgets.get(param);
            if(widget.isRequired()){
                if((!params.containsKey(param) || params.get(param).isEmpty()) && (widget.getDefaultValue() == null))
                    return false;
            }
        }

        return true;
    }

    /**
     * Copy all parameters required by widget.
     */
    private void copyParametersToWidget(){
        Map<String, String> params = this.getPluginParams();

        for(String param : this.widgets.keySet()){
            this.widgets.get(param).setValue(params.get(param));
        }
    }
}
