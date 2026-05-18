package com.labo.anapath.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRequestDto {

    @NotNull
    private UUID receiverId;

    @NotBlank
    private String message;
}
