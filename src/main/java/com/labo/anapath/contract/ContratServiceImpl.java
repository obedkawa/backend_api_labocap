package com.labo.anapath.contract;

import com.labo.anapath.client.ClientRepository;
import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.doctor.HospitalRepository;
import com.labo.anapath.finance.Invoice;
import com.labo.anapath.finance.InvoiceRepository;
import com.labo.anapath.finance.InvoiceStatus;
import com.labo.anapath.test.LabTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements ContratService {

    private final ContratRepository contratRepository;
    private final HospitalRepository hospitalRepository;
    private final LabTestRepository labTestRepository;
    private final DetailsContratRepository detailsContratRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final ContratMapper contratMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContratResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(contratRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(c -> {
                    ContratResponseDto dto = contratMapper.toResponseDto(c);
                    return addClientName(c, dto);
                }));
    }

    @Override
    @Transactional(readOnly = true)
    public ContratResponseDto findById(UUID id) {
        Contrat c = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
        return addClientName(c, contratMapper.toResponseDto(c));
    }

    @Override
    @Transactional
    public ContratResponseDto create(ContratRequestDto dto, UUID branchId) {
        Contrat contrat = new Contrat();
        contrat.setBranchId(branchId);
        contrat.setName(dto.getName());
        contrat.setType(dto.getType());
        contrat.setDescription(dto.getDescription());
        contrat.setStartDate(dto.getStartDate());
        contrat.setEndDate(dto.getEndDate());
        contrat.setNbrTests(dto.getNbrTests());
        contrat.setStatus("INACTIF");
        contrat.setInvoiceUnique(dto.getInvoiceUnique() != null ? dto.getInvoiceUnique() : true);
        contrat.setClientId(dto.getClientId());

        if (dto.getHospitalId() != null) {
            contrat.setHospital(hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hôpital", dto.getHospitalId())));
        }

        List<DetailsContrat> details = new ArrayList<>();
        for (ContratRequestDto.ContratDetailRequestDto detailDto : dto.getDetails()) {
            DetailsContrat detail = new DetailsContrat();
            detail.setContrat(contrat);
            detail.setPrice(detailDto.getPrice());
            detail.setLabTest(labTestRepository.findById(detailDto.getLabTestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Analyse", detailDto.getLabTestId())));
            details.add(detail);
        }
        contrat.setDetails(details);

        Contrat saved = contratRepository.save(contrat);
        return addClientName(saved, contratMapper.toResponseDto(saved));
    }

    @Override
    @Transactional
    public ContratResponseDto update(UUID id, ContratRequestDto dto) {
        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
        contrat.setName(dto.getName());
        contrat.setType(dto.getType());
        contrat.setDescription(dto.getDescription());
        contrat.setStartDate(dto.getStartDate());
        contrat.setEndDate(dto.getEndDate());
        contrat.setNbrTests(dto.getNbrTests());
        return addClientName(contrat, contratMapper.toResponseDto(contratRepository.save(contrat)));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
        contratRepository.delete(contrat);
    }

    @Override
    @Transactional
    public DetailsContratDto addCategoryDetail(UUID contractId, CategoryDetailRequestDto dto) {
        Contrat contrat = contratRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", contractId));
        boolean exists = contrat.getDetails().stream()
                .anyMatch(d -> dto.getCategoryTestId().equals(d.getCategoryTestId()) && d.getPourcentage() != null);
        if (exists) {
            throw new InvalidOperationException("CATEGORY_DETAIL_ALREADY_EXISTS");
        }
        DetailsContrat detail = new DetailsContrat();
        detail.setContrat(contrat);
        detail.setCategoryTestId(dto.getCategoryTestId());
        detail.setPourcentage(dto.getDiscount());
        return contratMapper.toDetailsDto(detailsContratRepository.save(detail));
    }

    @Override
    @Transactional
    public DetailsContratDto addTestDetail(UUID contractId, TestDetailRequestDto dto) {
        Contrat contrat = contratRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", contractId));
        var labTest = labTestRepository.findById(dto.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Analyse", dto.getTestId()));

        DetailsContrat detail = new DetailsContrat();
        detail.setContrat(contrat);
        detail.setLabTest(labTest);
        detail.setAmountRemise(dto.getAmountRemise());
        detail.setAmountAfterRemise(dto.getAmountAfterRemise());
        detail.setCategoryTestId(labTest.getCategoryTest() != null ? labTest.getCategoryTest().getId() : null);
        return contratMapper.toDetailsDto(detailsContratRepository.save(detail));
    }

    @Override
    @Transactional
    public ContratResponseDto activate(UUID contractId) {
        Contrat contrat = contratRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", contractId));
        if ("ACTIF".equals(contrat.getStatus())) {
            throw new InvalidOperationException("CONTRACT_ALREADY_ACTIVE");
        }
        contrat.setStatus("ACTIF");

        if (Boolean.TRUE.equals(contrat.getInvoiceUnique())) {
            boolean hasInvoice = invoiceRepository.findFirstByContratIdOrderByCreatedAtDesc(contractId).isPresent();
            if (!hasInvoice) {
                String code = generateCodeFacture(contrat.getBranchId());
                Invoice invoice = new Invoice();
                invoice.setBranchId(contrat.getBranchId());
                invoice.setContrat(contrat);
                invoice.setCode(code);
                invoice.setStatus(InvoiceStatus.PENDING);
                invoice.setClientName(resolveClientName(contrat));
                invoiceRepository.save(invoice);
            }
        }

        Contrat saved = contratRepository.save(contrat);
        return addClientName(saved, contratMapper.toResponseDto(saved));
    }

    @Override
    @Transactional
    public ContratResponseDto close(UUID contractId) {
        Contrat contrat = contratRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", contractId));
        contrat.setIsClose(true);
        return addClientName(contrat, contratMapper.toResponseDto(contratRepository.save(contrat)));
    }

    @Override
    @Transactional
    public void deleteDetail(UUID contractId, UUID detailId) {
        DetailsContrat detail = detailsContratRepository.findById(detailId)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne contrat", detailId));
        detailsContratRepository.delete(detail);
    }

    private String generateCodeFacture(UUID branchId) {
        int year = LocalDate.now().getYear();
        List<Invoice> invoices = invoiceRepository.findByBranchIdAndCodeNotNullAndYear(
                branchId, year, PageRequest.of(0, 1));
        String seq = "0001";
        if (!invoices.isEmpty()) {
            String lastCode = invoices.get(0).getCode();
            try {
                int lastSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 4));
                seq = String.format("%04d", lastSeq + 1);
            } catch (NumberFormatException e) {
                seq = "0001";
            }
        }
        return "FA" + (year % 100) + seq;
    }

    private String resolveClientName(Contrat contrat) {
        if (contrat.getClientId() != null) {
            return clientRepository.findById(contrat.getClientId())
                    .map(c -> c.getName())
                    .orElse("");
        }
        if (contrat.getHospital() != null) {
            return contrat.getHospital().getName();
        }
        return "";
    }

    // MapStruct doesn't know clientName (it's resolved from clientId via a repo lookup)
    private ContratResponseDto addClientName(Contrat contrat, ContratResponseDto dto) {
        String clientName = resolveClientName(contrat);
        return new ContratResponseDto(
                dto.id(), dto.name(), dto.type(), dto.description(),
                dto.hospitalId(), dto.hospitalName(),
                contrat.getClientId(), clientName,
                dto.nbrTests(), dto.startDate(), dto.endDate(),
                dto.status(), dto.invoiceUnique(), dto.isClose(),
                dto.details(), dto.branchId(), dto.createdAt());
    }
}
