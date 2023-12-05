package io.rewardsapp.enums;

/**
 * The VerificationType enum represents different types of verifications in the recycling rewards application.
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
