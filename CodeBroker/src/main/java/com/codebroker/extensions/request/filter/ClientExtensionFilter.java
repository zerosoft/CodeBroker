package com.codebroker.extensions.request.filter;

import com.codebroker.extensions.AppListenerExtension;


public abstract class ClientExtensionFilter implements IFilter {

    protected AppListenerExtension appListenerExtension;

    private int filterId;

    public void init(AppListenerExtension player) {
        this.appListenerExtension = player;
    }

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

}
