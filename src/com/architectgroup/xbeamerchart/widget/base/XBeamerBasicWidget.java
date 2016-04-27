package com.architectgroup.xbeamerchart.widget.base;

import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerBasicWidget extends XBeamerWidget {
    private String htmlTag;
    private Map<String, String> attributes;

    public XBeamerBasicWidget(WikiContext ctx, String htmlTag) {
        super(ctx, "xbeamerchart/includes/widget-basic.vm");
        this.htmlTag = htmlTag;
        this.attributes = new HashMap<>();

        this.cssStyle("width", "200px");
        this.cssStyle("height", "50px");
    }

    public Map<String, String> getAttributes(){ return this.attributes; }
    public String attribute(String attr){ return this.attributes.get(attr); }
    public void attribute(String attr, Object value){
        if(value != null) this.attributes.put(attr, String.valueOf(value));
    }

    public String getHtmlTag(){ return this.htmlTag; }
    public void setHtmlTag(String htmlTag){ this.htmlTag = htmlTag; }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        velocityContext.put("htmlTag", this.htmlTag);
        velocityContext.put("attributes", this.attributes);
    }

    @Nullable
    @Override
    public String getSubject(@NotNull String argument) { return argument; }
}
