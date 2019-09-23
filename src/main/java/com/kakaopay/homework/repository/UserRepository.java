package com.kakaopay.homework.repository;

import com.kakaopay.homework.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    long countByUserName (String userName);

    User findByUserName (String userName);

    void deleteByUserName (String userName);
}

