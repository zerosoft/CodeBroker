FROM zerosoft/new_game_world:0.1

MAINTAINER zerosoft

COPY ./CodeBroker/build/libs/  /home/game/server/libs
COPY ./CodeBroker/build/lib/  /home/game/server/lib
COPY ./account_server/build/libs/  /home/game/server/libs
COPY ./account_server/build/lib/  /home/game/server/lib
WORKDIR /home/game/server/libs
RUN pwd
RUN ls

WORKDIR /home/game/server/lib
RUN pwd
RUN ls

#RUN javac TestMain.java
#CMD ["java", "com.kunlun.game.server.TestMain"]

RUN ls
