package com.codebroker.api;

/**
 * 世界Actor封装
 *
 * @author zero
 */
public interface IWorld {
    /**
     * 创建NPC
     *
     * @param npcId
     * @param control
     */
    public void createNPC(String npcId, NPCControl control);
}
