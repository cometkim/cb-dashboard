package com.intland.codebeamer.wiki.plugins;

import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.Config;
import com.intland.codebeamer.manager.ProjectManager;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.TrackerDto;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import com.intland.codebeamer.persistence.dto.TrackerTypeDto;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.intland.codebeamer.search.query.antlr.CbQLQueryHandler;
import com.intland.codebeamer.search.query.antlr.QueryContext;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-22.
 */
public class TestExecutionTrendsPlugin extends AutoWiringCodeBeamerPlugin {
    private Calendar calendar = Calendar.getInstance();

    @Autowired
    private ProjectManager projectManager;

    @Autowired
    private TrackerManager trackerManager;

    @Autowired
    private TrackerItemDao trackerItemDao;

    @Autowired
    @Qualifier("TRACKER_ITEM")
    private CbQLQueryHandler<TrackerItemDto> cbQLQueryHandler;

    @Override
    protected String getTemplateFilename() {
        return "TestExecutionTrends-plugin.vm";
    }

    private String getFromToQuery(){
        Map<String, String> params = this.getPluginParams();

        String query = "";

        if(params.containsKey("from")){
            query += "closedAt >= " + params.get("from");
        }
        if(params.containsKey("to")){
            if(!query.isEmpty())
                query += " AND ";
            query += "closedAt <= " + params.get("to");
        }

        return query;
    }

    private Set<String> getExecutionTrends(String resolution){
        UserDto user = this.getUser();

        Map<String, String> params = this.getPluginParams();

        Integer trackerId = Integer.parseInt((String)params.get("trackerId"));
        TrackerDto tracker = trackerManager.findById(user, trackerId);

        Integer projectId = tracker.getProject().getId();

        String cbQl = "'" + projectId + "." + trackerId + ".resolution' = '" + resolution + "'";

        String fromTo = this.getFromToQuery();
        if(!fromTo.isEmpty())
            cbQl += " AND " + fromTo;

        int pageLength = Config.getItemsPerPage();
        int pageNo = 0;

        QueryContext context = new QueryContext();
        context.addParameter(QueryContext.PARAM_TYPE.USER, user);

        List<TrackerItemDto> items = cbQLQueryHandler.findByCbQL(cbQl, context, pageNo, pageLength).getList();
        Map<Date, Integer> trends = new HashMap<>();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();

        Date aMonthAgo = DateUtils.addMonths(today, -1);

        for(TrackerItemDto item : items){
            if(item.isDeleted()) continue;
            if(item.getParent() != null) continue;
            if(item.isClosed()){
                calendar.setTime(item.getClosedAt());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date closedAt = calendar.getTime();

                if(closedAt.compareTo(aMonthAgo) < 0) continue;

                if(trends.containsKey(closedAt)){
                    trends.put(closedAt, trends.get(closedAt) + 1);
                }else{
                    trends.put(closedAt, 1);
                }
            }
        }

        Set<String> formatted = new HashSet<>();
        for(Date date : trends.keySet()){
            Integer count = trends.get(date);
            calendar.setTime(date);
            formatted.add("[Date.UTC(" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH) + ")," + count +"]");
        }

        return formatted;
    }

    private boolean isInitialized(){
        Map<String, String> params = this.getPluginParams();
        return params.containsKey("trackerId");
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        boolean isInitialized = this.isInitialized();

        if(isInitialized){
            velocityContext.put("chartId", RandomStringUtils.randomAlphanumeric(10));

            Integer trackerId = Integer.parseInt((String)params.get("trackerId"));
            TrackerDto tracker = trackerManager.findById(this.getUser(), trackerId);

            if(!tracker.getType().equals(TrackerTypeDto.TESTRUN))
                throw new PluginException("The tracker is not TestRun");

            String title;

            if(params.containsKey("title"))
                title = (String)params.get("title");
            else
                title = "Test Execution Trends for " + tracker.getProject().getName() + " → " + tracker.getName();

            Set<String> passed = this.getExecutionTrends("Passed");
            Set<String> partlyPassed = this.getExecutionTrends("Partly Passed");
            Set<String> failed = this.getExecutionTrends("Failed");
            Set<String> blocked = this.getExecutionTrends("Blocked");

            boolean isEmpty = passed.isEmpty() && partlyPassed.isEmpty() && failed.isEmpty() && blocked.isEmpty();

            if(!isEmpty) {
                if (!passed.isEmpty())
                    velocityContext.put("passed", passed.toString());
                if (!partlyPassed.isEmpty())
                    velocityContext.put("partlyPassed", partlyPassed.toString());
                if (!failed.isEmpty())
                    velocityContext.put("failed", failed.toString());
                if (!blocked.isEmpty())
                    velocityContext.put("blocked", blocked.toString());
            }

            velocityContext.put("title", title);
            velocityContext.put("empty", isEmpty);
        }else{
            throw new PluginException("The parameter 'trackerId' is required");
        }
    }
}
