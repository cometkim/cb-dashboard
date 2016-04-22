package com.architectgroup.xbeamerchart.widget;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import com.intland.codebeamer.persistence.dto.UserDto;
import org.apache.velocity.VelocityContext;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-12.
 */
public class XBeamerBasicInputWidget extends XBeamerWidget {
    public enum Type{
        TEXT("text"), NUMBER("number");

        private String type;

        Type(String type){
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public boolean isText(){
            return this == TEXT;
        }

        public boolean isNumber(){
            return this == NUMBER;
        }
    }

    private Type type;

    public XBeamerBasicInputWidget(WikiContext context, Type type) {
        super(context, "xbeamerchart/widgets/BasicInput-widget.vm");
        this.type = type;
        this.cssStyle("width", "200px");
        this.cssStyle("height", "50px");
    }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        velocityContext.put("type", this.type.getType());
    }

    @Override
    public @Nullable <T> T getSubject(@NotNull String argument){
        if(this.type.isText())
            return (T)argument;

        else if(this.type.isNumber())
            return (T)Integer.valueOf(argument);

        return null;
    }
}
