package com.kakaopay.homework.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Value("${resource.id:resource-server}")
    private String resourceId;

    @Value("${security.oauth2.resource.jwt.key-value}")
    private String authorizationServerPublicKey;

    @Bean(name = "resourceServerTokenStore")
    public TokenStore tokenStore () {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean(name = "resourceServerAccessTokenConverter")
    public JwtAccessTokenConverter accessTokenConverter () {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        converter.setVerifierKey(authorizationServerPublicKey);

        return converter;
    }

    @Bean(name = "resourceServerTokenService")
    public DefaultTokenServices tokenService () {
        DefaultTokenServices defaultTokenService = new DefaultTokenServices();

        defaultTokenService.setTokenStore(tokenStore());
        defaultTokenService.setSupportRefreshToken(true);

        return defaultTokenService;
    }

    @Override
    public void configure (HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .antMatchers("/api/v1/user/**").permitAll()
                    .antMatchers("/api/v1/devices/**").hasAnyRole("ADMIN", "USER")
                    .antMatchers("/api/v1/token/**").hasAnyRole("ADMIN", "USER")
                    .antMatchers("/api/v1/statistics/**").hasAnyRole("ADMIN", "USER");
    }

    @Override
    public void configure (ResourceServerSecurityConfigurer configurer) throws Exception {
        configurer.resourceId(resourceId);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

