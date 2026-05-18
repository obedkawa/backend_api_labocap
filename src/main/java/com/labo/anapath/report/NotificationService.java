package com.labo.anapath.report;

import java.util.UUID;

public interface NotificationService {

    CallResponseDto callPatient(UUID reportId, UUID userId);

    SmsResponseDto sendSms(UUID reportId, UUID userId);

    AppelResponseDto getAppelStatus(UUID reportId);
}
