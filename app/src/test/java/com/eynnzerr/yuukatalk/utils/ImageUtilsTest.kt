package com.eynnzerr.yuukatalk.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ImageUtilsTest {

    @Test
    fun compressSuffix_shouldMapFormatIndex() {
        assertEquals("jpeg", ImageUtils.compressSuffix(0))
        assertEquals("png", ImageUtils.compressSuffix(1))
        assertEquals("webp", ImageUtils.compressSuffix(2))
        assertEquals("webp", ImageUtils.compressSuffix(3))
        assertEquals("webp", ImageUtils.compressSuffix(99))
    }

    @Test
    fun ensureValidRange_shouldAllowValidOrEmptyRange() {
        ImageUtils.ensureValidRange(itemCount = 10, range = 0..9)
        ImageUtils.ensureValidRange(itemCount = 10, range = 3..3)
        ImageUtils.ensureValidRange(itemCount = 10, range = 5..3)
    }

    @Test
    fun ensureValidRange_shouldThrowOnOutOfBoundsRange() {
        assertThrows(IllegalArgumentException::class.java) {
            ImageUtils.ensureValidRange(itemCount = 5, range = -1..1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            ImageUtils.ensureValidRange(itemCount = 5, range = 0..5)
        }
    }
}
