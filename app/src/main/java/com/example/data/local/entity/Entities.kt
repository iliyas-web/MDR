package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "staff",
    indices = [Index(value = ["staffId"], unique = true)]
)
data class StaffEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val staffId: String,
    val name: String,
    val email: String,
    val phone: String,
    val department: String,
    val designation: String,
    val specialization: String,
    val maxWeeklyHours: Int = 18
)

@Entity(
    tableName = "students",
    indices = [
        Index(value = ["regNo"], unique = true),
        Index(value = ["rollNo"], unique = true)
    ]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val regNo: String,
    val rollNo: String,
    val name: String,
    val email: String,
    val department: String,
    val year: Int,
    val semester: Int,
    val section: String
)

@Entity(
    tableName = "subjects",
    indices = [Index(value = ["code"], unique = true)]
)
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val department: String,
    val semester: Int,
    val credits: Int,
    val type: String, // Theory, Lab, Elective
    val assignedStaffId: String = ""
)

@Entity(
    tableName = "rooms",
    indices = [Index(value = ["roomNumber"], unique = true)]
)
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomNumber: String,
    val blockName: String,
    val floor: Int,
    val capacity: Int,
    val examCapacity: Int,
    val roomType: String, // Lecture Hall, Computer Lab, Seminar Hall
    val hasProjector: Boolean = true,
    val isAirConditioned: Boolean = false
)

@Entity(
    tableName = "timetable_slots",
    indices = [
        Index(value = ["dayOfWeek", "periodNumber", "department", "semester", "section"]),
        Index(value = ["dayOfWeek", "periodNumber", "staffId"]),
        Index(value = ["dayOfWeek", "periodNumber", "roomId"])
    ]
)
data class TimetableSlotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: Int, // 1 = Monday ... 6 = Saturday
    val periodNumber: Int, // 1 to 7
    val startTime: String, // e.g. "09:00 AM"
    val endTime: String,   // e.g. "09:50 AM"
    val subjectCode: String,
    val subjectName: String,
    val staffId: String,
    val staffName: String,
    val roomId: Long,
    val roomNumber: String,
    val department: String,
    val year: Int,
    val semester: Int,
    val section: String
)

@Entity(tableName = "exam_schedules")
data class ExamScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val examDate: String, // "YYYY-MM-DD"
    val session: String, // "FN" (09:30 AM - 12:30 PM) or "AN" (01:30 PM - 04:30 PM)
    val startTime: String,
    val endTime: String,
    val subjectCode: String,
    val subjectName: String,
    val department: String,
    val year: Int,
    val semester: Int
)

@Entity(
    tableName = "exam_seatings",
    indices = [
        Index(value = ["examScheduleId", "studentRegNo"], unique = true),
        Index(value = ["examScheduleId", "roomId", "deskNumber"], unique = true)
    ]
)
data class ExamSeatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examScheduleId: Long,
    val examTitle: String,
    val examDate: String,
    val session: String,
    val roomId: Long,
    val roomNumber: String,
    val deskNumber: Int,
    val rowNumber: Int,
    val colNumber: Int,
    val studentRegNo: String,
    val studentRollNo: String,
    val studentName: String,
    val studentDept: String,
    val studentSemester: Int,
    val studentSection: String,
    val subjectCode: String,
    val subjectName: String,
    val invigilatorStaffId: String = "",
    val invigilatorName: String = ""
)
