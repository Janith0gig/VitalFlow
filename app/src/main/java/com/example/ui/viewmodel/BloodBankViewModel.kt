package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.remote.SupabaseClient
import com.example.data.remote.SupabaseConfig
import com.example.data.repository.BloodBankRepository
import com.example.data.repository.SyncStatus
import com.example.model.BloodBag
import com.example.model.BloodComponent
import com.example.model.BloodStatus
import com.example.model.BloodType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

enum class StockLevelFilter(val label: String) {
    ALL("All Stock"),
    LOW("Low Stock (<2 units)"),
    HIGH("High Stock (≥3 units)")
}

enum class InventorySort(val label: String) {
    EXPIRATION_SOONEST("Expiration: Soonest"),
    EXPIRATION_LATEST("Expiration: Latest"),
    BLOOD_TYPE("Blood Group (A-Z)"),
    QUANTITY_DESC("Volume (High to Low)")
}

data class RareShortageAlert(
    val bloodType: BloodType,
    val unitsAvailable: Int,
    val totalVolumeMl: Int,
    val criticalThreshold: Int = 2,
    val message: String
)

class BloodBankViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val config = SupabaseConfig(application)
    val supabaseClient = SupabaseClient(config)
    val repository = BloodBankRepository(
        dao = database.bloodBagDao(),
        supabaseClient = supabaseClient,
        config = config
    )

    val syncStatus: StateFlow<SyncStatus> = repository.syncStatus

    // Laboratory Display Theme (Warm Sand vs High-Contrast Lab Dark Mode)
    val isLabDarkMode = MutableStateFlow(config.isLabDarkMode)

    fun toggleTheme() {
        val next = !isLabDarkMode.value
        isLabDarkMode.value = next
        config.isLabDarkMode = next
    }

    fun setLabDarkMode(enabled: Boolean) {
        isLabDarkMode.value = enabled
        config.isLabDarkMode = enabled
    }

    // Raw stream of blood bags from Room
    val allBloodBags: StateFlow<List<BloodBag>> = repository.bloodBags.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filter and Search states
    val searchQuery = MutableStateFlow("")
    val selectedBloodTypeFilter = MutableStateFlow<BloodType?>(null)
    val selectedStatusFilter = MutableStateFlow<BloodStatus?>(null)
    val selectedStockLevelFilter = MutableStateFlow(StockLevelFilter.ALL)
    val selectedSort = MutableStateFlow(InventorySort.EXPIRATION_SOONEST)

    private data class FilterCriteria(
        val query: String,
        val bloodType: BloodType?,
        val status: BloodStatus?,
        val stockLevel: StockLevelFilter,
        val sort: InventorySort
    )

    private val filterCriteria = combine(
        searchQuery,
        selectedBloodTypeFilter,
        selectedStatusFilter,
        selectedStockLevelFilter,
        selectedSort
    ) { query, typeFilter, statusFilter, stockFilter, sort ->
        FilterCriteria(query, typeFilter, statusFilter, stockFilter, sort)
    }

    // Filtered inventory list
    val filteredBloodBags: StateFlow<List<BloodBag>> = combine(
        allBloodBags,
        filterCriteria
    ) { bags, criteria ->
        var list = bags

        // Blood type counts for stock level filtering
        val countsByType = bags.groupBy { it.bloodType }.mapValues { it.value.size }

        // Search query
        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase()
            list = list.filter {
                it.id.lowercase().contains(q) ||
                it.donorName.lowercase().contains(q) ||
                it.donorId.lowercase().contains(q) ||
                it.bloodType.label.lowercase().contains(q) ||
                it.storageLocation.lowercase().contains(q)
            }
        }

        // Blood type filter
        if (criteria.bloodType != null) {
            list = list.filter { it.bloodType == criteria.bloodType }
        }

        // Status filter
        if (criteria.status != null) {
            list = list.filter { it.getEffectiveStatus() == criteria.status }
        }

        // Stock level filter
        list = when (criteria.stockLevel) {
            StockLevelFilter.LOW -> list.filter { (countsByType[it.bloodType] ?: 0) <= 2 }
            StockLevelFilter.HIGH -> list.filter { (countsByType[it.bloodType] ?: 0) >= 3 }
            StockLevelFilter.ALL -> list
        }

        // Sorting
        when (criteria.sort) {
            InventorySort.EXPIRATION_SOONEST -> list.sortedBy { it.getDaysUntilExpiration() }
            InventorySort.EXPIRATION_LATEST -> list.sortedByDescending { it.getDaysUntilExpiration() }
            InventorySort.BLOOD_TYPE -> list.sortedBy { it.bloodType.label }
            InventorySort.QUANTITY_DESC -> list.sortedByDescending { it.quantityMl }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Expiration alerts: Within 7 days
    val expiringWithin7Days: StateFlow<List<BloodBag>> = allBloodBags.map { bags ->
        bags.filter {
            val days = it.getDaysUntilExpiration()
            days in 0..7
        }.sortedBy { it.getDaysUntilExpiration() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expired bags: days < 0
    val expiredBags: StateFlow<List<BloodBag>> = allBloodBags.map { bags ->
        bags.filter { it.getDaysUntilExpiration() < 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Rare blood type shortage alerts (e.g. O- or AB- dropping below 2 units)
    val rareBloodShortages: StateFlow<List<RareShortageAlert>> = allBloodBags.map { bags ->
        val rareTypes = listOf(BloodType.O_NEG, BloodType.AB_NEG, BloodType.B_NEG)
        val validBags = bags.filter { it.getDaysUntilExpiration() >= 0 }
        val byType = validBags.groupBy { it.bloodType }

        rareTypes.mapNotNull { type ->
            val units = byType[type]?.size ?: 0
            val vol = byType[type]?.sumOf { it.quantityMl } ?: 0
            if (units <= 2) {
                RareShortageAlert(
                    bloodType = type,
                    unitsAvailable = units,
                    totalVolumeMl = vol,
                    criticalThreshold = 2,
                    message = when {
                        units == 0 -> "CRITICAL DEPLETION: Zero ${type.label} units available in blood bank!"
                        units == 1 -> "URGENT SHORTAGE: Only 1 unit of ${type.label} remaining."
                        else -> "LOW RESERVE: ${units} units of rare ${type.label} remaining."
                    }
                )
            } else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Rare blood bags that are expiring soon (Highest clinical priority)
    val rareExpiringBags: StateFlow<List<BloodBag>> = allBloodBags.map { bags ->
        bags.filter { it.isRare && it.getDaysUntilExpiration() in 0..7 }
            .sortedBy { it.getDaysUntilExpiration() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cross-match recipient compatibility selector
    val selectedRecipientType = MutableStateFlow(BloodType.A_POS)

    // Compatible blood bags for selected recipient
    val crossMatchResult: StateFlow<Pair<Set<BloodType>, List<BloodBag>>> = combine(
        allBloodBags,
        selectedRecipientType
    ) { bags, recipient ->
        val compatibleTypes = BloodType.getCompatibleDonorsForRbc(recipient)
        val matchingBags = bags.filter {
            compatibleTypes.contains(it.bloodType) && it.getDaysUntilExpiration() >= 0
        }.sortedBy { it.getDaysUntilExpiration() }
        Pair(compatibleTypes, matchingBags)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(emptySet(), emptyList()))

    // Form State for Add Blood Bag
    val formBloodType = MutableStateFlow(BloodType.O_POS)
    val formQuantityMl = MutableStateFlow("450")
    val formComponent = MutableStateFlow(BloodComponent.WHOLE_BLOOD)
    val formDonorName = MutableStateFlow("")
    val formDonorId = MutableStateFlow("D-${(10000..99999).random()}")
    val formDonorPhone = MutableStateFlow("")
    val formCollectionDate = MutableStateFlow(LocalDate.now().format(BloodBag.DATE_FORMATTER))
    val formStorageLocation = MutableStateFlow("Refrigerator 1 (4°C)")
    val formNotes = MutableStateFlow("")
    val formIsSubmitting = MutableStateFlow(false)
    val formSuccessMessage = MutableStateFlow<String?>(null)
    val formErrorMessage = MutableStateFlow<String?>(null)

    // Calculate auto expiration date based on collection date & component
    val formAutoExpirationDate: StateFlow<String> = combine(
        formCollectionDate,
        formComponent
    ) { date, comp ->
        BloodBag.calculateExpiration(date, comp.standardShelfLifeDays)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDate.now().plusDays(42).format(BloodBag.DATE_FORMATTER))

    fun submitAddBag(onSuccess: () -> Unit) {
        val name = formDonorName.value.trim()
        if (name.isBlank()) {
            formErrorMessage.value = "Please enter donor name"
            return
        }

        val qty = formQuantityMl.value.toIntOrNull()
        if (qty == null || qty <= 0) {
            formErrorMessage.value = "Please enter a valid volume (e.g. 450 mL)"
            return
        }

        formIsSubmitting.value = true
        formErrorMessage.value = null

        val newBag = BloodBag(
            id = "BB-2026-${(1000..9999).random()}",
            bloodType = formBloodType.value,
            quantityMl = qty,
            component = formComponent.value,
            donorName = name,
            donorId = formDonorId.value.ifBlank { "D-${(10000..99999).random()}" },
            donorPhone = formDonorPhone.value.trim(),
            collectionDate = formCollectionDate.value,
            expirationDate = formAutoExpirationDate.value,
            storageLocation = formStorageLocation.value,
            status = BloodStatus.FRESH,
            isRare = formBloodType.value.isRare,
            notes = formNotes.value.trim(),
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val result = repository.addBloodBag(newBag)
            formIsSubmitting.value = false
            if (result.isSuccess) {
                formSuccessMessage.value = "Blood Bag ${newBag.id} logged successfully!"
                resetForm()
                onSuccess()
            } else {
                formErrorMessage.value = "Failed to add blood bag: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun removeBloodBag(bagId: String) {
        viewModelScope.launch {
            repository.removeBloodBag(bagId)
        }
    }

    fun updateBloodBag(bag: BloodBag) {
        viewModelScope.launch {
            repository.updateBloodBag(bag)
        }
    }

    fun markAsTransfused(bag: BloodBag) {
        viewModelScope.launch {
            repository.updateBloodBag(bag.copy(status = BloodStatus.TRANSFUSED))
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
    }

    fun resetForm() {
        formDonorName.value = ""
        formDonorId.value = "D-${(10000..99999).random()}"
        formDonorPhone.value = ""
        formQuantityMl.value = "450"
        formNotes.value = ""
        formCollectionDate.value = LocalDate.now().format(BloodBag.DATE_FORMATTER)
    }

    fun resetToDemoData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }

    // Connection testing
    val isTestingConnection = MutableStateFlow(false)
    val testConnectionResult = MutableStateFlow<Pair<Boolean, String>?>(null)

    fun testSupabaseConnection() {
        viewModelScope.launch {
            isTestingConnection.value = true
            val res = supabaseClient.testConnection()
            testConnectionResult.value = res
            isTestingConnection.value = false
        }
    }

    fun updateSupabaseSettings(url: String, key: String) {
        config.supabaseUrl = url
        config.supabaseAnonKey = key
    }
}
