package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Role
import com.example.ui.theme.AcademiaBlueDark
import com.example.ui.theme.AcademiaBluePrimary
import com.example.ui.theme.AcademiaElectricBlue
import com.example.ui.theme.AcademiaError
import com.example.ui.theme.AcademiaSkyBlue
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    authUiState: AuthUiState,
    onLoginSuccess: () -> Unit
) {
    var selectedRoleIndex by remember { mutableIntStateOf(0) } // 0 = Admin, 1 = Staff, 2 = Student

    // Form inputs
    var adminPin by remember { mutableStateOf("1225") }
    var staffId by remember { mutableStateOf("MDR-FAC-101") }
    var staffName by remember { mutableStateOf("Dr. R. Vignesh") }
    var staffDept by remember { mutableStateOf("CSE") }

    var studentRegNo by remember { mutableStateOf("711222104001") }
    var studentName by remember { mutableStateOf("Aarav Sharma") }
    var studentDept by remember { mutableStateOf("CSE") }
    var studentYear by remember { mutableStateOf("3") }
    var studentSem by remember { mutableStateOf("5") }
    var studentSec by remember { mutableStateOf("A") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Hero Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_college_hero),
                contentDescription = "MDR 1225 TECH Campus",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                AcademiaBlueDark.copy(alpha = 0.85f),
                                AcademiaBlueDark
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AcademiaElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MDR 1225 TECH",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "ACADEMIA &bull; Smart College Management",
                            fontSize = 12.sp,
                            color = AcademiaSkyBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Login Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Sign In to Portal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Select your role to access schedule & exam tools",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Role Tabs
                    TabRow(
                        selectedTabIndex = selectedRoleIndex,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedRoleIndex == 0,
                            onClick = { selectedRoleIndex = 0; authViewModel.clearError() },
                            text = { Text("Admin", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            modifier = Modifier.testTag("tab_login_admin")
                        )
                        Tab(
                            selected = selectedRoleIndex == 1,
                            onClick = { selectedRoleIndex = 1; authViewModel.clearError() },
                            text = { Text("Staff", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            modifier = Modifier.testTag("tab_login_staff")
                        )
                        Tab(
                            selected = selectedRoleIndex == 2,
                            onClick = { selectedRoleIndex = 2; authViewModel.clearError() },
                            text = { Text("Student", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            modifier = Modifier.testTag("tab_login_student")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AnimatedVisibility(visible = authUiState.loginError != null) {
                        authUiState.loginError?.let { err ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = AcademiaError.copy(alpha = 0.1f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = err,
                                    color = AcademiaError,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    when (selectedRoleIndex) {
                        0 -> { // Admin
                            OutlinedTextField(
                                value = adminPin,
                                onValueChange = { adminPin = it },
                                label = { Text("Admin PIN / Password (Default: 1225)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_pin_input")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    authViewModel.loginAsAdmin(adminPin)
                                    onLoginSuccess()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_submit_button")
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enter Admin Dashboard", fontWeight = FontWeight.Bold)
                            }
                        }
                        1 -> { // Staff
                            OutlinedTextField(
                                value = staffId,
                                onValueChange = { staffId = it },
                                label = { Text("Faculty / Staff ID (e.g. MDR-FAC-101)") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("staff_id_input")
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = staffName,
                                onValueChange = { staffName = it },
                                label = { Text("Faculty Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = staffDept,
                                onValueChange = { staffDept = it },
                                label = { Text("Department (CSE, IT, ECE, AI&DS)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    authViewModel.loginAsStaff(staffId, staffName, staffDept)
                                    onLoginSuccess()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_submit_button")
                            ) {
                                Text("Login as Faculty Member", fontWeight = FontWeight.Bold)
                            }
                        }
                        2 -> { // Student
                            OutlinedTextField(
                                value = studentRegNo,
                                onValueChange = { studentRegNo = it },
                                label = { Text("Register Number (e.g. 711222104001)") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_regno_input")
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = studentName,
                                onValueChange = { studentName = it },
                                label = { Text("Student Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = studentDept,
                                    onValueChange = { studentDept = it },
                                    label = { Text("Dept") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = studentSem,
                                    onValueChange = { studentSem = it },
                                    label = { Text("Sem") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = studentSec,
                                    onValueChange = { studentSec = it },
                                    label = { Text("Sec") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    authViewModel.loginAsStudent(
                                        regNo = studentRegNo,
                                        name = studentName,
                                        dept = studentDept,
                                        year = studentYear.toIntOrNull() ?: 3,
                                        sem = studentSem.toIntOrNull() ?: 5,
                                        sec = studentSec
                                    )
                                    onLoginSuccess()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_submit_button")
                            ) {
                                Text("Login as Student", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Demo Presets
            Text(
                text = "QUICK DEMO ONE-TAP ACCESS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        authViewModel.switchRole(Role.ADMIN)
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_demo_admin")
                ) {
                    Text("Admin Demo", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        authViewModel.switchRole(Role.STAFF)
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_demo_staff")
                ) {
                    Text("Staff Demo", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        authViewModel.switchRole(Role.STUDENT)
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_demo_student")
                ) {
                    Text("Student Demo", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
