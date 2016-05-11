package com.architectgroup.xbeamerchart.widget.foundation;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-25.
 */
public class XBeamerBooleanWidget extends XBeamerWidget<Boolean>{
    public XBeamerBooleanWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/boolean.vm");

        this.cssStyle("width", "200px");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
    }

    @Nullable
    @Override
    public Boolean getObject(@NotNull String argument) {
        return Boolean.parseBoolean(argument);
    }
}
