package com.architectgroup.xbeamerchart.widget.foundation;

import com.architectgroup.xbeamerchart.widget.base.XBeamerWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created by comet on 2016-04-25.
 */
public class XBeamerNumberWidget extends XBeamerWidget {
    private Integer min;
    private Integer max;
    private Float step;
    private Boolean enable;
    private Boolean readonly;

    public XBeamerNumberWidget(WikiContext ctx) {
        super(ctx, "xbeamerchart/widgets/text.vm");
    }

    public void allowSign(boolean allow){
        if(!allow && min > 0) min = 0;
    }

    public Integer getMin(){ return this.min; }

    public void setMin(int min){ this.min = min; }

    public Integer getMax(){ return this.max; }

    public void setMax(int max){ this.max = max; }

    public Float getStep(){ return this.step; }

    public void setStep(float step){ this.step = step; }

    public Boolean isEnable(){ return this.enable; }

    public void setEnable(boolean enable){ this.enable = enable; }

    public Boolean isReadonly(){ return this.readonly; }

    public void setReadonly(boolean readonly){ this.readonly = readonly; }

    @Override
    public void populateContext(VelocityContext velocityContext) {

    }

    @Nullable
    @Override
    public <T> T getSubject(@NotNull String argument) {
        return null;
    }
}
