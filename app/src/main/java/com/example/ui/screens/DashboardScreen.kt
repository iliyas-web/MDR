package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CurrentUser
import com.example.data.model.Role
import com.example.ui.components.StatsCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.AcademiaBlueDark
import com.example.ui.theme.AcademiaBlueLight
import com.example.ui.theme.AcademiaBluePrimary
import com.example.ui.theme.AcademiaCyanAccent
import com.example.ui.theme.AcademiaElectricBlue
import com.example.ui.theme.AcademiaError
import com.example.ui.theme.AcademiaInfo
import com.example.ui.theme.AcademiaSkyBlue
import com.example.ui.theme.AcademiaSuccess
import com.example.ui.theme.AcademiaWarning
import com.example.ui.viewmodel.AcademiaViewModel

@Composable
fun DashboardScreen(
    viewModel: AcademiaViewModel,
    currentUser: CurrentUser,
    onNavigate: (String) -> Unit
) {
    val staffCount by viewModel.staffCount.collectAsStateWithLifecycle()
    val studentCount by viewModel.studentCount.collectAsStateWithLifecycle()
    val roomCount by viewModel.roomCount.collectAsStateWithLifecycle()
    val examCount by viewModel.examCount.collectAsStateWithLifecycle()

    val allTimetableSlots by viewModel.allTimetableSlots.collectAsStateWithLifecycle()
    val allExams by viewModel.allExams.collectAsStateWithLifecycle()

    val currentDayInt = viewModel.getDayOfWeekInt()
    val todaySlots = allTimetableSlots.filter { it.dayOfWeek == currentDayInt }

    // User-specific slots
    val userSlots = when (currentUser.role) {
        Role.ADMIN -> todaySlots
        Role.STAFF -> todaySlots.filter { it.staffId == currentUser.identifier }
        Role.STUDENT -> todaySlots.filter {
            it.department.equals(currentUser.department, ignoreCase = true) &&
            it.semester == currentUser.semester &&
            it.section.equals(currentUser.section, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Box
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AcademiaBlueDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AcademiaBlueDark, AcademiaBluePrimary, AcademiaElectricBlue)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "MDR 1225 TECH CAMPUS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = viewModel.getFormattedTodayDate(),
                                fontSize = 12.sp,
                                color = AcademiaSkyBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Welcome back, ${currentUser.displayName}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = when (currentUser.role) {
                                Role.ADMIN -> "Academic Director & COE &bull; Full Timetable & Seating Control"
                                Role.STAFF -> "Faculty Dept: ${currentUser.department} &bull; ${userSlots.size} classes today"
                                Role.STUDENT -> "B.Tech ${currentUser.department} (Sem ${currentUser.semester}, Sec ${currentUser.section})"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = AcademiaSkyBlue.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Live Ongoing Period Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AcademiaSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = AcademiaSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Today's Academic Status",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (userSlots.isNotEmpty()) "${userSlots.size} Lectures Scheduled Today" else "No scheduled classes for today",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Auto Clash-Protection Active &bull; Zero Hall Collisions",
                            fontSize = 11.sp,
                            color = AcademiaElectricBlue
                        )
                    }

                    OutlinedButton(
                        onClick = { onNavigate(Screen.Timetable.route) },
                        modifier = Modifier.testTag("dashboard_view_timetable_btn")
                    ) {
                        Text("View", fontSize = 12.sp)
                    }
                }
            }
        }

        // Statistics Metrics Grid
        item {
            Text(
                text = "CAMPUS OVERVIEW",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatsCard(
                    title = "Faculty",
                    value = staffCount.toString(),
                    subtitle = "Active Staff",
                    icon = Icons.Default.Person,
                    accentColor = AcademiaBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.Management.route) }
                )
                StatsCard(
                    title = "Students",
                    value = studentCount.toString(),
                    subtitle = "Enrolled",
                    icon = Icons.Default.People,
                    accentColor = AcademiaCyanAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.Management.route) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatsCard(
                    title = "Halls & Labs",
                    value = roomCount.toString(),
                    subtitle = "Lecture Rooms",
                    icon = Icons.Default.MeetingRoom,
                    accentColor = AcademiaWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.Management.route) }
                )
                StatsCard(
                    title = "Exams",
                    value = examCount.toString(),
                    subtitle = "Scheduled",
                    icon = Icons.Default.Assignment,
                    accentColor = AcademiaSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.ExamPlanner.route) }
                )
            }
        }

        // Quick Action Center
        item {
            Text(
                text = "SMART MANAGEMENT ACTIONS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Screen.ExamPlanner.route) }
                            .testTag("action_exam_planner")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.EventSeat, contentDescription = null, tint = AcademiaElectricBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Exam Seating", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Auto Hall Allocation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Screen.Timetable.route) }
                            .testTag("action_timetable")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AcademiaSuccess)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Timetables", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Staff & Class Grid", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Screen.Management.route) }
                            .testTag("action_directory")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AcademiaWarning)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("CRUD Directory", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Staff & Students", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(Screen.PrintExport.route) }
                            .testTag("action_print_export")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = AcademiaInfo)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Print & PDF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Schedules & Tickets", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Today's Classes Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S CLASSES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${userSlots.size} Periods",
                    fontSize = 12.sp,
                    color = AcademiaElectricBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (userSlots.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No periods scheduled for today. Enjoy your day!",
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(userSlots) { slot ->
                ElevatedCard(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AcademiaBluePrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "P${slot.periodNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = AcademiaBluePrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = slot.subjectCode,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AcademiaElectricBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${slot.department} Sem ${slot.semester} ${slot.section}",
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = slot.subjectName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${slot.startTime} - ${slot.endTime} &bull; Room: ${slot.roomNumber} &bull; Faculty: ${slot.staffName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Upcoming Examinations Preview
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UPCOMING EXAMINATIONS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Smart Hall Planner",
                    fontSize = 12.sp,
                    color = AcademiaSuccess,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigate(Screen.ExamPlanner.route) }
                )
            }
        }

        items(allExams) { exam ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AcademiaSuccess.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${exam.department} &bull; Sem ${exam.semester}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AcademiaSuccess,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "${exam.examDate} (${exam.session})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademiaElectricBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${exam.subjectCode} - ${exam.subjectName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${exam.startTime} - ${exam.endTime} &bull; Title: ${exam.title}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
