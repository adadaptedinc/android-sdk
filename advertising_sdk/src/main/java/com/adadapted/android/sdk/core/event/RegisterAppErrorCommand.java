package com.adadapted.android.sdk.core.event;

import java.util.Map;

public class RegisterAppErrorCommand {
    private final String errorCode;
    private final String errorMessage;
    private final Map<String, String> errorParams;

    public RegisterAppErrorCommand(final String errorCode,
                                   final String errorMessage,
                                   final Map<String, String> errorParams) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorParams = errorParams;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, String> getErrorParams() {
        return errorParams;
    }
}
