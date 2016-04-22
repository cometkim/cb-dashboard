package com.intland.codebeamer.wiki.plugins;

import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.manager.ProjectManager;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.*;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by comet on 2016-03-10.
 */
@Deprecated
public class OpenIssuesPlugin extends AutoWiringCodeBeamerPlugin{
    public enum Type{
        TABLE, CHART;

        public boolean isTable(){ return this == TABLE; }
        public boolean isChart(){ return this == CHART; }

        public String getTemplateFileName(){
            return this.isTable() ?
                "OpenIssuesTable-plugin.vm" :
                "OpenIssuesChart-plugin.vm";
        }
    }

    private Type type;

    @Autowired
    private ProjectManager projectManager;

    @Autowired
    private TrackerManager trackerManager;

    @Autowired
    private TrackerItemDao trackerItemDao;

    @Override
    protected String getTemplateFilename() {
        return type.getTemplateFileName();
    }

    private int getCountOfOpenIssues(ProjectDto project, TrackerTypeDto type){
        List<TrackerDto> trackers = trackerManager.findByProjectAndTypes(this.getUser(), project.getId(), Collections.singletonList(type));
        List<TrackerItemDto> issues = trackerItemDao.findByTrackers(this.getUser(), trackers, null);

        int count = 0;

        for(TrackerItemDto issue : issues){
            if(issue.isDeleted()) continue;

            if(!issue.isResolvedOrClosed())
                count++;
        }

        return count;
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        UserDto user = this.getUser();

        List<ProjectDto> projects = projectManager.findAll(user);

        String typeValue = this.getStringParameter(params, "type", "table");

        if(typeValue.equals("table")) {
            this.type = Type.TABLE;
            Map<ProjectDto, Integer> countOfOpenRequirements = new HashMap<>();
            Map<ProjectDto, Integer> countOfOpenBugs = new HashMap<>();
            Map<ProjectDto, Integer> countOfOpenTasks = new HashMap<>();
            Map<ProjectDto, Integer> countOfOpenTestRuns = new HashMap<>();

            for (ProjectDto project : projects) {
                if(project.isDeleted()) continue;

                countOfOpenRequirements.put(project, this.getCountOfOpenIssues(project, TrackerTypeDto.REQUIREMENT));
                countOfOpenBugs.put(project, this.getCountOfOpenIssues(project, TrackerTypeDto.BUG));
                countOfOpenTasks.put(project, this.getCountOfOpenIssues(project, TrackerTypeDto.TASK));
                countOfOpenTestRuns.put(project, this.getCountOfOpenIssues(project, TrackerTypeDto.TESTRUN));
            }

            velocityContext.put("searchPath", "?view_id=-9");

            velocityContext.put("projects", projects);

            velocityContext.put("countOfOpenRequirements", countOfOpenRequirements);
            velocityContext.put("countOfOpenBugs", countOfOpenBugs);
            velocityContext.put("countOfOpenTasks", countOfOpenTasks);
            velocityContext.put("countOfOpenTestRuns", countOfOpenTestRuns);
        }else
        if(typeValue.equals("chart")){
            this.type = Type.CHART;

            List<String> categories = new ArrayList<>();
            Map<String, String> data = new HashMap<>();

            List<Integer> countOfOpenRequirements = new ArrayList<>();
            List<Integer> countOfOpenBugs = new ArrayList<>();
            List<Integer> countOfOpenTasks = new ArrayList<>();
            List<Integer> countOfOpenTestRuns = new ArrayList<>();

            Map<String, String> color = new HashMap<>();
            color.put("Requirement", TrackerTypeDto.REQUIREMENT.getColor());
            color.put("Task", TrackerTypeDto.TASK.getColor());
            color.put("Test Run", TrackerTypeDto.TESTRUN.getColor());
            color.put("Bug", TrackerTypeDto.BUG.getColor());

            for(ProjectDto project : projects){
                if(project.isDeleted()) continue;

                categories.add("\"" + project.getName() + "\"");

                countOfOpenRequirements.add(this.getCountOfOpenIssues(project, TrackerTypeDto.REQUIREMENT));
                countOfOpenTasks.add(this.getCountOfOpenIssues(project, TrackerTypeDto.TASK));
                countOfOpenTestRuns.add(this.getCountOfOpenIssues(project, TrackerTypeDto.TESTRUN));
                countOfOpenBugs.add(this.getCountOfOpenIssues(project, TrackerTypeDto.BUG));
            }

            data.put("Requirement", countOfOpenRequirements.toString());
            data.put("Task", countOfOpenTasks.toString());
            data.put("Test Run", countOfOpenTestRuns.toString());
            data.put("Bug", countOfOpenBugs.toString());

            velocityContext.put("categories", categories);
            velocityContext.put("data", data);
        }else{
            throw new PluginException("Dashboard Type is required");
        }

        velocityContext.put("user", user);
        velocityContext.put("contextPath", this.getContextPath());
    }
}
