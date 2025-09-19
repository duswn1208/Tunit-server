package com.tunit.domain.user.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.student.dto.StudentInfoResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.dto.UserMainResponseDto;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.student.service.StudentService;
import com.tunit.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TutorProfileService tutorProfileService;
    private final StudentService studentService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = attributes.get("id").toString();

        UserMain userMain = userService.getUserProviderInfo(UserMain.findFrom(UserProvider.NAVER, providerId));
        return ResponseEntity.ok(userMain);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.removeAttribute("LOGIN_USER");
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @GetMapping("/profile/me")
    public ResponseEntity<?> profileMe(@LoginUser(field = "userNo") Long userNo) {
        UserMain userMain = userService.findByUserNo(userNo);

        if (userMain.getUserRole().isTutor()) {
            var tutorProfileInfo = tutorProfileService.findTutorProfileInfo(userNo);
            return ResponseEntity.ok(UserMainResponseDto.tutorFrom(userMain, tutorProfileInfo));
        } else {
            StudentInfoResponseDto studentByUserNo = studentService.findStudentByUserNo(userNo);
            return ResponseEntity.ok(UserMainResponseDto.studentFrom(userMain, studentByUserNo));
        }
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> authMe(@LoginUser(field = "userNo") Long userNo) {
        UserMain userMain = userService.findByUserNo(userNo);
        return ResponseEntity.ok(userMain);
    }
}
