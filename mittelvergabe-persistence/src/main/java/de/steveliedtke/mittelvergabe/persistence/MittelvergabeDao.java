package de.steveliedtke.mittelvergabe.persistence;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

public class MittelvergabeDao extends AbstractVerticle {

	@Override
	public void start() {
		vertx.eventBus().consumer("mittelvergabe.persist", this::create);
		vertx.eventBus().consumer("mittelvergabe.fetch", this::findByName);
	}

	private LocalMap<String, JsonObject> getDatabase() {
		SharedData sharedData = vertx.sharedData();
		LocalMap<String, JsonObject> localMap = sharedData.getLocalMap("mittelvergabeData");
		return localMap;
	}

	private void create(Message<JsonObject> message) {
		LocalMap<String, JsonObject> localMap = getDatabase();
		JsonObject requestBody = message.body();
		localMap.put(requestBody.getString("name"), requestBody);
		message.reply(null);

//		vertx.sharedData().<String, JsonObject>getAsyncMap("mittelvergabeData", res -> {
//			if (res.succeeded()) {
//				AsyncMap<String, JsonObject> asyncMap = res.result();
//				asyncMap.put(requestBody.getString("name"), requestBody, resPut -> {
//					// handle failure or success
//				});
//				message.reply(null);
//			} else {
//				// Something went wrong!
//			}
//		});
	}

	private void findByName(Message<String> message) {
		LocalMap<String, JsonObject> localMap = getDatabase();
		JsonObject mittelvergabe = localMap.get(message.body());
		if (mittelvergabe == null) {
			message.fail(1, "Unknown Mittelvergabe");
		} else {
			message.reply(mittelvergabe);
		}
	}
}
