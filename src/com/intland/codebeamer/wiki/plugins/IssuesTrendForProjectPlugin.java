package com.intland.codebeamer.wiki.plugins;

import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.manager.ProjectManager;
import com.intland.codebeamer.manager.TrackerItemManager;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.*;
import com.intland.codebeamer.search.query.antlr.QueryContext;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by comet on 2016-03-09.
 */
@Deprecated
public class IssuesTrendForProjectPlugin extends AutoWiringCodeBeamerPlugin {
    @Autowired
    private ProjectManager projectManager;

    @Autowired
    private TrackerManager trackerManager;

    @Autowired
    private TrackerItemManager trackerItemManager;

    @Autowired
    private TrackerItemDao trackerItemDao;

    @Override
    protected String getTemplateFilename() {
        return "IssuesTrendForProject-plugin.vm";
    }

    private int countOpen= 0;
    private int countOpenForAll = 0;

    private int countInprogress = 0;
    private int countInprogressForAll = 0;

    private int countClosed = 0;
    private int countClosedForAll = 0;

    private void countStatus(List<TrackerItemDto> items){
        for(TrackerItemDto item : items){
            if(item.isDeleted()) continue;

            if(TrackerItemDto.Flag.InProgress.check(item.getFlags())){
                countInprogress++;
            }else if(item.isResolvedOrClosed()){
                countClosed++;
            }else{
                countOpen++;
            }
        }
    }

    private void moveCount(){
        countOpenForAll += countOpen;
        countOpen = 0;

        countInprogressForAll += countInprogress;
        countInprogress = 0;

        countClosedForAll += countClosed;
        countClosed = 0;
    }

    private void clearCount(){
        this.moveCount();

        countOpenForAll = 0;
        countInprogressForAll = 0;
        countClosedForAll = 0;
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        ProjectDto currentProject = this.getProject();
        UserDto currentUser = this.getUser();

        String title;

        if(currentProject == null && params.containsKey("projectId")){
            Integer projectId = Integer.parseInt((String)params.get("projectId"));
            currentProject = projectManager.findById(currentUser, projectId);
        }

        List<String> categories = new ArrayList<>();

        List<Integer> dataOpen = new ArrayList<>();
        List<Integer> dataInprogress = new ArrayList<>();
        List<Integer> dataClosed = new ArrayList<>();

        if(currentProject == null){ // For all projects
            title = "Issues Trend for All Projects";

            List<ProjectDto> projects = projectManager.findAll(currentUser);

            for(ProjectDto project : projects){
                if(project.isDeleted()) continue;

                categories.add("'" + project.getKeyName() + "'");

                List<TrackerDto> trackers = trackerManager.findByProject(currentUser, project);

                for (TrackerDto tracker : trackers) {
                    if(tracker.isDeleted()) continue;

                    List<TrackerItemDto> items = trackerItemDao.findByTracker(tracker.getId());
                    this.countStatus(items);
                    this.moveCount();
                }

                dataOpen.add(countOpenForAll);
                dataInprogress.add(countInprogressForAll);
                dataClosed.add(countClosedForAll);
                this.clearCount();
            }

        }else{ // For all trackers in specified project
            title = "Issues Trend for " + currentProject.getName();

            List<TrackerDto> trackers = trackerManager.findByProject(currentUser, currentProject);

            for(TrackerDto tracker : trackers){
                if(tracker.isDeleted()) continue;

                categories.add("'" + tracker.getKeyName() + "'");

                List<TrackerItemDto> items = trackerItemDao.findByTracker(tracker.getId());
                this.countStatus(items);

                dataOpen.add(countOpen);
                dataInprogress.add(countInprogress);
                dataClosed.add(countClosed);
                this.moveCount();
            }

            this.clearCount();
        }

        velocityContext.put("title", title);

        velocityContext.put("categories", categories.toString());

        velocityContext.put("open", dataOpen.toString());
        velocityContext.put("inprogress", dataInprogress.toString());
        velocityContext.put("closed", dataClosed.toString());
    }
}
