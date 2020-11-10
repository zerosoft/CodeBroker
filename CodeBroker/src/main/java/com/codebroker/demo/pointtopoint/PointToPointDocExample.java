package com.codebroker.demo.pointtopoint;

import akka.actor.typed.ActorSystem;

public class PointToPointDocExample {


	public static void main(String[] args) {
		ActorSystem.create(Guardian.create(), "FibonacciExample");
	}
}