package com.kakaopay.homework.service;

import com.kakaopay.homework.controller.dto.RefreshTokenResponse;
import com.kakaopay.homework.exception.InvalidTokenException;
import com.kakaopay.homework.model.RefreshToken;
import com.kakaopay.homework.model.User;
import com.kakaopay.homework.repository.RefreshTokenRespository;
import com.kakaopay.homework.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class JwtTokenService {

    @Autowired
    private OAuth2RequestFactory oauth2RequestFactory;

    @Autowired
    private DefaultTokenServices tokenService;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    @Autowired
    private RefreshTokenRespository refreshTokenRespository;

    @Autowired
    private UserRepository userRepository;

    private HashMap<String, String> createAuthorizationParameters (String userName, String clientId) {
        HashMap<String, String> authorizationParameters = new HashMap<String, String>();

        authorizationParameters.put("scope", "read,write");
        authorizationParameters.put("grant", "password");
        authorizationParameters.put("username", userName);
        authorizationParameters.put("client_id", clientId);

        return authorizationParameters;
    }

    private AuthorizationRequest createAuthorizationRequest (String userName, String clientId) {
        AuthorizationRequest authorizationRequest = oauth2RequestFactory.createAuthorizationRequest(
                createAuthorizationParameters(userName, clientId));

        authorizationRequest.setApproved(true);

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_TRUSTED_CLIENT"));
        authorizationRequest.setAuthorities(authorities);

        HashSet<String> resourceIds = new HashSet<>();
        resourceIds.add("resource-server");
        authorizationRequest.setResourceIds(resourceIds);

        return authorizationRequest;
    }

    public OAuth2AccessToken issueAccessToken (String clientId,
                                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        String userName = usernamePasswordAuthenticationToken.getName();
        AuthorizationRequest authorizationRequest = createAuthorizationRequest(userName, clientId);

        OAuth2Request authRequest = oauth2RequestFactory.createOAuth2Request(authorizationRequest);

        OAuth2Authentication authentication = new OAuth2Authentication(authRequest, usernamePasswordAuthenticationToken);
        authentication.setAuthenticated(true);

        OAuth2AccessToken accessToken = tokenService.createAccessToken(authentication);

        return accessTokenConverter.enhance(accessToken, authentication);
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponse refreshAccessToken (String token) {
        String[] arr = token.split(" ");
        if (arr == null || arr.length < 2) {
            throw new InvalidTokenException("Invalid authorization token");
        }

        token = arr[1];

        try {
            OAuth2AccessToken accessToken = tokenService.readAccessToken(token);
            if (accessToken == null) {
                throw new InvalidTokenException("Couldn't read access token");
            }

            OAuth2Authentication oauth2Authentication = tokenService.loadAuthentication(token);
            if (oauth2Authentication == null) {
                throw new InvalidTokenException("Couldn't load authentication from access token.");
            }

            Authentication authentication = oauth2Authentication.getUserAuthentication();
            if (authentication == null) {
                throw new InvalidTokenException("Couldn't get user authentication from OAuth2 authentication.");
            }

            String userName = authentication.getName();

            String clientId = tokenService.getClientId(token);

            AuthorizationRequest authorizationRequest = createAuthorizationRequest(userName, clientId);

            TokenRequest tokenRequest = oauth2RequestFactory.createTokenRequest(authorizationRequest, "password");

            User user = userRepository.findByUserName(userName);
            if (user == null) {
                throw new InvalidTokenException("Couldn't find user(" + userName + ")");
            }

            RefreshToken refreshToken = refreshTokenRespository.findRefreshTokenByUser(user);
            if (refreshToken == null) {
                throw new InvalidTokenException("Couldn't find refresh token");
            }

            accessToken = tokenService.refreshAccessToken(refreshToken.getRefreshToken(), tokenRequest);
            if (accessToken == null) {
                token = accessToken.getValue();
                throw new InvalidTokenException("Couldn't refresh access token");
            }

            accessToken = accessTokenConverter.enhance(accessToken, oauth2Authentication);
            if (accessToken == null) {
                token = accessToken.getValue();
                throw new InvalidTokenException("Couldn't enhance access token");
            }

            RefreshTokenResponse response = new RefreshTokenResponse();

            response.setAccessToken(accessToken.getValue());

            return response;
        }
        catch (InvalidTokenException re) {
            tokenService.revokeToken(token);
            throw re;
        }
    }
}
