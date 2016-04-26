package com.architectgroup.xbeamerchart.widget.foundation;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerTextWidget extends XBeamerWidget {
    private String placeholder;

    public XBeamerTextWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/text.vm");
    }

    public String getPlaceholder(){ return this.placeholder; }

    public void setPlaceholder(String placeholder){ this.placeholder = placeholder; }

    @Override
    public void populateContext(VelocityContext velocityContext) {

    }

    @Override
    public String getSubject(String paramValue) {
        return paramValue;
    }
}
