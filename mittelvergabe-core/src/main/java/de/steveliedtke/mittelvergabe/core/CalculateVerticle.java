package de.steveliedtke.mittelvergabe.core;

import java.util.List;

import de.steveliedtke.mittelvergabe.algorithm.MittelvergabeAlgorithm;
import de.steveliedtke.mittelvergabe.algorithm.Project;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CalculateVerticle extends AbstractVerticle {

	private MittelvergabeAlgorithm algorithm;

	@Override
	public void start() {
		this.algorithm = new MittelvergabeAlgorithm();

		vertx.eventBus().consumer("mittelvergabe.calculate", (Message<JsonObject> message) -> {
			JsonObject body = message.body();
			
			final List<Project> projects = Project.parse(body.getJsonArray("projects"));
			final Double availableMoney = body.getDouble("moneyAvailable");

			algorithm.sortAndCalculateProjects(availableMoney, projects);

			final JsonArray array = new JsonArray();
			projects.stream().map(project -> project.toJson()).forEach(array::add);
			body.put("projects", array);

			message.reply(body);
		});
	}

}
