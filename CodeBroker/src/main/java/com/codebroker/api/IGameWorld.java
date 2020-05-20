package com.codebroker.api;


import java.util.Optional;

public interface IGameWorld {

	Optional<IGameUser> findIGameUserById(String id);

}
