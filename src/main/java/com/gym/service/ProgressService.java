package com.gym.service;

import com.gym.dto.ProgressDTO;
import java.util.List;

public interface ProgressService {
    ProgressDTO addProgressRecord(ProgressDTO progressDTO);
    List<ProgressDTO> getMemberProgressHistory(Long memberId);
    ProgressDTO getProgressById(Long id);
    void deleteProgressRecord(Long id);
}
