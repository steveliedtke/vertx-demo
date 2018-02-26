package de.steveliedtke.mittelvergabe.spring.rest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.steveliedtke.mittelvergabe.spring.dto.MittelvergabeDto;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

@RestController
public class RootRestController {

	private EventBus eventBus;

	public RootRestController(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/calculate", headers = "Accept=application/json")
	public MittelvergabeDto calculate(@RequestBody MittelvergabeDto dto)
			throws InterruptedException, ExecutionException {
		CompletableFuture<MittelvergabeDto> future = new CompletableFuture<>();
		eventBus.send("mittelvergabe.calculate", dto.toJson(), ar -> {
			if (ar.succeeded()) {
				future.complete(new MittelvergabeDto(((JsonObject) ar.result().body())));
			} else {
				future.completeExceptionally(new RuntimeException("Failed calculating"));
			}
		});

		return future.get();
	}

}
