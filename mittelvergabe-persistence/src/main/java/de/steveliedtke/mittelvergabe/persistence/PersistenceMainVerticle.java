package de.steveliedtke.mittelvergabe.persistence;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;

public class PersistenceMainVerticle extends AbstractVerticle{

	public static void main(String[] args) {
		Launcher.main(new String[] { "run", PersistenceMainVerticle.class.getName(), "-cluster" });
	}
	
	@Override
	public void start() {
		vertx.deployVerticle(MittelvergabeDao.class.getName());
	}
}
