package com.scm.config;
// import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
// import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.scm.services.impl.SecurityCustomUserDetailService;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // }

    // @Bean
    // public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

    //     UserDetails user1 = User
    //             .withDefaultPasswordEncoder()
    //             .username("aparna")
    //             .password("aparna1234")
    //             .roles("USER")
    //             .build();

    //     UserDetails user2 = User
    //             .withDefaultPasswordEncoder()
    //             .username("ritu")
    //             .password("ritu1234")
    //             .build();

    //     return new InMemoryUserDetailsManager(user1, user2);
    // }
    @Autowired
    private SecurityCustomUserDetailService userDetailsService;

    @Autowired
    private OAuthAuthenticationSuccessHandler handler;

    //configuration for authentication provider for spring security

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        //user detail service and password encoder
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        //configuration
        //these urls will tell will be protected (private) and which will not be protected(public)
        httpSecurity.authorizeHttpRequests(authorize -> {
            //authorize.requestMatchers("/home", "/register", "/services").permitAll();
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
    
        });
        //form login configuration
        //we can change anything related to form login using this method

        httpSecurity.formLogin((formLogin) -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .successForwardUrl("/user/dashboard")
                //.failureForwardUrl("/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
            
                // .failureHandler(new AuthenticationFailureHandler() {

                //     @Override
                //     public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                //             AuthenticationException exception) throws IOException, ServletException {
                //         // TODO Auto-generated method stub
                //         throw new UnsupportedOperationException("Unimplemented method 'onAuthenticationFailure'");
                //     }
                    
                    
                    
         );

        //logout configuration
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.logout(logoutForm -> logoutForm
            .logoutUrl("/do-logout")
            .logoutSuccessUrl("/login?logout=true"));
            // .permitAll());

        //         .permitAll());

        //oauth2 login configuration
        httpSecurity.oauth2Login(oauth->{oauth
            .loginPage("/login")
            .successHandler(handler);
           // .defaultSuccessUrl("/user/dashboard")
            //.failureUrl("/login?error=true");
    });

        httpSecurity.authenticationProvider(authenticationProvider());

         return httpSecurity.build();
    }

    @Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
}
