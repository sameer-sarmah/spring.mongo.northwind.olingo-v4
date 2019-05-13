package northwind.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import northwind.config.MongoConfig;
import northwind.listner.NorthwindApplicationListener;

@SpringBootApplication
@Import(MongoConfig.class)
public class NorthwindApplication  extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    	application.listeners(new NorthwindApplicationListener());
    	return application.sources(NorthwindApplication.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(NorthwindApplication.class, args);
		System.err.println("##########");
		
	}

}
