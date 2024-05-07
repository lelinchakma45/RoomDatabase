package com.example.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(studentData: StudentData)

    @Update
    suspend fun updateData(studentData: StudentData)

    @Delete
    suspend fun delete(studentData: StudentData)

    @Query("Select * From Student_Table ORDER BY id ASC")
    suspend fun getAllData():List<StudentData>

//    @Query("Select * From student_table where roll_num = :roll")
//    suspend fun findById(roll:Int):StudentData

    @Query("DELETE FROM Student_Table WHERE id = :id")
    suspend fun deleteOne(id: Int)


    @Query("Delete From Student_Table")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM Student_Table")
    suspend fun getCount(): Int

    @Transaction
    suspend fun isEmpty(): Boolean {
        val count = getCount()
        return count == 0
    }
}