package com.tunit.domain.user.repository;

import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderId(UserProvider provider, String providerId);
}
