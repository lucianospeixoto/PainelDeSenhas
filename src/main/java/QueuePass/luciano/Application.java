package QueuePass.luciano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;

@SpringBootApplication
public class Application {
	@Bean
	WebServerFactoryCustomizer<TomcatServletWebServerFactory> monitorConnector() {
		return factory -> factory.addAdditionalConnectors(monitorPort());
	}

	private Connector monitorPort() {
		Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setPort(8081);
		return connector;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
