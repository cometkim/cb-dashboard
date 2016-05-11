package com.architectgroup.xbeamerchart.widget.foundation;

import com.ecyrd.jspwiki.WikiContext;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import org.apache.velocity.VelocityContext;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-26.
 */
public class XBeamerColorWidget extends XBeamerWidget<String> {
    public XBeamerColorWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/color.vm");

        this.cssStyle("width", "200px");
        this.cssStyle("height", "50px");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
    }

    @Override
    public String getObject(String paramValue) {
        return paramValue;
    }
}
