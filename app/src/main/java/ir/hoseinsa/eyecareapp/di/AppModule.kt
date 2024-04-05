package ir.hoseinsa.eyecareapp.di

import ir.hoseinsa.eyecareapp.ui.main.TimerScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { TimerScreenViewModel() }
}