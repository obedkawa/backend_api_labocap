package com.labo.anapath.finance;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/refund-reasons")
@RequiredArgsConstructor
public class RefundReasonController {

    private final RefundReasonRepository refundReasonRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('view-invoices')")
    public ResponseEntity<ApiResponse<List<RefundReasonResponseDto>>> findAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<RefundReasonResponseDto> list = refundReasonRepository.findAll()
                .stream()
                .filter(r -> principal.getBranchId().equals(r.getBranchId()))
                .map(r -> new RefundReasonResponseDto(r.getId(), r.getLabel(), r.getBranchId()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('edit-invoices')")
    public ResponseEntity<ApiResponse<RefundReasonResponseDto>> create(
            @Valid @RequestBody RefundReasonRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        RefundReason reason = new RefundReason();
        reason.setLabel(dto.getLabel());
        reason.setBranchId(principal.getBranchId());
        RefundReason saved = refundReasonRepository.save(reason);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Motif créé",
                        new RefundReasonResponseDto(saved.getId(), saved.getLabel(), saved.getBranchId())));
    }
}
