package com.brixo.config;

import com.brixo.security.BrixoUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final BrixoUserDetailsService userDetailsService;

    public SecurityConfig(BrixoUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        // Públicas
                        .requestMatchers(
                                "/", "/login", "/register",
                                "/cotizador", "/cotizador/generar",
                                "/especialidades", "/especialidades/**",
                                "/map",
                                "/sobre-nosotros", "/como-funciona", "/seguridad",
                                "/ayuda", "/politica-cookies", "/terminos",
                                "/css/**", "/js/**", "/images/**", "/favicon.svg",
                                "/api/v1/track",
                                "/internal/migrations/**")
                        .permitAll()
                        // Solo admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Resto requiere login
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("correo")
                        .passwordParameter("contrasena")
                        .defaultSuccessUrl("/panel", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(s -> s.maximumSessions(5))
                .csrf(csrf -> csrf
                        // Deshabilitar CSRF para el endpoint de analytics (beacon no envía tokens)
                        .ignoringRequestMatchers("/api/v1/track", "/internal/migrations/**"));

        return http.build();
    }
}
