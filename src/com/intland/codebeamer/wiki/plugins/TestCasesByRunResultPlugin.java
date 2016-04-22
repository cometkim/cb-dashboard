package com.intland.codebeamer.wiki.plugins;

import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.manager.ProjectManager;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.manager.testmanagement.TestRun;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.ProjectDto;
import com.intland.codebeamer.persistence.dto.TrackerDto;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import com.intland.codebeamer.persistence.dto.TrackerTypeDto;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-22.
 */
public class TestCasesByRunResultPlugin extends AutoWiringCodeBeamerPlugin{
    @Autowired
    private ProjectManager projectManager;

    @Autowired
    private TrackerManager trackerManager;

    @Autowired
    private TrackerItemDao trackerItemDao;

    @Override
    protected String getTemplateFilename() {
        return "TestCasesByRunResult-plugin.vm";
    }

    private boolean isInitialized(){
        Map<String, String> params = this.getPluginParams();
        return params.containsKey("trackerId");
    }

    private Map<String, Integer> getCoverages(){
        Map<String, Integer> coverages = new HashMap<>();

        int notRanYet = 0;
        int incomplete = 0;
        int passed = 0;
        int partlyPassed = 0;
        int blocked = 0;
        int failed = 0;

        Map<String, String> params = this.getPluginParams();
        Integer trackerId = Integer.parseInt(params.get("trackerId"));
        for(TrackerItemDto item : trackerItemDao.findByTracker(trackerId)){
            if(item.isDeleted()) continue;

            TestRun run = new TestRun(item, this.getUser(), this.getApplicationContext());
            if(!run.isTestSetRun()) continue;

            TestRun.TestRunStatus status = run.getTestRunStatus();

            if(status.equals(TestRun.TestRunStatus.COMPLETED)){
                TestRun.TestRunResult result = run.getTestRunResult();

                if(result.equals(TestRun.TestRunResult.PASSED))
                    passed++;
                else if(result.equals(TestRun.TestRunResult.PARTLY_PASSED))
                    partlyPassed++;
                else if(result.equals(TestRun.TestRunResult.BLOCKED))
                    blocked++;
                else if(result.equals(TestRun.TestRunResult.FAILED))
                    failed++;

            }else if(status.equals(TestRun.TestRunStatus.IN_PROGRESS) || status.equals(TestRun.TestRunStatus.SUSPENDED)){
                incomplete++;
            }else{
                notRanYet++;
            }
        }

        if(notRanYet > 0)
            coverages.put("notRanYet", notRanYet);
        if(incomplete > 0)
            coverages.put("incomplete", incomplete);
        if(passed > 0)
            coverages.put("passed", passed);
        if(partlyPassed > 0)
            coverages.put("partlyPassed", partlyPassed);
        if(blocked > 0)
            coverages.put("blocked", blocked);
        if(failed > 0)
            coverages.put("failed", failed);

        return coverages;
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        if(isInitialized()){
            Integer trackerId = Integer.parseInt((String)params.get("trackerId"));
            TrackerDto tracker = trackerManager.findById(this.getUser(), trackerId);

            if(!tracker.getType().equals(TrackerTypeDto.TESTRUN))
                throw new PluginException("The tracker is not TestRun");

            Map<String, Integer> coverages = this.getCoverages();
            boolean isEmpty = coverages.isEmpty();

            if(!isEmpty){
                velocityContext.put("chartId", RandomStringUtils.randomAlphanumeric(10));
                velocityContext.put("coverages", this.getCoverages());

                String title;

                if(params.containsKey("title"))
                    title = (String)params.get("title");
                else
                    title = "Test Run Results for " + tracker.getProject().getName() + " → " + tracker.getName();

                velocityContext.put("title", title);
            }

            velocityContext.put("empty", isEmpty);

        }else{
            throw new PluginException("The parameter 'trackerId' is required");
        }
    }
}
