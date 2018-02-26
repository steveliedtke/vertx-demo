package de.steveliedtke.mittelvergabe.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;

public class MainVerticle extends AbstractVerticle {
	
	public static void main(String[] args) {
		Launcher.main(new String[] {"run", MainVerticle.class.getName(), "-cluster"});
	}

	@Override
	public void start() {
		vertx.deployVerticle(CalculateVerticle.class.getName());
	}
}
