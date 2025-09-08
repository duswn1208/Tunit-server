package com.tunit.domain.user.service;

import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.exception.UserException;
import com.tunit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserMain getUserProviderInfo(UserMain userMain) {
        return userRepository.findByProviderAndProviderId(userMain.getProvider(), userMain.getProviderId())
                .orElseThrow(UserException::new);
    }

    public UserMain saveUser(UserMain userMain) {
        return userRepository.save(userMain);
    }

    public UserMain findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId).orElse(null);
    }

    public UserMain findByUserNo(Long userNo) {
        return userRepository.findById(userNo).orElseThrow(UserException::new);
    }

    public UserMain findByNameAndPhone(String name, String phone) {
        return userRepository.findByNameAndPhone(name, phone).orElse(null);
    }

    public UserMain saveWaitingStudent(String name, String phone) {
        return userRepository.save(UserMain.saveWaitingStudent(name, phone));
    }

    public UserMain getOrCreateWaitingStudent(String name, String phone) {
        UserMain student = this.findByNameAndPhone(name, phone);
        if (student == null) {
            student = this.saveWaitingStudent(name, phone);
        }

        if (student == null) {
            throw new UserException("학생 조회 혹은 생성에 실패했습니다.");
        }
        return student;
    }

}
