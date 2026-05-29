package com.labo.anapath.hr;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.User;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeTimeoffServiceImpl implements EmployeeTimeoffService {

    private final EmployeeTimeoffRepository employeeTimeoffRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeTimeoffResponseDto> findAll(int page, int size, UUID employeeId) {
        return PageResponse.of(employeeTimeoffRepository.findByEmployeeId(employeeId,
                PageRequest.of(page, size)).map(this::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeTimeoffResponseDto findById(UUID id) {
        return toDto(employeeTimeoffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id)));
    }

    @Override
    @Transactional
    public EmployeeTimeoffResponseDto create(EmployeeTimeoffRequestDto dto, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", employeeId));
        EmployeeTimeoff timeoff = new EmployeeTimeoff();
        timeoff.setEmployee(employee);
        timeoff.setStartDate(dto.getStartDate());
        timeoff.setEndDate(dto.getEndDate());
        timeoff.setReason(dto.getReason());
        timeoff.setStatus(TimeoffStatus.PENDING);
        return toDto(employeeTimeoffRepository.save(timeoff));
    }

    @Override
    @Transactional
    public EmployeeTimeoffResponseDto updateStatus(UUID id, TimeoffStatusUpdateDto dto) {
        EmployeeTimeoff timeoff = employeeTimeoffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));

        TimeoffStatus previousStatus = timeoff.getStatus();
        timeoff.setStatus(dto.getStatus());
        EmployeeTimeoff saved = employeeTimeoffRepository.save(timeoff);

        Employee employee = timeoff.getEmployee();
        if (employee.getUser() != null) {
            User user = employee.getUser();
            if (dto.getStatus() == TimeoffStatus.APPROVED) {
                user.setActive(false);
                userRepository.save(user);
            } else if (previousStatus == TimeoffStatus.APPROVED
                    && (dto.getStatus() == TimeoffStatus.REJECTED
                        || dto.getStatus() == TimeoffStatus.PENDING)) {
                user.setActive(true);
                userRepository.save(user);
            }
        }

        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        EmployeeTimeoff timeoff = employeeTimeoffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));
        if (timeoff.getStatus() == TimeoffStatus.APPROVED) {
            throw new InvalidOperationException("Impossible de supprimer un congé approuvé");
        }
        employeeTimeoffRepository.deleteById(id);
    }

    private EmployeeTimeoffResponseDto toDto(EmployeeTimeoff t) {
        return new EmployeeTimeoffResponseDto(
                t.getId(), t.getEmployee().getId(), t.getStartDate(),
                t.getEndDate(), t.getReason(), t.getStatus(), t.getCreatedAt());
    }
}
