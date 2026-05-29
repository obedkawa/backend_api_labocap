package com.labo.anapath.dashboard;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardDto.AdminStats>> getAdminStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getAdminStats(principal.getBranchId())));
    }

    @GetMapping("/secretariat-stats")
    public ResponseEntity<ApiResponse<DashboardDto.SecretariatStats>> getSecretariatStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getSecretariatStats(principal.getBranchId())));
    }

    @GetMapping("/reports-today")
    public ResponseEntity<ApiResponse<List<DashboardDto.ReportToday>>> getReportsToday(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getReportsToday(principal.getBranchId())));
    }

    @GetMapping("/doctor-stats")
    public ResponseEntity<ApiResponse<List<DashboardDto.DoctorStat>>> getDoctorStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getDoctorStats(principal.getBranchId())));
    }

    @GetMapping("/top-examens")
    public ResponseEntity<ApiResponse<List<DashboardDto.TopExamen>>> getTopExamens(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getTopExamens(principal.getBranchId())));
    }

    @GetMapping("/monthly-stats")
    public ResponseEntity<ApiResponse<DashboardDto.MonthlyStats>> getMonthlyStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getMonthlyStats(principal.getBranchId())));
    }

    @GetMapping("/connected-users")
    public ResponseEntity<ApiResponse<List<DashboardDto.ConnectedUser>>> getConnectedUsers(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getConnectedUsers(principal.getBranchId())));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<DashboardDto.RevenueData>> getRevenue(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getRevenueData(principal.getBranchId())));
    }

    @GetMapping("/invoice-status")
    public ResponseEntity<ApiResponse<DashboardDto.InvoiceStatus>> getInvoiceStatus(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getInvoiceStatus(principal.getBranchId())));
    }

    @GetMapping("/doctor/exam-status")
    public ResponseEntity<ApiResponse<DashboardDto.ExamStatusChart>> getDoctorExamStatus(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getExamStatusForDoctor(principal.getId(), principal.getBranchId())));
    }

    @GetMapping("/doctor/appointments")
    public ResponseEntity<ApiResponse<List<DashboardDto.AppointmentDto>>> getDoctorAppointments(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getAppointmentsForDoctor(principal.getId(), principal.getBranchId())));
    }

    @GetMapping("/doctor/orders")
    public ResponseEntity<ApiResponse<List<DashboardDto.DoctorOrder>>> getDoctorOrders(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getDoctorOrders(principal.getId(), principal.getBranchId())));
    }

    @GetMapping("/doctor/orders-today")
    public ResponseEntity<ApiResponse<List<DashboardDto.DoctorOrder>>> getDoctorOrdersToday(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getDoctorOrdersToday(principal.getId(), principal.getBranchId())));
    }
}
