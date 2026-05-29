package com.labo.anapath.hr;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeContratServiceImpl implements EmployeeContratService {

    private final EmployeeContratRepository employeeContratRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeContratResponseDto> findAll(int page, int size, UUID employeeId) {
        return PageResponse.of(employeeContratRepository.findByEmployeeId(employeeId,
                PageRequest.of(page, size)).map(this::toDto));
    }

    @Override
    @Transactional
    public EmployeeContratResponseDto create(EmployeeContratRequestDto dto, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", employeeId));
        EmployeeContrat contrat = new EmployeeContrat();
        contrat.setEmployee(employee);
        contrat.setType(dto.getType());
        contrat.setStartDate(dto.getStartDate());
        contrat.setEndDate(dto.getEndDate());
        contrat.setSalary(dto.getSalary());
        return toDto(employeeContratRepository.save(contrat));
    }

    @Override
    @Transactional
    public EmployeeContratResponseDto update(UUID id, EmployeeContratRequestDto dto) {
        EmployeeContrat contrat = employeeContratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat employé", id));
        contrat.setType(dto.getType());
        contrat.setStartDate(dto.getStartDate());
        contrat.setEndDate(dto.getEndDate());
        contrat.setSalary(dto.getSalary());
        return toDto(employeeContratRepository.save(contrat));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        employeeContratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat employé", id));
        employeeContratRepository.deleteById(id);
    }

    private EmployeeContratResponseDto toDto(EmployeeContrat c) {
        return new EmployeeContratResponseDto(
                c.getId(), c.getEmployee().getId(), c.getType(),
                c.getStartDate(), c.getEndDate(), c.getSalary(), c.getCreatedAt());
    }
}
