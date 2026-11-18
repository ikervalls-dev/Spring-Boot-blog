package springbootblog;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/single_post/*/like")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/single_post/*/comment").authenticated()
                .requestMatchers("/blog/new").authenticated()
                .requestMatchers("/", "/about", "/blog", "/blog/**", "/contact", "/contact/**",
                                 "/single_post", "/single_post/**",
                                 "/login", "/register", "/acceso-denegado", "/error",
                                 "/css/**", "/js/**", "/bootstrap/**",
                                 "/font-awesome/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                // Acepta POST (formularios con CSRF) y GET para poder salir escribiendo
                // http://localhost:8080/logout directamente en la barra del navegador.
                .logoutRequestMatcher(new OrRequestMatcher(
                    PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/logout"),
                    PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/logout")
                ))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/acceso-denegado")
            );

        return http.build();
    }
}