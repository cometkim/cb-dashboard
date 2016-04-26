package com.architectgroup.xbeamerchart.widget.foundation;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import org.apache.velocity.VelocityContext;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerReleaseWidget extends XBeamerWidget{
    public XBeamerReleaseWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/release");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {

    }

    @Nullable
    @Override
    public List<TrackerItemDto> getSubject(@NotNull String argument) {
        return null;
    }
}
