package com.architectgroup.xbeamerchart.widget;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import org.apache.velocity.VelocityContext;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-23.
 */
public class XBeamerColorPickerWidget extends XBeamerWidget {
    public XBeamerColorPickerWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/ColorPicker-widget.vm");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {

    }

    @Override
    public String getSubject(String paramValue) {
        return paramValue;
    }
}
