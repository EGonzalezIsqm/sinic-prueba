package co.gov.igac.snc.xtfajson;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class XtfToJsonApplication {

	public static void main(String[] args) {
		SpringApplication.run(XtfToJsonApplication.class, args);
	}
	
	@Bean
	public GroupedOpenApi publicApi() {
	      return GroupedOpenApi.builder()
	              .group("springshop-public")
	              .packagesToScan("co.gov.igac.snc.xtfajson.controller")
	              .build();
	}
}
