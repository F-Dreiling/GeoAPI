package dev.dreiling.GeoAPI.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${app.api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal( HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain ) throws ServletException, IOException {
        String path = request.getRequestURI();

        if ( path.startsWith("/auth") ) {
            filterChain.doFilter(request, response);
            return;
        }

        String headerKey = request.getHeader("X-API-KEY");

        if ( !apiKey.equals(headerKey) ) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API key");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken( "php-client", null, List.of() );

        authentication.setDetails( new WebAuthenticationDetailsSource().buildDetails(request) );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}