package com.codebroker.extensions.request.filter;

/**
 * 过滤链条的下面是否执行.
 *
 * @author LongJu
 * @Code{FilterAction.HALT}挂起状态，对应的handler不执行
 */
public enum FilterAction {

    /**
     * 继续
     */
    CONTINUE,
    /**
     * 停止
     */
    HALT;
}
