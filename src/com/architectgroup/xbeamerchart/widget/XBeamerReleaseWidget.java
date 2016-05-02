package com.architectgroup.xbeamerchart.widget;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerReleaseWidget extends XBeamerWidget{
    @Autowired
    private TrackerManager trackerManager;

    @Autowired
    private TrackerItemDao trackerItemDao;

    public XBeamerReleaseWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/release.vm");

        this.cssStyle("width", "100%");
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
