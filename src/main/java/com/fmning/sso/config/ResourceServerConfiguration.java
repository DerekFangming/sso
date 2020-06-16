package com.fmning.sso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
//                .oauth2Login()
//                .loginPage("/login/oauth2")
//                .and()
                .antMatcher("/user").authorizeRequests()
//                .antMatchers("/login/oauth2").permitAll()
                .anyRequest().authenticated();
    }
}
