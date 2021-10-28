package com.ultrasound.app.security;

import com.ultrasound.app.model.user.ERole;
import com.ultrasound.app.security.jwt.AuthEntryPointJwt;
import com.ultrasound.app.security.service.UserDetailsServiceImpl;
import com.ultrasound.app.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
//@EnableAutoConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
//        securedEnabled = true,
//        // jsr250Enabled = true,
        prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
//    @Autowired
//    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] noAuthRoutes = new String[]{"/", "/api/date", "/api/auth/sign-up", "/api/auth/sign-in"};
        String[] userAuthRoutes = new String[]{"/api/classifications", "/api/classifications/**", "/api/submenu/**", "/api/user/**", "/api/S3/link/**"};
        String[] adminAuthRoutes = new String[]{"/**", "/api/**", "/api/tables/clear", "/api/admin/**"};

                http.cors().configurationSource(corsConfigurationSource())
                        .and()
                .csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                        .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                .authorizeRequests()
                        .antMatchers(noAuthRoutes).permitAll()
                        .antMatchers(userAuthRoutes).hasAnyAuthority(ERole.ROLE_USER.toString(), ERole.ROLE_ADMIN.toString())
                        .antMatchers(adminAuthRoutes).hasAuthority(ERole.ROLE_ADMIN.toString())
                        .anyRequest().authenticated()
                        .and()
                        .httpBasic()
                        .and()
                        .formLogin().loginPage("/login").permitAll()
                        .and()
                        .logout().permitAll();

//                .successHandler(loginSuccessHandler)
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
       configuration.setAllowCredentials(true);
        // configuration.addAllowedOriginPattern("**");
        configuration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:8080/**",
//                "http://localhost:8080**",
                "http://localhost:3000",
//                "http://localhost/**",
                "http://localhost"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "response-type", "x-access-token", "Access-Control-Allow-Origin", "x-requested-with", "access-control-allow-methods", "Accept", "Accept-Language", "Content-Language", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("authorization", "accessToken", "refreshToken", "Access-Control-Allow-Origin"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}
