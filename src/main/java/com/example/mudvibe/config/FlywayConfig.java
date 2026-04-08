package com.example.mudvibe.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

	/**
	 * New for Spring 4.0.	 */
    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
    }
    
    /**
     * Needed so that jpa doesn't try to validate before flyway migrates.
     */
    @Bean
    public static BeanFactoryPostProcessor jpaDependsOnFlyway() {
        return bf -> {
            if (bf.containsBeanDefinition("entityManagerFactory")) {
                bf.getBeanDefinition("entityManagerFactory").setDependsOn("flyway");
            }
        };
    }

}
