package com.example.model

enum class BloodType(val label: String, val isRare: Boolean, val rarityDescription: String) {
    O_NEG("O-", true, "Universal RBC Donor - Critically Rare (<7%)"),
    AB_NEG("AB-", true, "Rarest Blood Type in Population (<1%)"),
    B_NEG("B-", true, "Rare Rh-Negative Type (~1.5%)"),
    A_NEG("A-", false, "Rh-Negative Donor Type (~6%)"),
    O_POS("O+", false, "High Demand Whole Blood (~38%)"),
    A_POS("A+", false, "Common Blood Type (~34%)"),
    B_POS("B+", false, "Regular Stock (~9%)"),
    AB_POS("AB+", false, "Universal Plasma Donor / RBC Recipient (~3%)");

    companion object {
        fun fromLabel(label: String): BloodType {
            return entries.find { it.label.equals(label.trim(), ignoreCase = true) } ?: O_POS
        }

        // Returns compatible DONOR blood types for a given RECIPIENT
        fun getCompatibleDonorsForRbc(recipient: BloodType): Set<BloodType> {
            return when (recipient) {
                O_NEG -> setOf(O_NEG)
                O_POS -> setOf(O_NEG, O_POS)
                A_NEG -> setOf(O_NEG, A_NEG)
                A_POS -> setOf(O_NEG, O_POS, A_NEG, A_POS)
                B_NEG -> setOf(O_NEG, B_NEG)
                B_POS -> setOf(O_NEG, O_POS, B_NEG, B_POS)
                AB_NEG -> setOf(O_NEG, A_NEG, B_NEG, AB_NEG)
                AB_POS -> entries.toSet() // Universal Recipient
            }
        }

        // Returns compatible RECIPIENTS for a given DONOR
        fun getCompatibleRecipientsForRbc(donor: BloodType): Set<BloodType> {
            return when (donor) {
                O_NEG -> entries.toSet() // Universal Donor
                O_POS -> setOf(O_POS, A_POS, B_POS, AB_POS)
                A_NEG -> setOf(A_NEG, A_POS, AB_NEG, AB_POS)
                A_POS -> setOf(A_POS, AB_POS)
                B_NEG -> setOf(B_NEG, B_POS, AB_NEG, AB_POS)
                B_POS -> setOf(B_POS, AB_POS)
                AB_NEG -> setOf(AB_NEG, AB_POS)
                AB_POS -> setOf(AB_POS)
            }
        }
    }
}
