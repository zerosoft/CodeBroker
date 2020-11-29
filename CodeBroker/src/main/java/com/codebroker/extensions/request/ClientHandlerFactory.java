package com.codebroker.extensions.request;

import com.codebroker.api.IHandlerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandlerFactory<T> implements IHandlerFactory<T> {

    private final Map<T, Class<?>> handlers = new ConcurrentHashMap();

    private final Map<T, Object> cachedHandlers = new ConcurrentHashMap();

    public void addHandler(T handlerKey, Class<?> handlerClass) {
        handlers.put(handlerKey, handlerClass);
    }

    public void addHandler(T handlerKey, Object requestHandler) {
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

    public  Optional<Object> findHandler(T handlerKey)  {
        Object handler = cachedHandlers.get(handlerKey);
        if (handler != null) {
            return Optional.of(handler);
        }

        Class<?> handlerClass = handlers.get(handlerKey);
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
