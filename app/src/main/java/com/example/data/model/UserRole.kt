package com.example.data.model

enum class Role {
    ADMIN,
    STAFF,
    STUDENT
}

data class CurrentUser(
    val role: Role,
    val identifier: String, // Staff ID, Reg No, or "admin"
    val displayName: String,
    val department: String = "ALL",
    val email: String = "",
    val year: Int = 1,
    val semester: Int = 1,
    val section: String = "A"
)
