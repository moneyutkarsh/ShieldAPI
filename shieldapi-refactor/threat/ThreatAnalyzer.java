package com.shieldapi.shieldapi.threat;

/**
 * Threat analysis utility class.
 * Contains domain-specific logic for analyzing and categorizing threats.
 */
public class ThreatAnalyzer {

    /**
     * Analyzes the severity level of a threat based on its attributes.
     * @param name the threat name
     * @param description the threat description
     * @return severity level: LOW, MEDIUM, HIGH, or CRITICAL
     */
    public static String analyzeSeverity(String name, String description) {
        // TODO: Implement real threat severity analysis logic
        return "MEDIUM";
    }

    /**
     * Checks if a threat should trigger an alert.
     * @param severity the severity level
     * @return true if the threat should trigger an alert
     */
    public static boolean shouldAlert(String severity) {
        return "HIGH".equalsIgnoreCase(severity) || "CRITICAL".equalsIgnoreCase(severity);
    }
}
