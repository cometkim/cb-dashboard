package com.architectgroup.xbeamerchart.widget;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import org.apache.velocity.VelocityContext;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-21.
 */
@Deprecated
public class XBeamerInputTextWidget extends XBeamerWidget {
    //private String label;

    public XBeamerInputTextWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/InputText-widget.vm");
        //this.label = label;
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {

    }

    @Override
    public String getSubject(String paramValue) {
        return paramValue;
    }
}
