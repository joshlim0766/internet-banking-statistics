package com.kakaopay.homework.internetbanking.service;

import com.kakaopay.homework.internetbanking.controller.dto.LoginResponse;
import com.kakaopay.homework.internetbanking.controller.dto.RefreshTokenResponse;
import com.kakaopay.homework.internetbanking.controller.dto.SingupResponse;
import com.kakaopay.homework.internetbanking.model.RefreshToken;
import com.kakaopay.homework.internetbanking.model.User;
import com.kakaopay.homework.internetbanking.repository.RefreshTokenRespository;
import com.kakaopay.homework.internetbanking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.Date;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRespository refreshTokenRespository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    protected User createUser (MultiValueMap<String, String> signupInformation) {
        String userName = signupInformation.getFirst("user_name");

        if (userRepository.countByUserName(userName) != 0) {
            throw new RuntimeException("User(" + userName + ") already exists.");
        }

        String password = signupInformation.getFirst("password");

        User user = new User();

        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserType(UserDetailServiceImpl.ROLE.USER.getRoleCode());
        user.setCreatedAt(new Date());

        userRepository.saveAndFlush(user);

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SingupResponse signup (MultiValueMap<String, String> signupInformation) {
        User user = createUser(signupInformation);
        String clientId = signupInformation.getFirst("client_id");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword(),
                        UserDetailServiceImpl.ROLE.of(user.getUserType()).toGrantedAuthority());

        OAuth2AccessToken accessToken = jwtTokenService.issueAccessToken(
                clientId, usernamePasswordAuthenticationToken, true);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(accessToken.getRefreshToken().getValue());
        refreshToken.setUser(user);

        refreshTokenRespository.saveAndFlush(refreshToken);

        SingupResponse response = new SingupResponse();

        response.setUserId(user.getUserName());
        response.setAccessToken(accessToken.getValue());

        return response;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LoginResponse login (MultiValueMap<String, String> loginInformation) {
        String userName = loginInformation.getFirst("user_name");
        String password = loginInformation.getFirst("password");
        String clientId = loginInformation.getFirst("client_id");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);

        authenticationManager.authenticate(token);

        OAuth2AccessToken accessToken = jwtTokenService.issueAccessToken(clientId, token, false);

        User user = userRepository.findByUserName(userName);
        RefreshToken refreshToken = refreshTokenRespository.findRefreshTokenByUser(user);
        if (refreshToken != null) {
            refreshToken.setRefreshToken(accessToken.getRefreshToken().getValue());
        }
        else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setRefreshToken(accessToken.getRefreshToken().getValue());
        }

        refreshTokenRespository.saveAndFlush(refreshToken);

        LoginResponse response = new LoginResponse();

        response.setUserId(userName);
        response.setAccessToken(accessToken.getValue());

        return response;
    }
}

