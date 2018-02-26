package de.steveliedtke.mittelvergabe.http;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class HttpVerticle extends AbstractVerticle {

	private static final Logger log = LoggerFactory.getLogger(HttpVerticle.class);

	@Override
	public void start(Future<Void> startFuture) {

		int port = config().getInteger("mittelvergabePort", 80).intValue();

		HttpServer httpServer = vertx.createHttpServer();

		Router router = Router.router(vertx);

		router.post().handler(BodyHandler.create());
		router.post("/calculate").handler(this::handleCalculate);
		router.post("/persist").handler(this::persistResult);
		router.get("/mittelvergabe/:name").handler(this::getMittelvergabe);

		// SockJS Bridge Code
		BridgeOptions bridgeOptions = new BridgeOptions()
				.addInboundPermitted(new PermittedOptions().setAddress("mittelvergabe.calculate"));
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx).bridge(bridgeOptions);
		router.route("/eventbus/*").handler(sockJSHandler);

		// for html, js, css
		router.route().handler(StaticHandler.create());

		httpServer.requestHandler(router::accept).listen(port, ar -> {
			if (ar.succeeded()) {
				log.info("HttpServer started on port "+port);
				startFuture.complete();
			} else {
				log.error("Failed starting HttpServer", ar.cause());
				startFuture.fail(ar.cause());
			}
		});
	}

	private void handleCalculate(RoutingContext context) {
		JsonObject bodyAsJson = context.getBodyAsJson();
		vertx.eventBus().send("mittelvergabe.calculate", bodyAsJson, asyncResult -> {
			if (asyncResult.failed()) {
				context.response().setStatusCode(400).putHeader("content-type", "text/plain").end();
			} else {
				context.response().putHeader("content-type", "application/json")
						.end(asyncResult.result().body().toString());
			}
		});
	}

	private void persistResult(RoutingContext context) {
		JsonObject bodyAsJson = context.getBodyAsJson();
		vertx.eventBus().send("mittelvergabe.persist", bodyAsJson, asyncResult -> {
			if (asyncResult.failed()) {
				context.response().setStatusCode(400).putHeader("content-type", "text/plain").end();
			} else {
				context.response().end();
			}
		});
	}

	private void getMittelvergabe(RoutingContext context) {
		String requested = context.request().getParam("name");
		vertx.eventBus().send("mittelvergabe.fetch", requested, asyncResult -> {
			if (asyncResult.failed()) {
				context.response().setStatusCode(400).putHeader("content-type", "text/plain").end();
			} else {
				context.response().putHeader("content-type", "application/json")
						.end(asyncResult.result().body().toString());
			}
		});
	}
}
