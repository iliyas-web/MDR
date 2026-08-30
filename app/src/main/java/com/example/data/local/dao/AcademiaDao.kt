package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ExamScheduleEntity
import com.example.data.local.entity.ExamSeatingEntity
import com.example.data.local.entity.RoomEntity
import com.example.data.local.entity.StaffEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TimetableSlotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademiaDao {

    // ================= Staff Queries =================
    @Query("SELECT * FROM staff ORDER BY name ASC")
    fun getAllStaff(): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff WHERE department = :dept ORDER BY name ASC")
    fun getStaffByDepartment(dept: String): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff WHERE staffId = :staffId LIMIT 1")
    suspend fun getStaffById(staffId: String): StaffEntity?

    @Query("SELECT * FROM staff WHERE staffId = :staffId LIMIT 1")
    fun getStaffByIdFlow(staffId: String): Flow<StaffEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffList(staffList: List<StaffEntity>)

    @Update
    suspend fun updateStaff(staff: StaffEntity)

    @Delete
    suspend fun deleteStaff(staff: StaffEntity)

    @Query("SELECT COUNT(*) FROM staff")
    fun getStaffCount(): Flow<Int>

    // ================= Student Queries =================
    @Query("SELECT * FROM students ORDER BY regNo ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE department = :dept AND semester = :semester AND section = :section ORDER BY rollNo ASC")
    fun getStudentsByClass(dept: String, semester: Int, section: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE department = :dept AND semester = :semester ORDER BY rollNo ASC")
    suspend fun getStudentsForExam(dept: String, semester: Int): List<StudentEntity>

    @Query("SELECT * FROM students WHERE regNo = :regNo OR rollNo = :regNo LIMIT 1")
    suspend fun getStudentByRegOrRoll(regNo: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentList(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    // ================= Subject Queries =================
    @Query("SELECT * FROM subjects ORDER BY code ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE department = :dept AND semester = :semester ORDER BY code ASC")
    fun getSubjectsByDeptAndSem(dept: String, semester: Int): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjectList(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("SELECT COUNT(*) FROM subjects")
    fun getSubjectCount(): Flow<Int>

    // ================= Room Queries =================
    @Query("SELECT * FROM rooms ORDER BY roomNumber ASC")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms WHERE id = :roomId LIMIT 1")
    suspend fun getRoomById(roomId: Long): RoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomList(rooms: List<RoomEntity>)

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)

    @Query("SELECT COUNT(*) FROM rooms")
    fun getRoomCount(): Flow<Int>

    // ================= Timetable Queries =================
    @Query("SELECT * FROM timetable_slots ORDER BY dayOfWeek, periodNumber ASC")
    fun getAllTimetableSlots(): Flow<List<TimetableSlotEntity>>

    @Query("SELECT * FROM timetable_slots WHERE staffId = :staffId ORDER BY dayOfWeek, periodNumber ASC")
    fun getTimetableForStaff(staffId: String): Flow<List<TimetableSlotEntity>>

    @Query("SELECT * FROM timetable_slots WHERE staffId = :staffId AND dayOfWeek = :dayOfWeek ORDER BY periodNumber ASC")
    fun getStaffTimetableForDay(staffId: String, dayOfWeek: Int): Flow<List<TimetableSlotEntity>>

    @Query("SELECT * FROM timetable_slots WHERE department = :dept AND semester = :semester AND section = :section ORDER BY dayOfWeek, periodNumber ASC")
    fun getTimetableForClass(dept: String, semester: Int, section: String): Flow<List<TimetableSlotEntity>>

    @Query("SELECT * FROM timetable_slots WHERE department = :dept AND semester = :semester AND section = :section AND dayOfWeek = :dayOfWeek ORDER BY periodNumber ASC")
    fun getClassTimetableForDay(dept: String, semester: Int, section: String, dayOfWeek: Int): Flow<List<TimetableSlotEntity>>

    @Query("SELECT * FROM timetable_slots WHERE dayOfWeek = :dayOfWeek ORDER BY periodNumber ASC")
    fun getTodayTimetableSlots(dayOfWeek: Int): Flow<List<TimetableSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableSlot(slot: TimetableSlotEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableSlotList(slots: List<TimetableSlotEntity>)

    @Update
    suspend fun updateTimetableSlot(slot: TimetableSlotEntity)

    @Delete
    suspend fun deleteTimetableSlot(slot: TimetableSlotEntity)

    @Query("DELETE FROM timetable_slots WHERE id = :slotId")
    suspend fun deleteTimetableSlotById(slotId: Long)

    // Conflict Check Queries for Timetable
    @Query("SELECT * FROM timetable_slots WHERE dayOfWeek = :dayOfWeek AND periodNumber = :period AND staffId = :staffId AND id != :excludeId LIMIT 1")
    suspend fun findStaffConflict(dayOfWeek: Int, period: Int, staffId: String, excludeId: Long = -1): TimetableSlotEntity?

    @Query("SELECT * FROM timetable_slots WHERE dayOfWeek = :dayOfWeek AND periodNumber = :period AND roomId = :roomId AND id != :excludeId LIMIT 1")
    suspend fun findRoomConflict(dayOfWeek: Int, period: Int, roomId: Long, excludeId: Long = -1): TimetableSlotEntity?

    @Query("SELECT * FROM timetable_slots WHERE dayOfWeek = :dayOfWeek AND periodNumber = :period AND department = :dept AND semester = :semester AND section = :section AND id != :excludeId LIMIT 1")
    suspend fun findClassConflict(dayOfWeek: Int, period: Int, dept: String, semester: Int, section: String, excludeId: Long = -1): TimetableSlotEntity?

    // ================= Exam Schedule Queries =================
    @Query("SELECT * FROM exam_schedules ORDER BY examDate ASC, session ASC")
    fun getAllExamSchedules(): Flow<List<ExamScheduleEntity>>

    @Query("SELECT * FROM exam_schedules WHERE department = :dept AND semester = :semester ORDER BY examDate ASC")
    fun getExamsForClass(dept: String, semester: Int): Flow<List<ExamScheduleEntity>>

    @Query("SELECT * FROM exam_schedules WHERE examDate >= :todayDate ORDER BY examDate ASC LIMIT 5")
    fun getUpcomingExams(todayDate: String): Flow<List<ExamScheduleEntity>>

    @Query("SELECT * FROM exam_schedules WHERE id = :id LIMIT 1")
    suspend fun getExamScheduleById(id: Long): ExamScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamSchedule(exam: ExamScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamScheduleList(exams: List<ExamScheduleEntity>)

    @Update
    suspend fun updateExamSchedule(exam: ExamScheduleEntity)

    @Delete
    suspend fun deleteExamSchedule(exam: ExamScheduleEntity)

    @Query("DELETE FROM exam_schedules WHERE id = :examId")
    suspend fun deleteExamScheduleById(examId: Long)

    @Query("SELECT COUNT(*) FROM exam_schedules")
    fun getExamCount(): Flow<Int>

    // ================= Exam Seating Queries =================
    @Query("SELECT * FROM exam_seatings WHERE examScheduleId = :scheduleId ORDER BY roomNumber ASC, deskNumber ASC")
    fun getSeatingsForExam(scheduleId: Long): Flow<List<ExamSeatingEntity>>

    @Query("SELECT * FROM exam_seatings WHERE studentRegNo = :regNo OR studentRollNo = :regNo ORDER BY examDate ASC")
    fun getSeatingsForStudent(regNo: String): Flow<List<ExamSeatingEntity>>

    @Query("SELECT * FROM exam_seatings WHERE (studentRegNo = :query OR studentRollNo = :query OR studentName LIKE '%' || :query || '%') ORDER BY examDate ASC")
    fun searchStudentSeating(query: String): Flow<List<ExamSeatingEntity>>

    @Query("SELECT * FROM exam_seatings WHERE examScheduleId = :scheduleId AND roomId = :roomId ORDER BY deskNumber ASC")
    fun getRoomSeatingsForExam(scheduleId: Long, roomId: Long): Flow<List<ExamSeatingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeatingList(seatings: List<ExamSeatingEntity>)

    @Query("DELETE FROM exam_seatings WHERE examScheduleId = :scheduleId")
    suspend fun clearSeatingsForExam(scheduleId: Long)

    @Query("SELECT COUNT(*) FROM exam_seatings WHERE examScheduleId = :scheduleId")
    fun getSeatingCountForExam(scheduleId: Long): Flow<Int>
}
