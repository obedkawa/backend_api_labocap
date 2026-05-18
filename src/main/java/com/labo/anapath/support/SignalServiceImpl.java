package com.labo.anapath.support;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.testorder.TestOrderRepository;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignalServiceImpl implements SignalService {

    private final SignalRepository signalRepository;
    private final TestOrderRepository testOrderRepository;
    private final UserRepository userRepository;
    private final SignalMapper signalMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SignalResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(signalRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(signalMapper::toResponseDto));
    }

    @Override
    @Transactional
    public SignalResponseDto create(SignalRequestDto dto, UUID userId, UUID branchId) {
        Signal signal = new Signal();
        signal.setBranchId(branchId);
        signal.setTypeSignal(dto.getTypeSignal());
        signal.setCommentaire(dto.getCommentaire());
        signal.setStatus(false);
        signal.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId)));
        signal.setTestOrder(testOrderRepository.findById(dto.getTestOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'examen", dto.getTestOrderId())));
        return signalMapper.toResponseDto(signalRepository.save(signal));
    }
}
