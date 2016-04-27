package com.architectgroup.xbeamerchart.widget;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.persistence.dao.UserDao;
import com.intland.codebeamer.persistence.dto.UserDto;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerUserWidget extends XBeamerWidget{
    @Autowired
    private UserDao userDao;

    private boolean multiple;

    public XBeamerUserWidget(WikiContext ctx, boolean multiple) {
        super(ctx, "xbeamerchart/widgets/user.vm");
        this.multiple = multiple;

        this.cssStyle("width", "100%");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        velocityContext.put("currentUser", this.getUser());
        velocityContext.put("users", userDao.findAll());
        velocityContext.put("multiple", multiple);
    }

    @Nullable
    @Override
    public List<UserDto> getSubject(@NotNull String argument) {
        return userDao.findById(Arrays.asList(argument.split(",")));
    }
}
