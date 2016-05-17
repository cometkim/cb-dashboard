package com.architectgroup.xbeamerchart.widget.foundation;

import com.architectgroup.xbeamerchart.widget.base.XBeamerBasicWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-26.
 */
public class XBeamerTextWidget extends XBeamerBasicWidget {
    private boolean disable = false;
    private boolean readonly = false;

    public XBeamerTextWidget(WikiContext ctx) {
        super(ctx, "input");
        this.attribute("type", "text");
    }

    public boolean isDisable(){ return this.disable; }

    public void setDisable(boolean disable){ this.disable = disable; }

    public boolean isReadonly(){ return this.readonly; }

    public void setReadonly(boolean readonly){ this.readonly = readonly; }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        if(this.disable) this.attribute("disable", "disable");
        if(this.readonly) this.attribute("readonly", "readonly");

        super.populateContext(velocityContext);
    }

    @Override
    public String getObject(String paramValue) {
        return paramValue;
    }
}
