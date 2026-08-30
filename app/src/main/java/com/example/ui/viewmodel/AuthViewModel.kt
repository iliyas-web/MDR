package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.model.CurrentUser
import com.example.data.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUiState(
    val isLoggedIn: Boolean = true, // default to logged in with Admin demo for seamless exploration
    val currentUser: CurrentUser = CurrentUser(
        role = Role.ADMIN,
        identifier = "ADMIN-001",
        displayName = "Academic Administrator",
        department = "ALL",
        email = "admin@mdrtech.edu"
    ),
    val loginError: String? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun loginAsAdmin(pin: String) {
        if (pin.isEmpty() || pin == "1225" || pin == "admin") {
            _uiState.value = AuthUiState(
                isLoggedIn = true,
                currentUser = CurrentUser(
                    role = Role.ADMIN,
                    identifier = "ADMIN-001",
                    displayName = "Dr. S. K. Narayanan (Principal)",
                    department = "ALL",
                    email = "principal@mdrtech.edu"
                ),
                loginError = null
            )
        } else {
            _uiState.value = _uiState.value.copy(loginError = "Invalid Admin PIN. (Default: 1225)")
        }
    }

    fun loginAsStaff(staffId: String, name: String, dept: String) {
        if (staffId.isNotBlank()) {
            _uiState.value = AuthUiState(
                isLoggedIn = true,
                currentUser = CurrentUser(
                    role = Role.STAFF,
                    identifier = staffId,
                    displayName = name.ifBlank { "Faculty Member" },
                    department = dept.ifBlank { "CSE" },
                    email = "${staffId.lowercase()}@mdrtech.edu"
                ),
                loginError = null
            )
        } else {
            _uiState.value = _uiState.value.copy(loginError = "Please select or enter Staff ID")
        }
    }

    fun loginAsStudent(regNo: String, name: String, dept: String, year: Int, sem: Int, sec: String) {
        if (regNo.isNotBlank()) {
            _uiState.value = AuthUiState(
                isLoggedIn = true,
                currentUser = CurrentUser(
                    role = Role.STUDENT,
                    identifier = regNo,
                    displayName = name.ifBlank { "Student" },
                    department = dept,
                    year = year,
                    semester = sem,
                    section = sec,
                    email = "${regNo.lowercase()}@mdrtech.edu"
                ),
                loginError = null
            )
        } else {
            _uiState.value = _uiState.value.copy(loginError = "Please enter Register Number")
        }
    }

    fun switchRole(role: Role) {
        when (role) {
            Role.ADMIN -> loginAsAdmin("1225")
            Role.STAFF -> loginAsStaff("MDR-FAC-101", "Dr. R. Vignesh", "CSE")
            Role.STUDENT -> loginAsStudent("711222104001", "Aarav Sharma", "CSE", 3, 5, "A")
        }
    }

    fun logout() {
        _uiState.value = AuthUiState(
            isLoggedIn = false,
            currentUser = CurrentUser(Role.STUDENT, "", "Guest")
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(loginError = null)
    }
}
