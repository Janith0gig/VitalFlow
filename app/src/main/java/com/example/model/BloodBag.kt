package com.example.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class BloodStatus(val displayName: String) {
    FRESH("Fresh"),
    EXPIRING_SOON("Expiring Soon"),
    EXPIRED("Expired"),
    TRANSFUSED("Transfused");

    companion object {
        fun fromDisplayName(name: String): BloodStatus {
            return entries.find { it.displayName.equals(name.trim(), ignoreCase = true) } ?: FRESH
        }
    }
}

data class BloodBag(
    val id: String,
    val bloodType: BloodType,
    val quantityMl: Int,
    val component: BloodComponent = BloodComponent.WHOLE_BLOOD,
    val donorName: String,
    val donorId: String,
    val donorPhone: String = "",
    val collectionDate: String, // ISO-8601 YYYY-MM-DD
    val expirationDate: String, // ISO-8601 YYYY-MM-DD
    val storageLocation: String = "Refrigerator 1 (4°C)",
    val status: BloodStatus = BloodStatus.FRESH,
    val isRare: Boolean = bloodType.isRare,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED" // SYNCED, PENDING_INSERT, PENDING_UPDATE, PENDING_DELETE
) {
    companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun calculateExpiration(collectionDateStr: String, daysToAdd: Long = 42): String {
            return try {
                val parsed = LocalDate.parse(collectionDateStr, DATE_FORMATTER)
                parsed.plusDays(daysToAdd).format(DATE_FORMATTER)
            } catch (e: Exception) {
                LocalDate.now().plusDays(daysToAdd).format(DATE_FORMATTER)
            }
        }
    }

    fun getDaysUntilExpiration(referenceDate: LocalDate = LocalDate.now()): Long {
        return try {
            val exp = LocalDate.parse(expirationDate, DATE_FORMATTER)
            ChronoUnit.DAYS.between(referenceDate, exp)
        } catch (e: Exception) {
            0
        }
    }

    fun getEffectiveStatus(referenceDate: LocalDate = LocalDate.now()): BloodStatus {
        if (status == BloodStatus.TRANSFUSED) return BloodStatus.TRANSFUSED
        val days = getDaysUntilExpiration(referenceDate)
        return when {
            days < 0 -> BloodStatus.EXPIRED
            days <= 7 -> BloodStatus.EXPIRING_SOON
            else -> BloodStatus.FRESH
        }
    }

    val isUrgent: Boolean
        get() {
            val days = getDaysUntilExpiration()
            return days in 0..7 || isRare
        }
}
