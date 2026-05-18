package com.labo.anapath.testorder;

import java.util.List;

public record AssignmentPrintDto(
        AssignmentResponseDto assignment,
        List<AssignmentDetailResponseDto> details,
        String branchName,
        String branchAddress
) {}
