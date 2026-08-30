package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.EventSeat
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Print
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard")
    object Timetable : Screen("timetable", "Timetable", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "nav_timetable")
    object ExamPlanner : Screen("exam_planner", "Exam Halls", Icons.Filled.EventSeat, Icons.Outlined.EventSeat, "nav_exam_planner")
    object Management : Screen("management", "Directory", Icons.Filled.Layers, Icons.Outlined.Layers, "nav_management")
    object PrintExport : Screen("print_export", "Print & Export", Icons.Filled.Print, Icons.Outlined.Print, "nav_print_export")
    object Login : Screen("login", "Login", Icons.Filled.Assessment, Icons.Outlined.Assessment, "nav_login")
}

val BottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.Timetable,
    Screen.ExamPlanner,
    Screen.Management,
    Screen.PrintExport
)
