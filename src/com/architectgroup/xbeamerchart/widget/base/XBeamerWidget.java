package com.architectgroup.xbeamerchart.widget.base;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.controller.ControllerUtils;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.intland.codebeamer.wiki.CodeBeamerWikiContext;
import org.apache.velocity.VelocityContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-21.
 */
public abstract class XBeamerWidget<T extends Object>{
    private boolean autowire = true;
    private WikiContext wikiContext;

    private UserDto user;
    private String fileName;

    private String label;
    private String shortDescription;

    private boolean required;

    private String argument;
    private String defaultValue;

    private Map<String, String> styleMap;

    /**
     * @param ctx           WikiContext from plugin
     * @param fileName      Should implements front-end as velocity template
     */
    protected XBeamerWidget(WikiContext ctx, String fileName){
        this.wikiContext = ctx;
        this.autowire(); // Support @Autowire annotations

        this.user = ((CodeBeamerWikiContext)ctx).getUser();
        this.fileName = fileName;

        this.styleMap = new HashMap<>();
    }

    /**
     * Populate context that needs for widget
     * @param velocityContext context for only this
     */
    public abstract void populateContext(VelocityContext velocityContext);

    public final WikiContext getWikiContext(){ return this.wikiContext; }

    public final UserDto getUser() { return this.user; }
    public final void setUser(UserDto user) { this.user = user; }

    public final String getFileName() { return this.fileName; }
    public final void setFileName(String fileName) { this.fileName = fileName; }

    public final String getLabel() { return this.label; }
    public final void setLabel(String label){ this.label = label; }

    public final String getShortDescription() { return this.shortDescription; }
    public final void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public final void setDefaultValue(String defaultArgument) { this.defaultValue = defaultArgument; }
    public final String getDefaultValue() { return this.defaultValue; }

    public final String getValue() { return this.argument != null ? this.argument : this.defaultValue; }
    public final void setValue(String value) { this.argument = value; }

    public final boolean isRequired() { return this.required; }
    public final void setRequired(boolean required) { this.required = required; }

    public final String cssStyle(@NotNull String style){ return this.styleMap.get(style); }
    public final void cssStyle(@NotNull String style, @NotNull String value){ this.styleMap.put(style, value); }

    public final Map<String, String> getCssStyles() { return this.styleMap; }

    public final ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
    }

    public final ServletContext getServletContext() {
        WikiContext context = this.wikiContext;

        ServletContext servletContext;
        if(context.getHttpRequest() != null) {
            servletContext = context.getHttpRequest().getSession(true).getServletContext();
        } else {
            servletContext = context.getEngine().getServletContext();
        }

        return servletContext;
    }

    public final boolean isAutowire() {
        return this.autowire;
    }

    /**
     * The widget can resolve its target subject from argument
     * @param value      The plugin's argument entered by end-user (it can be null or empty)
     * @return              Target object
     */
    public abstract @Nullable T getObject(@NotNull String value);

    public final @Nullable T getObject(){
        String arg = this.getValue();
        return arg != null ? this.getObject(arg) : null;
    }

    /**
     * Support @Autowire annotations
     */
    private final void autowire(){
        if(this.autowire) {
            ApplicationContext context = this.getApplicationContext();
            ControllerUtils.autoWire(this, context);
            this.autowire = false;
        }
    }
}
