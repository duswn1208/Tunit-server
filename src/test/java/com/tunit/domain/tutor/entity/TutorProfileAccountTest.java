package com.tunit.domain.tutor.entity;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.region.dto.RegionSaveDto;
import com.tunit.domain.tutor.dto.TutorProfileModifyDto;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TutorProfile 계좌 필드 테스트")
class TutorProfileAccountTest {

    private static final RegionSaveDto REGION = new RegionSaveDto(11010, "서울 종로구", "DISTRICT", 11, "서울");
    private static final List<LessonSubCategory> SUB_CATEGORIES = List.of(LessonSubCategory.ENGLISH);

    @Nested
    @DisplayName("saveFrom()")
    class SaveFrom {

        @Test
        @DisplayName("계좌 정보가 있으면 엔티티에 반영된다")
        void saveFrom_withAccountInfo_persistsFields() {
            // given
            TutorProfileSaveDto dto = TutorProfileSaveDto.of()
                    .userNo(1L)
                    .introduce("소개")
                    .mainCategory(LessonCategory.LANGUAGE)
                    .subCategoryList(SUB_CATEGORIES)
                    .regionList(List.of(REGION))
                    .careerYears(3)
                    .pricePerHour(30000)
                    .durationMin(60)
                    .bankName("국민은행")
                    .accountNumber("123-456-789012")
                    .accountHolder("홍길동")
                    .tutorAvailableTimeSaveDtoList(List.of())
                    .build();

            // when
            TutorProfile profile = TutorProfile.saveFrom(1L, dto);

            // then
            assertThat(profile.getBankName()).isEqualTo("국민은행");
            assertThat(profile.getAccountNumber()).isEqualTo("123-456-789012");
            assertThat(profile.getAccountHolder()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("계좌 정보 없이 저장하면 null이다")
        void saveFrom_withoutAccountInfo_fieldsAreNull() {
            // given
            TutorProfileSaveDto dto = TutorProfileSaveDto.of()
                    .userNo(1L)
                    .introduce("소개")
                    .mainCategory(LessonCategory.LANGUAGE)
                    .subCategoryList(SUB_CATEGORIES)
                    .regionList(List.of(REGION))
                    .careerYears(3)
                    .pricePerHour(30000)
                    .durationMin(60)
                    .tutorAvailableTimeSaveDtoList(List.of())
                    .build();

            // when
            TutorProfile profile = TutorProfile.saveFrom(1L, dto);

            // then
            assertThat(profile.getBankName()).isNull();
            assertThat(profile.getAccountNumber()).isNull();
            assertThat(profile.getAccountHolder()).isNull();
        }
    }

    @Nested
    @DisplayName("updateProfile()")
    class UpdateProfile {

        @Test
        @DisplayName("계좌 정보를 수정하면 엔티티에 반영된다")
        void updateProfile_withAccountInfo_updatesFields() {
            // given
            TutorProfile profile = buildProfile("신한은행", "111-222-333333", "김철수");

            TutorProfileModifyDto dto = TutorProfileModifyDto.of()
                    .introduce("수정된 소개")
                    .careerYears(5)
                    .pricePerHour(40000)
                    .durationMin(60)
                    .bankName("우리은행")
                    .accountNumber("999-888-777777")
                    .accountHolder("이영희")
                    .build();

            // when
            profile.updateProfile(dto);

            // then
            assertThat(profile.getBankName()).isEqualTo("우리은행");
            assertThat(profile.getAccountNumber()).isEqualTo("999-888-777777");
            assertThat(profile.getAccountHolder()).isEqualTo("이영희");
        }

        @Test
        @DisplayName("계좌 정보를 null로 수정하면 null이 된다")
        void updateProfile_withNullAccountInfo_fieldsBecomNull() {
            // given
            TutorProfile profile = buildProfile("신한은행", "111-222-333333", "김철수");

            TutorProfileModifyDto dto = TutorProfileModifyDto.of()
                    .introduce("소개")
                    .careerYears(3)
                    .pricePerHour(30000)
                    .durationMin(60)
                    .build();

            // when
            profile.updateProfile(dto);

            // then
            assertThat(profile.getBankName()).isNull();
            assertThat(profile.getAccountNumber()).isNull();
            assertThat(profile.getAccountHolder()).isNull();
        }

        private TutorProfile buildProfile(String bankName, String accountNumber, String accountHolder) {
            return TutorProfile.of()
                    .userNo(1L)
                    .introduce("소개")
                    .bankName(bankName)
                    .accountNumber(accountNumber)
                    .accountHolder(accountHolder)
                    .build();
        }
    }
}
