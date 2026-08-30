package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.ExamScheduleEntity
import com.example.data.local.entity.ExamSeatingEntity
import com.example.data.model.CurrentUser
import com.example.data.model.Role
import com.example.ui.components.EmptyStateView
import com.example.ui.navigation.Screen
import com.example.ui.theme.AcademiaBlueDark
import com.example.ui.theme.AcademiaBluePrimary
import com.example.ui.theme.AcademiaCyanAccent
import com.example.ui.theme.AcademiaElectricBlue
import com.example.ui.theme.AcademiaError
import com.example.ui.theme.AcademiaSkyBlue
import com.example.ui.theme.AcademiaSuccess
import com.example.ui.theme.AcademiaWarning
import com.example.ui.viewmodel.AcademiaViewModel

@Composable
fun ExamPlannerScreen(
    viewModel: AcademiaViewModel,
    currentUser: CurrentUser,
    onNavigate: (String) -> Unit
) {
    val allExams by viewModel.allExams.collectAsStateWithLifecycle()
    val allRooms by viewModel.allRooms.collectAsStateWithLifecycle()
    val plannerState by viewModel.examPlannerState.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Smart Auto Allocation, 1 = Visual Seating Grid, 2 = Student Seat Finder, 3 = Exam Schedule CRUD
    var showAddExamDialog by remember { mutableStateOf(false) }

    val selectedExam = allExams.find { it.id == plannerState.selectedExamId } ?: allExams.firstOrNull()
    val currentSeatingsFlow = remember(selectedExam?.id) {
        viewModel.getSeatingsForCurrentExam(selectedExam?.id ?: 1L)
    }
    val currentSeatings by currentSeatingsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    val searchResultsFlow = remember(plannerState.studentSearchQuery) {
        viewModel.searchStudentSeating(plannerState.studentSearchQuery)
    }
    val searchResults by searchResultsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    // Hall capacity calculations
    val selectedRoomsList = allRooms.filter { plannerState.selectedRoomIds.contains(it.id) }
    val totalSelectedExamCapacity = selectedRoomsList.sumOf { it.examCapacity }

    Scaffold(
        floatingActionButton = {
            if (currentUser.role == Role.ADMIN && activeTab == 3) {
                FloatingActionButton(
                    onClick = { showAddExamDialog = true },
                    containerColor = AcademiaBluePrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_exam_schedule_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Exam")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Tab Navigation
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Smart Planner", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_smart_planner")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Seating Grid", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_seating_grid")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Seat Finder", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_seat_finder")
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    text = { Text("Exams (${allExams.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_exams_crud")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (activeTab) {
                0 -> {
                    // Smart Auto Allocation Engine
                    SmartPlannerTab(
                        allExams = allExams,
                        selectedExam = selectedExam,
                        allRooms = allRooms,
                        plannerState = plannerState,
                        totalCapacity = totalSelectedExamCapacity,
                        allocatedCount = currentSeatings.size,
                        onSelectExam = { viewModel.selectExam(it) },
                        onToggleRoom = { viewModel.toggleRoomSelection(it) },
                        onToggleAlternate = { enabled -> viewModel.setAlternateSeating(enabled) },
                        onRunAllocation = { viewModel.runAutoAllocation() },
                        onViewSeatingGrid = { activeTab = 1 },
                        onPrintHallPlan = { onNavigate(Screen.PrintExport.route) },
                        isAdmin = currentUser.role == Role.ADMIN
                    )
                }
                1 -> {
                    // Visual Seating Grid Visualizer
                    SeatingGridVisualizerTab(
                        selectedExam = selectedExam,
                        seatings = currentSeatings,
                        allRooms = selectedRoomsList,
                        onPrintChart = { onNavigate(Screen.PrintExport.route) }
                    )
                }
                2 -> {
                    // Student Seat Finder & Hall Ticket
                    StudentSeatFinderTab(
                        query = plannerState.studentSearchQuery,
                        onQueryChange = { viewModel.setStudentSearchQuery(it) },
                        searchResults = searchResults,
                        currentUser = currentUser,
                        allExams = allExams,
                        onPrintTicket = { onNavigate(Screen.PrintExport.route) }
                    )
                }
                3 -> {
                    // Exam Schedule CRUD
                    ExamScheduleTab(
                        exams = allExams,
                        isAdmin = currentUser.role == Role.ADMIN,
                        onDeleteExam = { viewModel.deleteExam(it) },
                        onPlanSeating = { examId ->
                            viewModel.selectExam(examId)
                            activeTab = 0
                        }
                    )
                }
            }
        }
    }

    if (showAddExamDialog) {
        AddExamDialog(
            onDismiss = { showAddExamDialog = false },
            onSave = { newExam ->
                viewModel.saveExam(newExam)
                showAddExamDialog = false
            }
        )
    }
}

@Composable
fun SmartPlannerTab(
    allExams: List<ExamScheduleEntity>,
    selectedExam: ExamScheduleEntity?,
    allRooms: List<com.example.data.local.entity.RoomEntity>,
    plannerState: com.example.ui.viewmodel.ExamPlannerUiState,
    totalCapacity: Int,
    allocatedCount: Int,
    onSelectExam: (Long) -> Unit,
    onToggleRoom: (Long) -> Unit,
    onToggleAlternate: (Boolean) -> Unit,
    onRunAllocation: () -> Unit,
    onViewSeatingGrid: () -> Unit,
    onPrintHallPlan: () -> Unit,
    isAdmin: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Engine Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AcademiaElectricBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AcademiaElectricBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Intelligent Exam Hall Planner",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Automatic clash-free seat matrix & capacity balancing",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Exam Picker Chips
                    Text(
                        text = "1. Select Scheduled Examination:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allExams.forEach { ex ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedExam?.id == ex.id) AcademiaBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelectExam(ex.id) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "${ex.subjectCode} (${ex.department} S${ex.semester})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (selectedExam?.id == ex.id) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${ex.examDate} - ${ex.session}",
                                        fontSize = 10.sp,
                                        color = if (selectedExam?.id == ex.id) AcademiaSkyBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Room Multi-Selector & Live Capacity Meter
                    Text(
                        text = "2. Select Exam Halls / Rooms (Multi-Select):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allRooms.forEach { rm ->
                            val isSelected = plannerState.selectedRoomIds.contains(rm.id)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AcademiaSuccess.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AcademiaSuccess) else null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onToggleRoom(rm.id) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MeetingRoom,
                                        contentDescription = null,
                                        tint = if (isSelected) AcademiaSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = rm.roomNumber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${rm.examCapacity} exam desks",
                                            fontSize = 10.sp,
                                            color = AcademiaSuccess,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Capacity Status Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Selected Exam Capacity: $totalCapacity Desks",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = AcademiaElectricBlue
                                )
                                Text(
                                    text = "Current Seated: $allocatedCount Students",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (totalCapacity >= 10) AcademiaSuccess.copy(alpha = 0.15f) else AcademiaWarning.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (totalCapacity >= 10) "Capacity Adequate" else "Add More Rooms",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalCapacity >= 10) AcademiaSuccess else AcademiaWarning,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Anti-Malpractice Alternate Seating Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Anti-Malpractice Alternate Seating",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Alternates desks with secondary department (ECE / IT) to prevent exam misconduct",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = plannerState.isAlternateSeatingEnabled,
                            onCheckedChange = { onToggleAlternate(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Allocation Action Button
                    if (isAdmin) {
                        Button(
                            onClick = onRunAllocation,
                            enabled = !plannerState.isGenerating && selectedExam != null,
                            colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_auto_allocation_button")
                        ) {
                            if (plannerState.isGenerating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating Matrix...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Smart Seating Matrix", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Action Quick Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewSeatingGrid,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.EventSeat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Grid ($allocatedCount)", fontSize = 12.sp)
                }

                Button(
                    onClick = onPrintHallPlan,
                    colors = ButtonDefaults.buttonColors(containerColor = AcademiaSuccess),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export / Print", fontSize = 12.sp)
                }
            }
        }

        // Summary result notice if available
        plannerState.lastResult?.let { res ->
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (res.success) AcademiaSuccess.copy(alpha = 0.12f) else AcademiaError.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (res.success) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (res.success) AcademiaSuccess else AcademiaError
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = res.message,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (res.success) AcademiaSuccess else AcademiaError
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeatingGridVisualizerTab(
    selectedExam: ExamScheduleEntity?,
    seatings: List<ExamSeatingEntity>,
    allRooms: List<com.example.data.local.entity.RoomEntity>,
    onPrintChart: () -> Unit
) {
    if (seatings.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.EventSeat,
            title = "No Seats Allocated Yet",
            message = "Run the Smart Planner or select an exam with generated seating to view the visual desk matrix."
        )
        return
    }

    val distinctHalls = seatings.map { it.roomNumber }.distinct()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Visual Hall Desk Matrix",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${selectedExam?.subjectCode} &bull; ${seatings.size} Seats Allocated",
                        fontSize = 11.sp,
                        color = AcademiaElectricBlue
                    )
                }

                Button(
                    onClick = onPrintChart,
                    colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Print Chart", fontSize = 11.sp)
                }
            }
        }

        distinctHalls.forEach { hallName ->
            val hallSeatings = seatings.filter { it.roomNumber == hallName }
            val invigilator = hallSeatings.firstOrNull()?.invigilatorName ?: "Faculty"

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                                color = AcademiaBluePrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "HALL: $hallName",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AcademiaBluePrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Invigilator: $invigilator",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Desk Grid
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            hallSeatings.chunked(2).forEach { rowDesks ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowDesks.forEach { seat ->
                                        DeskItem(
                                            seat = seat,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowDesks.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DeskItem(
    seat: ExamSeatingEntity,
    modifier: Modifier = Modifier
) {
    val isPrimaryDept = seat.studentDept == "CSE"
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isPrimaryDept) AcademiaBluePrimary.copy(alpha = 0.08f) else AcademiaSuccess.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPrimaryDept) AcademiaElectricBlue.copy(alpha = 0.4f) else AcademiaSuccess.copy(alpha = 0.4f)
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isPrimaryDept) AcademiaElectricBlue else AcademiaSuccess
                ) {
                    Text(
                        text = "Desk #${seat.deskNumber}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "R${seat.rowNumber}-C${seat.colNumber}",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = seat.studentRollNo,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = seat.studentName,
                fontSize = 11.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${seat.studentDept} (S${seat.studentSemester})",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPrimaryDept) AcademiaBluePrimary else AcademiaSuccess
            )
        }
    }
}

@Composable
fun StudentSeatFinderTab(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<ExamSeatingEntity>,
    currentUser: CurrentUser,
    allExams: List<ExamScheduleEntity>,
    onPrintTicket: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search by Register No, Roll No, or Name") },
            placeholder = { Text("e.g. 22CSE01 or Aarav") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("student_seat_search_input")
        )

        // Preset quick chips for easy 1-tap testing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Sample Roll Nos:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            listOf("22CSE01", "22CSE02", "23ECE01", "23ECE02", "711222104001").forEach { sample ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { onQueryChange(sample) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(sample, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AcademiaElectricBlue)
                }
            }
        }

        if (searchResults.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Badge,
                title = "No Seating Allocation Found",
                message = "Search for a student's register/roll number or make sure smart seating has been generated."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(searchResults) { seating ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = seating.studentName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Reg No: ${seating.studentRegNo} &bull; Roll: ${seating.studentRollNo}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AcademiaBluePrimary
                                ) {
                                    Text(
                                        text = "Desk #${seating.deskNumber}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Highlighted Hall Card
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AcademiaBluePrimary.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Allocated Hall: ${seating.roomNumber}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = AcademiaBluePrimary
                                        )
                                        Text(
                                            text = "Position: Row ${seating.rowNumber}, Column ${seating.colNumber}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Invigilator: ${seating.invigilatorName}",
                                            fontSize = 11.sp,
                                            color = AcademiaSuccess,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = onPrintTicket,
                                        colors = ButtonDefaults.buttonColors(containerColor = AcademiaElectricBlue)
                                    ) {
                                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Hall Ticket", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamScheduleTab(
    exams: List<ExamScheduleEntity>,
    isAdmin: Boolean,
    onDeleteExam: (ExamScheduleEntity) -> Unit,
    onPlanSeating: (Long) -> Unit
) {
    if (exams.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Assignment,
            title = "No Exams Scheduled",
            message = "Add new semester examinations to schedule halls and generate seats."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(exams) { exam ->
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
                                color = AcademiaBluePrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${exam.department} Sem ${exam.semester}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademiaBluePrimary,
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${exam.startTime} - ${exam.endTime} &bull; ${exam.title}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { onPlanSeating(exam.id) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(Icons.Default.EventSeat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Allocate Seats", fontSize = 12.sp)
                            }

                            if (isAdmin) {
                                IconButton(
                                    onClick = { onDeleteExam(exam) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademiaError)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExamDialog(
    onDismiss: () -> Unit,
    onSave: (ExamScheduleEntity) -> Unit
) {
    var title by remember { mutableStateOf("End Semester Exam - Nov 2026") }
    var examDate by remember { mutableStateOf("2026-11-22") }
    var session by remember { mutableStateOf("FN") }
    var startTime by remember { mutableStateOf("09:30 AM") }
    var endTime by remember { mutableStateOf("12:30 PM") }
    var subjectCode by remember { mutableStateOf("CS3581") }
    var subjectName by remember { mutableStateOf("Networks Lab Exam") }
    var department by remember { mutableStateOf("CSE") }
    var semester by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule New Examination", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Exam Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Code") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Dept") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = semester,
                        onValueChange = { semester = it },
                        label = { Text("Sem") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Subject Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = examDate,
                        onValueChange = { examDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = session,
                        onValueChange = { session = it },
                        label = { Text("Session (FN/AN)") },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val exam = ExamScheduleEntity(
                        title = title,
                        examDate = examDate,
                        session = session,
                        startTime = startTime,
                        endTime = endTime,
                        subjectCode = subjectCode,
                        subjectName = subjectName,
                        department = department,
                        year = ((semester.toIntOrNull() ?: 5) + 1) / 2,
                        semester = semester.toIntOrNull() ?: 5
                    )
                    onSave(exam)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary)
            ) {
                Text("Schedule Exam")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
