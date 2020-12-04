FROM zerosoft/new_game_world:latest
MAINTAINER zerosoft

COPY ./build/libs/GameServer-1.0-SNAPSHOT.jar /home/runtime/extensions/GameServer/

WORKDIR /home/runtime

RUN pwd

CMD ["sh", "start.sh"]

CMD /bin/bash

EXPOSE 9551
EXPOSE 22334