package com.codebroker.api;

import com.codebroker.api.event.Event;

public interface NPCControl {
    /**
     * 初始化
     */
    public void init();

    /**
     * 执行
     */
    public void execute(Event event);

    /**
     * 销毁
     */
    public void destroy();
}
