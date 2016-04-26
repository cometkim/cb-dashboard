package com.architectgroup.xbeamerchart.widget.foundation;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.manager.ProjectManager;
import com.intland.codebeamer.persistence.dto.ProjectDto;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-25.
 */
public class XBeamerProjectWidget extends XBeamerWidget {
    @Autowired
    private ProjectManager projectManager;

    private boolean multiple;

    public XBeamerProjectWidget(WikiContext ctx, boolean multiple){
        super(ctx, "xbeamerchart/widgets/project.vm");
        this.multiple = multiple;
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        velocityContext.put("projects", projectManager.findAll(this.getUser()));
        velocityContext.put("multiple", multiple);
    }

    @Override
    public List<ProjectDto> getSubject(String paramValue) {
        return projectManager.findById(this.getUser(), Arrays.asList(paramValue.split(",")));
    }
}

