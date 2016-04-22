package com.intland.codebeamer.wiki.plugins;

import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.manager.TrackerItemManager;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dao.TrackerLayoutDao;
import com.intland.codebeamer.persistence.dto.TrackerChoiceOptionDto;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import com.intland.codebeamer.persistence.dto.TrackerStatusOptionDto;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.intland.codebeamer.persistence.dto.base.NamedDto;
import com.intland.codebeamer.text.excel.FieldAccessor;
import com.intland.codebeamer.ui.view.IssueStatusStyles;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-31.
 */
public class WorkItemStatisticsTablePlugin extends AutoWiringCodeBeamerPlugin{
    @Autowired
    private TrackerItemManager trackerItemManager;

    @Autowired
    private TrackerLayoutDao trackerLayoutDao;


    @Override
    protected String getTemplateFilename() {
        return "WorkItemStatisticsTable-plugin.vm";
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        UserDto user = this.getUser();

        FieldAccessor fieldAccessor = new FieldAccessor(this.getApplicationContext());
        fieldAccessor.setUser(user);

        String title = this.getStringParameter(params, "title", "");

        String field;

        if(params.containsKey("field"))
            field = (String)params.get("field");

        else
            throw new PluginException("Parameter 'field' is required");

        //boolean versus = this.getBooleanParameter(params, "versus", false);

        List<String> paramIds = Arrays.asList(((String)params.get("trackerId")).split(","));
        List<Integer> trackerIds = new ArrayList<>(paramIds.size());

        for(String paramId : paramIds){
            trackerIds.add(Integer.parseInt(paramId));
        }

        // trackerItemManager.findByTracker(user, trackerIds, Collections.singleton(TrackerItemDto.Flag.Closed));

        List<TrackerItemDto> items = trackerItemManager.findByTracker(user, trackerIds, null);

        short fieldType;

        final short LIST_OF_NAMED = 0;
        final short NAMED = 1;
        final short TEXT = 2;

        if(items != null && !items.isEmpty()){
            if(fieldAccessor.getFieldByName(items.get(0), field) == null)
                throw new PluginException("Can't not found field");

            Object fieldObject = fieldAccessor.getByLabel(items.get(0), field);

            if(fieldObject instanceof List)
                fieldType = LIST_OF_NAMED;
            else if(fieldObject instanceof NamedDto)
                fieldType = NAMED;
            else if(fieldObject instanceof String)
                fieldType = TEXT;
            else
                throw new PluginException("The field type is not supported: " + fieldObject.getClass());

        }else{
            throw new PluginException("There is no item to display");
        }

        Map<String, Integer> countTable = new TreeMap<>();
        Map<String, Integer> total = new HashMap<>();

        //trackerLayoutDao.findChoiceOptionsByTrackerAndLabel(trackerId, field);

        for(TrackerItemDto item : items){
            if(item.isDeleted() || item.isFolder() || item.isInformation()) continue;

            String statusName;

            if(item.isClosed()) statusName = "Closed";
            else if(item.isResolved()) statusName = "Resolved";
            else if(TrackerItemDto.Flag.InProgress.check(item.getFlags())) statusName = "In Progress";
            else statusName = "New";

            Object fieldObject = fieldAccessor.getByLabel(item, field);
            String cell = "--";

            if(fieldObject == null){
                cell += ";" + statusName;

            }else if(fieldType == NAMED){
                NamedDto fieldValue = (NamedDto)fieldObject;
                cell = fieldValue.getName() + ";" + statusName;

            }else if(fieldType == TEXT){
                String fieldValue = (String)fieldObject;
                cell = fieldValue + ";" + statusName;

            }else if(fieldType == LIST_OF_NAMED){
                List<NamedDto> fieldValues = (List)fieldObject;

                for (NamedDto fieldValue : fieldValues) {
                    if(fieldValue != null) {
                        cell = fieldValue.getName() + ";";
                    }
                    cell += statusName;

                    if (countTable.containsKey(cell))
                        countTable.put(cell, countTable.get(cell) + 1);
                    else
                        countTable.put(cell, 1);

                    if (total.containsKey(statusName))
                        total.put(statusName, total.get(statusName) + 1);
                    else
                        total.put(statusName, 1);
                }
                continue;
            }

            if(countTable.containsKey(cell))
                countTable.put(cell, countTable.get(cell) + 1);
            else
                countTable.put(cell, 1);

            if (total.containsKey(statusName))
                total.put(statusName, total.get(statusName) + 1);
            else
                total.put(statusName, 1);
        }

        velocityContext.put("countTable", countTable);
        velocityContext.put("total", total);
        velocityContext.put("title", title);
    }
}
