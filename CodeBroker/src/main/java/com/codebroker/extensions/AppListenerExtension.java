package com.codebroker.extensions;

import com.codebroker.api.CodeBrokerAppListener;
import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IHandlerFactory;
import com.codebroker.api.IGameUser;
import com.codebroker.extensions.request.ClientExtensionFilterChain;
import com.codebroker.extensions.request.ClientHandlerFactory;
import com.codebroker.extensions.request.filter.ClientExtensionFilter;
import com.codebroker.extensions.request.filter.FilterAction;
import com.codebroker.extensions.request.filter.IFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
/**
 * 请求拓展接口.
 *
 * @author LongJu
 */
public abstract class AppListenerExtension implements CodeBrokerAppListener {

    private final IHandlerFactory handlerFactory = new ClientHandlerFactory();
    private final IFilterChain filterChain = new ClientExtensionFilterChain(this);
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String name;

    public void destroy(Object obj) {
        handlerFactory.clearAll();
        filterChain.destroy();
    }

    public void addRequestHandler(int requestId, Class<?> theClass) {
        if (!(IClientRequestHandler.class).isAssignableFrom(theClass)) {
            // throw new
            // ALawsRuntimeException(String.format("Provided Request Handler
            // does not implement IClientRequestHandler: %s, Cmd: %s",
            // new Object[] {theClass, requestId }));
        } else {
            logger.info("add handler id {} class {}", requestId, theClass);
            handlerFactory.addHandler(requestId, theClass);
        }
    }

    protected void addRequestHandler(int requestId, IClientRequestHandler requestHandler) {
        handlerFactory.addHandler(requestId, requestHandler);
    }

    protected void removeRequestHandler(int requestId) {
        handlerFactory.removeHandler(requestId);
    }

    protected void clearAllHandlers() {
        handlerFactory.clearAll();
    }

    public void handleClientRequest(IGameUser user, int requestId, Object params) {
        if (filterChain.size() > 0 && filterChain.runRequestInChain(requestId, this, params) == FilterAction.HALT) {
            return;
        }
        try {
            IClientRequestHandler handler = (IClientRequestHandler) handlerFactory.findHandler(requestId);
            if (handler == null) {
                logger.info("hander is no found" + requestId);
            }
            handler.handleClientRequest(user, params);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public final void addFilter(int filterId, ClientExtensionFilter filter) {
        filterChain.addFilter(filterId, filter);
    }

    public void removeFilter(int filterId) {
        filterChain.remove(filterId);
    }

    public void clearFilters() {
        filterChain.destroy();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void handleMessage(Object obj) {
    }

}
