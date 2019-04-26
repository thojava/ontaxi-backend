package vn.ontaxi.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.WebApplicationInitializer;
import reactor.Environment;
import reactor.bus.EventBus;

@ComponentScan("vn.ontaxi")
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "vn.ontaxi.jpa.repository")
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {
    @Bean
    public Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    public EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
