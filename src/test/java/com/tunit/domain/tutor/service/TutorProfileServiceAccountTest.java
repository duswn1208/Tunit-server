package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("TutorProfileService.findByTutorProfileNo() 테스트")
class TutorProfileServiceAccountTest {

    @Mock
    private TutorProfileRepository tutorProfileRepository;

    @Mock
    private TutorAvailableTimeService tutorAvailableTimeService;

    @Mock
    private TutorHolidayService tutorHolidayService;

    @Mock
    private com.tunit.domain.user.service.UserService userService;

    @Mock
    private com.tunit.domain.review.service.LessonReviewService lessonReviewService;

    @InjectMocks
    private TutorProfileService tutorProfileService;

    @Nested
    @DisplayName("findByTutorProfileNo()")
    class FindByTutorProfileNo {

        @Test
        @DisplayName("존재하는 tutorProfileNo이면 엔티티를 반환한다")
        void findByTutorProfileNo_found_returnsEntity() {
            // given
            TutorProfile profile = TutorProfile.of()
                    .tutorProfileNo(1L)
                    .userNo(10L)
                    .bankName("신한은행")
                    .accountNumber("110-123-456789")
                    .accountHolder("테스트")
                    .build();

            given(tutorProfileRepository.findByTutorProfileNo(1L)).willReturn(Optional.of(profile));

            // when
            TutorProfile result = tutorProfileService.findByTutorProfileNo(1L);

            // then
            assertThat(result.getTutorProfileNo()).isEqualTo(1L);
            assertThat(result.getBankName()).isEqualTo("신한은행");
            assertThat(result.getAccountNumber()).isEqualTo("110-123-456789");
            assertThat(result.getAccountHolder()).isEqualTo("테스트");
        }

        @Test
        @DisplayName("존재하지 않는 tutorProfileNo이면 예외가 발생한다")
        void findByTutorProfileNo_notFound_throwsException() {
            // given
            given(tutorProfileRepository.findByTutorProfileNo(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tutorProfileService.findByTutorProfileNo(999L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
