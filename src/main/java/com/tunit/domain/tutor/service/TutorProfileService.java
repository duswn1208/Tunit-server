package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;

    public Long save(TutorProfileSaveDto tutorProfileSaveDto) {
        TutorProfile tutorProfile = TutorProfile.saveFrom(tutorProfileSaveDto);

        TutorProfile save = tutorProfileRepository.save(tutorProfile);

        return save.getTutorProfileNo();
    }
}
