package com.codebroker.extensions.request.filter;

import com.codebroker.extensions.AppListenerExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * 过滤链.
 *
 * @author LongJu
 */
public interface IFilterChain {

    Logger log = LoggerFactory.getLogger(IFilterChain.class);

    Collection<ClientExtensionFilter> filters = new ConcurrentLinkedQueue<>();

    /**
     * 默认过滤器函数
     * @param requestId
     * @param player
     * @param params
     * @return
     */
    default FilterAction runRequestInChain(int requestId, AppListenerExtension player, Object params) {
        FilterAction filterAction = FilterAction.CONTINUE;
        for (Iterator<ClientExtensionFilter> iterator = filters.iterator(); iterator.hasNext(); ) {
            ClientExtensionFilter filter = iterator.next();
            try {
                if (filterAction == FilterAction.HALT) {
                    break;
                }
                filterAction = filter.handleClientRequest(requestId, player, params);
            } catch (Exception e) {
                log.warn(String.format("Exception in FilterChain execution:%s --- Filter: %s, Req: %s, Ext: %s",
                        new Object[]{e.toString(), filter.getFilterId(), requestId, player}));
            }
        }

        return filterAction;
    }

    void addFilter(int filterId, ClientExtensionFilter clientExtensionFilter);

    void remove(int filterId);

    int size();

    void destroy();
}
