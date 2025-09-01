package com.tunit.domain.user.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = attributes.get("id").toString();

        UserMain userMain = userService.getUserProviderInfo(UserMain.findFrom(UserProvider.NAVER, providerId));
        return ResponseEntity.ok(userMain);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> authMe(@LoginUser(field = "userNo") Long userNo) {
        if (userNo == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok().build(); // user 정보 반환하지 않고, 로그인 상태만 체크
    }
}
