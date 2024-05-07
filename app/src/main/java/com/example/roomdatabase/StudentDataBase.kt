package com.example.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.concurrent.Volatile

@Database(entities = [StudentData::class], version = 1)
abstract class StudentDataBase:RoomDatabase() {
    abstract fun studentDao():StudentDao

    companion object{
        @Volatile
        private var INSTANCE : StudentDataBase?= null

        fun getDatabase(context: Context):StudentDataBase{
            val tempIntanse = INSTANCE
            if (tempIntanse != null){
                return tempIntanse
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDataBase::class.java,
                    name = "Student_Database"
                ).build()
                INSTANCE = instance
                return  instance
            }
        }
    }
}