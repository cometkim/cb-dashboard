package com.architectgroup.xbeamerchart.widget.foundation;

import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.Config;
import com.intland.codebeamer.persistence.dao.PaginatedDtoList;
import com.intland.codebeamer.persistence.dao.TrackerItemDao;
import com.intland.codebeamer.persistence.dto.TrackerItemDto;
import com.intland.codebeamer.persistence.dto.UserDto;
import com.intland.codebeamer.search.query.antlr.CbQLQueryHandler;
import com.intland.codebeamer.search.query.antlr.QueryContext;
import com.intland.codebeamer.search.query.antlr.impl.exception.CbQLException;
import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-03-21.
 */
public class XBeamerQueryConditionWidget extends XBeamerWidget {
    @Autowired
    @Qualifier("TRACKER_ITEM")
    private CbQLQueryHandler<TrackerItemDto> cbQLQueryHandler;

    @Autowired
    private TrackerItemDao trackerItemDao;

    public XBeamerQueryConditionWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/QueryCondition-widget.vm");
        this.cssStyle("width", "100%");
        this.cssStyle("float", "none");
        this.cssStyle("margin-bottom", "20px");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
    }

    @Override
    public List<TrackerItemDto> getObject(String paramValue){
        UserDto user = this.getUser();

        int pageLength = Config.getItemsPerPage();
        int pageNo = 0;

        QueryContext queryContext = new QueryContext();
        queryContext.addParameter(QueryContext.PARAM_TYPE.USER, user);

        String cbQl = paramValue;

        List<TrackerItemDto> items;

        cbQl = cbQl.replace("^", "'");

        PaginatedDtoList<TrackerItemDto> paginatedItems;

        try {
            paginatedItems = cbQLQueryHandler.findByCbQL(cbQl, queryContext, pageNo++, pageLength);
        } catch (CbQLException exception) {
            paginatedItems = new PaginatedDtoList(0, 0, Collections.emptyList(), 0);
        }

        items = paginatedItems.getList();

        while(paginatedItems.hasNextPage()) {
            paginatedItems = cbQLQueryHandler.findByCbQL(cbQl, queryContext, pageNo++, pageLength);
            items.addAll(paginatedItems.getList());
        }

        return items;
    }
}
