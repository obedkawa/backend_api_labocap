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
public class SettingInvoiceServiceImpl implements SettingInvoiceService {

    private final SettingInvoiceRepository settingInvoiceRepository;
    private final SettingInvoiceMapper settingInvoiceMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SettingInvoiceResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(settingInvoiceRepository.findByBranchId(branchId, PageRequest.of(page, size))
                .map(settingInvoiceMapper::toResponseDto));
    }

    @Override
    @Transactional(readOnly = true)
    public SettingInvoiceResponseDto findById(UUID id) {
        return settingInvoiceMapper.toResponseDto(settingInvoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration MECeF", id)));
    }

    @Override
    @Transactional
    public SettingInvoiceResponseDto update(UUID id, SettingInvoiceRequestDto dto) {
        SettingInvoice si = settingInvoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration MECeF", id));
        if (dto.getIfu() != null)    si.setIfu(dto.getIfu());
        if (dto.getToken() != null)  si.setToken(dto.getToken());
        if (dto.getStatus() != null) si.setStatus(dto.getStatus());
        return settingInvoiceMapper.toResponseDto(settingInvoiceRepository.save(si));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SettingInvoice si = settingInvoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration MECeF", id));
        settingInvoiceRepository.delete(si);
    }
}
