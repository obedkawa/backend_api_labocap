package com.labo.anapath.setting;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implémentation de {@link SettingService} gérant la logique métier
 * de la configuration applicative du laboratoire.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SettingServiceImpl implements SettingService {

    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<SettingResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(settingRepository.findByBranchId(branchId, PageRequest.of(page, size))
                .map(settingMapper::toResponseDto));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public SettingResponseDto findById(UUID id) {
        return settingMapper.toResponseDto(settingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre", id)));
    }

    /**
     * {@inheritDoc}
     * La logique upsert utilise {@code orElseGet} pour créer l'entité uniquement
     * si aucun paramètre existant ne correspond à la paire (clé, filiale).
     */
    @Override
    @Transactional
    public SettingResponseDto upsert(SettingRequestDto dto, UUID branchId) {
        Setting setting = settingRepository.findByKeyAndBranchId(dto.getKey(), branchId)
                .orElseGet(() -> {
                    Setting s = new Setting();
                    s.setBranchId(branchId);
                    s.setKey(dto.getKey());
                    return s;
                });
        setting.setValue(dto.getValue());
        return settingMapper.toResponseDto(settingRepository.save(setting));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(UUID id) {
        Setting setting = settingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre", id));
        settingRepository.delete(setting);
    }
}
