package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.CurrentUser
import com.example.data.model.Role
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.AcademiaBluePrimary
import com.example.ui.theme.AcademiaCyanAccent
import com.example.ui.theme.AcademiaElectricBlue
import com.example.ui.theme.AcademiaError
import com.example.ui.theme.AcademiaSuccess
import com.example.ui.theme.AcademiaWarning
import com.example.ui.viewmodel.AcademiaViewModel

@Composable
fun ManagementScreen(
    viewModel: AcademiaViewModel,
    currentUser: CurrentUser
) {
    val mState by viewModel.managementState.collectAsStateWithLifecycle()
    val allStaff by viewModel.allStaff.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val allRooms by viewModel.allRooms.collectAsStateWithLifecycle()

    var showAddStaffDialog by remember { mutableStateOf(false) }
    var editingStaff by remember { mutableStateOf<StaffEntity?>(null) }

    var showAddStudentDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<StudentEntity?>(null) }

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<SubjectEntity?>(null) }

    var showAddRoomDialog by remember { mutableStateOf(false) }
    var editingRoom by remember { mutableStateOf<RoomEntity?>(null) }

    val isAdmin = currentUser.role == Role.ADMIN

    // Filters
    val query = mState.searchQuery.lowercase()
    val deptFilter = mState.deptFilter

    val filteredStaff = allStaff.filter {
        (deptFilter == "ALL" || it.department.equals(deptFilter, ignoreCase = true)) &&
        (it.name.lowercase().contains(query) || it.staffId.lowercase().contains(query) || it.designation.lowercase().contains(query))
    }

    val filteredStudents = allStudents.filter {
        (deptFilter == "ALL" || it.department.equals(deptFilter, ignoreCase = true)) &&
        (it.name.lowercase().contains(query) || it.regNo.lowercase().contains(query) || it.rollNo.lowercase().contains(query))
    }

    val filteredSubjects = allSubjects.filter {
        (deptFilter == "ALL" || it.department.equals(deptFilter, ignoreCase = true)) &&
        (it.name.lowercase().contains(query) || it.code.lowercase().contains(query))
    }

    val filteredRooms = allRooms.filter {
        it.roomNumber.lowercase().contains(query) || it.blockName.lowercase().contains(query) || it.roomType.lowercase().contains(query)
    }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = {
                        when (mState.activeTab) {
                            0 -> { editingStaff = null; showAddStaffDialog = true }
                            1 -> { editingStudent = null; showAddStudentDialog = true }
                            2 -> { editingSubject = null; showAddSubjectDialog = true }
                            3 -> { editingRoom = null; showAddRoomDialog = true }
                        }
                    },
                    containerColor = AcademiaBluePrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("management_add_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Entry")
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

            // Tab Row
            TabRow(
                selectedTabIndex = mState.activeTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = mState.activeTab == 0,
                    onClick = { viewModel.setManagementTab(0) },
                    text = { Text("Staff (${filteredStaff.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_manage_staff")
                )
                Tab(
                    selected = mState.activeTab == 1,
                    onClick = { viewModel.setManagementTab(1) },
                    text = { Text("Students (${filteredStudents.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_manage_students")
                )
                Tab(
                    selected = mState.activeTab == 2,
                    onClick = { viewModel.setManagementTab(2) },
                    text = { Text("Subjects (${filteredSubjects.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_manage_subjects")
                )
                Tab(
                    selected = mState.activeTab == 3,
                    onClick = { viewModel.setManagementTab(3) },
                    text = { Text("Halls (${filteredRooms.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_manage_rooms")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar & Filter Chips
            OutlinedTextField(
                value = mState.searchQuery,
                onValueChange = { viewModel.setManagementSearch(it) },
                placeholder = { Text("Search by name, ID, roll no, code...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("management_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (mState.activeTab != 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Department:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    listOf("ALL", "CSE", "IT", "ECE", "AI&DS").forEach { dept ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (deptFilter == dept) AcademiaBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { viewModel.setManagementDeptFilter(dept) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = dept,
                                fontSize = 11.sp,
                                fontWeight = if (deptFilter == dept) FontWeight.Bold else FontWeight.Normal,
                                color = if (deptFilter == dept) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // List Content
            when (mState.activeTab) {
                0 -> {
                    // Staff List
                    if (filteredStaff.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Person,
                            title = "No Faculty Found",
                            message = "No faculty match the current query or department filter."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredStaff) { staff ->
                                StaffCard(
                                    staff = staff,
                                    isAdmin = isAdmin,
                                    onEdit = { editingStaff = staff; showAddStaffDialog = true },
                                    onDelete = { viewModel.deleteStaff(staff) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(70.dp)) }
                        }
                    }
                }
                1 -> {
                    // Students List
                    if (filteredStudents.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.People,
                            title = "No Students Found",
                            message = "No students match the current query or department filter."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredStudents) { student ->
                                StudentCard(
                                    student = student,
                                    isAdmin = isAdmin,
                                    onEdit = { editingStudent = student; showAddStudentDialog = true },
                                    onDelete = { viewModel.deleteStudent(student) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(70.dp)) }
                        }
                    }
                }
                2 -> {
                    // Subjects List
                    if (filteredSubjects.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Book,
                            title = "No Subjects Found",
                            message = "No courses match the current search query."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredSubjects) { subject ->
                                SubjectCard(
                                    subject = subject,
                                    isAdmin = isAdmin,
                                    onEdit = { editingSubject = subject; showAddSubjectDialog = true },
                                    onDelete = { viewModel.deleteSubject(subject) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(70.dp)) }
                        }
                    }
                }
                3 -> {
                    // Rooms List
                    if (filteredRooms.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.MeetingRoom,
                            title = "No Halls / Rooms Found",
                            message = "No campus rooms match the current search query."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredRooms) { room ->
                                RoomCard(
                                    room = room,
                                    isAdmin = isAdmin,
                                    onEdit = { editingRoom = room; showAddRoomDialog = true },
                                    onDelete = { viewModel.deleteRoom(room) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(70.dp)) }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddStaffDialog) {
        AddEditStaffDialog(
            staff = editingStaff,
            onDismiss = { showAddStaffDialog = false },
            onSave = {
                viewModel.saveStaff(it)
                showAddStaffDialog = false
            }
        )
    }

    if (showAddStudentDialog) {
        AddEditStudentDialog(
            student = editingStudent,
            onDismiss = { showAddStudentDialog = false },
            onSave = {
                viewModel.saveStudent(it)
                showAddStudentDialog = false
            }
        )
    }

    if (showAddSubjectDialog) {
        AddEditSubjectDialog(
            subject = editingSubject,
            onDismiss = { showAddSubjectDialog = false },
            onSave = {
                viewModel.saveSubject(it)
                showAddSubjectDialog = false
            }
        )
    }

    if (showAddRoomDialog) {
        AddEditRoomDialog(
            room = editingRoom,
            onDismiss = { showAddRoomDialog = false },
            onSave = {
                viewModel.saveRoom(it)
                showAddRoomDialog = false
            }
        )
    }
}

@Composable
fun StaffCard(
    staff: StaffEntity,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(AcademiaBluePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = staff.name.split(" ").lastOrNull()?.take(1) ?: "F",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AcademiaBluePrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = staff.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AcademiaSuccess.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = staff.department,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademiaSuccess,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${staff.designation} &bull; ID: ${staff.staffId}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${staff.email} &bull; ${staff.phone}",
                    fontSize = 11.sp,
                    color = AcademiaElectricBlue
                )
            }

            if (isAdmin) {
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademiaError, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(
    student: StudentEntity,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(AcademiaCyanAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.rollNo.takeLast(2),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AcademiaBluePrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AcademiaBluePrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${student.department} S${student.semester}-${student.section}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademiaBluePrimary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Reg: ${student.regNo} &bull; Roll: ${student.rollNo}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = student.email,
                    fontSize = 11.sp,
                    color = AcademiaElectricBlue
                )
            }

            if (isAdmin) {
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademiaError, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: SubjectEntity,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AcademiaBluePrimary.copy(alpha = 0.12f),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = subject.code,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AcademiaBluePrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${subject.department} &bull; Sem ${subject.semester} &bull; ${subject.credits} Credits (${subject.type})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isAdmin) {
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademiaError, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomCard(
    room: RoomEntity,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AcademiaWarning.copy(alpha = 0.15f),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = AcademiaWarning, modifier = Modifier.size(20.dp))
                    Text(
                        text = room.roomNumber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${room.blockName} - Floor ${room.floor}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Type: ${room.roomType} &bull; Class Capacity: ${room.capacity}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Exam Capacity: ${room.examCapacity} Desks",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AcademiaSuccess
                )
            }

            if (isAdmin) {
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademiaError, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// Dialogs
@Composable
fun AddEditStaffDialog(
    staff: StaffEntity?,
    onDismiss: () -> Unit,
    onSave: (StaffEntity) -> Unit
) {
    var staffId by remember { mutableStateOf(staff?.staffId ?: "MDR-FAC-106") }
    var name by remember { mutableStateOf(staff?.name ?: "") }
    var department by remember { mutableStateOf(staff?.department ?: "CSE") }
    var designation by remember { mutableStateOf(staff?.designation ?: "Assistant Professor") }
    var specialization by remember { mutableStateOf(staff?.specialization ?: "Cloud Computing") }
    var email by remember { mutableStateOf(staff?.email ?: "") }
    var phone by remember { mutableStateOf(staff?.phone ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (staff == null) "Add Faculty Member" else "Edit Faculty Member", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = staffId, onValueChange = { staffId = it }, label = { Text("Staff ID") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Department") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = designation, onValueChange = { designation = it }, label = { Text("Designation") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = specialization, onValueChange = { specialization = it }, label = { Text("Specialization") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        StaffEntity(
                            id = staff?.id ?: 0L,
                            staffId = staffId,
                            name = name,
                            department = department,
                            designation = designation,
                            specialization = specialization,
                            email = email,
                            phone = phone
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary)
            ) { Text("Save Staff") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddEditStudentDialog(
    student: StudentEntity?,
    onDismiss: () -> Unit,
    onSave: (StudentEntity) -> Unit
) {
    var regNo by remember { mutableStateOf(student?.regNo ?: "") }
    var rollNo by remember { mutableStateOf(student?.rollNo ?: "") }
    var name by remember { mutableStateOf(student?.name ?: "") }
    var dept by remember { mutableStateOf(student?.department ?: "CSE") }
    var year by remember { mutableStateOf((student?.year ?: 3).toString()) }
    var sem by remember { mutableStateOf((student?.semester ?: 5).toString()) }
    var sec by remember { mutableStateOf(student?.section ?: "A") }
    var email by remember { mutableStateOf(student?.email ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (student == null) "Enroll New Student" else "Edit Student Record", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = regNo, onValueChange = { regNo = it }, label = { Text("Register Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = rollNo, onValueChange = { rollNo = it }, label = { Text("Roll Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Student Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = dept, onValueChange = { dept = it }, label = { Text("Dept") }, singleLine = true, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(value = sem, onValueChange = { sem = it }, label = { Text("Sem") }, singleLine = true, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(value = sec, onValueChange = { sec = it }, label = { Text("Sec") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        StudentEntity(
                            id = student?.id ?: 0L,
                            regNo = regNo,
                            rollNo = rollNo,
                            name = name,
                            department = dept,
                            year = year.toIntOrNull() ?: 3,
                            semester = sem.toIntOrNull() ?: 5,
                            section = sec,
                            email = email
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary)
            ) { Text("Save Student") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddEditSubjectDialog(
    subject: SubjectEntity?,
    onDismiss: () -> Unit,
    onSave: (SubjectEntity) -> Unit
) {
    var code by remember { mutableStateOf(subject?.code ?: "") }
    var name by remember { mutableStateOf(subject?.name ?: "") }
    var dept by remember { mutableStateOf(subject?.department ?: "CSE") }
    var sem by remember { mutableStateOf((subject?.semester ?: 5).toString()) }
    var credits by remember { mutableStateOf((subject?.credits ?: 3).toString()) }
    var type by remember { mutableStateOf(subject?.type ?: "Theory") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (subject == null) "Add Subject Course" else "Edit Subject", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Course Code (e.g. CS3501)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Course Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = dept, onValueChange = { dept = it }, label = { Text("Dept") }, singleLine = true, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(value = sem, onValueChange = { sem = it }, label = { Text("Sem") }, singleLine = true, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(value = credits, onValueChange = { credits = it }, label = { Text("Credits") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (Theory / Practical)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        SubjectEntity(
                            id = subject?.id ?: 0L,
                            code = code,
                            name = name,
                            department = dept,
                            semester = sem.toIntOrNull() ?: 5,
                            credits = credits.toIntOrNull() ?: 3,
                            type = type
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary)
            ) { Text("Save Subject") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddEditRoomDialog(
    room: RoomEntity?,
    onDismiss: () -> Unit,
    onSave: (RoomEntity) -> Unit
) {
    var roomNum by remember { mutableStateOf(room?.roomNumber ?: "") }
    var blockName by remember { mutableStateOf(room?.blockName ?: "Main Academic Block") }
    var floor by remember { mutableStateOf((room?.floor ?: 1).toString()) }
    var capacity by remember { mutableStateOf((room?.capacity ?: 60).toString()) }
    var examCap by remember { mutableStateOf((room?.examCapacity ?: 30).toString()) }
    var type by remember { mutableStateOf(room?.roomType ?: "Lecture Hall") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (room == null) "Add Hall / Room" else "Edit Hall", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = roomNum, onValueChange = { roomNum = it }, label = { Text("Room / Hall Number (e.g. LH-201)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = blockName, onValueChange = { blockName = it }, label = { Text("Block Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Class Cap") }, singleLine = true, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(value = examCap, onValueChange = { examCap = it }, label = { Text("Exam Cap") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Room Type (Lecture Hall / Lab / Drawing)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        RoomEntity(
                            id = room?.id ?: 0L,
                            roomNumber = roomNum,
                            blockName = blockName,
                            floor = floor.toIntOrNull() ?: 1,
                            capacity = capacity.toIntOrNull() ?: 60,
                            examCapacity = examCap.toIntOrNull() ?: 30,
                            roomType = type
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary)
            ) { Text("Save Room") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
