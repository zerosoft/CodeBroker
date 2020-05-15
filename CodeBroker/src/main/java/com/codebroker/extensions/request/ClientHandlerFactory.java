package com.codebroker.extensions.request;

import com.codebroker.api.IHandlerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandlerFactory implements IHandlerFactory {

    private final Map<Integer, Class<?>> handlers = new ConcurrentHashMap<Integer, Class<?>>();

    private final Map<Integer, Object> cachedHandlers = new ConcurrentHashMap<Integer, Object>();

    public void addHandler(int handlerKey, Class<?> handlerClass) {
        handlers.put(handlerKey, handlerClass);
    }

    public void addHandler(int handlerKey, Object requestHandler) {
        cachedHandlers.put(handlerKey, requestHandler);
    }

    public synchronized void clearAll() {
        handlers.clear();
        cachedHandlers.clear();
    }

    public synchronized void removeHandler(int handlerKey) {
        handlers.remove(handlerKey);
        if (cachedHandlers.containsKey(handlerKey)) {
            cachedHandlers.remove(handlerKey);
        }
    }

    public Object findHandler(int handlerKey) throws InstantiationException, IllegalAccessException {
        Object handler = getHandlerInstance(handlerKey);
        if (handler == null) {
            handler = getHandlerInstance(handlerKey);
        }
        return handler;
    }

    private Object getHandlerInstance(int handlerKey) throws InstantiationException, IllegalAccessException {
        Object handler = cachedHandlers.get(handlerKey);
        if (handler != null) {
            return handler;
        }

        Class<?> handlerClass = handlers.get(handlerKey);
        if (handlerClass == null) {
            return null;
        }
        handler = handlerClass.newInstance();
        cachedHandlers.put(handlerKey, handler);
        return handler;
    }

}
