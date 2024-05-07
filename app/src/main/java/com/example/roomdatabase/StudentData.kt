package com.example.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Student_Table")
data class StudentData(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo(name = "First Name")
    val fname:String,
    @ColumnInfo(name = "Last Name")
    val lname:String,
    @ColumnInfo(name = "ID Number")
    val roll:Int
)
