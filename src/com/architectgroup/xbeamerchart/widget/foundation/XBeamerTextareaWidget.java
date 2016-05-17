package com.architectgroup.xbeamerchart.widget.foundation;

import com.architectgroup.xbeamerchart.widget.base.XBeamerBasicWidget;
import com.ecyrd.jspwiki.WikiContext;
import org.apache.velocity.VelocityContext;

/**
 * Created by comet on 2016-04-26.
 */
public class XBeamerTextareaWidget extends XBeamerBasicWidget {
    private int cols;
    private int rows;

    private boolean disable = false;
    private boolean readonly = false;

    public XBeamerTextareaWidget(WikiContext ctx, int cols, int rows){
        super(ctx, "textarea");
        this.cols = cols;
        this.rows = rows;
    }

    public boolean isDisable(){ return this.disable; }

    public void setDisable(boolean disable){ this.disable = disable; }

    public boolean isReadonly(){ return this.readonly; }

    public void setReadonly(boolean readonly){ this.readonly = readonly; }

    public int getCols(){ return this.cols; }

    public int getRows(){ return this.rows; }

    @Override
    public void populateContext(VelocityContext velocityContext) {
        this.attribute("cols", this.cols);
        this.attribute("rows", this.rows);

        if(this.disable) this.attribute("disable", "disable");
        if(this.readonly) this.attribute("readonly", "readonly");

        super.populateContext(velocityContext);
    }
}
