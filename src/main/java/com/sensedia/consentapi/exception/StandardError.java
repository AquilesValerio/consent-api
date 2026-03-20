package com.sensedia.consentapi.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardError {

    private int code;
    private String title;
    private String detail;
    private String requestDateTime;
}
