package com.codebroker.core.actortype.message;

import com.fasterxml.jackson.annotation.JsonCreator;

public interface IService {

    final class Init implements IService {
        public Object object;

        public Init(Object obj) {
            this.object=obj;
        }
    }

    final class Destroy implements IService {
        public Object object;

        public Destroy(Object obj) {
            this.object=obj;
        }
    }


    final class HandleMessage implements IService {

        public Object object;
        @JsonCreator
        public HandleMessage(Object obj) {
            this.object=obj;
        }
    }
}
