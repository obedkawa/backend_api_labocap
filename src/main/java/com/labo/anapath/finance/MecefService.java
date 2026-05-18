package com.labo.anapath.finance;

import java.util.UUID;

public interface MecefService {

    InvoiceResponseDto confirmInvoice(UUID invoiceId, String uid, UUID branchId);

    void cancelInvoice(UUID invoiceId, String uid, UUID branchId);
}
