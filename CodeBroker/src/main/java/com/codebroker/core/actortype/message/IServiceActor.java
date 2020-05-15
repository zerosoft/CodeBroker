package com.codebroker.core.actortype.message;

public interface IServiceActor {
    final class Init implements IServiceActor {
        public Object object;

        public Init(Object obj) {
            this.object=obj;
        }
    }
    final class Destroy implements IServiceActor {
        public Object object;

        public Destroy(Object obj) {
            this.object=obj;
        }
    }

    final class HandleMessage implements IServiceActor {
        public Object object;

        public HandleMessage(Object obj) {
            this.object=obj;
        }
    }
}
