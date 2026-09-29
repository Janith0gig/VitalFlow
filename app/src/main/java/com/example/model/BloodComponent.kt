package com.example.model

enum class BloodComponent(val displayName: String, val standardShelfLifeDays: Long, val standardUnitMl: Int) {
    WHOLE_BLOOD("Whole Blood", 42, 450),
    PACKED_RBC("Packed Red Blood Cells (PRBC)", 42, 350),
    PLATELETS("Platelets", 5, 250),
    FRESH_FROZEN_PLASMA("Fresh Frozen Plasma (FFP)", 365, 300),
    CRYOPRECIPITATE("Cryoprecipitate", 365, 150);

    companion object {
        fun fromDisplayName(name: String): BloodComponent {
            return entries.find { it.displayName.equals(name.trim(), ignoreCase = true) } ?: WHOLE_BLOOD
        }
    }
}
