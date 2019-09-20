package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.model.RefreshToken;
import com.kakaopay.homework.internetbanking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRespository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findRefreshTokenByUser (User user);
}
