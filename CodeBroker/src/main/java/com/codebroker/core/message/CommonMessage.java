package com.codebroker.core.message;

import java.io.Serializable;

public class CommonMessage implements Serializable {

    /**
     * 初始化系统
     *
     * @author zero
     */
    public static class Start implements Serializable {
        private static final long serialVersionUID = 6462859024035662121L;
    }

    /**
     * 关闭系统
     *
     * @author zero
     */
    public static class Close implements Serializable {
        private static final long serialVersionUID = 806701713038586180L;
    }

    /**
     * 重启系统
     *
     * @author zero
     */
    public static class Restart implements Serializable {
        private static final long serialVersionUID = 806701713038586180L;
    }
}
