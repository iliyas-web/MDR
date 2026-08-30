package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ExamScheduleEntity
import com.example.data.local.entity.ExamSeatingEntity
import com.example.data.local.entity.RoomEntity
import com.example.data.local.entity.StaffEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TimetableSlotEntity
import com.example.data.model.CurrentUser
import com.example.data.model.Role
import com.example.data.repository.AcademiaRepository
import com.example.data.repository.ClashCheckResult
import com.example.data.repository.SeatingPlanResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TimetableFilterState(
    val department: String = "CSE",
    val year: Int = 3,
    val semester: Int = 5,
    val section: String = "A",
    val selectedStaffId: String = "MDR-FAC-101",
    val selectedDayOfWeek: Int = 1, // 1 = Mon .. 6 = Sat
    val isWeeklyView: Boolean = false,
    val isStaffMode: Boolean = false // false = Class View, true = Staff View
)

data class ExamPlannerUiState(
    val selectedExamId: Long? = 1L,
    val selectedRoomIds: Set<Long> = setOf(1L),
    val mixWithDept: String = "ECE",
    val mixWithSem: Int = 3,
    val isAlternateSeatingEnabled: Boolean = true,
    val studentSearchQuery: String = "",
    val isGenerating: Boolean = false,
    val lastResult: SeatingPlanResult? = null
)

data class ManagementUiState(
    val activeTab: Int = 0, // 0 = Staff, 1 = Students, 2 = Subjects, 3 = Rooms
    val searchQuery: String = "",
    val deptFilter: String = "ALL"
)

class AcademiaViewModel(
    private val repository: AcademiaRepository
) : ViewModel() {

    // Global Streams
    val allStaff: StateFlow<List<StaffEntity>> = repository.allStaff
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRooms: StateFlow<List<RoomEntity>> = repository.allRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTimetableSlots: StateFlow<List<TimetableSlotEntity>> = repository.allTimetableSlots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExams: StateFlow<List<ExamScheduleEntity>> = repository.allExamSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Counts
    val staffCount = repository.staffCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val studentCount = repository.studentCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val roomCount = repository.roomCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val examCount = repository.examCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Sub-Screen States
    private val _timetableFilter = MutableStateFlow(TimetableFilterState())
    val timetableFilter: StateFlow<TimetableFilterState> = _timetableFilter.asStateFlow()

    private val _examPlannerState = MutableStateFlow(ExamPlannerUiState())
    val examPlannerState: StateFlow<ExamPlannerUiState> = _examPlannerState.asStateFlow()

    private val _managementState = MutableStateFlow(ManagementUiState())
    val managementState: StateFlow<ManagementUiState> = _managementState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // ================= Timetable Filtering & Operations =================
    fun updateTimetableFilter(
        dept: String = _timetableFilter.value.department,
        year: Int = _timetableFilter.value.year,
        sem: Int = _timetableFilter.value.semester,
        sec: String = _timetableFilter.value.section,
        staffId: String = _timetableFilter.value.selectedStaffId,
        day: Int = _timetableFilter.value.selectedDayOfWeek,
        weekly: Boolean = _timetableFilter.value.isWeeklyView,
        staffMode: Boolean = _timetableFilter.value.isStaffMode
    ) {
        _timetableFilter.value = TimetableFilterState(
            department = dept,
            year = year,
            semester = sem,
            section = sec,
            selectedStaffId = staffId,
            selectedDayOfWeek = day,
            isWeeklyView = weekly,
            isStaffMode = staffMode
        )
    }

    // Filtered Timetable Slots for UI
    val currentFilteredSlots: StateFlow<List<TimetableSlotEntity>> = combine(
        allTimetableSlots,
        timetableFilter
    ) { slots, filter ->
        if (filter.isStaffMode) {
            val staffSlots = slots.filter { it.staffId == filter.selectedStaffId }
            if (filter.isWeeklyView) staffSlots else staffSlots.filter { it.dayOfWeek == filter.selectedDayOfWeek }
        } else {
            val classSlots = slots.filter {
                it.department.equals(filter.department, ignoreCase = true) &&
                it.semester == filter.semester &&
                it.section.equals(filter.section, ignoreCase = true)
            }
            if (filter.isWeeklyView) classSlots else classSlots.filter { it.dayOfWeek == filter.selectedDayOfWeek }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun saveTimetableSlotWithClashCheck(
        slot: TimetableSlotEntity,
        forceOverride: Boolean = false
    ): ClashCheckResult {
        if (!forceOverride) {
            val clash = repository.checkClash(slot)
            if (clash !is ClashCheckResult.NoClash) {
                return clash
            }
        }
        if (slot.id == 0L) {
            repository.saveTimetableSlot(slot)
            _snackbarEvent.emit("Timetable period scheduled successfully!")
        } else {
            repository.updateTimetableSlot(slot)
            _snackbarEvent.emit("Timetable period updated successfully!")
        }
        return ClashCheckResult.NoClash
    }

    fun deleteTimetableSlot(slotId: Long) {
        viewModelScope.launch {
            repository.deleteTimetableSlotById(slotId)
            _snackbarEvent.emit("Timetable slot removed.")
        }
    }

    // ================= Exam Planner & Seating =================
    fun selectExam(examId: Long) {
        _examPlannerState.value = _examPlannerState.value.copy(selectedExamId = examId)
    }

    fun toggleRoomSelection(roomId: Long) {
        val current = _examPlannerState.value.selectedRoomIds.toMutableSet()
        if (current.contains(roomId)) {
            if (current.size > 1) current.remove(roomId)
        } else {
            current.add(roomId)
        }
        _examPlannerState.value = _examPlannerState.value.copy(selectedRoomIds = current)
    }

    fun setAlternateSeating(enabled: Boolean, mixDept: String = "ECE", mixSem: Int = 3) {
        _examPlannerState.value = _examPlannerState.value.copy(
            isAlternateSeatingEnabled = enabled,
            mixWithDept = mixDept,
            mixWithSem = mixSem
        )
    }

    fun setStudentSearchQuery(query: String) {
        _examPlannerState.value = _examPlannerState.value.copy(studentSearchQuery = query)
    }

    fun getSeatingsForCurrentExam(examId: Long) = repository.getSeatingsForExam(examId)

    fun searchStudentSeating(query: String) = repository.searchStudentSeating(query)

    fun runAutoAllocation() {
        val state = _examPlannerState.value
        val examId = state.selectedExamId ?: return
        viewModelScope.launch {
            _examPlannerState.value = state.copy(isGenerating = true)
            val result = repository.autoAllocateExamSeats(
                examScheduleId = examId,
                selectedRoomIds = state.selectedRoomIds.toList(),
                mixDepartment = if (state.isAlternateSeatingEnabled) state.mixWithDept else null,
                mixSemester = if (state.isAlternateSeatingEnabled) state.mixWithSem else null
            )
            _examPlannerState.value = _examPlannerState.value.copy(
                isGenerating = false,
                lastResult = result
            )
            _snackbarEvent.emit(result.message)
        }
    }

    // ================= CRUD for Staff, Student, Subject, Room =================
    fun setManagementTab(tab: Int) {
        _managementState.value = _managementState.value.copy(activeTab = tab)
    }

    fun setManagementSearch(query: String) {
        _managementState.value = _managementState.value.copy(searchQuery = query)
    }

    fun setManagementDeptFilter(dept: String) {
        _managementState.value = _managementState.value.copy(deptFilter = dept)
    }

    fun saveStaff(staff: StaffEntity) {
        viewModelScope.launch {
            if (staff.id == 0L) {
                repository.saveStaff(staff)
                _snackbarEvent.emit("Staff member ${staff.name} added.")
            } else {
                repository.updateStaff(staff)
                _snackbarEvent.emit("Staff member ${staff.name} updated.")
            }
        }
    }

    fun deleteStaff(staff: StaffEntity) {
        viewModelScope.launch {
            repository.deleteStaff(staff)
            _snackbarEvent.emit("Staff ${staff.name} deleted.")
        }
    }

    fun saveStudent(student: StudentEntity) {
        viewModelScope.launch {
            if (student.id == 0L) {
                repository.saveStudent(student)
                _snackbarEvent.emit("Student ${student.name} enrolled.")
            } else {
                repository.updateStudent(student)
                _snackbarEvent.emit("Student ${student.name} updated.")
            }
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            _snackbarEvent.emit("Student ${student.name} deleted.")
        }
    }

    fun saveSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            if (subject.id == 0L) {
                repository.saveSubject(subject)
                _snackbarEvent.emit("Subject ${subject.code} added.")
            } else {
                repository.updateSubject(subject)
                _snackbarEvent.emit("Subject ${subject.code} updated.")
            }
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            _snackbarEvent.emit("Subject ${subject.code} deleted.")
        }
    }

    fun saveRoom(room: RoomEntity) {
        viewModelScope.launch {
            if (room.id == 0L) {
                repository.saveRoom(room)
                _snackbarEvent.emit("Room ${room.roomNumber} added.")
            } else {
                repository.updateRoom(room)
                _snackbarEvent.emit("Room ${room.roomNumber} updated.")
            }
        }
    }

    fun deleteRoom(room: RoomEntity) {
        viewModelScope.launch {
            repository.deleteRoom(room)
            _snackbarEvent.emit("Room ${room.roomNumber} deleted.")
        }
    }

    fun saveExam(exam: ExamScheduleEntity) {
        viewModelScope.launch {
            if (exam.id == 0L) {
                repository.saveExamSchedule(exam)
                _snackbarEvent.emit("Exam ${exam.subjectCode} scheduled.")
            } else {
                repository.updateExamSchedule(exam)
                _snackbarEvent.emit("Exam ${exam.subjectCode} updated.")
            }
        }
    }

    fun deleteExam(exam: ExamScheduleEntity) {
        viewModelScope.launch {
            repository.deleteExamSchedule(exam)
            _snackbarEvent.emit("Exam schedule removed.")
        }
    }

    // Helper to get day name
    fun getDayOfWeekInt(): Int {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_WEEK)
        // Calendar.MONDAY is 2, SUNDAY is 1
        return when (d) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> 1 // default to Monday for Sunday
        }
    }

    fun getFormattedTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
