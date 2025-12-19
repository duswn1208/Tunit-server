package com.tunit.domain.faq.service;

import com.tunit.domain.faq.dto.TutorFaqCreateRequestDto;
import com.tunit.domain.faq.dto.TutorFaqOrderUpdateRequestDto;
import com.tunit.domain.faq.dto.TutorFaqResponseDto;
import com.tunit.domain.faq.dto.TutorFaqUpdateRequestDto;
import com.tunit.domain.faq.entity.TutorFaq;
import com.tunit.domain.faq.exception.TutorFaqException;
import com.tunit.domain.faq.repository.TutorFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorFaqService {

    private final TutorFaqRepository tutorFaqRepository;

    public List<TutorFaqResponseDto> getFaqsForUser(Long tutorProfileNo) {
        return tutorFaqRepository.findByTutorProfileNoAndIsExposedTrueOrderByDisplayOrderAsc(tutorProfileNo)
                .stream()
                .map(TutorFaqResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<TutorFaqResponseDto> getFaqsForTutor(Long tutorProfileNo) {
        return tutorFaqRepository.findByTutorProfileNoOrderByDisplayOrderAsc(tutorProfileNo)
                .stream()
                .map(TutorFaqResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public TutorFaqResponseDto createFaq(Long tutorProfileNo, TutorFaqCreateRequestDto requestDto) {
        int nextOrder = tutorFaqRepository.findFirstByTutorProfileNoOrderByDisplayOrderDesc(tutorProfileNo)
                .map(lastFaq -> lastFaq.getDisplayOrder() + 1)
                .orElse(1);

        TutorFaq newFaq = TutorFaq.builder()
                .tutorProfileNo(tutorProfileNo)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .displayOrder(nextOrder)
                .isExposed(true)
                .build();

        tutorFaqRepository.save(newFaq);
        return new TutorFaqResponseDto(newFaq);
    }

    @Transactional
    public void updateFaq(Long tutorProfileNo, Long faqId, TutorFaqUpdateRequestDto requestDto) {
        TutorFaq faq = findFaq(faqId, tutorProfileNo);
        faq.update(requestDto.getTitle(), requestDto.getContent(), requestDto.isExposed());
    }

    @Transactional
    public void deleteFaq(Long tutorProfileNo, Long faqId) {
        TutorFaq faq = findFaq(faqId, tutorProfileNo);
        tutorFaqRepository.delete(faq);
    }

    @Transactional
    public void updateFaqOrder(Long tutorProfileNo, TutorFaqOrderUpdateRequestDto requestDto) {
        List<TutorFaq> faqs = tutorFaqRepository.findByTutorProfileNoOrderByDisplayOrderAsc(tutorProfileNo);
        Map<Long, TutorFaq> faqMap = faqs.stream().collect(Collectors.toMap(TutorFaq::getTutorFaqNo, faq -> faq));

        for (TutorFaqOrderUpdateRequestDto.FaqOrderDto dto : requestDto.getFaqs()) {
            TutorFaq faq = faqMap.get(dto.getTutorFaqNo());
            if (faq != null) {
                faq.updateOrder(dto.getDisplayOrder());
            }
        }
    }

    private TutorFaq findFaq(Long faqId, Long tutorProfileNo) {
        return tutorFaqRepository.findByTutorFaqNoAndTutorProfileNo(faqId, tutorProfileNo)
                .orElseThrow(TutorFaqException::new);
    }
}
