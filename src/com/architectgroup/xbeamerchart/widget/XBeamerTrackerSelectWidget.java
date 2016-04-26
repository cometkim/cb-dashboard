package com.architectgroup.xbeamerchart.widget;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.manager.ProjectManager;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dto.ProjectDto;
import com.intland.codebeamer.persistence.dto.TrackerDto;
import com.intland.codebeamer.persistence.dto.TrackerTypeDto;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-21.
 */
@Deprecated
public class XBeamerTrackerSelectWidget extends XBeamerWidget {
    @Autowired
    private ProjectManager projectManager;

    @Autowired
    private TrackerManager trackerManager;

    List<TrackerTypeDto> types;
    private boolean multiple;

    public XBeamerTrackerSelectWidget(WikiContext ctx, boolean multiple) {
        this(ctx, multiple, TrackerTypeDto.TYPES);
    }

    public XBeamerTrackerSelectWidget(WikiContext ctx, boolean multiple, List<TrackerTypeDto> types){
        super(ctx, "xbeamerchart/widgets/TrackerSelect-widget.vm");
        this.types = types;
        this.multiple = multiple;
        this.cssStyle("width", "200px");
        this.cssStyle("height", "50px");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        List<ProjectDto> projects = projectManager.findAll(this.getUser());

        Map<ProjectDto, List<TrackerDto>> trackers = new TreeMap<>();

        for(ProjectDto project : projects){
            trackers.put(project, trackerManager.findByProjectAndTypes(this.getUser(), project.getId(), this.types));
        }

        velocityContext.put("trackers", trackers);
        velocityContext.put("multiple", multiple ? "multi" : "");
    }

    @Override
    public List<TrackerDto> getSubject(String paramValue) {
        return null;
    }
}
