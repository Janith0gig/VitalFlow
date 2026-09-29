package com.example.data.repository

import com.example.model.BloodBag
import com.example.model.BloodComponent
import com.example.model.BloodStatus
import com.example.model.BloodType
import java.time.LocalDate

object SampleData {
    fun getInitialStock(): List<BloodBag> {
        val today = LocalDate.now()

        return listOf(
            // URGENT ALERT: Rare O- expiring in 2 days!
            BloodBag(
                id = "BB-2026-0819",
                bloodType = BloodType.O_NEG,
                quantityMl = 450,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "Eleanor Vance",
                donorId = "D-10924",
                donorPhone = "+1 (555) 234-9812",
                collectionDate = today.minusDays(40).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(2).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit A - Emergency Vault (4°C)",
                status = BloodStatus.EXPIRING_SOON,
                isRare = true,
                notes = "CRITICAL: Universal RBC Donor. Expiry in 48 hours. Priority cross-match required."
            ),
            // URGENT ALERT: Rare AB- expiring in 4 days!
            BloodBag(
                id = "BB-2026-0824",
                bloodType = BloodType.AB_NEG,
                quantityMl = 450,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "Marcus Thorne",
                donorId = "D-20381",
                donorPhone = "+1 (555) 345-6789",
                collectionDate = today.minusDays(38).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(4).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit A - Tier 1 (4°C)",
                status = BloodStatus.EXPIRING_SOON,
                isRare = true,
                notes = "Rarest blood group (<1%). Contact Hematology ICU for waiting recipients."
            ),
            // Rare O- Fresh bag (low stock reminder!)
            BloodBag(
                id = "BB-2026-0901",
                bloodType = BloodType.O_NEG,
                quantityMl = 450,
                component = BloodComponent.PACKED_RBC,
                donorName = "Clara Oswald",
                donorId = "D-30491",
                donorPhone = "+1 (555) 456-7890",
                collectionDate = today.minusDays(8).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(34).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit A - Emergency Vault (4°C)",
                status = BloodStatus.FRESH,
                isRare = true,
                notes = "Emergency trauma reserve. Only 2 O- units currently in cold storage."
            ),
            // Expiring soon: A+ within 5 days
            BloodBag(
                id = "BB-2026-0798",
                bloodType = BloodType.A_POS,
                quantityMl = 450,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "Arthur Pendelton",
                donorId = "D-11822",
                donorPhone = "+1 (555) 567-8901",
                collectionDate = today.minusDays(37).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(5).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit B - Shelf 2 (4°C)",
                status = BloodStatus.EXPIRING_SOON,
                isRare = false,
                notes = "Scheduled for surgical cross-match."
            ),
            // Expired stock for removal / audit demonstration
            BloodBag(
                id = "BB-2026-0711",
                bloodType = BloodType.B_NEG,
                quantityMl = 350,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "David Kim",
                donorId = "D-09823",
                donorPhone = "+1 (555) 678-9012",
                collectionDate = today.minusDays(44).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.minusDays(2).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit C - Quarantine Box",
                status = BloodStatus.EXPIRED,
                isRare = true,
                notes = "Expired 2 days ago. Flagged for safe biomedical disposal."
            ),
            // Fresh A+ Stock
            BloodBag(
                id = "BB-2026-0915",
                bloodType = BloodType.A_POS,
                quantityMl = 500,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "Sophia Martinez",
                donorId = "D-40112",
                donorPhone = "+1 (555) 789-0123",
                collectionDate = today.minusDays(5).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(37).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit B - Shelf 1 (4°C)",
                status = BloodStatus.FRESH,
                isRare = false,
                notes = "Routine mobile donor drive collection."
            ),
            // Fresh A- Stock
            BloodBag(
                id = "BB-2026-0912",
                bloodType = BloodType.A_NEG,
                quantityMl = 450,
                component = BloodComponent.PACKED_RBC,
                donorName = "Liam Gallagher",
                donorId = "D-40552",
                donorPhone = "+1 (555) 890-1234",
                collectionDate = today.minusDays(6).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(36).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit B - Shelf 3 (4°C)",
                status = BloodStatus.FRESH,
                isRare = false,
                notes = "High vitality unit."
            ),
            // Fresh B+ Stock
            BloodBag(
                id = "BB-2026-0920",
                bloodType = BloodType.B_POS,
                quantityMl = 450,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "Amara Okafor",
                donorId = "D-51009",
                donorPhone = "+1 (555) 901-2345",
                collectionDate = today.minusDays(2).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(40).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit C - Shelf 1 (4°C)",
                status = BloodStatus.FRESH,
                isRare = false,
                notes = "New donation."
            ),
            // Fresh B- Stock
            BloodBag(
                id = "BB-2026-0918",
                bloodType = BloodType.B_NEG,
                quantityMl = 450,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "Ethan Wright",
                donorId = "D-51234",
                donorPhone = "+1 (555) 012-3456",
                collectionDate = today.minusDays(4).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(38).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit C - Shelf 2 (4°C)",
                status = BloodStatus.FRESH,
                isRare = true,
                notes = "Rare Rh-negative stock."
            ),
            // Fresh AB+ (Universal Plasma / Recipient)
            BloodBag(
                id = "BB-2026-0922",
                bloodType = BloodType.AB_POS,
                quantityMl = 450,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "Hannah Zhang",
                donorId = "D-60199",
                donorPhone = "+1 (555) 123-4567",
                collectionDate = today.minusDays(1).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(41).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit D - Shelf 1 (4°C)",
                status = BloodStatus.FRESH,
                isRare = false,
                notes = "Optimal for plasma extraction."
            ),
            // Fresh O+ High Volume Stock
            BloodBag(
                id = "BB-2026-0923",
                bloodType = BloodType.O_POS,
                quantityMl = 500,
                component = BloodComponent.WHOLE_BLOOD,
                donorName = "Gabriel Silva",
                donorId = "D-70882",
                donorPhone = "+1 (555) 234-5671",
                collectionDate = today.minusDays(3).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(39).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit D - Shelf 2 (4°C)",
                status = BloodStatus.FRESH,
                isRare = false,
                notes = "Highest clinical turnover type."
            ),
            // Fresh O+ Second Unit
            BloodBag(
                id = "BB-2026-0925",
                bloodType = BloodType.O_POS,
                quantityMl = 450,
                component = BloodComponent.PACKED_RBC,
                donorName = "Zoe Campbell",
                donorId = "D-70993",
                donorPhone = "+1 (555) 345-6712",
                collectionDate = today.minusDays(2).format(BloodBag.DATE_FORMATTER),
                expirationDate = today.plusDays(40).format(BloodBag.DATE_FORMATTER),
                storageLocation = "Unit D - Shelf 3 (4°C)",
                status = BloodStatus.FRESH,
                isRare = false,
                notes = "Routine surgical reservation."
            )
        )
    }
}
