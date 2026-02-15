package com.antigravity.moneytracker

import androidx.room.Room
import com.antigravity.moneytracker.data.local.AppDatabase
import com.antigravity.moneytracker.presentation.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "money_tracker.db"
        ).build()
    }

    // DAOs
    single { get<AppDatabase>().transactionDao() }

    // Repositories
    single<com.antigravity.moneytracker.domain.repository.TransactionRepository> { 
        com.antigravity.moneytracker.data.repository.TransactionRepositoryImpl(get()) 
    }

    // AI
    factory { com.antigravity.moneytracker.domain.ai.SpendingAnalyzer() }

    // ViewModels
    viewModel { MainViewModel() }
    viewModel { com.antigravity.moneytracker.presentation.home.HomeViewModel(get(), get()) }
}

