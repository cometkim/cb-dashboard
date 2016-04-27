package com.architectgroup.xbeamerchart.widget;

import com.architectgroup.xbeamerchart.widget.base.XBeamerBasicWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerTextWidget extends XBeamerBasicWidget {
    private String placeholder;

    private boolean disable = false;
    private boolean readonly = false;

    public XBeamerTextWidget(WikiContext ctx) {
        super(ctx, "input");
        this.attribute("type", "text");
    }

    public String getPlaceholder(){ return this.placeholder; }

    public void setPlaceholder(String placeholder){ this.placeholder = placeholder; }

    public boolean isDisable(){ return this.disable; }

    public void setDisable(boolean disable){ this.disable = disable; }

    public boolean isReadonly(){ return this.readonly; }

    public void setReadonly(boolean readonly){ this.readonly = readonly; }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        this.attribute("placeholder", this.placeholder);

        if(this.disable) this.attribute("disable", "disable");
        if(this.readonly) this.attribute("readonly", "readonly");

        super.populateContext(velocityContext);
    }

    @Override
    public String getSubject(String paramValue) {
        return paramValue;
    }
}
