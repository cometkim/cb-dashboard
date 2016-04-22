package com.intland.codebeamer.wiki.plugins;

import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dao.ChoiceOptionDao;
import com.intland.codebeamer.persistence.dao.TrackerFieldDao;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dao.TrackerLayoutDao;
import com.intland.codebeamer.persistence.dto.*;
import com.intland.codebeamer.persistence.dto.base.NamedDto;
import com.intland.codebeamer.text.excel.FieldAccessor;
import com.intland.codebeamer.ui.view.IssueStatusStyles;
import com.intland.codebeamer.wiki.plugins.base.AutoWiringCodeBeamerPlugin;
import org.apache.ibatis.plugin.Plugin;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by comet on 2016-04-07.
 */
public class IssueStatisticsByOrderPlugin extends AutoWiringCodeBeamerPlugin {
    @Autowired
    private TrackerItemDao trackerItemDao;

    @Autowired
    private TrackerManager trackerManager;

    @Autowired
    private TrackerLayoutDao trackerLayoutDao;

    @Autowired
    private IssueStatusStyles issueStatusStyles;

    private class PluginParam{
        private Integer trackerId;
        private String filter;
        private String orderBy;
        private String title = "";

        public PluginParam(Map<String, String> params) throws PluginException{
            String message = "";
            if(!params.containsKey("trackerId")) message += "'trackerId' is required ";
            if(!params.containsKey("orderBy")) message += "'orderBy' is required ";
            if(!message.isEmpty()) throw new PluginException(message);

            try {
                this.trackerId = Integer.parseInt(params.get("trackerId"));
            }catch (NumberFormatException e){
                throw new PluginException("error in trackerId parameter: " + e.getMessage());
            }
            this.filter = params.containsKey("filter") ? params.get("filter") : "";
            this.orderBy = params.get("orderBy");

            if(params.containsKey("title"))
                this.title = params.get("title");
        }

        public Integer getTrackerId(){ return this.trackerId; }
        public String getFilter(){ return this.filter; }
        public String getOrderBy(){ return this.orderBy; }
        public String getTitle(){ return this.title; }
    }

    @Override
    protected String getTemplateFilename() {
        return "IssueStatisticsByOrder-plugin.vm";
    }

    private void addCountOnTable(Map<String,Integer> table, String status, String order, String severity){
        // Total
        String key = order;

        if(table.containsKey(key)){
            table.put(key, table.get(key) + 1);

        }else{
            table.put(key, 1);
        }

        // Sub-Total
        key += ";" + severity;

        if(table.containsKey(key)){
            table.put(key, table.get(key) + 1);

        }else{
            table.put(key, 1);
        }

        // Count
        key = status + ";" + key;

        if(table.containsKey(key)){
            table.put(key, table.get(key) + 1);

        }else{
            table.put(key, 1);
        }

    }

    @Override
    protected void populateContext(VelocityContext velocityContext, Map params) throws PluginException {
        try {
            UserDto user = this.getUser();

            PluginParam pluginParam = new PluginParam(params);

            Integer trackerId = pluginParam.getTrackerId();
            String orderBy = pluginParam.getOrderBy();
            String filter = pluginParam.getFilter();
            String title = pluginParam.getTitle();

            String filterField = null;
            String filterValue = null;
            boolean filtered = false;
            if (!filter.isEmpty()) {
                try {
                    filterField = filter.split("=")[0];
                    filterValue = filter.split("=")[1];
                } catch (Exception e) {
                    throw new PluginException("error in filter parameter: " + e.getMessage());
                }

                filtered = true;
            }

            FieldAccessor fieldAccessor = new FieldAccessor(this.getApplicationContext());
            fieldAccessor.setUser(user);

            Map<String, Integer> countTable = new TreeMap<>();

            List<TrackerStatusOptionDto> statusOptions = trackerManager.getStatusChoiceList(trackerId);
            List<TrackerChoiceOptionDto> severityOptions = trackerLayoutDao.findChoiceOptionsByTrackerAndLabel(trackerId, TrackerLayoutLabelDto.SEVERITY_LABEL_ID);

            Integer maxOrder = 0;

            List<TrackerItemDto> items = trackerItemDao.findByTracker(trackerId);

            for (TrackerItemDto item : items) {
                if (item.isDeleted() || item.isFolder() || item.isInformation()) continue;

                boolean skip = false;

                if (filtered) {
                    Object filterObject = fieldAccessor.getByLabel(item, filterField);

                    if (filterObject == null) {
                        skip = true;

                    } else if (filterObject instanceof String) {
                        skip = !filterValue.equals((String) filterObject);

                    } else if (filterObject instanceof NamedDto) {
                        skip = !filterValue.equals(((NamedDto) filterObject).getName());

                    } else if (filterObject instanceof List) {
                        List<NamedDto> list = (List) filterObject;
                        skip = list.get(0) != null ? !filterValue.equals(list.get(0).getName()) : true;

                    } else {
                        throw new PluginException("filter field type is not supported");
                    }
                }
                if (skip) continue;

                Object fieldValue = fieldAccessor.getByLabel(item, orderBy);
                Integer orderValue = null;

                if (fieldValue != null) {
                    try {
                        if (fieldValue instanceof Integer) {
                            orderValue = (Integer) fieldValue;

                        } else if (fieldValue instanceof List) {
                            NamedDto opt = ((List<NamedDto>) fieldValue).get(0);
                            if (opt != null) orderValue = Integer.parseInt(opt.getName());

                        } else if (fieldValue instanceof String) {
                            orderValue = Integer.parseInt(filterValue);
                        }
                    } catch (NumberFormatException e) {
                        throw new PluginException("value type is not supported");
                    }
                }

                // Row
                String status = item.getStatus() != null ? item.getStatus().getName() : "--";

                // Column 1
                String order;
                if (orderValue != null) {
                    order = String.valueOf(orderValue);
                    if (maxOrder < orderValue) maxOrder = orderValue;

                } else {
                    order = "0";
                }

                // Column 2
                String severity = item.getSeverities() != null ? (item.getSeverities().get(0) != null ? item.getSeverities().get(0).getName() : "--") : "--";

                // Count on table
                addCountOnTable(countTable, status, order, severity);
            }

            velocityContext.put("title", title);

            velocityContext.put("no_issue", items.isEmpty());

            velocityContext.put("issueStatusStyles", issueStatusStyles);

            velocityContext.put("filterField", filterField);
            velocityContext.put("filterValue", filterValue);

            // Table
            velocityContext.put("countTable", countTable);

            // Key of Row
            velocityContext.put("statuses", statusOptions);

            // Key of Column 1
            velocityContext.put("severities", severityOptions);

            // Key of Column 2
            velocityContext.put("orderBy", orderBy);
            velocityContext.put("maxOrder", maxOrder);
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new PluginException(e.getMessage());
        }
    }
}
