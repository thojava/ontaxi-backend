package vn.ontaxi.hub.config;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.ontaxi.hub.scope.ViewScope;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by hopnv on 11/06/2017.
 */
@Configuration
@EnableWebMvc
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = "vn.ontaxi")
@EnableJpaRepositories(basePackages = "vn.ontaxi.common.jpa.repository")
public class WebConfiguration implements WebMvcConfigurer {

    private final DataSource dataSource;

    private final ApplicationContext applicationContext;

    public WebConfiguration(DataSource dataSource, ApplicationContext applicationContext) {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
    }

    @Bean
    public static CustomScopeConfigurer customScope() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        Map<String, Object> viewScope = new HashMap<>();
        viewScope.put("view", new ViewScope());
        configurer.setScopes(viewScope);
        return configurer;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("/i18/messages");
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        return resourceBundleMessageSource;
    }

    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true).
                favorParameter(false).
                ignoreAcceptHeader(true).
                useRegisteredExtensionsOnly(true).
                defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public SchedulerFactoryBean configureScheduler() throws IOException {
        SchedulerFactoryBean f = new SchedulerFactoryBean();
        f.setDataSource(dataSource);
        f.setJobFactory(new SpringBeanJobFactory());
        f.setAutoStartup(true);
        f.setQuartzProperties(quartzProperties());
        f.setApplicationContext(applicationContext);
        f.setApplicationContextSchedulerContextKey("applicationContext");
        return f;
    }

    private Properties quartzProperties() throws IOException
    {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
