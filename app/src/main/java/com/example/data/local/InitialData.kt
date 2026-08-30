package com.example.data.local

import com.example.data.local.entity.ExamScheduleEntity
import com.example.data.local.entity.ExamSeatingEntity
import com.example.data.local.entity.RoomEntity
import com.example.data.local.entity.StaffEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TimetableSlotEntity

object InitialData {

    val sampleStaff = listOf(
        StaffEntity(
            staffId = "MDR-FAC-101",
            name = "Dr. R. Vignesh",
            email = "vignesh.r@mdrtech.edu",
            phone = "+91 98401 23456",
            department = "CSE",
            designation = "Professor & HOD",
            specialization = "Compiler Design, Distributed Systems",
            maxWeeklyHours = 14
        ),
        StaffEntity(
            staffId = "MDR-FAC-102",
            name = "Prof. Ananya Iyer",
            email = "ananya.i@mdrtech.edu",
            phone = "+91 98402 34567",
            department = "CSE",
            designation = "Assistant Professor",
            specialization = "Computer Networks, Cyber Security",
            maxWeeklyHours = 18
        ),
        StaffEntity(
            staffId = "MDR-FAC-103",
            name = "Dr. K. Ramesh",
            email = "ramesh.k@mdrtech.edu",
            phone = "+91 98403 45678",
            department = "IT",
            designation = "Associate Professor",
            specialization = "Cloud Computing, DevOps",
            maxWeeklyHours = 16
        ),
        StaffEntity(
            staffId = "MDR-FAC-104",
            name = "Prof. Priya Sundaram",
            email = "priya.s@mdrtech.edu",
            phone = "+91 98404 56789",
            department = "ECE",
            designation = "Assistant Professor",
            specialization = "VLSI & Digital Signal Processing",
            maxWeeklyHours = 18
        ),
        StaffEntity(
            staffId = "MDR-FAC-105",
            name = "Dr. S. Balaji",
            email = "balaji.s@mdrtech.edu",
            phone = "+91 98405 67890",
            department = "AI&DS",
            designation = "Professor",
            specialization = "Deep Learning, Natural Language Processing",
            maxWeeklyHours = 16
        ),
        StaffEntity(
            staffId = "MDR-FAC-106",
            name = "Dr. Meera Nair",
            email = "meera.n@mdrtech.edu",
            phone = "+91 98406 78901",
            department = "Maths",
            designation = "Associate Professor",
            specialization = "Discrete Mathematics & Graph Theory",
            maxWeeklyHours = 18
        )
    )

    val sampleRooms = listOf(
        RoomEntity(
            roomNumber = "LH-101",
            blockName = "Turing Tech Block",
            floor = 1,
            capacity = 60,
            examCapacity = 30,
            roomType = "Lecture Hall",
            hasProjector = true,
            isAirConditioned = true
        ),
        RoomEntity(
            roomNumber = "LH-102",
            blockName = "Turing Tech Block",
            floor = 1,
            capacity = 60,
            examCapacity = 30,
            roomType = "Lecture Hall",
            hasProjector = true,
            isAirConditioned = false
        ),
        RoomEntity(
            roomNumber = "LAB-201",
            blockName = "Ada Lovelace Block",
            floor = 2,
            capacity = 40,
            examCapacity = 20,
            roomType = "Computer Lab",
            hasProjector = true,
            isAirConditioned = true
        ),
        RoomEntity(
            roomNumber = "LAB-202",
            blockName = "Ada Lovelace Block",
            floor = 2,
            capacity = 40,
            examCapacity = 20,
            roomType = "Computer Lab",
            hasProjector = true,
            isAirConditioned = true
        ),
        RoomEntity(
            roomNumber = "AUDI-301",
            blockName = "Kalam Central Hall",
            floor = 3,
            capacity = 120,
            examCapacity = 60,
            roomType = "Seminar Hall",
            hasProjector = true,
            isAirConditioned = true
        )
    )

    val sampleSubjects = listOf(
        SubjectEntity(
            code = "CS3501",
            name = "Compiler Design",
            department = "CSE",
            semester = 5,
            credits = 4,
            type = "Theory",
            assignedStaffId = "MDR-FAC-101"
        ),
        SubjectEntity(
            code = "CS3591",
            name = "Computer Networks",
            department = "CSE",
            semester = 5,
            credits = 3,
            type = "Theory",
            assignedStaffId = "MDR-FAC-102"
        ),
        SubjectEntity(
            code = "CS3581",
            name = "Networks & Compiler Lab",
            department = "CSE",
            semester = 5,
            credits = 2,
            type = "Lab",
            assignedStaffId = "MDR-FAC-102"
        ),
        SubjectEntity(
            code = "IT3502",
            name = "Cloud Computing & DevOps",
            department = "IT",
            semester = 5,
            credits = 3,
            type = "Theory",
            assignedStaffId = "MDR-FAC-103"
        ),
        SubjectEntity(
            code = "EC3301",
            name = "Digital Electronics & VLSI",
            department = "ECE",
            semester = 3,
            credits = 4,
            type = "Theory",
            assignedStaffId = "MDR-FAC-104"
        ),
        SubjectEntity(
            code = "AD3101",
            name = "Foundations of AI & ML",
            department = "AI&DS",
            semester = 1,
            credits = 4,
            type = "Theory",
            assignedStaffId = "MDR-FAC-105"
        ),
        SubjectEntity(
            code = "MA3151",
            name = "Discrete Mathematics",
            department = "CSE",
            semester = 5,
            credits = 4,
            type = "Theory",
            assignedStaffId = "MDR-FAC-106"
        )
    )

    val sampleStudents = listOf(
        // CSE Year 3, Sem 5, Sec A
        StudentEntity(regNo = "711222104001", rollNo = "22CSE01", name = "Aarav Sharma", email = "aarav.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104002", rollNo = "22CSE02", name = "Bhavna Patel", email = "bhavna.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104003", rollNo = "22CSE03", name = "Chetan Verma", email = "chetan.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104004", rollNo = "22CSE04", name = "Divya Krishnan", email = "divya.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104005", rollNo = "22CSE05", name = "Eashan Gupta", email = "eashan.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104006", rollNo = "22CSE06", name = "Farhan Ali", email = "farhan.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104007", rollNo = "22CSE07", name = "Gayathri Ramesh", email = "gayathri.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104008", rollNo = "22CSE08", name = "Harish Kumar", email = "harish.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104009", rollNo = "22CSE09", name = "Ishaan Reddy", email = "ishaan.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222104010", rollNo = "22CSE10", name = "Janani Swaminathan", email = "janani.22cse@mdrtech.edu", department = "CSE", year = 3, semester = 5, section = "A"),

        // IT Year 3, Sem 5, Sec A
        StudentEntity(regNo = "711222205001", rollNo = "22IT01", name = "Karthik Raja", email = "karthik.22it@mdrtech.edu", department = "IT", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222205002", rollNo = "22IT02", name = "Lavanya Mohan", email = "lavanya.22it@mdrtech.edu", department = "IT", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222205003", rollNo = "22IT03", name = "Manish Raghav", email = "manish.22it@mdrtech.edu", department = "IT", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222205004", rollNo = "22IT04", name = "Nandhini Prakash", email = "nandhini.22it@mdrtech.edu", department = "IT", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222205005", rollNo = "22IT05", name = "Omkar Deshmukh", email = "omkar.22it@mdrtech.edu", department = "IT", year = 3, semester = 5, section = "A"),
        StudentEntity(regNo = "711222205006", rollNo = "22IT06", name = "Pooja Hegde", email = "pooja.22it@mdrtech.edu", department = "IT", year = 3, semester = 5, section = "A"),

        // ECE Year 2, Sem 3, Sec A
        StudentEntity(regNo = "711223106001", rollNo = "23ECE01", name = "Rahul Nambiar", email = "rahul.23ece@mdrtech.edu", department = "ECE", year = 2, semester = 3, section = "A"),
        StudentEntity(regNo = "711223106002", rollNo = "23ECE02", name = "Sanya Mirza", email = "sanya.23ece@mdrtech.edu", department = "ECE", year = 2, semester = 3, section = "A"),
        StudentEntity(regNo = "711223106003", rollNo = "23ECE03", name = "Tarun Vijay", email = "tarun.23ece@mdrtech.edu", department = "ECE", year = 2, semester = 3, section = "A"),
        StudentEntity(regNo = "711223106004", rollNo = "23ECE04", name = "Urvashi Sen", email = "urvashi.23ece@mdrtech.edu", department = "ECE", year = 2, semester = 3, section = "A"),

        // AI&DS Year 1, Sem 1, Sec A
        StudentEntity(regNo = "711224243001", rollNo = "24AIDS01", name = "Varun Kapoor", email = "varun.24aids@mdrtech.edu", department = "AI&DS", year = 1, semester = 1, section = "A"),
        StudentEntity(regNo = "711224243002", rollNo = "24AIDS02", name = "Yashasvi Jha", email = "yashasvi.24aids@mdrtech.edu", department = "AI&DS", year = 1, semester = 1, section = "A")
    )

    val sampleTimetableSlots = listOf(
        // Monday (dayOfWeek = 1) for CSE Sem 5 Sec A
        TimetableSlotEntity(dayOfWeek = 1, periodNumber = 1, startTime = "09:00 AM", endTime = "09:50 AM", subjectCode = "CS3501", subjectName = "Compiler Design", staffId = "MDR-FAC-101", staffName = "Dr. R. Vignesh", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 1, periodNumber = 2, startTime = "09:50 AM", endTime = "10:40 AM", subjectCode = "CS3591", subjectName = "Computer Networks", staffId = "MDR-FAC-102", staffName = "Prof. Ananya Iyer", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 1, periodNumber = 3, startTime = "11:00 AM", endTime = "11:50 AM", subjectCode = "MA3151", subjectName = "Discrete Mathematics", staffId = "MDR-FAC-106", staffName = "Dr. Meera Nair", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 1, periodNumber = 4, startTime = "11:50 AM", endTime = "12:40 PM", subjectCode = "IT3502", subjectName = "Cloud Computing & DevOps", staffId = "MDR-FAC-103", staffName = "Dr. K. Ramesh", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 1, periodNumber = 5, startTime = "01:30 PM", endTime = "03:10 PM", subjectCode = "CS3581", subjectName = "Networks & Compiler Lab", staffId = "MDR-FAC-102", staffName = "Prof. Ananya Iyer", roomId = 3, roomNumber = "LAB-201", department = "CSE", year = 3, semester = 5, section = "A"),

        // Tuesday (dayOfWeek = 2) for CSE Sem 5 Sec A
        TimetableSlotEntity(dayOfWeek = 2, periodNumber = 1, startTime = "09:00 AM", endTime = "09:50 AM", subjectCode = "CS3591", subjectName = "Computer Networks", staffId = "MDR-FAC-102", staffName = "Prof. Ananya Iyer", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 2, periodNumber = 2, startTime = "09:50 AM", endTime = "10:40 AM", subjectCode = "CS3501", subjectName = "Compiler Design", staffId = "MDR-FAC-101", staffName = "Dr. R. Vignesh", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 2, periodNumber = 3, startTime = "11:00 AM", endTime = "11:50 AM", subjectCode = "IT3502", subjectName = "Cloud Computing & DevOps", staffId = "MDR-FAC-103", staffName = "Dr. K. Ramesh", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 2, periodNumber = 4, startTime = "11:50 AM", endTime = "12:40 PM", subjectCode = "MA3151", subjectName = "Discrete Mathematics", staffId = "MDR-FAC-106", staffName = "Dr. Meera Nair", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),

        // Wednesday (dayOfWeek = 3) for CSE Sem 5 Sec A
        TimetableSlotEntity(dayOfWeek = 3, periodNumber = 1, startTime = "09:00 AM", endTime = "09:50 AM", subjectCode = "MA3151", subjectName = "Discrete Mathematics", staffId = "MDR-FAC-106", staffName = "Dr. Meera Nair", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 3, periodNumber = 2, startTime = "09:50 AM", endTime = "10:40 AM", subjectCode = "CS3501", subjectName = "Compiler Design", staffId = "MDR-FAC-101", staffName = "Dr. R. Vignesh", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 3, periodNumber = 3, startTime = "11:00 AM", endTime = "11:50 AM", subjectCode = "CS3591", subjectName = "Computer Networks", staffId = "MDR-FAC-102", staffName = "Prof. Ananya Iyer", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),

        // Thursday (dayOfWeek = 4) for CSE Sem 5 Sec A
        TimetableSlotEntity(dayOfWeek = 4, periodNumber = 1, startTime = "09:00 AM", endTime = "09:50 AM", subjectCode = "IT3502", subjectName = "Cloud Computing & DevOps", staffId = "MDR-FAC-103", staffName = "Dr. K. Ramesh", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 4, periodNumber = 2, startTime = "09:50 AM", endTime = "10:40 AM", subjectCode = "CS3501", subjectName = "Compiler Design", staffId = "MDR-FAC-101", staffName = "Dr. R. Vignesh", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),

        // Friday (dayOfWeek = 5) for CSE Sem 5 Sec A
        TimetableSlotEntity(dayOfWeek = 5, periodNumber = 1, startTime = "09:00 AM", endTime = "09:50 AM", subjectCode = "CS3591", subjectName = "Computer Networks", staffId = "MDR-FAC-102", staffName = "Prof. Ananya Iyer", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),
        TimetableSlotEntity(dayOfWeek = 5, periodNumber = 2, startTime = "09:50 AM", endTime = "10:40 AM", subjectCode = "MA3151", subjectName = "Discrete Mathematics", staffId = "MDR-FAC-106", staffName = "Dr. Meera Nair", roomId = 1, roomNumber = "LH-101", department = "CSE", year = 3, semester = 5, section = "A"),

        // ECE & IT Slots
        TimetableSlotEntity(dayOfWeek = 1, periodNumber = 1, startTime = "09:00 AM", endTime = "09:50 AM", subjectCode = "EC3301", subjectName = "Digital Electronics & VLSI", staffId = "MDR-FAC-104", staffName = "Prof. Priya Sundaram", roomId = 2, roomNumber = "LH-102", department = "ECE", year = 2, semester = 3, section = "A"),
        TimetableSlotEntity(dayOfWeek = 1, periodNumber = 2, startTime = "09:50 AM", endTime = "10:40 AM", subjectCode = "AD3101", subjectName = "Foundations of AI & ML", staffId = "MDR-FAC-105", staffName = "Dr. S. Balaji", roomId = 2, roomNumber = "LH-102", department = "AI&DS", year = 1, semester = 1, section = "A")
    )

    val sampleExams = listOf(
        ExamScheduleEntity(
            id = 1,
            title = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            startTime = "09:30 AM",
            endTime = "12:30 PM",
            subjectCode = "CS3501",
            subjectName = "Compiler Design",
            department = "CSE",
            year = 3,
            semester = 5
        ),
        ExamScheduleEntity(
            id = 2,
            title = "End Semester Examination - Nov 2026",
            examDate = "2026-11-18",
            session = "FN",
            startTime = "09:30 AM",
            endTime = "12:30 PM",
            subjectCode = "CS3591",
            subjectName = "Computer Networks",
            department = "CSE",
            year = 3,
            semester = 5
        ),
        ExamScheduleEntity(
            id = 3,
            title = "End Semester Examination - Nov 2026",
            examDate = "2026-11-20",
            session = "FN",
            startTime = "09:30 AM",
            endTime = "12:30 PM",
            subjectCode = "IT3502",
            subjectName = "Cloud Computing & DevOps",
            department = "IT",
            year = 3,
            semester = 5
        ),
        ExamScheduleEntity(
            id = 4,
            title = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            startTime = "09:30 AM",
            endTime = "12:30 PM",
            subjectCode = "EC3301",
            subjectName = "Digital Electronics & VLSI",
            department = "ECE",
            year = 2,
            semester = 3
        )
    )

    // Pre-allocated Seating for Exam 1 (CS3501) with smart alternate allocation across CSE & ECE in LH-101
    val sampleSeatings = listOf(
        ExamSeatingEntity(
            examScheduleId = 1,
            examTitle = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            roomId = 1,
            roomNumber = "LH-101",
            deskNumber = 1,
            rowNumber = 1,
            colNumber = 1,
            studentRegNo = "711222104001",
            studentRollNo = "22CSE01",
            studentName = "Aarav Sharma",
            studentDept = "CSE",
            studentSemester = 5,
            studentSection = "A",
            subjectCode = "CS3501",
            subjectName = "Compiler Design",
            invigilatorStaffId = "MDR-FAC-104",
            invigilatorName = "Prof. Priya Sundaram"
        ),
        ExamSeatingEntity(
            examScheduleId = 1,
            examTitle = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            roomId = 1,
            roomNumber = "LH-101",
            deskNumber = 2,
            rowNumber = 1,
            colNumber = 2,
            studentRegNo = "711223106001",
            studentRollNo = "23ECE01",
            studentName = "Rahul Nambiar",
            studentDept = "ECE",
            studentSemester = 3,
            studentSection = "A",
            subjectCode = "EC3301",
            subjectName = "Digital Electronics & VLSI",
            invigilatorStaffId = "MDR-FAC-104",
            invigilatorName = "Prof. Priya Sundaram"
        ),
        ExamSeatingEntity(
            examScheduleId = 1,
            examTitle = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            roomId = 1,
            roomNumber = "LH-101",
            deskNumber = 3,
            rowNumber = 1,
            colNumber = 3,
            studentRegNo = "711222104002",
            studentRollNo = "22CSE02",
            studentName = "Bhavna Patel",
            studentDept = "CSE",
            studentSemester = 5,
            studentSection = "A",
            subjectCode = "CS3501",
            subjectName = "Compiler Design",
            invigilatorStaffId = "MDR-FAC-104",
            invigilatorName = "Prof. Priya Sundaram"
        ),
        ExamSeatingEntity(
            examScheduleId = 1,
            examTitle = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            roomId = 1,
            roomNumber = "LH-101",
            deskNumber = 4,
            rowNumber = 1,
            colNumber = 4,
            studentRegNo = "711223106002",
            studentRollNo = "23ECE02",
            studentName = "Sanya Mirza",
            studentDept = "ECE",
            studentSemester = 3,
            studentSection = "A",
            subjectCode = "EC3301",
            subjectName = "Digital Electronics & VLSI",
            invigilatorStaffId = "MDR-FAC-104",
            invigilatorName = "Prof. Priya Sundaram"
        ),
        ExamSeatingEntity(
            examScheduleId = 1,
            examTitle = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            roomId = 1,
            roomNumber = "LH-101",
            deskNumber = 5,
            rowNumber = 2,
            colNumber = 1,
            studentRegNo = "711222104003",
            studentRollNo = "22CSE03",
            studentName = "Chetan Verma",
            studentDept = "CSE",
            studentSemester = 5,
            studentSection = "A",
            subjectCode = "CS3501",
            subjectName = "Compiler Design",
            invigilatorStaffId = "MDR-FAC-104",
            invigilatorName = "Prof. Priya Sundaram"
        ),
        ExamSeatingEntity(
            examScheduleId = 1,
            examTitle = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            roomId = 1,
            roomNumber = "LH-101",
            deskNumber = 6,
            rowNumber = 2,
            colNumber = 2,
            studentRegNo = "711223106003",
            studentRollNo = "23ECE03",
            studentName = "Tarun Vijay",
            studentDept = "ECE",
            studentSemester = 3,
            studentSection = "A",
            subjectCode = "EC3301",
            subjectName = "Digital Electronics & VLSI",
            invigilatorStaffId = "MDR-FAC-104",
            invigilatorName = "Prof. Priya Sundaram"
        ),
        ExamSeatingEntity(
            examScheduleId = 1,
            examTitle = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            roomId = 1,
            roomNumber = "LH-101",
            deskNumber = 7,
            rowNumber = 2,
            colNumber = 3,
            studentRegNo = "711222104004",
            studentRollNo = "22CSE04",
            studentName = "Divya Krishnan",
            studentDept = "CSE",
            studentSemester = 5,
            studentSection = "A",
            subjectCode = "CS3501",
            subjectName = "Compiler Design",
            invigilatorStaffId = "MDR-FAC-104",
            invigilatorName = "Prof. Priya Sundaram"
        ),
        ExamSeatingEntity(
            examScheduleId = 1,
            examTitle = "End Semester Examination - Nov 2026",
            examDate = "2026-11-16",
            session = "FN",
            roomId = 1,
            roomNumber = "LH-101",
            deskNumber = 8,
            rowNumber = 2,
            colNumber = 4,
            studentRegNo = "711223106004",
            studentRollNo = "23ECE04",
            studentName = "Urvashi Sen",
            studentDept = "ECE",
            studentSemester = 3,
            studentSection = "A",
            subjectCode = "EC3301",
            subjectName = "Digital Electronics & VLSI",
            invigilatorStaffId = "MDR-FAC-104",
            invigilatorName = "Prof. Priya Sundaram"
        )
    )
}
