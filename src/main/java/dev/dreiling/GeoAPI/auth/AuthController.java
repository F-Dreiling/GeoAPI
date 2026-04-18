package dev.dreiling.GeoAPI.auth;

import dev.dreiling.GeoAPI.user.User;
import dev.dreiling.GeoAPI.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login( @RequestBody Map<String, String> body ) {
        String username = body.get("username");
        String password = body.get("password");

        return userRepository.findByUsername(username)
                .map( (User user) -> {
                    Map<String, Object> response = new HashMap<>();

                    if ( passwordEncoder.matches( password, user.getPassword() ) ) {
                        response.put( "success", true );
                        response.put( "userId", user.getId() );
                    }
                    else {
                        response.put( "success", false );
                    }

                    return response;
                } )
                .orElse( Map.of( "success", false ) );
    }
}