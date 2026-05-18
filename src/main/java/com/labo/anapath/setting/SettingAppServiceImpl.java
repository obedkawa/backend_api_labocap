package com.labo.anapath.setting;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingAppServiceImpl implements SettingAppService {

    private final SettingAppRepository settingAppRepository;
    private final SettingAppMapper settingAppMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SettingAppResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(settingAppRepository.findByBranchId(branchId, PageRequest.of(page, size))
                .map(settingAppMapper::toResponseDto));
    }

    @Override
    @Transactional(readOnly = true)
    public SettingAppResponseDto findById(UUID id) {
        return settingAppMapper.toResponseDto(settingAppRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre app", id)));
    }

    @Override
    @Transactional
    public SettingAppResponseDto upsert(SettingAppRequestDto dto, UUID branchId) {
        SettingApp sa = settingAppRepository.findByKeyAndBranchId(dto.getKey(), branchId)
                .orElseGet(() -> {
                    SettingApp s = new SettingApp();
                    s.setBranchId(branchId);
                    s.setKey(dto.getKey());
                    return s;
                });
        sa.setValue(dto.getValue());
        return settingAppMapper.toResponseDto(settingAppRepository.save(sa));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SettingApp sa = settingAppRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre app", id));
        settingAppRepository.delete(sa);
    }
}
