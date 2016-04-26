package com.architectgroup.xbeamerchart.widget;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.manager.ProjectManager;
import com.intland.codebeamer.persistence.dto.ProjectDto;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-21.
 */
@Deprecated
public class XBeamerProjectSelectWidget extends XBeamerWidget {
    @Autowired
    private ProjectManager projectManager;

    private boolean multiple;

    public XBeamerProjectSelectWidget(WikiContext ctx, boolean multiple){
        super(ctx, "xbeamerchart/widgets/ProjectSelect-widget.vm");
        this.multiple = multiple;
        this.cssStyle("width", "200px");
        this.cssStyle("height", "50px");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        velocityContext.put("projects", projectManager.findAll(this.getUser()));
        velocityContext.put("multiple", multiple ? "multi" : "");
    }

    @Override
    public List<ProjectDto> getSubject(String paramValue) {
        return projectManager.findById(this.getUser(), Arrays.asList(paramValue.split(",")));
    }
}

