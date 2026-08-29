package com.gym.serviceImpl;

import com.gym.dto.ProgressDTO;
import com.gym.entity.Member;
import com.gym.entity.Progress;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.MemberRepository;
import com.gym.repository.ProgressRepository;
import com.gym.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressServiceImpl implements ProgressService {

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public ProgressDTO addProgressRecord(ProgressDTO dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", dto.getMemberId()));

        Progress progress = new Progress();
        progress.setMember(member);
        progress.setRecordedDate(dto.getRecordedDate() != null ? dto.getRecordedDate() : LocalDate.now());
        
        progress.setWeight(BigDecimal.valueOf(dto.getWeight()));
        progress.setHeight(BigDecimal.valueOf(dto.getHeight()));
        progress.setBodyFatPercentage(dto.getBodyFatPercentage() != null ? BigDecimal.valueOf(dto.getBodyFatPercentage()) : null);
        progress.setMuscleMass(dto.getMuscleMass() != null ? BigDecimal.valueOf(dto.getMuscleMass()) : null);
        progress.setChest(dto.getChest() != null ? BigDecimal.valueOf(dto.getChest()) : null);
        progress.setWaist(dto.getWaist() != null ? BigDecimal.valueOf(dto.getWaist()) : null);
        progress.setHips(dto.getHips() != null ? BigDecimal.valueOf(dto.getHips()) : null);
        progress.setNotes(dto.getNotes());

        // Calculate BMI: weight (kg) / height^2 (m)
        if (dto.getHeight() > 0) {
            double bmi = dto.getWeight() / (dto.getHeight() * dto.getHeight());
            progress.setBmi(BigDecimal.valueOf(bmi).setScale(2, RoundingMode.HALF_UP));
        } else {
            progress.setBmi(BigDecimal.ZERO);
        }

        Progress saved = progressRepository.save(progress);
        return convertToDTO(saved);
    }

    @Override
    public List<ProgressDTO> getMemberProgressHistory(Long memberId) {
        // Return ordered by date ascending for easier charting
        return progressRepository.findByMemberIdOrderByRecordedDateAsc(memberId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProgressDTO getProgressById(Long id) {
        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress", "id", id));
        return convertToDTO(progress);
    }

    @Override
    @Transactional
    public void deleteProgressRecord(Long id) {
        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress", "id", id));
        progressRepository.delete(progress);
    }

    private ProgressDTO convertToDTO(Progress p) {
        ProgressDTO dto = new ProgressDTO();
        dto.setId(p.getId());
        dto.setMemberId(p.getMember().getId());
        dto.setMemberName(p.getMember().getUser().getFullName());
        dto.setRecordedDate(p.getRecordedDate());
        dto.setWeight(p.getWeight() != null ? p.getWeight().doubleValue() : 0.0);
        dto.setHeight(p.getHeight() != null ? p.getHeight().doubleValue() : 0.0);
        dto.setBodyFatPercentage(p.getBodyFatPercentage() != null ? p.getBodyFatPercentage().doubleValue() : null);
        dto.setMuscleMass(p.getMuscleMass() != null ? p.getMuscleMass().doubleValue() : null);
        dto.setChest(p.getChest() != null ? p.getChest().doubleValue() : null);
        dto.setWaist(p.getWaist() != null ? p.getWaist().doubleValue() : null);
        dto.setHips(p.getHips() != null ? p.getHips().doubleValue() : null);
        dto.setBmi(p.getBmi() != null ? p.getBmi().doubleValue() : 0.0);
        dto.setNotes(p.getNotes());
        return dto;
    }
}
