package de.steveliedtke.mittelvergabe.spring.dto;

import java.util.List;

import de.steveliedtke.mittelvergabe.algorithm.Project;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MittelvergabeDto {

	private Double moneyAvailable;

	private List<Project> projects;

	public MittelvergabeDto(JsonObject json) {
		this.moneyAvailable = json.getDouble("moneyAvailable");
		projects = Project.parse(json.getJsonArray("projects"));
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.put("moneyAvailable", moneyAvailable);
		JsonArray projectArray = new JsonArray();
		projects.forEach(project -> {
			projectArray.add(project.toJson());
		});
		jsonObject.put("projects", projectArray);
		return jsonObject;
	}
}
