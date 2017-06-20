package com.codebroker.core.remote;

import java.io.Serializable;

public interface IRemoteActorMessage extends Serializable {

	public class InvokeRPC implements IRemoteActorMessage {

		private static final long serialVersionUID = 1954674398108019155L;
		/**
		 * 调用的类全名
		 */
		public final String className;
		/**
		 * 函数名
		 */
		public final String method;
		/**
		 * 参数
		 */
		public final Object[] args;

		public InvokeRPC(String className, String method, Object[] args) {
			super();
			this.className = className;
			this.method = method;
			this.args = args;
		}

	}

}
