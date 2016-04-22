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
public abstract class XBeamerWidget{
    private boolean autowire = true;
    private WikiContext wikiContext;

    private UserDto user;
    private String fileName;

    @Deprecated
    private String selector;

    private String summary;
    private String shortDescription;

    private boolean required;

    private String argument;
    private String defaultArgument;

    private Map<String, String> styleMap;

    @Deprecated
    public XBeamerWidget(UserDto user, String fileName) {
        this.user = user;
        this.fileName = fileName;
        this.required = false;
        this.selector = null;
    }

    /**
     * @param ctx           WikiContext from plugin
     * @param fileName      Should implements front-end as velocity template
     */
    protected XBeamerWidget(WikiContext ctx, String fileName){
        this.wikiContext = ctx;
        this.autowire(); // Support @Autowire annotations

        this.user = ((CodeBeamerWikiContext)ctx).getUser();
        this.fileName = fileName;
        this.selector = null;

        this.styleMap = new HashMap<>();
    }

    public WikiContext getWikiContext(){ return this.wikiContext; }

    public UserDto getUser() { return this.user; }
    public void setUser(UserDto user) { this.user = user; }

    public String getFileName() { return this.fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    @Deprecated
    public String getSelector() { return this.selector; }
    @Deprecated
    public void setSelector(String selector) { this.selector = selector; }

    public String getSummary() { return this.summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getShortDescription() { return this.shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public void setDefaultArgument(String defaultArgument) { this.defaultArgument = defaultArgument; }
    public String getDefaultArgument() { return this.defaultArgument; }

    public String getValue() { return this.argument != null ? this.argument : this.defaultArgument; }
    public void setValue(String value) { this.argument = value; }

    public boolean isRequired() { return this.required; }
    public void setRequired(boolean required) { this.required = required; }

    public String cssStyle(String style){ return this.styleMap.get(style); }
    public void cssStyle(String style, String value){ this.styleMap.put(style, value); }

    public Map<String, String> getCssStyles() { return this.styleMap; }

    public ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
    }

    public ServletContext getServletContext() {
        WikiContext context = this.getWikiContext();

        ServletContext servletContext;
        if(context.getHttpRequest() != null) {
            servletContext = context.getHttpRequest().getSession(true).getServletContext();
        } else {
            servletContext = context.getEngine().getServletContext();
        }

        return servletContext;
    }

    public boolean isAutowire() {
        return this.autowire;
    }

    /**
     * Populate context that needs for widget
     * @param velocityContext context for only this
     */
    public abstract void populateContext(VelocityContext velocityContext);

    /**
     * The widget can resolve its target subject from argument
     * @param argument      The plugin's argument entered by end-user (it can be null or empty)
     * @param <T>           Type of target subject.
     * @return              Target subject.
     */
    public abstract @Nullable <T extends Object> T getSubject(@NotNull String argument);

    public @Nullable <T extends Object> T getSubject(){
        String arg = this.getValue();
        return arg != null ? (T)this.getSubject(arg) : null;
    }

    /**
     * Support @Autowire annotations
     */
    private void autowire(){
        if(this.autowire) {
            ApplicationContext context = this.getApplicationContext();
            ControllerUtils.autoWire(this, context);
            this.autowire = false;
        }
    }
}
