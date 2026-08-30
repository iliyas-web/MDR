package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AcademiaDatabase
import com.example.data.repository.AcademiaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val database by lazy { AcademiaDatabase.getDatabase(context, applicationScope) }
    private val repository by lazy { AcademiaRepository(database.academiaDao()) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel() as T
            }
            modelClass.isAssignableFrom(AcademiaViewModel::class.java) -> {
                AcademiaViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
