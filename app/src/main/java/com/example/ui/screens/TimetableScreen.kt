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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.RoomEntity
import com.example.data.local.entity.StaffEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TimetableSlotEntity
import com.example.data.model.CurrentUser
import com.example.data.model.Role
import com.example.data.repository.ClashCheckResult
import com.example.ui.components.ClashConflictAlertDialog
import com.example.ui.components.EmptyStateView
import com.example.ui.navigation.Screen
import com.example.ui.theme.AcademiaBluePrimary
import com.example.ui.theme.AcademiaElectricBlue
import com.example.ui.theme.AcademiaError
import com.example.ui.theme.AcademiaSkyBlue
import com.example.ui.theme.AcademiaSuccess
import com.example.ui.theme.AcademiaWarning
import com.example.ui.viewmodel.AcademiaViewModel
import kotlinx.coroutines.launch

@Composable
fun TimetableScreen(
    viewModel: AcademiaViewModel,
    currentUser: CurrentUser,
    onNavigate: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val filter by viewModel.timetableFilter.collectAsStateWithLifecycle()
    val slots by viewModel.currentFilteredSlots.collectAsStateWithLifecycle()
    val allStaff by viewModel.allStaff.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val allRooms by viewModel.allRooms.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingSlot by remember { mutableStateOf<TimetableSlotEntity?>(null) }
    var activeClash by remember { mutableStateOf<ClashCheckResult>(ClashCheckResult.NoClash) }
    var pendingSaveSlot by remember { mutableStateOf<TimetableSlotEntity?>(null) }

    val daysList = listOf(
        1 to "Mon",
        2 to "Tue",
        3 to "Wed",
        4 to "Thu",
        5 to "Fri",
        6 to "Sat"
    )

    Scaffold(
        floatingActionButton = {
            if (currentUser.role == Role.ADMIN) {
                FloatingActionButton(
                    onClick = {
                        editingSlot = null
                        showAddDialog = true
                    },
                    containerColor = AcademiaBluePrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_timetable_slot_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Timetable Slot")
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

            // Timetable Mode Segmented Control: Class vs Staff
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = !filter.isStaffMode,
                    onClick = { viewModel.updateTimetableFilter(staffMode = false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    modifier = Modifier.testTag("seg_class_timetable")
                ) {
                    Text("Student / Class View", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                SegmentedButton(
                    selected = filter.isStaffMode,
                    onClick = { viewModel.updateTimetableFilter(staffMode = true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    modifier = Modifier.testTag("seg_staff_timetable")
                ) {
                    Text("Faculty / Staff View", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Bar
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (!filter.isStaffMode) {
                        // Class filters: Dept, Year, Sem, Sec
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Dept:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            listOf("CSE", "IT", "ECE", "AI&DS").forEach { dept ->
                                FilterChip(
                                    selected = filter.department == dept,
                                    onClick = { viewModel.updateTimetableFilter(dept = dept) },
                                    label = dept
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sem:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            listOf(1, 3, 5, 7).forEach { sem ->
                                FilterChip(
                                    selected = filter.semester == sem,
                                    onClick = { viewModel.updateTimetableFilter(sem = sem, year = (sem + 1) / 2) },
                                    label = "S$sem"
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sec:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            listOf("A", "B").forEach { sec ->
                                FilterChip(
                                    selected = filter.section == sec,
                                    onClick = { viewModel.updateTimetableFilter(sec = sec) },
                                    label = sec
                                )
                            }
                        }
                    } else {
                        // Staff dropdown/chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Faculty:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            allStaff.forEach { staff ->
                                FilterChip(
                                    selected = filter.selectedStaffId == staff.staffId,
                                    onClick = { viewModel.updateTimetableFilter(staffId = staff.staffId) },
                                    label = staff.name.split(" ").last()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Day Selector + Weekly View Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    daysList.forEach { (dInt, dName) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (filter.selectedDayOfWeek == dInt && !filter.isWeeklyView) {
                                AcademiaBluePrimary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.updateTimetableFilter(day = dInt, weekly = false)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = dName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (filter.selectedDayOfWeek == dInt && !filter.isWeeklyView) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Weekly Matrix Chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (filter.isWeeklyView) AcademiaElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.updateTimetableFilter(weekly = true) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("weekly_view_chip")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = if (filter.isWeeklyView) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Weekly Matrix",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (filter.isWeeklyView) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timetable Content
            if (slots.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.CalendarMonth,
                    title = "No Classes Scheduled",
                    message = "No periods found for the selected day/filter. Click + to schedule a clash-free period.",
                    actionLabel = if (currentUser.role == Role.ADMIN) "Add Timetable Slot" else null,
                    onActionClick = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(slots) { slot ->
                        TimetablePeriodCard(
                            slot = slot,
                            canEdit = currentUser.role == Role.ADMIN,
                            onEdit = {
                                editingSlot = slot
                                showAddDialog = true
                            },
                            onDelete = {
                                viewModel.deleteTimetableSlot(slot.id)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Slot Dialog
    if (showAddDialog) {
        AddEditSlotDialog(
            slotToEdit = editingSlot,
            currentDept = filter.department,
            currentSem = filter.semester,
            currentSec = filter.section,
            currentDay = filter.selectedDayOfWeek,
            allSubjects = allSubjects,
            allStaff = allStaff,
            allRooms = allRooms,
            onDismiss = { showAddDialog = false },
            onSave = { newSlot ->
                scope.launch {
                    val result = viewModel.saveTimetableSlotWithClashCheck(newSlot, forceOverride = false)
                    if (result !is ClashCheckResult.NoClash) {
                        pendingSaveSlot = newSlot
                        activeClash = result
                    } else {
                        showAddDialog = false
                    }
                }
            }
        )
    }

    // Clash Conflict Warning Dialog
    if (activeClash !is ClashCheckResult.NoClash) {
        ClashConflictAlertDialog(
            clash = activeClash,
            onDismiss = {
                activeClash = ClashCheckResult.NoClash
                pendingSaveSlot = null
            },
            onForceSchedule = {
                pendingSaveSlot?.let { slot ->
                    scope.launch {
                        viewModel.saveTimetableSlotWithClashCheck(slot, forceOverride = true)
                        activeClash = ClashCheckResult.NoClash
                        pendingSaveSlot = null
                        showAddDialog = false
                    }
                }
            }
        )
    }
}

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (selected) AcademiaBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TimetablePeriodCard(
    slot: TimetableSlotEntity,
    canEdit: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dayNames = mapOf(1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat")

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
            // Period Number Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AcademiaBluePrimary.copy(alpha = 0.12f),
                modifier = Modifier.size(48.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "P${slot.periodNumber}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = AcademiaBluePrimary
                    )
                    Text(
                        text = dayNames[slot.dayOfWeek] ?: "",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AcademiaElectricBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = slot.subjectCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AcademiaElectricBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${slot.department} S${slot.semester} Sec ${slot.section}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = slot.subjectName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${slot.startTime} - ${slot.endTime} &bull; Room: ${slot.roomNumber}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Faculty: ${slot.staffName}",
                    fontSize = 11.sp,
                    color = AcademiaBluePrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            if (canEdit) {
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Slot",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Slot",
                            tint = AcademiaError,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSlotDialog(
    slotToEdit: TimetableSlotEntity?,
    currentDept: String,
    currentSem: Int,
    currentSec: String,
    currentDay: Int,
    allSubjects: List<SubjectEntity>,
    allStaff: List<StaffEntity>,
    allRooms: List<RoomEntity>,
    onDismiss: () -> Unit,
    onSave: (TimetableSlotEntity) -> Unit
) {
    var periodNumber by remember { mutableIntStateOf(slotToEdit?.periodNumber ?: 1) }
    var dayOfWeek by remember { mutableIntStateOf(slotToEdit?.dayOfWeek ?: currentDay) }
    var selectedSubjectCode by remember { mutableStateOf(slotToEdit?.subjectCode ?: allSubjects.firstOrNull()?.code ?: "CS3501") }
    var selectedSubjectName by remember { mutableStateOf(slotToEdit?.subjectName ?: allSubjects.firstOrNull()?.name ?: "Compiler Design") }
    var selectedStaffId by remember { mutableStateOf(slotToEdit?.staffId ?: allStaff.firstOrNull()?.staffId ?: "MDR-FAC-101") }
    var selectedStaffName by remember { mutableStateOf(slotToEdit?.staffName ?: allStaff.firstOrNull()?.name ?: "Dr. R. Vignesh") }
    var selectedRoomId by remember { mutableLongStateOf(slotToEdit?.roomId ?: allRooms.firstOrNull()?.id ?: 1L) }
    var selectedRoomNumber by remember { mutableStateOf(slotToEdit?.roomNumber ?: allRooms.firstOrNull()?.roomNumber ?: "LH-101") }

    var dept by remember { mutableStateOf(slotToEdit?.department ?: currentDept) }
    var sem by remember { mutableIntStateOf(slotToEdit?.semester ?: currentSem) }
    var sec by remember { mutableStateOf(slotToEdit?.section ?: currentSec) }

    val periodTimes = mapOf(
        1 to ("09:00 AM" to "09:50 AM"),
        2 to ("09:50 AM" to "10:40 AM"),
        3 to ("11:00 AM" to "11:50 AM"),
        4 to ("11:50 AM" to "12:40 PM"),
        5 to ("01:30 PM" to "02:20 PM"),
        6 to ("02:20 PM" to "03:10 PM"),
        7 to ("03:10 PM" to "04:00 PM")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (slotToEdit == null) "Schedule Timetable Slot" else "Edit Timetable Slot",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Day & Period Selector
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Day of Week:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat").forEach { (d, name) ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (dayOfWeek == d) AcademiaBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clickable { dayOfWeek = d }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(name, fontSize = 10.sp, color = if (dayOfWeek == d) Color.White else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Period Number (1 to 7):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..7).forEach { p ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (periodNumber == p) AcademiaElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clickable { periodNumber = p }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text("P$p", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (periodNumber == p) Color.White else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                // Subject Selector
                Text("Select Subject:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    allSubjects.forEach { sub ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedSubjectCode == sub.code) AcademiaBluePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSubjectCode = sub.code
                                    selectedSubjectName = sub.name
                                }
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(sub.code, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AcademiaBluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(sub.name, fontSize = 12.sp, maxLines = 1)
                            }
                        }
                    }
                }

                // Faculty Selector
                Text("Assigned Faculty:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    allStaff.forEach { st ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedStaffId == st.staffId) AcademiaSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedStaffId = st.staffId
                                    selectedStaffName = st.name
                                }
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(st.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AcademiaSuccess)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("(${st.department})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Room Selector
                Text("Lecture Hall / Lab:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allRooms.forEach { rm ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedRoomId == rm.id) AcademiaWarning.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable {
                                    selectedRoomId = rm.id
                                    selectedRoomNumber = rm.roomNumber
                                }
                                .padding(8.dp)
                        ) {
                            Text("${rm.roomNumber} (${rm.roomType})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val timePair = periodTimes[periodNumber] ?: ("09:00 AM" to "09:50 AM")
                    val slot = TimetableSlotEntity(
                        id = slotToEdit?.id ?: 0L,
                        dayOfWeek = dayOfWeek,
                        periodNumber = periodNumber,
                        startTime = timePair.first,
                        endTime = timePair.second,
                        subjectCode = selectedSubjectCode,
                        subjectName = selectedSubjectName,
                        staffId = selectedStaffId,
                        staffName = selectedStaffName,
                        roomId = selectedRoomId,
                        roomNumber = selectedRoomNumber,
                        department = dept,
                        year = (sem + 1) / 2,
                        semester = sem,
                        section = sec
                    )
                    onSave(slot)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary),
                modifier = Modifier.testTag("save_slot_button")
            ) {
                Text("Check Clashes & Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
