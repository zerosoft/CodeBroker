package com.codebroker.api.internal.net;

/**
 * 消息编解码责任链
 */
public interface MessageChainOfResponsibility
{
		default void deco(int cp,byte[] bytes){


		}
}
