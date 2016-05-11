package com.intland.codebeamer.wiki.plugins;

import java.util.*;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.manager.TrackerItemManager;
import com.intland.codebeamer.manager.TrackerManager;
import com.intland.codebeamer.persistence.dto.*;
import com.intland.codebeamer.persistence.dto.base.NamedDto;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

public class IssueStatisticPlugin extends AbstractCodeBeamerWikiPlugin {
    private static final String FIELD_FILTER = "component";

    public class Param {
        private String title = "Issue Statistics";
        private int trackerId = 0;
        private UserDto currentUser;
        private String fieldName = "component";
        private List<String> includeList = new ArrayList<String>();
        private List<String> excludeList = new ArrayList<String>();

        public Param(Map params, UserDto currentUser) throws Exception {
            if (params == null) throw new Exception("Error in params");
            if (!params.containsKey("tracker")) throw new Exception("Should include `tracker` parameter");

            try {
                this.trackerId = Integer.parseInt((String) params.get("tracker"));
            } catch (NumberFormatException exception) {
                throw new Exception("Error format tracker id: " + exception.getMessage());
            }

            if (params.containsKey("title")) {
                this.title = (String) params.get("title");
            }
            if (params.containsKey("exclude")) {
                String excludeString = (String) params.get("exclude");
                if (excludeString.trim().length() > 0) {
                    for (String exclude : excludeString.split(",")) {
                        this.excludeList.add(exclude.trim());
                    }
                }
            }

            if (params.containsKey("field")) {
                this.fieldName = (String) params.get("field");
            } else {
                this.fieldName = FIELD_FILTER;
            }

            if (params.containsKey("include")) {
                String includeString = (String) params.get("include");
                if (includeString.trim().length() > 0) {
                    for (String include : includeString.split(",")) {
                        this.includeList.add(include.trim());
                    }
                }
            }

            this.currentUser = currentUser;
        }

        public UserDto getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(UserDto currentUser) {
            this.currentUser = currentUser;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getTrackerId() {
            return trackerId;
        }

        public void setTrackerId(int trackerId) {
            this.trackerId = trackerId;
        }

        public List<String> getIncludeList() {
            return includeList;
        }

        public void setIncludeList(List<String> includeList) {
            this.includeList = includeList;
        }

        public String getIncludeString() {
            return includeList.size() > 0 ? StringUtils.join(includeList, ",") : "*";
        }

        public List<String> getExcludeList() {
            return excludeList;
        }

        public void setExcludeList(List<String> excludeList) {
            this.excludeList = excludeList;
        }

        public String getExcludeString() {
            return excludeList.size() > 0 ? StringUtils.join(excludeList, ",") : "*";
        }
    }

    public class IssueStaticModel {
        private String componentName;
        private int count = 0;
        private int percentage = 0;

        public IssueStaticModel(String componentName, int count) {
            this.componentName = componentName;
            this.count = count;
        }

        public String getComponentName() {
            return componentName;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getPercentage() {
            return percentage;
        }

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }

        public boolean equals(Object o) {
            if (o instanceof IssueStaticModel) {
                IssueStaticModel object = (IssueStaticModel) o;
                return componentName.equalsIgnoreCase(object.getComponentName())
                        && count == object.getCount()
                        && percentage == object.getPercentage();
            } else {
                return super.equals(o);
            }
        }

        public String toString() {
            return componentName + ", " + count + ", " + percentage + "%";
        }
    }

    /**
     * Check item status in Include List and not in Exclude List
     * @param item          item to check
     * @param includeList   include list to add
     * @param excludeList   exclude list to remove
     * @return
     */
    private boolean hasInclude(TrackerItemDto item, List<String> includeList, List<String> excludeList) {
        boolean isMatch = false;
        if (item.getStatus() != null) {
            String itemStatusName = item.getStatus().getName();
            for (String include : includeList) {
                if (include.equalsIgnoreCase(itemStatusName)) {
                    isMatch = true;
                    break;
                }
            }
            for (String exclude : excludeList) {
                if (exclude.equalsIgnoreCase(itemStatusName)) {
                    isMatch = false;
                    break;
                }
            }
        }

        return isMatch;
    }

    /**
     * Calculate the percentage of each component
     * @param statisticMap     list of statistic item
     * @param total             the total component item count
     */
    public void fillPercentage(Map<String, IssueStaticModel> statisticMap, int total) {
        if (total > 0) {
            for (String key : statisticMap.keySet()) {
                int percentage = (statisticMap.get(key).getCount()*100 / total);
                statisticMap.get(key).setPercentage(percentage);
            }
        }
    }

    /**
     * find component value of an item
     * a value can be a list or text field
     * componentFieldName can be custom field or built-in field
     * for customfield :
     *      + choicefield : getChoiceList() -> get all choice -> getName
     *      + textfield : convert to string
     * for built-in field :
     *      + choicefield: try to convert the List<NamedDto> or NamedDto. If casting fail then convert to string
     * @param item                  item to find component value
     * @param componentFieldName    name of component field
     * @return                      list of component value
     */
    public ArrayList<String> findFieldComponentValue(TrackerItemDto item, String componentFieldName) {
        ArrayList<String> valueList = new ArrayList<String>();
        TrackerManager trackerManager = TrackerManager.getInstance();
        BasicLayoutDto layout = trackerManager.getBasicLayout(item.getTracker().getId());
        List<TrackerLayoutLabelDto> fields = layout.getFields();
        for (TrackerLayoutLabelDto field : fields) {
            if (field.getName().equalsIgnoreCase(componentFieldName)) {
                if (field.isChoiceField()) {
                    if (field.isUserDefined()) {
                        List<? extends NamedDto> namedList = item.getChoiceList(TrackerLayoutLabelDto.getChoiceFieldIndex(field.getId()));
                        if (namedList != null) {
                            for (NamedDto value : namedList) {
                                valueList.add(value.getName());
                            }
                        }
                    } else {
                        try {
                            if (field.getMultipleSelection() != null && field.getMultipleSelection() == true) {
                                List<? extends NamedDto> namedList = (List<? extends NamedDto>) layout.getItemFieldValueByFieldName(item, componentFieldName);
                                if (namedList != null) {
                                    for (NamedDto value : namedList) {
                                        valueList.add(value.getName());
                                    }
                                }
                            } else {
                                Object fieldValue = layout.getItemFieldValueByFieldName(item, componentFieldName);
                                if (fieldValue != null) {
                                    if (fieldValue instanceof Collection) {
                                        List<? extends NamedDto> namedList = (List<? extends NamedDto> ) fieldValue;
                                        if (namedList != null) {
                                            for (NamedDto value : namedList) {
                                                valueList.add(value.getName());
                                            }
                                        }
                                    } else {
                                        NamedDto valueName = (NamedDto) fieldValue;
                                        valueList.add(valueName.getName());
                                    }
                                }
                            }
                        } catch (Exception err) {
                            valueList.add(layout.getItemFieldValueByFieldName(item, componentFieldName).toString());
                        }
                    }
                } else {
                    if (field.isUserDefined()) {
                        valueList.add(item.getCustomField(TrackerLayoutLabelDto.getCustomFieldIndex(field.getId())));
                    } else {
                        valueList.add(layout.getItemFieldValueByFieldName(item, componentFieldName).toString());
                    }
                }
            }
        }

        return valueList;
    }

    /**
     * Init statistic map with 0 value.
     * Find all option of 'component' field in a tracker
     * @param statisticMap      map to be filled
     */
    private void fillStatisticMap(Map<String, IssueStaticModel> statisticMap, Param params) {
        TrackerManager trackerManager = TrackerManager.getInstance();
        TrackerDto trackerDto = trackerManager.findById(params.getCurrentUser(), params.getTrackerId());
        BasicLayoutDto layout = trackerManager.getBasicLayout(trackerDto.getId());

        List<TrackerLayoutLabelDto> fields = layout.getFields();
        for (TrackerLayoutLabelDto field : fields) {
            if (field.getName().equalsIgnoreCase(params.getFieldName())) {
                // With dyamic choice we don't initalize the list, because it can be hugh amount of option
                if (!field.isDynamicChoice()) {
                    Map<Object, TrackerChoiceOptionDto> optionMap = trackerManager.getTrackerChoiceOptions(trackerDto).get(field.getId());
                    if (optionMap != null) {
                        for (Object key : optionMap.keySet()) {
                            String componentName = optionMap.get(key).getName();
                            if (!statisticMap.containsKey(componentName)) {
                                statisticMap.put(componentName, new IssueStaticModel(componentName, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate count number of issue based on field
     * @param pluginParam   plugin param of this issue
     * @return              a map of field name and count(field_name)
     */
    public Map<String, IssueStaticModel> calculateNumberIssueBasedOnField(Param pluginParam) {
        TrackerItemManager trackerItemManager = TrackerItemManager.getInstance();
        TrackerManager trackerManager = TrackerManager.getInstance();

        Map<String, IssueStaticModel> statisticMap = new HashMap<String, IssueStaticModel>();
        fillStatisticMap(statisticMap, pluginParam);
        TrackerDto tracker = trackerManager.findById(pluginParam.getCurrentUser(), pluginParam.getTrackerId());
        Enumeration<TrackerItemDto> itemList = trackerItemManager.getItems(pluginParam.getCurrentUser(), tracker, null);

        // for each item in tracker, check if its status match with include/exclude list, then find
        // component value of that item. Increase count and total in the map.
        // finally calculate the percentage and output the statistic map.
        int total = 0;
        while (itemList.hasMoreElements()) {
            TrackerItemDto item = itemList.nextElement();
            if (hasInclude(item, pluginParam.getIncludeList(), pluginParam.getExcludeList())) {
                ArrayList<String> valuesOfItem = findFieldComponentValue(item, pluginParam.getFieldName());
                for (String component : valuesOfItem) {
                    if (statisticMap.containsKey(component)) {
                        int currentCount = statisticMap.get(component).getCount();
                        statisticMap.get(component).setCount(currentCount+1);
                    } else {
                        IssueStaticModel newComponent = new IssueStaticModel(component, 1);
                        statisticMap.put(component, newComponent);
                    }
                }
                total++;
            }
        }
        fillPercentage(statisticMap, total);

        return statisticMap;
    }

    public String execute(WikiContext context, Map params) {
        UserDto currentUser = getUserFromContext(context);
        VelocityContext velocityContext = getDefaultVelocityContextFromContext(context);

        Param pluginParam = null;
        try {
            pluginParam = new Param(params, currentUser);
        } catch (Exception err) {
            velocityContext.put("error", err.getMessage());
        }

        if (pluginParam != null) {
            velocityContext.put("params", pluginParam);
            Map<String, IssueStaticModel> statisticMap = calculateNumberIssueBasedOnField(pluginParam);
            velocityContext.put("statisticMap", statisticMap);
        }

        return renderPluginTemplate("issue-statistic-plugin.vm", velocityContext);
    }
}