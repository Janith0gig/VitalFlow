package com.example.data.remote

import com.example.model.BloodBag
import com.example.model.BloodComponent
import com.example.model.BloodStatus
import com.example.model.BloodType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseBloodBagDto(
    @Json(name = "id") val id: String,
    @Json(name = "blood_type") val bloodType: String,
    @Json(name = "quantity_ml") val quantityMl: Int,
    @Json(name = "component_type") val componentType: String,
    @Json(name = "donor_name") val donorName: String,
    @Json(name = "donor_id") val donorId: String,
    @Json(name = "donor_phone") val donorPhone: String = "",
    @Json(name = "collection_date") val collectionDate: String,
    @Json(name = "expiration_date") val expirationDate: String,
    @Json(name = "storage_location") val storageLocation: String = "Refrigerator 1 (4°C)",
    @Json(name = "status") val status: String = "Fresh",
    @Json(name = "is_rare") val isRare: Boolean = false,
    @Json(name = "notes") val notes: String = "",
    @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): BloodBag {
        return BloodBag(
            id = id,
            bloodType = BloodType.fromLabel(bloodType),
            quantityMl = quantityMl,
            component = BloodComponent.fromDisplayName(componentType),
            donorName = donorName,
            donorId = donorId,
            donorPhone = donorPhone,
            collectionDate = collectionDate,
            expirationDate = expirationDate,
            storageLocation = storageLocation,
            status = BloodStatus.fromDisplayName(status),
            isRare = isRare,
            notes = notes,
            createdAt = createdAt,
            syncStatus = "SYNCED"
        )
    }

    companion object {
        fun fromDomain(domain: BloodBag): SupabaseBloodBagDto {
            return SupabaseBloodBagDto(
                id = domain.id,
                bloodType = domain.bloodType.label,
                quantityMl = domain.quantityMl,
                componentType = domain.component.displayName,
                donorName = domain.donorName,
                donorId = domain.donorId,
                donorPhone = domain.donorPhone,
                collectionDate = domain.collectionDate,
                expirationDate = domain.expirationDate,
                storageLocation = domain.storageLocation,
                status = domain.status.displayName,
                isRare = domain.isRare,
                notes = domain.notes,
                createdAt = domain.createdAt
            )
        }
    }
}
