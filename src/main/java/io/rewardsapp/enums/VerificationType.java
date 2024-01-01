package io.rewardsapp.enums;

/**
 * Represents different types of verifications in the RecyclingRewards application.
 */
public enum VerificationType {
    ACCOUNT("ACCOUNT"),
    PASSWORD("PASSWORD");

    private final String type;

    VerificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type.toLowerCase();
    }
}
