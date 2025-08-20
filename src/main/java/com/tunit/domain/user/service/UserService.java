package com.tunit.domain.user.service;

import com.tunit.domain.user.entity.User;
import com.tunit.domain.user.exception.UserException;
import com.tunit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserProviderInfo(User user) {
        return userRepository.findByProviderAndProviderId(user.getProvider(), user.getProviderId())
                .orElseThrow(UserException::new);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
