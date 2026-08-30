package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CurrentUser
import com.example.ui.theme.AcademiaBlueDark
import com.example.ui.theme.AcademiaBluePrimary
import com.example.ui.theme.AcademiaElectricBlue
import com.example.ui.theme.AcademiaSkyBlue
import com.example.ui.theme.AcademiaSuccess
import com.example.ui.viewmodel.AcademiaViewModel
import com.example.util.PrintHelper

@Composable
fun PrintExportScreen(
    viewModel: AcademiaViewModel,
    currentUser: CurrentUser
) {
    val context = LocalContext.current
    var selectedExportTab by remember { mutableIntStateOf(0) } // 0 = Class Timetable, 1 = Staff Timetable, 2 = Exam Seating Chart, 3 = Student Hall Ticket

    val allSlots by viewModel.allTimetableSlots.collectAsStateWithLifecycle()
    val allExams by viewModel.allExams.collectAsStateWithLifecycle()
    val allStaff by viewModel.allStaff.collectAsStateWithLifecycle()

    val activeExam = allExams.firstOrNull()
    val seatingsFlow = remember(activeExam?.id) {
        viewModel.getSeatingsForCurrentExam(activeExam?.id ?: 1L)
    }
    val seatings by seatingsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    // Class timetable filter
    val classSlots = allSlots.filter { it.department == "CSE" && it.semester == 5 && it.section == "A" }
    // Staff timetable filter
    val staffSlots = allSlots.filter { it.staffId == "MDR-FAC-101" }

    val activeStudentSeating = seatings.find { it.studentRegNo == currentUser.identifier } ?: seatings.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero title
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AcademiaBluePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        tint = AcademiaBluePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "PDF & Document Center",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Official print-ready formats with academic seal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Export Tab Selection
        TabRow(
            selectedTabIndex = selectedExportTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedExportTab == 0,
                onClick = { selectedExportTab = 0 },
                text = { Text("Class Table", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                modifier = Modifier.testTag("tab_export_class")
            )
            Tab(
                selected = selectedExportTab == 1,
                onClick = { selectedExportTab = 1 },
                text = { Text("Faculty Table", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                modifier = Modifier.testTag("tab_export_staff")
            )
            Tab(
                selected = selectedExportTab == 2,
                onClick = { selectedExportTab = 2 },
                text = { Text("Seating Chart", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                modifier = Modifier.testTag("tab_export_seating")
            )
            Tab(
                selected = selectedExportTab == 3,
                onClick = { selectedExportTab = 3 },
                text = { Text("Hall Ticket", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                modifier = Modifier.testTag("tab_export_hallticket")
            )
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    when (selectedExportTab) {
                        0 -> {
                            val html = PrintHelper.generateTimetableHtml(
                                title = "CLASS TIMETABLE - B.TECH CSE (SEM 5, SEC A)",
                                subtitle = "Academic Year 2026-2027 &bull; Odd Semester",
                                slots = classSlots
                            )
                            PrintHelper.printHtml(context, "Class_Timetable_CSE_S5A", html)
                        }
                        1 -> {
                            val html = PrintHelper.generateTimetableHtml(
                                title = "FACULTY WORKLOAD TIMETABLE - DR. R. VIGNESH",
                                subtitle = "Associate Professor, Department of Computer Science & Engineering",
                                slots = staffSlots
                            )
                            PrintHelper.printHtml(context, "Faculty_Timetable_Dr_Vignesh", html)
                        }
                        2 -> {
                            activeExam?.let { exam ->
                                val html = PrintHelper.generateExamSeatingHtml(exam, seatings)
                                PrintHelper.printHtml(context, "Exam_Seating_Chart_${exam.subjectCode}", html)
                            }
                        }
                        3 -> {
                            if (activeStudentSeating != null && activeExam != null) {
                                val html = PrintHelper.generateHallTicketHtml(activeStudentSeating, activeExam)
                                PrintHelper.printHtml(context, "Hall_Ticket_${activeStudentSeating.studentRollNo}", html)
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademiaBluePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("print_document_button")
            ) {
                Icon(Icons.Default.Print, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Print / Save PDF", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    when (selectedExportTab) {
                        0 -> {
                            val summary = "MDR 1225 TECH - CSE S5A Timetable\nTotal Periods: ${classSlots.size}\nSubjects: CS3501, CS3581, CS3591\nGenerated via Academia System"
                            PrintHelper.shareText(context, "Class Timetable CSE S5A", summary)
                        }
                        1 -> {
                            val summary = "MDR 1225 TECH - Dr. R. Vignesh Workload\nTotal Assigned Hours: ${staffSlots.size}\nDept: CSE\nGenerated via Academia System"
                            PrintHelper.shareText(context, "Faculty Timetable Dr Vignesh", summary)
                        }
                        2 -> {
                            val summary = "MDR 1225 TECH - Exam Seating Chart\nExam: ${activeExam?.subjectCode} - ${activeExam?.subjectName}\nDate: ${activeExam?.examDate}\nTotal Seated: ${seatings.size} Students"
                            PrintHelper.shareText(context, "Exam Seating Chart", summary)
                        }
                        3 -> {
                            val summary = "MDR 1225 TECH - Hall Ticket\nStudent: ${activeStudentSeating?.studentName}\nReg: ${activeStudentSeating?.studentRegNo}\nHall: ${activeStudentSeating?.roomNumber}\nDesk: #${activeStudentSeating?.deskNumber}"
                            PrintHelper.shareText(context, "Exam Admit Card", summary)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(0.9f)
                    .height(48.dp)
                    .testTag("share_document_button")
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share Text")
            }
        }

        // Live Document Preview Card
        Text(
            text = "LIVE DOCUMENT PREVIEW",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Official College Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MDR 1225 TECH – ACADEMIA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = AcademiaBluePrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Autonomous Institution &bull; Office of the Controller of Examinations",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AcademiaElectricBlue.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = when (selectedExportTab) {
                                0 -> "OFFICIAL CLASS TIMETABLE (CSE S5A)"
                                1 -> "FACULTY ACADEMIC WORKLOAD (DR. R. VIGNESH)"
                                2 -> "EXAM HALL MASTER SEATING ALLOCATION"
                                3 -> "STUDENT EXAMINATION ADMIT CARD / HALL TICKET"
                                else -> "OFFICIAL DOCUMENT"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademiaElectricBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Document Specific Preview
                when (selectedExportTab) {
                    0 -> { // Class Timetable
                        classSlots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Period ${slot.periodNumber} (${slot.startTime})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademiaBluePrimary
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${slot.subjectCode} - ${slot.subjectName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Room: ${slot.roomNumber} &bull; Faculty: ${slot.staffName}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                    1 -> { // Faculty Timetable
                        staffSlots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Day ${slot.dayOfWeek} &bull; P${slot.periodNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademiaSuccess
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${slot.subjectCode} (${slot.department} S${slot.semester} ${slot.section})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Hall: ${slot.roomNumber} (${slot.startTime} - ${slot.endTime})",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                    2 -> { // Seating Chart
                        activeExam?.let { ex ->
                            Text(
                                text = "Exam: ${ex.title} (${ex.subjectCode} - ${ex.subjectName})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Date: ${ex.examDate} &bull; Total Allocated: ${seatings.size} Candidates",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        seatings.take(8).forEach { seat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Desk #${seat.deskNumber} (${seat.roomNumber})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademiaBluePrimary
                                )
                                Text(
                                    text = "${seat.studentRollNo} - ${seat.studentName} (${seat.studentDept})",
                                    fontSize = 11.sp
                                )
                            }
                        }
                        if (seatings.size > 8) {
                            Text(
                                text = "+ ${seatings.size - 8} more students in full printed sheet...",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    3 -> { // Hall Ticket Preview
                        if (activeStudentSeating != null && activeExam != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AcademiaBluePrimary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text("Student: ${activeStudentSeating.studentName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Register No: ${activeStudentSeating.studentRegNo} | Roll: ${activeStudentSeating.studentRollNo}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Course: B.Tech ${activeStudentSeating.studentDept} (Semester ${activeStudentSeating.studentSemester})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AcademiaBluePrimary
                                ) {
                                    Text(
                                        text = "HALL: ${activeStudentSeating.roomNumber}  &bull;  DESK #${activeStudentSeating.deskNumber}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Position: Row ${activeStudentSeating.rowNumber}, Column ${activeStudentSeating.colNumber} &bull; Invigilator: ${activeStudentSeating.invigilatorName}", fontSize = 10.sp, color = AcademiaElectricBlue)
                            }
                        } else {
                            Text("Please generate seating or select an exam to preview hall ticket.", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer signatures
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Generated by Academia Portal", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Authorized Signatory", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
