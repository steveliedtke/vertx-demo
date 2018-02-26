package de.steveliedtke.mittelvergabe.algorithm;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Project implements Comparable<Project> {

	private String name;

	private double moneyNeeded;

	private double moneyAlready;

	private double moneyAdded;

	private int votes;

	public Project(final JsonObject json) {
		if (!json.containsKey("name") || !json.containsKey("moneyNeeded") || !json.containsKey("moneyAlready")
				|| !json.containsKey("votes")) {
			throw new IllegalArgumentException(
					"The params 'name', 'moneyNeeded', 'moneyAdded' and 'votes' are mandatory");
		}
		this.name = json.getString("name");
		this.moneyNeeded = json.getDouble("moneyNeeded");
		this.moneyAlready = json.getDouble("moneyAlready");
		this.votes = json.getInteger("votes");
		this.moneyAdded = 0.0;
	}

	public JsonObject toJson() {
		final JsonObject json = new JsonObject();
		json.put("name", this.name);
		json.put("moneyNeeded", this.moneyNeeded);
		json.put("moneyAlready", this.moneyAlready);
		json.put("votes", this.votes);
		json.put("moneyAdded", this.moneyAdded);
		return json;
	}

	@Override
	public int compareTo(final Project compareProject) {
		int result;
		if (this.votes < compareProject.getVotes())
			result = 1;
		else if (this.votes > compareProject.getVotes())
			result = -1;
		else
			result = 0;

		return result;
	}

	public static List<Project> parse(JsonArray array) {
		final List<Project> projects = array.stream().map(jsonObject -> new Project((JsonObject) jsonObject))
				.collect(Collectors.toList());

		Collections.sort(projects);
		return projects;
	}
}
