package com.architectgroup.xbeamerchart.widget.foundation;

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

import java.util.*;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-25.
 */
public class XBeamerTrackerWidget extends XBeamerWidget {
    @Autowired
    private ProjectManager projectManager;

    @Autowired
    private TrackerManager trackerManager;

    List<TrackerTypeDto> types;
    private boolean multiple;

    public XBeamerTrackerWidget(WikiContext ctx, boolean multiple) {
        this(ctx, multiple, TrackerTypeDto.TYPES);
    }

    public XBeamerTrackerWidget(WikiContext ctx, boolean multiple, List<TrackerTypeDto> types){
        super(ctx, "xbeamerchart/widgets/tracker.vm");
        this.types = types;
        this.multiple = multiple;

        this.cssStyle("width", "100%");
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
        velocityContext.put("multiple", multiple);
    }

    @Override
    public List<TrackerDto> getSubject(String paramValue) {
        UserDto user = this.getUser();
        return trackerManager.findById(user, Arrays.asList(paramValue.split(",")));
    }
}
