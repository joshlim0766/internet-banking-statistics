package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    long countByUserName (String userName);

    User findByUserName (String userName);
}

