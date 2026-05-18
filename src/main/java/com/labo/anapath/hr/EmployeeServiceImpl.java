package com.labo.anapath.hr;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implémentation de {@link EmployeeService} gérant la logique métier
 * des employés du laboratoire.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;

    /**
     * {@inheritDoc}
     * Les employés sont triés par date de création décroissante.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(employeeRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(employeeMapper::toResponseDto));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto findById(UUID id) {
        return employeeMapper.toResponseDto(employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", id)));
    }

    /**
     * {@inheritDoc}
     * Le lien vers le compte utilisateur applicatif est optionnel : il est
     * établi uniquement si un {@code userId} est fourni dans le DTO.
     */
    @Override
    @Transactional
    public EmployeeResponseDto create(EmployeeRequestDto dto, UUID branchId) {
        Employee employee = employeeMapper.toEntity(dto);
        employee.setBranchId(branchId);
        if (dto.getUserId() != null) {
            employee.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", dto.getUserId())));
        }
        return employeeMapper.toResponseDto(employeeRepository.save(employee));
    }

    /**
     * {@inheritDoc}
     * Le salaire et la date d'embauche ne sont mis à jour que s'ils sont
     * renseignés dans le DTO (null = conserver la valeur existante).
     */
    @Override
    @Transactional
    public EmployeeResponseDto update(UUID id, EmployeeRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", id));
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setPhone(dto.getPhone());
        employee.setEmail(dto.getEmail());
        employee.setPosition(dto.getPosition());
        if (dto.getSalary() != null) employee.setSalary(dto.getSalary());
        if (dto.getHireDate() != null) employee.setHireDate(dto.getHireDate());
        return employeeMapper.toResponseDto(employeeRepository.save(employee));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", id));
        employeeRepository.delete(employee);
    }
}
