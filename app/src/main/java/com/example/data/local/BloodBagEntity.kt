package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.BloodBag
import com.example.model.BloodComponent
import com.example.model.BloodStatus
import com.example.model.BloodType

@Entity(tableName = "blood_bags")
data class BloodBagEntity(
    @PrimaryKey
    val id: String,
    val bloodType: String,
    val quantityMl: Int,
    val component: String,
    val donorName: String,
    val donorId: String,
    val donorPhone: String,
    val collectionDate: String,
    val expirationDate: String,
    val storageLocation: String,
    val status: String,
    val isRare: Boolean,
    val notes: String,
    val createdAt: Long,
    val syncStatus: String
) {
    fun toDomain(): BloodBag {
        return BloodBag(
            id = id,
            bloodType = BloodType.fromLabel(bloodType),
            quantityMl = quantityMl,
            component = BloodComponent.fromDisplayName(component),
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
            syncStatus = syncStatus
        )
    }

    companion object {
        fun fromDomain(bag: BloodBag): BloodBagEntity {
            return BloodBagEntity(
                id = bag.id,
                bloodType = bag.bloodType.label,
                quantityMl = bag.quantityMl,
                component = bag.component.displayName,
                donorName = bag.donorName,
                donorId = bag.donorId,
                donorPhone = bag.donorPhone,
                collectionDate = bag.collectionDate,
                expirationDate = bag.expirationDate,
                storageLocation = bag.storageLocation,
                status = bag.status.displayName,
                isRare = bag.isRare,
                notes = bag.notes,
                createdAt = bag.createdAt,
                syncStatus = bag.syncStatus
            )
        }
    }
}
