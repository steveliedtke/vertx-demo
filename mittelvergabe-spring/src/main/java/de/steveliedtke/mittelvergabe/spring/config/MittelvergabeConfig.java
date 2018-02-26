package de.steveliedtke.mittelvergabe.spring.config;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

@Configuration
@EnableWebMvc
@ComponentScan("de.steveliedtke.mittelvergabe.spring.rest.**")
public class MittelvergabeConfig implements WebMvcConfigurer {

	private Vertx vertx;

	@PostConstruct
	public void init() {
		CompletableFuture<Vertx> vertxFuture = new CompletableFuture<>();
		Vertx.clusteredVertx(new VertxOptions(), res -> {
			if (res.succeeded()) {
				vertxFuture.complete(res.result());
			} else {
				vertxFuture.complete(null);
			}
		});

		try {
			vertx = vertxFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Failed initializing vertx");
		}
	}

	@Bean
	public EventBus eventBus() {
		return vertx.eventBus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
		final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(new ObjectMapper());
		converters.add(0, converter);

		final Charset utf = Charset.forName("UTF-8"); // ISO-8859-1
		final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(utf);
		converters.add(1, stringConverter);

		converters.add(new ByteArrayHttpMessageConverter());

	}

}
