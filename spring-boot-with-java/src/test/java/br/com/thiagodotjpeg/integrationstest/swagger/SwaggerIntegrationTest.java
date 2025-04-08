package br.com.thiagodotjpeg.integrationstest.swagger;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
class SwaggerIntegrationTest extends AbstractIntegrationTest {

	@Test
	void shouldDisplaySwaggerUIPage() {
		var content = given()
						.basePath("/swagger-ui/index.html")
							.port(TestConfigs.SERVER_PORT)
						.when()
							.get()
						.then()
							.statusCode(200)
						.extract()
							.body()
								.asString();

		assertTrue(content.contains("Swagger UI"));
	}

}
