package com.codebroker.extensions.request;

import com.codebroker.api.IHandlerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandlerFactory<T,C> implements IHandlerFactory<T,C> {

    private final Map<T, Class<C>> handlers = new ConcurrentHashMap();

    private final Map<T, C> cachedHandlers = new ConcurrentHashMap();

    public void addHandler(T handlerKey, Class<C> handlerClass) {
        handlers.put(handlerKey, handlerClass);
    }

    public void addHandler(T handlerKey, C requestHandler) {
        cachedHandlers.put(handlerKey, requestHandler);
    }

    public synchronized void clearAll() {
        handlers.clear();
        cachedHandlers.clear();
    }

    public synchronized void removeHandler(T handlerKey) {
        handlers.remove(handlerKey);
        if (cachedHandlers.containsKey(handlerKey)) {
            cachedHandlers.remove(handlerKey);
        }
    }

    public  Optional<C> findHandler(T handlerKey)  {
        C handler = cachedHandlers.get(handlerKey);
        if (handler != null) {
            return Optional.of(handler);
        }

        Class<C> handlerClass = handlers.get(handlerKey);
        if (handlerClass == null) {
            return Optional.empty();
        }

        try {
            handler = handlerClass.newInstance();
        } catch (InstantiationException |IllegalAccessException e) {

            return Optional.empty();
        }
        cachedHandlers.put(handlerKey, handler);
        return Optional.of(handler);
    }

}
