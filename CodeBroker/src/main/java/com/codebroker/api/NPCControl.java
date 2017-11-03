package com.codebroker.api;

import com.codebroker.core.data.IObject;

public interface NPCControl {
    /**
     * 初始化
     */
    void init();

    /**
     * 执行
     */
    void execute(IObject event);

    /**
     * 销毁
     */
    void destroy();
}
