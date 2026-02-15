package com.antigravity.moneytracker.presentation

import androidx.lifecycle.ViewModel
import com.antigravity.moneytracker.ui.theme.AppThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _themeType = MutableStateFlow(AppThemeType.LIQUID_GLASS)
    val themeType: StateFlow<AppThemeType> = _themeType.asStateFlow()

    fun toggleTheme() {
        _themeType.value = when (_themeType.value) {
            AppThemeType.LIQUID_GLASS -> AppThemeType.MATERIAL_EXPRESSIVE
            AppThemeType.MATERIAL_EXPRESSIVE -> AppThemeType.LIQUID_GLASS
        }
    }
    
    fun setTheme(type: AppThemeType) {
        _themeType.value = type
    }
}
