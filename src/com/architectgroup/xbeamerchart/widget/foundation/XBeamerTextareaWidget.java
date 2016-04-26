package com.architectgroup.xbeamerchart.widget.foundation;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerTextareaWidget extends XBeamerWidget {
    public XBeamerTextareaWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/textarea.vm");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {

    }

    @Override
    public String getSubject(String paramValue) {
        return paramValue;
    }
}
