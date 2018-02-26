package de.steveliedtke.mittelvergabe.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Launcher;

public class MainVerticle extends AbstractVerticle {

	/**
	 * Main for running vertx module in eclipse.
	 * 
	 * @param args
	 *            -
	 */
	public static void main(String[] args) {
		Launcher.main(new String[] { "run", MainVerticle.class.getName(), "-cluster" });
	}

	@Override
	public void start(Future<Void> startFuture) {

		vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(8), ar2 -> {
			if (ar2.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(ar2.cause());
			}
		});

	}
}
