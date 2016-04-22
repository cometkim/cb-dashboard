package com.intland.codebeamer.wiki.plugins;

import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.controller.importexport.ChoicesProvider;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.*;
import com.intland.codebeamer.persistence.dto.base.NamedDto;
import com.intland.codebeamer.text.excel.FieldAccessor;
import com.intland.codebeamer.ui.view.IssueStatusStyles;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-04.
 */
public class IssueStatisticsTablePlugin extends AutoWiringCodeBeamerPlugin{
    @Autowired
    private TrackerItemDao trackerItemDao;

    @Autowired
    private TrackerManager trackerManager;

    @Autowired
    private IssueStatusStyles issueStatusStyles;

    private FieldAccessor fieldAccessor;

    private class PluginParam{
        private String title;

        private Integer trackerId;

        private String colFieldName;
        private String rowFieldName;

        private boolean includeClose;

        public PluginParam(Map params) throws PluginException{
            String message = "";

            if(!params.containsKey("trackerId")) message += " 'trackerId' is required";
            if(!params.containsKey("col")) message += " 'col' is required";
            if(!params.containsKey("row")) message += " 'row' is required";

            if(!message.isEmpty()) throw new PluginException(message);

            try{
                this.trackerId = Integer.parseInt((String)params.get("trackerId"));
            }catch (NumberFormatException e){
                throw new PluginException("Error format in 'trackerId': " + e.getMessage());
            }

            this.colFieldName = (String)params.get("col");
            if(this.colFieldName.isEmpty()) message += " 'col' is required";

            this.rowFieldName = (String)params.get("row");
            if(this.rowFieldName.isEmpty()) message += " 'row' is required";

            if(!message.isEmpty()) throw new PluginException(message);

            this.title = getStringParameter(params, "title", "");
            this.includeClose = getBooleanParameter(params, "includeClose", false);
        }

        public String getTitle(){ return this.title; }
        public Integer getTrackerId(){ return this.trackerId; }
        public String getColFieldName(){ return this.colFieldName; }
        public String getRowFieldName(){ return this.rowFieldName; }
        public Boolean getIncludeClose(){ return this.includeClose; }
    }

    private enum FieldType{
        LIST_OF_NAMED, NAMED, TEXT, NOT_SUPPORT;

        public boolean isNotSupported(){
            return this == NOT_SUPPORT;
        }
    }

    private FieldType getFieldType(Object fieldObject){
        FieldType fieldType;

        if(fieldObject instanceof List)
            fieldType = FieldType.LIST_OF_NAMED;
        else if(fieldObject instanceof NamedDto)
            fieldType = FieldType.NAMED;
        else if(fieldObject instanceof String)
            fieldType = FieldType.TEXT;
        else
            fieldType = FieldType.NOT_SUPPORT;

        return fieldType;
    }

    private Map<String, Integer> rowTotal = new HashMap<>();
    private Map<String, Integer> colTotal = new HashMap<>();

    private void addCountOnTable(Map<String, Integer> table, String row, String col){
        String cell = row + ";" + col;

        if(table.containsKey(cell))
            table.put(cell, table.get(cell) + 1);
        else
            table.put(cell, 1);

        if(rowTotal.containsKey(row))
            rowTotal.put(row, rowTotal.get(row) + 1);
        else
            rowTotal.put(row, 1);

        if(colTotal.containsKey(col))
            colTotal.put(col, colTotal.get(col) + 1);
        else
            colTotal.put(col, 1);
    }

    @Override
    protected String getTemplateFilename() {
        return "IssueStatisticsTable-plugin.vm";
    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        UserDto user = this.getUser();

        ApplicationContext context = this.getApplicationContext();

        PluginParam pluginParam = new PluginParam(params);
        String title = pluginParam.getTitle();
        Integer trackerId = pluginParam.getTrackerId();
        String colFieldName = pluginParam.getColFieldName();
        String rowFieldName = pluginParam.getRowFieldName();
        boolean includeClose = pluginParam.getIncludeClose();

        fieldAccessor = new FieldAccessor(context);
        fieldAccessor.setUser(user);

        ChoicesProvider choicesProvider = fieldAccessor.getChoicesProvider();

        List<TrackerItemDto> items = trackerItemDao.findByTracker(trackerId);

        if(items != null && !items.isEmpty()){
            TrackerItemDto firstItem = items.get(0);

            TrackerLayoutLabelDto rowField = fieldAccessor.getFieldByName(firstItem, rowFieldName);
            if(rowField == null) throw new PluginException("Cannot found field: " + rowFieldName);

            TrackerLayoutLabelDto colField = fieldAccessor.getFieldByName(firstItem, colFieldName);
            if(colField == null) throw new PluginException("Cannot found field: " + colFieldName);

            Object fieldObject = fieldAccessor.getByLabel(firstItem, rowFieldName);
            FieldType fieldType = getFieldType(fieldObject);

            if(fieldType.isNotSupported())
                throw new PluginException("row field type is not supported");

            Object columnObject = fieldAccessor.getByLabel(firstItem, colFieldName);
            FieldType columnType = getFieldType(columnObject);

            if(columnType != FieldType.NAMED && columnType != FieldType.LIST_OF_NAMED)
                throw new PluginException("col field type is not supported");

            List<String> columns = choicesProvider.getIssueChoices(user, firstItem, colFieldName);
            if(!columns.contains("--")) columns.add(0, "--");

            List<String> statuses = new ArrayList<>();
            statuses.add("--");
            for(TrackerStatusOptionDto status : trackerManager.getStatusChoiceList(trackerId)) {
                statuses.add(issueStatusStyles.getStyleForStatus(status, true).renderHtml(status.getName()));
            }

            Map<String, Integer> countTable = new TreeMap<>();

            for(TrackerItemDto item : items){
                if(item.isDeleted() || item.isFolder() || item.isInformation()) continue;
                if(!includeClose && item.isClosed()) continue;

                fieldObject = fieldAccessor.getByLabel(item, rowFieldName);

                NamedDto columnNamed = columnType == FieldType.NAMED ? (NamedDto)fieldAccessor.getByLabel(item, colFieldName) : ((List<NamedDto>)fieldAccessor.getByLabel(item, colFieldName)).get(0);
                String column = columnNamed != null ? columnNamed.getName() : "--";

                if(fieldObject == null){
                    addCountOnTable(countTable, "--", column);

                }else if(fieldType == FieldType.NAMED){
                    addCountOnTable(countTable, ((NamedDto)fieldObject).getName(), column);

                }else if(fieldType == FieldType.TEXT){
                    addCountOnTable(countTable, (String)fieldObject, column);

                }else if(fieldType == FieldType.LIST_OF_NAMED){
                    for(NamedDto named : (List<NamedDto>)fieldObject){
                        addCountOnTable(countTable, named != null ? named.getName() : "--", column);
                    }
                }
            }

            velocityContext.put("columns", columns);
            velocityContext.put("statuses", statuses);
            velocityContext.put("countTable", countTable);
            velocityContext.put("colFieldName", colFieldName);
            velocityContext.put("rowFieldName", rowFieldName);
            velocityContext.put("rowTotal", rowTotal);
            velocityContext.put("colTotal", colTotal);

        }else{
            velocityContext.put("no_issue", true);
        }

        velocityContext.put("title", title);
    }
}
