package com.example.data.repository

import com.example.data.local.BloodBagDao
import com.example.data.local.BloodBagEntity
import com.example.data.remote.SupabaseBloodBagDto
import com.example.data.remote.SupabaseClient
import com.example.data.remote.SupabaseConfig
import com.example.model.BloodBag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Success(val message: String, val timestamp: Long = System.currentTimeMillis()) : SyncStatus()
    data class Error(val message: String, val timestamp: Long = System.currentTimeMillis()) : SyncStatus()
}

class BloodBankRepository(
    private val dao: BloodBagDao,
    private val supabaseClient: SupabaseClient,
    val config: SupabaseConfig,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    val bloodBags: Flow<List<BloodBag>> = dao.getAllBloodBags().map { entities ->
        entities.map { it.toDomain() }
    }

    init {
        scope.launch {
            checkAndSeedInitialData()
        }
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val count = dao.getPendingSyncBags().size
        val existing = dao.getBloodBagById("BB-2026-0819")
        if (existing == null) {
            val initial = SampleData.getInitialStock().map { BloodBagEntity.fromDomain(it) }
            dao.insertAll(initial)
        }
    }

    suspend fun addBloodBag(bag: BloodBag): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Save locally first with PENDING_INSERT
            val entity = BloodBagEntity.fromDomain(bag.copy(syncStatus = "PENDING_INSERT"))
            dao.insertOrUpdate(entity)

            // Attempt push to Supabase if configured
            if (config.isConfigured) {
                try {
                    val dto = SupabaseBloodBagDto.fromDomain(bag)
                    val response = supabaseClient.getApi().upsertBloodBag(dto)
                    if (response.isSuccessful) {
                        dao.insertOrUpdate(BloodBagEntity.fromDomain(bag.copy(syncStatus = "SYNCED")))
                    }
                } catch (e: Exception) {
                    // Stays PENDING_INSERT locally, will sync later
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeBloodBag(bagId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.markForPendingDelete(bagId)

            if (config.isConfigured) {
                try {
                    val response = supabaseClient.getApi().deleteBloodBag("eq.$bagId")
                    if (response.isSuccessful) {
                        dao.deleteById(bagId)
                    }
                } catch (e: Exception) {
                    // Left as PENDING_DELETE in Room to be deleted on next sync
                }
            } else {
                dao.deleteById(bagId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBloodBag(bag: BloodBag): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = BloodBagEntity.fromDomain(bag.copy(syncStatus = "PENDING_UPDATE"))
            dao.insertOrUpdate(entity)

            if (config.isConfigured) {
                try {
                    val dto = SupabaseBloodBagDto.fromDomain(bag)
                    val response = supabaseClient.getApi().upsertBloodBag(dto)
                    if (response.isSuccessful) {
                        dao.insertOrUpdate(BloodBagEntity.fromDomain(bag.copy(syncStatus = "SYNCED")))
                    }
                } catch (e: Exception) {
                    // Stays pending
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncWithSupabase(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        _syncStatus.value = SyncStatus.Syncing
        try {
            val api = supabaseClient.getApi()

            // 1. Push pending local deletions
            val pendingDeletes = dao.getPendingSyncBags().filter { it.syncStatus == "PENDING_DELETE" }
            for (pending in pendingDeletes) {
                try {
                    val delRes = api.deleteBloodBag("eq.${pending.id}")
                    if (delRes.isSuccessful) {
                        dao.deleteById(pending.id)
                    }
                } catch (_: Exception) {}
            }

            // 2. Push pending local inserts/updates
            val pendingUpserts = dao.getPendingSyncBags().filter { it.syncStatus != "PENDING_DELETE" }
            if (pendingUpserts.isNotEmpty()) {
                val dtos = pendingUpserts.map { SupabaseBloodBagDto.fromDomain(it.toDomain()) }
                try {
                    val upRes = api.upsertBloodBags(dtos)
                    if (upRes.isSuccessful) {
                        pendingUpserts.forEach {
                            dao.insertOrUpdate(it.copy(syncStatus = "SYNCED"))
                        }
                    }
                } catch (_: Exception) {}
            }

            // 3. Pull latest remote blood bags
            val remoteResponse = api.getBloodBags()
            if (remoteResponse.isSuccessful) {
                val remoteBags = remoteResponse.body() ?: emptyList()
                if (remoteBags.isNotEmpty()) {
                    val entities = remoteBags.map { BloodBagEntity.fromDomain(it.toDomain()) }
                    dao.insertAll(entities)
                }
                val msg = "Sync successful. Pulled ${remoteBags.size} bags from Supabase."
                _syncStatus.value = SyncStatus.Success(msg)
                Pair(true, msg)
            } else {
                val errorMsg = "Supabase error (HTTP ${remoteResponse.code()}): ${remoteResponse.message()}"
                _syncStatus.value = SyncStatus.Error(errorMsg)
                Pair(false, errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Sync network error: ${e.localizedMessage ?: "Unknown error"}"
            _syncStatus.value = SyncStatus.Error(errorMsg)
            Pair(false, errorMsg)
        }
    }

    suspend fun resetToSampleData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.clearAll()
            val samples = SampleData.getInitialStock().map { BloodBagEntity.fromDomain(it) }
            dao.insertAll(samples)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
