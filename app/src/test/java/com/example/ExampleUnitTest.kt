package com.example

import com.example.model.BloodBag
import com.example.model.BloodStatus
import com.example.model.BloodType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {
  @Test
  fun test42DayExpirationCalculation() {
    val collection = "2026-09-01"
    val expectedExpiry = "2026-10-13" // 30 days in Sept + 12 days in Oct = 42 days
    val calculated = BloodBag.calculateExpiration(collection, 42)
    assertEquals(expectedExpiry, calculated)
  }

  @Test
  fun testUniversalDonorAndRecipientRules() {
    // O- is universal RBC donor
    val oNegRecipients = BloodType.getCompatibleRecipientsForRbc(BloodType.O_NEG)
    assertEquals(8, oNegRecipients.size)

    // AB+ is universal RBC recipient
    val abPosDonors = BloodType.getCompatibleDonorsForRbc(BloodType.AB_POS)
    assertEquals(8, abPosDonors.size)

    // O- recipient can only receive O-
    val oNegDonors = BloodType.getCompatibleDonorsForRbc(BloodType.O_NEG)
    assertEquals(setOf(BloodType.O_NEG), oNegDonors)
  }

  @Test
  fun testExpirationStatusThresholds() {
    val today = LocalDate.now()
    val bagExpiringSoon = BloodBag(
      id = "TEST-1",
      bloodType = BloodType.A_POS,
      quantityMl = 450,
      donorName = "Test",
      donorId = "D-1",
      collectionDate = today.minusDays(38).format(BloodBag.DATE_FORMATTER),
      expirationDate = today.plusDays(4).format(BloodBag.DATE_FORMATTER)
    )
    assertEquals(BloodStatus.EXPIRING_SOON, bagExpiringSoon.getEffectiveStatus(today))
  }

  @Test
  fun testCustomQuantityAndRareTypes() {
    val customBag = BloodBag(
      id = "TEST-CUSTOM-1",
      bloodType = BloodType.O_NEG,
      quantityMl = 425, // Custom amount
      donorName = "Jane Doe",
      donorId = "D-CUSTOM",
      collectionDate = "2026-09-01",
      expirationDate = "2026-10-13",
      isRare = true
    )
    assertEquals(425, customBag.quantityMl)
    assertTrue(customBag.isRare)
  }
}
