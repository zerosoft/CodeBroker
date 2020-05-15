package com.codebroker.extensions.request;

import com.codebroker.extensions.AppListenerExtension;
import com.codebroker.extensions.request.filter.ClientExtensionFilter;
import com.codebroker.extensions.request.filter.IFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientExtensionFilterChain implements IFilterChain {

    private final Collection<ClientExtensionFilter> filters = new ConcurrentLinkedQueue<ClientExtensionFilter>();

    private final AppListenerExtension player;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ClientExtensionFilterChain(AppListenerExtension clientExtension) {
        this.player = clientExtension;
    }

    public void addFilter(int filterId, ClientExtensionFilter filter) {
        if (filters.contains(filter)) {
            // throw new
            // ALawsRuntimeException("A filter with the same name already
            // exists: "
            // + filterName + ", Ext: " + player);
        } else {
            filter.setFilterId(filterId);
            // filter.init(parentExtension);
            filters.add(filter);
            return;
        }
    }


    public void remove(int filterId) {
        for (Iterator<ClientExtensionFilter> it = filters.iterator(); it.hasNext(); ) {
            ClientExtensionFilter filter = it.next();
            if (filter.getFilterId() == filterId) {
                it.remove();
                break;
            }
        }

    }


    public int size() {
        return filters.size();
    }

    public void destroy() {
        ClientExtensionFilter filter;
        for (Iterator<ClientExtensionFilter> iterator = filters.iterator(); iterator.hasNext(); filter.destroy()) {
            filter = iterator.next();
        }
        filters.clear();
    }

}
