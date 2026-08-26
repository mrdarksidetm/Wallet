package com.darkytm.wallet

import android.app.Application
import com.darkytm.wallet.data.WalletDatabase
import com.darkytm.wallet.data.repository.WalletRepository

class WalletApplication : Application() {
    val database: WalletDatabase by lazy {
        WalletDatabase.getInstance(this)
    }

    val repository: WalletRepository by lazy {
        WalletRepository(database)
    }
}
