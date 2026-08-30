package com.example.data.repository

import com.example.data.local.dao.AcademiaDao
import com.example.data.local.entity.ExamScheduleEntity
import com.example.data.local.entity.ExamSeatingEntity
import com.example.data.local.entity.RoomEntity
import com.example.data.local.entity.StaffEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TimetableSlotEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

sealed class ClashCheckResult {
    object NoClash : ClashCheckResult()
    data class StaffConflict(val staffName: String, val existingSlot: TimetableSlotEntity) : ClashCheckResult()
    data class RoomConflict(val roomNumber: String, val existingSlot: TimetableSlotEntity) : ClashCheckResult()
    data class ClassConflict(val classDesc: String, val existingSlot: TimetableSlotEntity) : ClashCheckResult()
}

data class SeatingPlanResult(
    val success: Boolean,
    val message: String,
    val allocatedCount: Int = 0,
    val totalStudents: Int = 0,
    val roomsUsed: Int = 0
)

class AcademiaRepository(private val dao: AcademiaDao) {

    // Streams
    val allStaff: Flow<List<StaffEntity>> = dao.getAllStaff()
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    val allRooms: Flow<List<RoomEntity>> = dao.getAllRooms()
    val allTimetableSlots: Flow<List<TimetableSlotEntity>> = dao.getAllTimetableSlots()
    val allExamSchedules: Flow<List<ExamScheduleEntity>> = dao.getAllExamSchedules()

    val staffCount: Flow<Int> = dao.getStaffCount()
    val studentCount: Flow<Int> = dao.getStudentCount()
    val subjectCount: Flow<Int> = dao.getSubjectCount()
    val roomCount: Flow<Int> = dao.getRoomCount()
    val examCount: Flow<Int> = dao.getExamCount()

    // Staff
    fun getStaffByDepartment(dept: String): Flow<List<StaffEntity>> = dao.getStaffByDepartment(dept)
    suspend fun getStaffById(staffId: String): StaffEntity? = dao.getStaffById(staffId)
    suspend fun saveStaff(staff: StaffEntity): Long = dao.insertStaff(staff)
    suspend fun updateStaff(staff: StaffEntity) = dao.updateStaff(staff)
    suspend fun deleteStaff(staff: StaffEntity) = dao.deleteStaff(staff)

    // Student
    fun getStudentsByClass(dept: String, sem: Int, sec: String): Flow<List<StudentEntity>> = dao.getStudentsByClass(dept, sem, sec)
    suspend fun getStudentByRegOrRoll(query: String): StudentEntity? = dao.getStudentByRegOrRoll(query)
    suspend fun saveStudent(student: StudentEntity): Long = dao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = dao.deleteStudent(student)

    // Subject
    fun getSubjectsByDeptAndSem(dept: String, sem: Int): Flow<List<SubjectEntity>> = dao.getSubjectsByDeptAndSem(dept, sem)
    suspend fun saveSubject(subject: SubjectEntity): Long = dao.insertSubject(subject)
    suspend fun updateSubject(subject: SubjectEntity) = dao.updateSubject(subject)
    suspend fun deleteSubject(subject: SubjectEntity) = dao.deleteSubject(subject)

    // Room
    suspend fun getRoomById(roomId: Long): RoomEntity? = dao.getRoomById(roomId)
    suspend fun saveRoom(room: RoomEntity): Long = dao.insertRoom(room)
    suspend fun updateRoom(room: RoomEntity) = dao.updateRoom(room)
    suspend fun deleteRoom(room: RoomEntity) = dao.deleteRoom(room)

    // Timetable
    fun getTimetableForStaff(staffId: String): Flow<List<TimetableSlotEntity>> = dao.getTimetableForStaff(staffId)
    fun getStaffTimetableForDay(staffId: String, dayOfWeek: Int): Flow<List<TimetableSlotEntity>> = dao.getStaffTimetableForDay(staffId, dayOfWeek)
    fun getTimetableForClass(dept: String, sem: Int, sec: String): Flow<List<TimetableSlotEntity>> = dao.getTimetableForClass(dept, sem, sec)
    fun getClassTimetableForDay(dept: String, sem: Int, sec: String, dayOfWeek: Int): Flow<List<TimetableSlotEntity>> = dao.getClassTimetableForDay(dept, sem, sec, dayOfWeek)
    fun getTodayTimetableSlots(dayOfWeek: Int): Flow<List<TimetableSlotEntity>> = dao.getTodayTimetableSlots(dayOfWeek)

    suspend fun checkClash(slot: TimetableSlotEntity): ClashCheckResult {
        val staffConflict = dao.findStaffConflict(slot.dayOfWeek, slot.periodNumber, slot.staffId, slot.id)
        if (staffConflict != null) {
            return ClashCheckResult.StaffConflict(slot.staffName, staffConflict)
        }

        val roomConflict = dao.findRoomConflict(slot.dayOfWeek, slot.periodNumber, slot.roomId, slot.id)
        if (roomConflict != null) {
            return ClashCheckResult.RoomConflict(slot.roomNumber, roomConflict)
        }

        val classConflict = dao.findClassConflict(slot.dayOfWeek, slot.periodNumber, slot.department, slot.semester, slot.section, slot.id)
        if (classConflict != null) {
            return ClashCheckResult.ClassConflict("${slot.department} Sem ${slot.semester} Sec ${slot.section}", classConflict)
        }

        return ClashCheckResult.NoClash
    }

    suspend fun saveTimetableSlot(slot: TimetableSlotEntity): Long {
        return dao.insertTimetableSlot(slot)
    }

    suspend fun updateTimetableSlot(slot: TimetableSlotEntity) {
        dao.updateTimetableSlot(slot)
    }

    suspend fun deleteTimetableSlot(slot: TimetableSlotEntity) {
        dao.deleteTimetableSlot(slot)
    }

    suspend fun deleteTimetableSlotById(id: Long) {
        dao.deleteTimetableSlotById(id)
    }

    // Exam Schedule
    fun getExamsForClass(dept: String, sem: Int): Flow<List<ExamScheduleEntity>> = dao.getExamsForClass(dept, sem)
    fun getUpcomingExams(todayDate: String): Flow<List<ExamScheduleEntity>> = dao.getUpcomingExams(todayDate)
    suspend fun getExamScheduleById(id: Long): ExamScheduleEntity? = dao.getExamScheduleById(id)
    suspend fun saveExamSchedule(exam: ExamScheduleEntity): Long = dao.insertExamSchedule(exam)
    suspend fun updateExamSchedule(exam: ExamScheduleEntity) = dao.updateExamSchedule(exam)
    suspend fun deleteExamSchedule(exam: ExamScheduleEntity) {
        dao.clearSeatingsForExam(exam.id)
        dao.deleteExamSchedule(exam)
    }

    // Exam Seating
    fun getSeatingsForExam(scheduleId: Long): Flow<List<ExamSeatingEntity>> = dao.getSeatingsForExam(scheduleId)
    fun getSeatingsForStudent(query: String): Flow<List<ExamSeatingEntity>> = dao.getSeatingsForStudent(query)
    fun searchStudentSeating(query: String): Flow<List<ExamSeatingEntity>> = dao.searchStudentSeating(query)
    fun getRoomSeatingsForExam(scheduleId: Long, roomId: Long): Flow<List<ExamSeatingEntity>> = dao.getRoomSeatingsForExam(scheduleId, roomId)
    fun getSeatingCountForExam(scheduleId: Long): Flow<Int> = dao.getSeatingCountForExam(scheduleId)

    // Smart Exam Hall Allocation Engine
    suspend fun autoAllocateExamSeats(
        examScheduleId: Long,
        selectedRoomIds: List<Long>,
        mixDepartment: String? = null,
        mixSemester: Int? = null
    ): SeatingPlanResult {
        val exam = dao.getExamScheduleById(examScheduleId)
            ?: return SeatingPlanResult(false, "Exam schedule not found.")

        // Primary student list
        val primaryStudents = dao.getStudentsForExam(exam.department, exam.semester)
        if (primaryStudents.isEmpty()) {
            return SeatingPlanResult(false, "No students registered for ${exam.department} Semester ${exam.semester}.")
        }

        // Secondary student list (for alternating seating if selected)
        val secondaryStudents = if (mixDepartment != null && mixSemester != null && mixDepartment.isNotEmpty()) {
            dao.getStudentsForExam(mixDepartment, mixSemester)
        } else {
            emptyList()
        }

        val allRoomsList = dao.getAllRooms().first()
        val roomsToUse = allRoomsList.filter { selectedRoomIds.contains(it.id) }
        if (roomsToUse.isEmpty()) {
            return SeatingPlanResult(false, "Please select at least one exam hall with available capacity.")
        }

        val totalExamCapacity = roomsToUse.sumOf { it.examCapacity }
        val totalStudentsToSeat = primaryStudents.size + secondaryStudents.size

        if (totalExamCapacity < totalStudentsToSeat) {
            return SeatingPlanResult(
                false,
                "Capacity Shortage: Selected rooms hold $totalExamCapacity seats, but $totalStudentsToSeat students need to be seated. Please add more rooms."
            )
        }

        val allStaffList = dao.getAllStaff().first()

        // Clear existing allocation for this exam
        dao.clearSeatingsForExam(examScheduleId)

        val seatings = mutableListOf<ExamSeatingEntity>()
        var primaryIndex = 0
        var secondaryIndex = 0
        var staffIndex = 0

        for (room in roomsToUse) {
            val invigilator = if (allStaffList.isNotEmpty()) {
                allStaffList[staffIndex % allStaffList.size].also { staffIndex++ }
            } else null

            val desksPerRow = 4
            var currentDesk = 1

            for (desk in 1..room.examCapacity) {
                val row = ((desk - 1) / desksPerRow) + 1
                val col = ((desk - 1) % desksPerRow) + 1

                // Smart alternating strategy: Odd desks get primary students, Even desks get secondary students (if available)
                val shouldTakePrimary = if (secondaryStudents.isNotEmpty()) {
                    if (desk % 2 != 0) {
                        primaryIndex < primaryStudents.size || secondaryIndex >= secondaryStudents.size
                    } else {
                        secondaryIndex >= secondaryStudents.size
                    }
                } else {
                    primaryIndex < primaryStudents.size
                }

                if (shouldTakePrimary && primaryIndex < primaryStudents.size) {
                    val st = primaryStudents[primaryIndex++]
                    seatings.add(
                        ExamSeatingEntity(
                            examScheduleId = examScheduleId,
                            examTitle = exam.title,
                            examDate = exam.examDate,
                            session = exam.session,
                            roomId = room.id,
                            roomNumber = room.roomNumber,
                            deskNumber = currentDesk++,
                            rowNumber = row,
                            colNumber = col,
                            studentRegNo = st.regNo,
                            studentRollNo = st.rollNo,
                            studentName = st.name,
                            studentDept = st.department,
                            studentSemester = st.semester,
                            studentSection = st.section,
                            subjectCode = exam.subjectCode,
                            subjectName = exam.subjectName,
                            invigilatorStaffId = invigilator?.staffId ?: "",
                            invigilatorName = invigilator?.name ?: "Assigned Faculty"
                        )
                    )
                } else if (!shouldTakePrimary && secondaryIndex < secondaryStudents.size) {
                    val st = secondaryStudents[secondaryIndex++]
                    seatings.add(
                        ExamSeatingEntity(
                            examScheduleId = examScheduleId,
                            examTitle = exam.title,
                            examDate = exam.examDate,
                            session = exam.session,
                            roomId = room.id,
                            roomNumber = room.roomNumber,
                            deskNumber = currentDesk++,
                            rowNumber = row,
                            colNumber = col,
                            studentRegNo = st.regNo,
                            studentRollNo = st.rollNo,
                            studentName = st.name,
                            studentDept = st.department,
                            studentSemester = st.semester,
                            studentSection = st.section,
                            subjectCode = "MIXED",
                            subjectName = "Concurrent Exam",
                            invigilatorStaffId = invigilator?.staffId ?: "",
                            invigilatorName = invigilator?.name ?: "Assigned Faculty"
                        )
                    )
                } else if (primaryIndex < primaryStudents.size) {
                    val st = primaryStudents[primaryIndex++]
                    seatings.add(
                        ExamSeatingEntity(
                            examScheduleId = examScheduleId,
                            examTitle = exam.title,
                            examDate = exam.examDate,
                            session = exam.session,
                            roomId = room.id,
                            roomNumber = room.roomNumber,
                            deskNumber = currentDesk++,
                            rowNumber = row,
                            colNumber = col,
                            studentRegNo = st.regNo,
                            studentRollNo = st.rollNo,
                            studentName = st.name,
                            studentDept = st.department,
                            studentSemester = st.semester,
                            studentSection = st.section,
                            subjectCode = exam.subjectCode,
                            subjectName = exam.subjectName,
                            invigilatorStaffId = invigilator?.staffId ?: "",
                            invigilatorName = invigilator?.name ?: "Assigned Faculty"
                        )
                    )
                }
            }
        }

        dao.insertSeatingList(seatings)
        return SeatingPlanResult(
            success = true,
            message = "Successfully allocated ${seatings.size} students across ${roomsToUse.size} halls with zero capacity overflow and invigilator assignments!",
            allocatedCount = seatings.size,
            totalStudents = totalStudentsToSeat,
            roomsUsed = roomsToUse.size
        )
    }
}
