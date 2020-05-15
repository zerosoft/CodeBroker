package com.codebroker.extensions.request.filter;

import com.codebroker.extensions.AppListenerExtension;

/**
 * 过滤器.
 *
 * @author LongJu
 */
public interface IFilter {

    /**
     * Inits the.
     *
     * @param clientExtension the client extension
     */
    public void init(AppListenerExtension clientExtension);

    /**
     * Destroy.
     */
    public void destroy();

    /**
     * Handle client request.
     *
     * @param handlerKey      the handler key
     * @param clientExtension the client extension
     * @param object          the object
     * @return the filter action
     */
    public FilterAction handleClientRequest(int handlerKey, AppListenerExtension clientExtension, Object object);

}
