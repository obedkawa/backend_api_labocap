package com.labo.anapath.dashboard;

import java.util.List;
import java.util.UUID;

public interface DashboardService {
    DashboardDto.AdminStats getAdminStats(UUID branchId);
    DashboardDto.SecretariatStats getSecretariatStats(UUID branchId);
    List<DashboardDto.ReportToday> getReportsToday(UUID branchId);
    List<DashboardDto.DoctorStat> getDoctorStats(UUID branchId);
    List<DashboardDto.TopExamen> getTopExamens(UUID branchId);
    DashboardDto.MonthlyStats getMonthlyStats(UUID branchId);
    List<DashboardDto.ConnectedUser> getConnectedUsers(UUID branchId);
    DashboardDto.RevenueData getRevenueData(UUID branchId);
    DashboardDto.InvoiceStatus getInvoiceStatus(UUID branchId);
    DashboardDto.ExamStatusChart getExamStatusForDoctor(UUID userId, UUID branchId);
    List<DashboardDto.AppointmentDto> getAppointmentsForDoctor(UUID userId, UUID branchId);
    List<DashboardDto.DoctorOrder> getDoctorOrders(UUID userId, UUID branchId);
    List<DashboardDto.DoctorOrder> getDoctorOrdersToday(UUID userId, UUID branchId);
}
