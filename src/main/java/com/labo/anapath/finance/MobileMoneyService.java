package com.labo.anapath.finance;

import java.util.UUID;

public interface MobileMoneyService {

    MobileMoneyStatusResponseDto initiate(MobileMoneyInitiateRequestDto dto, UUID branchId);

    MobileMoneyStatusResponseDto checkStatus(UUID paymentId, UUID branchId);
}
