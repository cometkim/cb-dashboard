package com.intland.codebeamer.wiki.plugins;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerWrapperPlugin;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;

import java.util.LinkedHashMap;
import java.util.Map;
public class XBeamerTrackerListPlugin extends XBeamerWrapperPlugin {


    @Override
    public String getChartName() {
        return null;
    }

    @Override
    public String getChartDescription() {
        return null;
    }

    @Override
    public String getImgUrl() {
        return null;
    }

    private XBeamerWidget itemWidget;
    private XBeamerWidget sortingWidget;
    private XBeamerWidget typeWidget;
    private XBeamerWidget typesExcludedWidget;
    private XBeamerWidget layoutWidget;

    @Override
    protected void initParameterWidgets() {

        WikiContext context = this.getWikiContext();
        Map<String,String> options=new LinkedHashMap<>();
        options.put("name","Name");
        options.put("key","Key");
        options.put("last modified","Last Modified");
        options.put("type","Type");

        Map<String, String> items = new LinkedHashMap<>();
        items.put("work", "Work Items");
        items.put("configuration", "CMDB");
        items.put("both", "Both");

        Map<String,String> types=new LinkedHashMap<>();
        types.put("Bugs","Bug");
        types.put("Change Requests","Change Request");
        types.put("Components","Component");
        //types.put("Configuration Items","Configuration Item");
        types.put("Contacts","Contact");
        types.put("Issues","issue");
        types.put("Platforms","Platform");
        types.put("Releases","Release");
        types.put("Requirements","Requirement");
        types.put("Risks","Risk");
        types.put("Tasks","Task");
        types.put("Teams","Team");
        types.put("Test Cases","Test Case");
        types.put("Test Configurations","Test Configuration");
        types.put("Test Runs","Test Run");
        types.put("Test Sets","Test Set");
        types.put("Test Trackers","Test Trackers");
        //types.put("Time Recordings","Time Recording");
        types.put("User Stories","User Story");

        Map<String,String> layout=new LinkedHashMap<>();
        layout.put("true","True");
        layout.put("false","False");

    }

    @Override
    protected Class<TrackerListPlugin> getOriginPlugin() {
        return TrackerListPlugin.class;
    }
}