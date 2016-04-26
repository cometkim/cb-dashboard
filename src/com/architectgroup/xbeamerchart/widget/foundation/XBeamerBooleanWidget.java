package com.architectgroup.xbeamerchart.widget.foundation;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created by comet on 2016-04-25.
 */
public class XBeamerBooleanWidget extends XBeamerWidget{
    public XBeamerBooleanWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/boolean.vm");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {

    }

    @Nullable
    @Override
    public <T> T getSubject(@NotNull String argument) {
        return null;
    }
}
