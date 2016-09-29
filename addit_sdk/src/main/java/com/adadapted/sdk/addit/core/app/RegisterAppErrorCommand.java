package com.adadapted.sdk.addit.core.app;

import com.adadapted.sdk.addit.core.common.Command;

import java.util.Map;

/**
 * Created by chrisweeden on 9/29/16.
 */

public class RegisterAppErrorCommand extends Command {
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
