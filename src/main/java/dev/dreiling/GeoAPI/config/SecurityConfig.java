package dev.dreiling.GeoAPI.config;

import dev.dreiling.GeoAPI.security.ApiKeyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception {
        http
                .csrf( csrf -> csrf.disable() )
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers( "/auth/**" ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore( apiKeyFilter(), UsernamePasswordAuthenticationFilter.class );

        return http.build();
    }

    @Bean
    public ApiKeyFilter apiKeyFilter() {
        return new ApiKeyFilter();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}