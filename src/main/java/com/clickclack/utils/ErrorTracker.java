package com.clickclack.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ErrorTracker {

    private static final Logger logger = LoggerFactory.getLogger(ErrorTracker.class);

    public void logError(String message, Exception e) {
        if (e != null) {
            logger.error(message, e);
        } else {
            logger.error(message);
        }
    }

    public void logWarning(String message) {
        logger.warn(message);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logDebug(String message) {
        logger.debug(message);
    }

    /**
     * Generate a user-friendly error message.
     *
     * @param errorCode A unique error code for tracking
     * @return A user-readable error message
     */
    public String generateUserFriendlyMessage(String errorCode) {
        switch (errorCode) {
            case "PDF_GEN_ERR":
                return "There was an issue generating the PDF. Please try again later.";
            case "IO_ERR":
                return "An input/output error occurred. Please check your file or input.";
            case "QR_GEN_ERR":
                return "Failed to generate the QR Code. Please check the provided URL.";
            default:
                return "An unexpected error occurred. Please try again later.";
        }
    }
}
