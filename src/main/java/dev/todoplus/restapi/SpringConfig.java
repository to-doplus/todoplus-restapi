package dev.todoplus.restapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Project: mminer
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Configuration
@EnableScheduling
@EnableWebMvc
@ComponentScan(basePackages = "dev.todoplus.restapi")
public class SpringConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedOrigins(getOrigins())
                .allowCredentials(true);
    }

    @Bean(name = "origins")
    public String[] getOrigins() {
        return new String[]{
                "https://api.todoplus.safar.dev",
                "http://localhost:3000",
                "http://localhost:5000",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5500",
                "null"
        };
    }

}
