package com.mrdarksidetm.wallet.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Phase 29: Privacy Masking & Security Gestures
 * 
 * Provides an app-wide reactive state to toggle privacy masking.
 * When true, monetary values are obfuscated with asterisks (***).
 * This state is exposed as a StateFlow to instantly trigger recomposition
 * on all active screens without needing an app restart.
 */
object PrivacyState {
    private val _isPrivacyEnabled = MutableStateFlow(false)
    val isPrivacyEnabled: StateFlow<Boolean> = _isPrivacyEnabled.asStateFlow()

    fun togglePrivacy() {
        _isPrivacyEnabled.value = !_isPrivacyEnabled.value
    }
    
    /**
     * Extension to format text conditionally based on privacy state.
     */
    fun String.maskIfRequired(isEnabled: Boolean): String {
        return if (isEnabled) "***" else this
    }
}
