package dev.dreiling.GeoAPI.config;

import dev.dreiling.GeoAPI.security.HeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new RuntimeException("Not used");
        };
    }

    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception {
        http
                .csrf( csrf -> csrf.disable() )
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers( "/auth/**" ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore( headerFilter(), UsernamePasswordAuthenticationFilter.class )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public HeaderFilter headerFilter() {
        return new HeaderFilter();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}