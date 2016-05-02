package com.architectgroup.xbeamerchart.widget.foundation;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import org.apache.velocity.VelocityContext;

import java.util.Map;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-25.
 */
public class XBeamerSelectWidget extends XBeamerWidget {
    private Map options;
    private boolean multiple;

    public XBeamerSelectWidget(WikiContext ctx, Map options, boolean multiple) {
        super(ctx, "xbeamerchart/widgets/select.vm");
        this.options = options;
        this.multiple = multiple;

        this.cssStyle("width", "100%");
        this.cssStyle("height", "50px");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        velocityContext.put("options", this.options);
        velocityContext.put("multiple", multiple);
    }

    @Override
    public String getSubject(String paramValue) {
        return paramValue;
    }
}
