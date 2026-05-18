package com.labo.anapath.finance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FinanceMapper {

    @Mapping(target = "code", source = "code")
    @Mapping(target = "paid", source = "paid")
    @Mapping(target = "testOrderId", source = "testOrder.id")
    @Mapping(target = "testOrderCode", source = "testOrder.code")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", expression = "java(invoice.getPatient() != null ? invoice.getPatient().getFirstName() + ' ' + invoice.getPatient().getLastName() : null)")
    @Mapping(target = "contratId", source = "contrat.id")
    @Mapping(target = "contratName", source = "contrat.name")
    @Mapping(target = "statusInvoice", source = "statusInvoice")
    @Mapping(target = "payment", source = "payment")
    @Mapping(target = "codeMecef", source = "codeMecef")
    @Mapping(target = "details", source = "details")
    InvoiceResponseDto toInvoiceResponseDto(Invoice invoice);

    @Mapping(target = "labTestId", source = "labTest.id")
    @Mapping(target = "testName", source = "testName")
    InvoiceDetailDto toInvoiceDetailDto(InvoiceDetail detail);

    @Mapping(target = "invoiceId", source = "invoice.id")
    PaymentResponseDto toPaymentResponseDto(Payment payment);
}
