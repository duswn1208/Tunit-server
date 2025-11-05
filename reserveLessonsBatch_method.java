/**
 * 계약 생성 시 여러 개의 대기 상태 레슨을 배치로 생성
 * 1개~56개까지 효율적으로 처리 (한 트랜잭션, 한 번의 saveAll)
 * @param userNo 학생 user_no
 * @param lessonDtos 생성할 레슨 정보 리스트
 */
@Transactional
public void reserveLessonsBatch(Long userNo, List<LessonReserveSaveDto> lessonDtos) {
    if (lessonDtos == null || lessonDtos.isEmpty()) {
        return;
    }

    // 1. 학생 정보 1번만 조회
    UserMain student = userService.findByUserNo(userNo);

    // 2. 캐싱을 통해 중복 조회 최소화
    List<LessonReservation> reservations = new ArrayList<>();
    Long cachedTutorProfileNo = null;
    TutorProfileResponseDto cachedTutorProfile = null;
    Integer cachedTutorLessonNo = null;
    TutorLessons cachedTutorLessons = null;

    for (LessonReserveSaveDto dto : lessonDtos) {
        // 튜터 프로필 정보 조회 (캐시 활용)
        if (cachedTutorProfileNo == null || !cachedTutorProfileNo.equals(dto.tutorProfileNo())) {
            cachedTutorProfile = tutorProfileService.findTutorProfileInfoByTutorProfileNo(dto.tutorProfileNo());
            cachedTutorProfileNo = dto.tutorProfileNo();
        }

        // 튜터 레슨 정보 조회 (캐시 활용)
        if (cachedTutorLessonNo == null || !cachedTutorLessonNo.equals(dto.tutorLessonNo())) {
            cachedTutorLessons = tutorLessonsRepository.findById(dto.tutorLessonNo())
                    .orElseThrow(() -> new IllegalArgumentException("해당 레슨 정보가 없습니다. tutorLessonNo: " + dto.tutorLessonNo()));
            cachedTutorLessonNo = dto.tutorLessonNo();
        }

        // 종료 시간 계산
        LocalTime endTime = dto.startTime().plusMinutes(cachedTutorProfile.durationMin());

        // 튜터 가용성 검증
        validateTutorAvailability(dto.tutorProfileNo(), dto.lessonDate(), dto.startTime(), endTime);

        // 레슨 예약 엔티티 생성
        LessonReservation reservation = LessonReservation.fromReserveLesson(
                dto,
                dto.tutorProfileNo(),
                cachedTutorLessons.getLessonSubCategory(),
                student.getUserNo(),
                endTime
        );
        reservations.add(reservation);
    }

    // 3. 한 번에 배치 저장
    lessonReservationRepository.saveAll(reservations);
    log.info("배치 레슨 예약 완료. studentNo: {}, 레슨 수: {}", student.getUserNo(), reservations.size());

