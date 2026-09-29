package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodBagDao {
    @Query("SELECT * FROM blood_bags WHERE syncStatus != 'PENDING_DELETE' ORDER BY expirationDate ASC")
    fun getAllBloodBags(): Flow<List<BloodBagEntity>>

    @Query("SELECT * FROM blood_bags WHERE id = :id LIMIT 1")
    suspend fun getBloodBagById(id: String): BloodBagEntity?

    @Query("SELECT * FROM blood_bags WHERE syncStatus != 'PENDING_DELETE' AND bloodType = :bloodType ORDER BY expirationDate ASC")
    fun getBloodBagsByType(bloodType: String): Flow<List<BloodBagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: BloodBagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<BloodBagEntity>)

    @Update
    suspend fun update(entity: BloodBagEntity)

    @Delete
    suspend fun delete(entity: BloodBagEntity)

    @Query("DELETE FROM blood_bags WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM blood_bags WHERE syncStatus IN ('PENDING_INSERT', 'PENDING_UPDATE', 'PENDING_DELETE')")
    suspend fun getPendingSyncBags(): List<BloodBagEntity>

    @Query("UPDATE blood_bags SET syncStatus = 'PENDING_DELETE' WHERE id = :id")
    suspend fun markForPendingDelete(id: String)

    @Query("DELETE FROM blood_bags")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM blood_bags WHERE syncStatus != 'PENDING_DELETE'")
    fun countTotal(): Flow<Int>
}
