package com.architectgroup.xbeamerchart.controller;

import com.intland.codebeamer.ajax.JsonView;
import com.intland.codebeamer.controller.AbstractJsonController;
import com.intland.codebeamer.controller.ControllerUtils;
import com.intland.codebeamer.controller.support.WikiPageControllerSupport;
import com.intland.codebeamer.manager.WikiPageManager;
import com.intland.codebeamer.persistence.dto.ReadOnlyWikiPageDto;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.intland.codebeamer.persistence.dto.WikiPageDto;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.*;

/**
 * Created by 'Hyeseong Kim<hyeseong.kim@architectgroup.com>' on 2016-03-14.
 */
@Controller
public class XBeamerChartController extends AbstractJsonController {
    private static final Logger logger = Logger.getLogger(XBeamerChartController.class);

    public static final String AJAX_CHART_CREATE_URL = "/ajax/xbeamerchart/create.spr";
    public static final String AJAX_CHART_INIT_URL = "/ajax/xbeamerchart/init.spr";
    public static final String AJAX_CHART_RESET_URL = "/ajax/xbeamerchart/reset.spr";
    @Deprecated
    public static final String AJAX_CHART_REMOVE_URL = "/ajax/xbeamerchart/remove.spr";
    public static final String AJAX_CHART_REORDER_URL = "/ajax/xbeamerchart/reorder.spr";

    public static final String REGEX_FORMAT = "\\[\\{%s id=[\\\'\\\"]?%s[\\\'\\\"]?[^\\}]*\\}\\]";

    @Autowired
    private WikiPageManager wikiPageManager;

    @Autowired
    private WikiPageControllerSupport wikiPageControllerSupport;

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @RequestMapping(value = {AJAX_CHART_CREATE_URL}, method = RequestMethod.POST)
    @ResponseBody
    public String createChart(HttpServletRequest request,
                              @RequestParam("page_id") Integer pageId,
                              @RequestParam("chart_name") String chartName){

        UserDto user = ControllerUtils.getCurrentUser(request);

        WikiPageDto page = wikiPageManager.findById(user, pageId);

        if(page instanceof ReadOnlyWikiPageDto)
            page = (WikiPageDto)page.clone();

        String markup = wikiPageControllerSupport.getWikiPageContent(request, page, page.getHeadRevision(), false);
        markup = markup.replace("[{XBeamerChartSupport}]", "[{XBeamerChartSupport\n\n\n}]");

        String newChart = "[{" + chartName + " id='" + RandomStringUtils.randomAlphanumeric(10) +"'}]";
        markup = markup.replace("\n}]", newChart + "\n}]");

        try {
            wikiPageManager.update(user, page, markup, request);
        }catch (Exception e){
            logger.error(e);
            return JsonView.SUCCESS_FALSE;
        }

        return JsonView.SUCCESS_TRUE;
    }

    @RequestMapping(value = {AJAX_CHART_INIT_URL}, method = RequestMethod.POST)
    @ResponseBody
    public String initChart(HttpServletRequest request,
                            @RequestParam("page_id") Integer pageId,
                            @RequestParam("chart_name") String chartName,
                            @RequestParam("chart_id") String chartId,
                            @RequestParam("params") String params){

        UserDto user = ControllerUtils.getCurrentUser(request);

        WikiPageDto page = wikiPageManager.findById(user, pageId);

        if(page instanceof ReadOnlyWikiPageDto)
            page = (WikiPageDto)page.clone();

        String markup = wikiPageControllerSupport.getWikiPageContent(request, page, page.getHeadRevision(), false);
        String regex = String.format(REGEX_FORMAT, chartName, chartId);

        String initMarkup = "[{" + chartName + " id=" + chartId;
        if(params != null && !params.isEmpty())
            initMarkup += params;
        initMarkup += "}]";

        //markup = markup.replaceFirst(regex, initMarkup); // not work :(

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(markup);
        if(matcher.find()){
            markup = matcher.replaceFirst(initMarkup);
        }else {
            return JsonView.SUCCESS_FALSE;
        }

        try {
            wikiPageManager.update(user, page, markup, request);
        }catch (Exception e){
            logger.error(e);
            return JsonView.SUCCESS_FALSE;
        }

        return JsonView.SUCCESS_TRUE;
    }

    @RequestMapping(value = {AJAX_CHART_RESET_URL}, method = RequestMethod.POST)
    @ResponseBody
    public String resetChart(HttpServletRequest request,
                            @RequestParam("page_id") Integer pageId,
                            @RequestParam("chart_name") String chartName,
                            @RequestParam("chart_id") String chartId){

        UserDto user = ControllerUtils.getCurrentUser(request);

        WikiPageDto page = wikiPageManager.findById(user, pageId);

        if(page instanceof ReadOnlyWikiPageDto)
            page = (WikiPageDto)page.clone();

        String markup = wikiPageControllerSupport.getWikiPageContent(request, page, page.getHeadRevision(), false);
        String regex = String.format(REGEX_FORMAT, chartName, chartId);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(markup);
        if(matcher.find()){
            markup = matcher.replaceFirst("[{"+chartName+" id="+chartId+"}]");
        }else {
            return JsonView.SUCCESS_FALSE;
        }

        try {
            wikiPageManager.update(user, page, markup, request);
        }catch (Exception e){
            logger.error(e);
            return JsonView.SUCCESS_FALSE;
        }

        //return JsonView.SUCCESS_TRUE;
        return JsonView.SUCCESS_TRUE;
    }

    @Deprecated
    @RequestMapping(value = {AJAX_CHART_REMOVE_URL}, method = RequestMethod.POST)
    @ResponseBody
    public String removeChart(HttpServletRequest request,
                              @RequestParam("page_id") Integer pageId,
                              @RequestParam("chart_name") String chartName,
                              @RequestParam("chart_id") String chartId){

        UserDto user = ControllerUtils.getCurrentUser(request);

        WikiPageDto page = wikiPageManager.findById(user, pageId);

        if(page instanceof ReadOnlyWikiPageDto)
            page = (WikiPageDto)page.clone();

        String markup = wikiPageControllerSupport.getWikiPageContent(request, page, page.getHeadRevision(), false);
        String regex = String.format(REGEX_FORMAT, chartName, chartId);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(markup);

        if(matcher.find()){
            markup = matcher.replaceFirst("");
        }else {
            return JsonView.SUCCESS_FALSE;
        }

        try {
            wikiPageManager.update(user, page, markup, request);
        }catch (Exception e){
            logger.error(e);
            return JsonView.SUCCESS_FALSE;
        }

        return JsonView.SUCCESS_TRUE;
    }

    @RequestMapping(value = {AJAX_CHART_REORDER_URL}, method = RequestMethod.POST)
    @ResponseBody
    public String reorderChart(HttpServletRequest request,
                               @RequestParam("page_id") Integer pageId,
                               @RequestParam("tags") String tags){

        UserDto user = ControllerUtils.getCurrentUser(request);

        WikiPageDto page = wikiPageManager.findById(user, pageId);

        if(page instanceof ReadOnlyWikiPageDto)
            page = (WikiPageDto)page.clone();

        String oldMarkup = wikiPageControllerSupport.getWikiPageContent(request, page, page.getHeadRevision(), false);

        String markup = "[{XBeamerChartSupport\n\n";
        for(String tag : tags.split(",")){
            String chartName = tag.split("-")[0];
            String chartId   = tag.split("-")[1];

            String regex = String.format(REGEX_FORMAT, chartName, chartId);

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(oldMarkup);

            markup += matcher.group(1) + "\n";
        }
        markup += "}]";

        try {
            wikiPageManager.update(user, page, markup, request);
        }catch (Exception e) {
            logger.error(e);
            return JsonView.SUCCESS_FALSE;
        }

        return JsonView.SUCCESS_TRUE;
    }
}
