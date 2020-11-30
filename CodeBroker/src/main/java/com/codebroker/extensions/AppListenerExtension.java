package com.codebroker.extensions;

import com.codebroker.api.AppListener;
import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IHandlerFactory;
import com.codebroker.api.IGameUser;
import com.codebroker.exception.CodeBrokerException;
import com.codebroker.extensions.request.ClientExtensionFilterChain;
import com.codebroker.extensions.request.ClientHandlerFactory;
import com.codebroker.extensions.request.filter.ClientExtensionFilter;
import com.codebroker.extensions.request.filter.FilterAction;
import com.codebroker.extensions.request.filter.IFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 请求拓展接口.
 *
 * @author LongJu
 */
public abstract class AppListenerExtension implements AppListener {

    private final IHandlerFactory handlerFactory = new ClientHandlerFactory();
    private final IFilterChain filterChain = new ClientExtensionFilterChain(this);
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String name;

    public void destroy(Object obj) {
        logger.info("AppListenerExtension destroy start");
        handlerFactory.clearAll();
        filterChain.destroy();
        logger.info("AppListenerExtension destroy end");
    }

    public void addRequestHandler(int requestId, Class<?> theClass) {
        if (!(IClientRequestHandler.class).isAssignableFrom(theClass)) {
             throw new CodeBrokerException(String.format("Provided Request Handler does not implement IClientRequestHandler: %s, Cmd: %s",
             new Object[] {theClass, requestId }));
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
            Optional<Object> handler =  handlerFactory.findHandler(requestId);
            if (!handler.isPresent()) {
                logger.info("handler is no found" + requestId);
                return;
            }
            ((IClientRequestHandler) handler.get()).handleClientRequest(user, params);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("IClientRequestHandler error",e);
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

    @Override
    public Object handleBackMessage(Object obj) {
        return null;
    }
}
