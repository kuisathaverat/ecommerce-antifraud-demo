package com.mycompany.ecommerce.antifraud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@RestController
public class AntiFraudApplication {

	private String format = "<html>"
		+ "<head><title>%s</title></head>"
		+ "<style>"
		+ "	.center-screen {"
		+ "		position: fixed;"
		+ "		top: 50%%;"
		+ "		left: 50%%;"
		+ "		transform: translate(-50%%, -50%%);"
		+ "	}"
		+ "	body { background-color: Ivory; }"
		+ " h1 { color: %s; font-size: 96px }"
		+ "</style>"
		+ "<body>"
		+ "	<div class='center-screen'>"
		+ "		<h1>%s</h1>"
		+ "	</div>"
		+ "</body>"
		+ "</html>";
	private String green = "SeaGreen";
	private String red = "DarkRed";

	@Value("${pom.version}")
	private String pomVersion;

	public static void main(String[] args) {
		SpringApplication.run(AntiFraudApplication.class, args);
	}

	@GetMapping("/")
	public String ecommerce(@RequestParam(value = "name", defaultValue = "World") String name) {
		//String prefix = "Bye";
		String prefix = "Hello";
		String title = String.format("%s %s!", prefix, name);
		String body = String.format("%s %s!", prefix, name);
		return String.format(format, title, green, body);
	}

	@RequestMapping(value = "/healthcheck", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map healthcheck() {
		// Comment out to generate a synthetic error
		//generateError();
		return Collections.singletonMap("status", "Up");
	}

	@RequestMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map version() {
		return Collections.singletonMap("version", pomVersion);
	}

	private void generateError() {
		// Comment out to generate a synthetic error
		/*if (pomVersion.equals("0.0.3")) {
			// Generate a synthetic error if version is not right
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		}*/
	}
}
