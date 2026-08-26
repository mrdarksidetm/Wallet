package com.darkytm.wallet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.darkytm.wallet.data.repository.WalletRepository

class WalletViewModelFactory(private val repository: WalletRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WalletViewModel(repository) as T
    }
}
