package com.architectgroup.xbeamerchart.widget;

import com.architectgroup.xbeamerchart.widget.base.XBeamerBasicWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created by comet on 2016-04-25.
 */
public class XBeamerNumberWidget extends XBeamerBasicWidget {
    private Integer min;
    private Integer max;
    private Float step;

    private boolean disable = false;
    private boolean readonly = false;

    public XBeamerNumberWidget(WikiContext ctx) {
        super(ctx, "input");
        this.attribute("type", "number");
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

    public Boolean isDisable(){ return this.disable; }

    public void setDisable(boolean disable){ this.disable = disable; }

    public Boolean isReadonly(){ return this.readonly; }

    public void setReadonly(boolean readonly){ this.readonly = readonly; }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        this.attribute("min", this.min);
        this.attribute("max", this.max);
        this.attribute("step", this.step);

        if(this.disable) this.attribute("disable", "disable");
        if(this.readonly) this.attribute("readonly", "readonly");

        super.populateContext(velocityContext);
    }
}
