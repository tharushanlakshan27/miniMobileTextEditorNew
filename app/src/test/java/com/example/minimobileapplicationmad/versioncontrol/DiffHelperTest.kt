package com.example.minimobileapplicationmad.versioncontrol

import org.junit.Assert.assertEquals
import org.junit.Test

class DiffHelperTest {

    @Test
    fun testGenerateAndApplyPatch() {
        val oldText = "Hello World\nLine 2"
        val newText = "Hello Android\nLine 2\nLine 3"
        
        val patch = DiffHelper.generateDiff(oldText, newText)
        val restored = DiffHelper.applyPatch(oldText, patch)
        
        assertEquals(newText, restored)
    }

    @Test
    fun testReversePatch() {
        val oldText = "Original content"
        val newText = "Modified content"
        
        val patch = DiffHelper.generateDiff(oldText, newText)
        val reversed = DiffHelper.reversePatch(newText, patch)
        
        assertEquals(oldText, reversed)
    }
}
