package com.example

import com.example.data.model.parseColorOptions
import com.example.data.model.parseSizes
import com.example.data.service.PinCodeService
import com.example.i18n.AppLanguage
import com.example.i18n.LocalizationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreUnitTest {

    @Test
    fun pinCodeService_validatesMetroAndPanIndiaPins() {
        val delhi = PinCodeService.checkPinCode("110001")
        assertTrue(delhi.isServiceable)
        assertTrue(delhi.regionLabel.contains("Delhi"))
        assertTrue(delhi.codAvailable)

        val kolkata = PinCodeService.checkPinCode("700016")
        assertTrue(kolkata.isServiceable)
        assertTrue(kolkata.regionLabel.contains("Kolkata"))

        val invalidShort = PinCodeService.checkPinCode("1100")
        assertFalse(invalidShort.isServiceable)

        val invalidZero = PinCodeService.checkPinCode("010001")
        assertFalse(invalidZero.isServiceable)
    }

    @Test
    fun localizationProvider_supportsEnglishHindiBengali() {
        val en = LocalizationProvider.forLanguage(AppLanguage.ENGLISH)
        val hi = LocalizationProvider.forLanguage(AppLanguage.HINDI)
        val bn = LocalizationProvider.forLanguage(AppLanguage.BENGALI)

        assertEquals("LADY LEBANAN", en.brandName)
        assertEquals(10, en.categoryNames.size)
        assertEquals(10, hi.categoryNames.size)
        assertEquals(10, bn.categoryNames.size)
        assertTrue(hi.categoryNames["Sarees"]!!.isNotBlank())
        assertTrue(bn.categoryNames["Sarees"]!!.isNotBlank())
    }

    @Test
    fun productParsers_handleColorsAndSizes() {
        val colors = parseColorOptions("Blush Rose:#E09BAE,Champagne Gold:#C9A461")
        assertEquals(2, colors.size)
        assertEquals("Blush Rose", colors[0].name)

        val sizes = parseSizes("XS,S,M,L,XL,XXL")
        assertEquals(listOf("XS", "S", "M", "L", "XL", "XXL"), sizes)
    }
}
