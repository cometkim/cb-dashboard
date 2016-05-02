package com.architectgroup.xbeamerchart.plugin.base;

import com.architectgroup.xbeamerchart.plugin.base.XBeamerPlugin;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.intland.codebeamer.wiki.CodeBeamerWikiContext;
import com.intland.codebeamer.wiki.WikiMarkupProcessor;
import com.intland.codebeamer.wiki.plugins.base.AbstractCodeBeamerWikiPlugin;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Hyeseong Kim <hyeseong.kim@architectgroup.com> on 2016-04-12.
 */
public abstract class XBeamerWrapperPlugin extends XBeamerPlugin{
    @Autowired
    private WikiMarkupProcessor wikiMarkupProcessor;

    public XBeamerWrapperPlugin(){
        this.setFrame(false);

        this.setShapeColspan(5);
        this.setShapeRowspan(3);
    }

    @Override
    protected final String execute() throws PluginException {
        String markup = "[{" + this.getOriginPlugin().getSimpleName();
        for(String param : this.widgets.keySet()) {
            String paramValue = this.widgets.get(param).getValue();
            if(paramValue != null || paramValue.isEmpty())
                markup += " " + param + "='" + paramValue + "'";
        }
        markup += "}]";
        return this.renderMarkup(markup);
    }

    protected abstract Class<? extends AbstractCodeBeamerWikiPlugin> getOriginPlugin();

    /**
     * @param markup
     * @return HTML
     */
    protected String renderMarkup(String markup){
        return wikiMarkupProcessor.transformToHtml(markup, "W", false, false, (CodeBeamerWikiContext)this.getWikiContext());
    }
}
