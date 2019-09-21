package com.kakaopay.homework.repository;

import com.kakaopay.homework.model.RefreshToken;
import com.kakaopay.homework.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRespository extends JpaRepository<RefreshToken, Long> {
    void deleteRefreshTokenByUser (User user);

    RefreshToken findRefreshTokenByUser (User user);
}
