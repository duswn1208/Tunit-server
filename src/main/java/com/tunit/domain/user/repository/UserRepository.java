package com.tunit.domain.user.repository;

import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.entity.UserMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserMain, Long> {

    Optional<UserMain> findByProviderAndProviderId(UserProvider provider, String providerId);

    Optional<UserMain> findByProviderId(String providerId);

    Optional<UserMain> findByNameAndPhone(String name, String phone);
    
    Optional<UserMain> findByPhone(String phone);
}
