package com.clickclack.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String errorCode;
    private String message;
    private String details;
    private LocalDateTime timestamp = LocalDateTime.now();
}

