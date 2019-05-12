package northwind.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import northwind.listner.NorthwindApplicationListener;

@SpringBootApplication
@ComponentScan(basePackages = {"northwind"})
@ServletComponentScan(basePackages= { "northwind" })
@EnableMongoRepositories(basePackages = { "northwind" }, mongoTemplateRef = "mongo-northwind")
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
