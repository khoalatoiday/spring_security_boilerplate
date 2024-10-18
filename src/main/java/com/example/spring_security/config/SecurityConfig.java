package com.example.spring_security.config;

import com.example.spring_security.exception.CustomAccessDeniedHandler;
import com.example.spring_security.exception.CustomAuthenticationEntryPoint;
import com.example.spring_security.filter.CsrfCookieFilter;
import com.example.spring_security.handler.CustomAuthenticationFailureHandler;
import com.example.spring_security.handler.CustomAuthenticationSuccessHandler;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // handle sử dụng để lấy giá trị csrf token từ cookie và set vào request attribute
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        http.cors(config -> config.configurationSource(request -> {
            CorsConfiguration cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("*"));
            cors.setAllowedMethods(List.of("*"));
            cors.setAllowedHeaders(List.of("*"));
            cors.setAllowCredentials(true);
            cors.setMaxAge(3600L);
            return cors;
        }));
        http.securityContext(config -> config.requireExplicitSave(false)); // spring security sẽ tự động lưu session sau khi xác thực
        http.sessionManagement(config -> config
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .sessionFixation().migrateSession().
                invalidSessionUrl("/invalid-session").maximumSessions(1)
        );
        http.requiresChannel(rcc->rcc
                .requestMatchers("/secure").requiresSecure() // only accept https with these url
                .requestMatchers("/unsecure").requiresInsecure()
//                .anyRequest().requiresSecure() // need for productions
        );
        http.csrf(config -> config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                .ignoringRequestMatchers("/csrf-disabled/**") // ignore csrf for these urls
        ); // set cookieHttpOnly = false để đảm bảo browser có thể đọc được csrf cookie
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers(  "oauth/current", "/myCustomer/**").authenticated() // những url phải xác thực mới được truy cập
//                        .requestMatchers("/myAccount/**").hasAuthority("VIEW_ACCOUNT")
//                        .requestMatchers("/myBalance/**").hasAuthority("VIEW_BALANCE")
//                        .requestMatchers("/myLoans/**", "/myCards/**").hasAnyAuthority("VIEW_LOAN", "VIEW_FUND")

                        .requestMatchers("/myAccount/**").hasAuthority("USER")
                        .requestMatchers("/myBalance/**").hasAuthority("USER")
                        .requestMatchers("/myLoans/**", "/myCards/**").hasAnyAuthority("USER", "ADMIN")

                        .requestMatchers( "/oauth/register").permitAll()  //  tất cả các request bắt đầu bằng /demo/ đều được phép truy cập
                        .requestMatchers("contact", "notices" ,"/custom-login").permitAll()
                        .requestMatchers("/deny").denyAll() // tất cả các request bắt đầu bằng /deny sẽ bị từ chối
                );
        http.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        http.formLogin(config -> config.loginPage("/login").permitAll()
                .loginProcessingUrl("/custom-login")
                .defaultSuccessUrl("/account", true)
                .failureUrl("/login?error=true")
                .successHandler(new CustomAuthenticationSuccessHandler())
                .failureHandler(new CustomAuthenticationFailureHandler()))
        ;
        http.logout(config -> config.logoutSuccessUrl("/login?logout=true").logoutUrl("/logout"));
        http.httpBasic(config -> config.authenticationEntryPoint(new CustomAuthenticationEntryPoint())); // config for only basic authentication
        http.exceptionHandling(config -> config
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // global config for all authentication flow
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        return http.build();
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("12345")
//                .authorities("read")
//                .build();
//
//        UserDetails user1 = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("12345")
//                .authorities("read")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user1);
//    }

//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource){
//        return new JdbcUserDetailsManager(dataSource);
//    }


//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

    @Bean
    static GrantedAuthorityDefaults userAuthority() {
        return new GrantedAuthorityDefaults("MY_PREFIX_");
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
