package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.*;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.*;
import com.tunit.domain.lesson.feedback.LessonLog;
import com.tunit.domain.lesson.repository.LessonLogRepository;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonLogService {

    private final LessonLogRepository lessonLogRepository;
    private final LessonReservationRepository lessonReservationRepository;

    @Transactional
    public LessonLogResponseDto create(Long tutorProfileNo, LessonLogCreateRequestDto dto) {
        LessonReservation lesson = lessonReservationRepository.findById(dto.lessonReservationNo())
                .orElseThrow(LessonNotFoundException::new);

        if (!lesson.getTutorProfileNo().equals(tutorProfileNo)) {
            throw new LessonStatusException("본인의 레슨에 대해서만 일지를 작성할 수 있습니다.");
        }
        if (lesson.getStatus() != ReservationStatus.COMPLETED) {
            throw new LessonStatusException("완료된 레슨에 대해서만 일지를 작성할 수 있습니다.");
        }
        if (lessonLogRepository.existsByLessonReservationNo(dto.lessonReservationNo())) {
            throw new LessonLogAlreadyExistsException();
        }

        LessonLog lessonLog = LessonLog.builder()
                .lessonReservationNo(dto.lessonReservationNo())
                .tutorProfileNo(lesson.getTutorProfileNo())
                .studentNo(lesson.getStudentNo())
                .progressContent(dto.progressContent())
                .feedback(dto.feedback())
                .build();

        LessonLog saved = lessonLogRepository.save(lessonLog);
        log.info("레슨 일지 작성: logNo={}, lessonReservationNo={}", saved.getLogNo(), saved.getLessonReservationNo());
        return LessonLogResponseDto.from(saved);
    }

    @Transactional
    public LessonLogResponseDto update(Long tutorProfileNo, Long logNo, LessonLogUpdateRequestDto dto) {
        LessonLog lessonLog = getLog(logNo);
        validateTutor(tutorProfileNo, lessonLog);
        lessonLog.update(dto.progressContent(), dto.feedback());
        return LessonLogResponseDto.from(lessonLog);
    }

    public LessonLogResponseDto getByLessonReservationNo(Long lessonReservationNo) {
        LessonLog lessonLog = lessonLogRepository.findByLessonReservationNo(lessonReservationNo)
                .orElseThrow(LessonLogNotFoundException::new);
        return LessonLogResponseDto.from(lessonLog);
    }

    public List<LessonLogResponseDto> getMyLogsAsTutor(Long tutorProfileNo) {
        return lessonLogRepository.findByTutorProfileNoOrderByCreatedAtDesc(tutorProfileNo)
                .stream().map(LessonLogResponseDto::from).collect(Collectors.toList());
    }

    public List<LessonLogResponseDto> getMyLogsAsStudent(Long studentNo) {
        return lessonLogRepository.findByStudentNoOrderByCreatedAtDesc(studentNo)
                .stream().map(LessonLogResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public LessonLogResponseDto registerQuestion(Long studentNo, Long logNo, LessonLogQuestionRequestDto dto) {
        LessonLog lessonLog = getLog(logNo);
        if (!lessonLog.getStudentNo().equals(studentNo)) {
            throw new LessonStatusException("본인의 레슨 일지에만 질문을 등록할 수 있습니다.");
        }
        lessonLog.registerQuestion(dto.question());
        return LessonLogResponseDto.from(lessonLog);
    }

    @Transactional
    public LessonLogResponseDto registerReply(Long tutorProfileNo, Long logNo, LessonLogReplyRequestDto dto) {
        LessonLog lessonLog = getLog(logNo);
        validateTutor(tutorProfileNo, lessonLog);
        if (lessonLog.getStudentQuestion() == null) {
            throw new LessonStatusException("학생의 질문이 없습니다.");
        }
        lessonLog.registerReply(dto.reply());
        return LessonLogResponseDto.from(lessonLog);
    }

    public List<UnwrittenLessonDto> getUnwrittenLessons(Long contractNo) {
        List<LessonReservation> completed = lessonReservationRepository
                .findByContractNoAndStatusIn(contractNo, List.of(ReservationStatus.COMPLETED));

        if (completed.isEmpty()) {
            return List.of();
        }

        Set<Long> writtenNos = lessonLogRepository
                .findByLessonReservationNoIn(completed.stream().map(LessonReservation::getLessonReservationNo).toList())
                .stream().map(LessonLog::getLessonReservationNo).collect(Collectors.toSet());

        return completed.stream()
                .filter(lr -> !writtenNos.contains(lr.getLessonReservationNo()))
                .sorted(java.util.Comparator.comparing(LessonReservation::getDate).reversed())
                .map(UnwrittenLessonDto::from)
                .toList();
    }

    private LessonLog getLog(Long logNo) {
        return lessonLogRepository.findById(logNo)
                .orElseThrow(LessonLogNotFoundException::new);
    }

    private void validateTutor(Long tutorProfileNo, LessonLog lessonLog) {
        if (!lessonLog.getTutorProfileNo().equals(tutorProfileNo)) {
            throw new LessonStatusException("본인의 레슨 일지만 수정할 수 있습니다.");
        }
    }
}
