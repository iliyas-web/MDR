package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AcademiaTopBar
import com.example.ui.navigation.BottomNavScreens
import com.example.ui.navigation.Screen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamPlannerScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ManagementScreen
import com.example.ui.screens.PrintExportScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.theme.AcademiaBluePrimary
import com.example.ui.theme.AcademiaTheme
import com.example.ui.viewmodel.AcademiaViewModel
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val factory = remember { ViewModelFactory(context.applicationContext) }
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            val academiaViewModel: AcademiaViewModel = viewModel(factory = factory)

            val isDarkTheme by academiaViewModel.isDarkTheme.collectAsStateWithLifecycle()

            AcademiaTheme(darkTheme = isDarkTheme) {
                AcademiaApp(
                    authViewModel = authViewModel,
                    academiaViewModel = academiaViewModel,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun AcademiaApp(
    authViewModel: AuthViewModel,
    academiaViewModel: AcademiaViewModel,
    isDarkTheme: Boolean
) {
    val navController = rememberNavController()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Listen for global snackbar messages
    LaunchedEffect(Unit) {
        academiaViewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (authUiState.isLoggedIn && currentRoute != Screen.Login.route) {
                AcademiaTopBar(
                    user = authUiState.currentUser,
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = { academiaViewModel.toggleDarkTheme() },
                    onSwitchRole = { role ->
                        authViewModel.switchRole(role)
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (authUiState.isLoggedIn && currentRoute != Screen.Login.route) {
                NavigationBar(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    contentColor = AcademiaBluePrimary
                ) {
                    BottomNavScreens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AcademiaBluePrimary,
                                selectedTextColor = AcademiaBluePrimary,
                                indicatorColor = AcademiaBluePrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag(screen.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authUiState.isLoggedIn) Screen.Dashboard.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    authUiState = authUiState,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = academiaViewModel,
                    currentUser = authUiState.currentUser,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            composable(Screen.Timetable.route) {
                TimetableScreen(
                    viewModel = academiaViewModel,
                    currentUser = authUiState.currentUser,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            composable(Screen.ExamPlanner.route) {
                ExamPlannerScreen(
                    viewModel = academiaViewModel,
                    currentUser = authUiState.currentUser,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            composable(Screen.Management.route) {
                ManagementScreen(
                    viewModel = academiaViewModel,
                    currentUser = authUiState.currentUser
                )
            }

            composable(Screen.PrintExport.route) {
                PrintExportScreen(
                    viewModel = academiaViewModel,
                    currentUser = authUiState.currentUser
                )
            }
        }
    }
}
