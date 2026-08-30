package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AcademiaDao
import com.example.data.local.entity.ExamScheduleEntity
import com.example.data.local.entity.ExamSeatingEntity
import com.example.data.local.entity.RoomEntity
import com.example.data.local.entity.StaffEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.local.entity.TimetableSlotEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StaffEntity::class,
        StudentEntity::class,
        SubjectEntity::class,
        RoomEntity::class,
        TimetableSlotEntity::class,
        ExamScheduleEntity::class,
        ExamSeatingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AcademiaDatabase : RoomDatabase() {

    abstract fun academiaDao(): AcademiaDao

    companion object {
        @Volatile
        private var INSTANCE: AcademiaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AcademiaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcademiaDatabase::class.java,
                    "mdr_academia_database.db"
                )
                    .addCallback(AcademiaDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AcademiaDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.academiaDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: AcademiaDao) {
                dao.insertStaffList(InitialData.sampleStaff)
                dao.insertRoomList(InitialData.sampleRooms)
                dao.insertSubjectList(InitialData.sampleSubjects)
                dao.insertStudentList(InitialData.sampleStudents)
                dao.insertTimetableSlotList(InitialData.sampleTimetableSlots)
                dao.insertExamScheduleList(InitialData.sampleExams)
                dao.insertSeatingList(InitialData.sampleSeatings)
            }
        }
    }
}
