package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CutDao {
    @Query("SELECT * FROM cuts ORDER BY id DESC")
    fun getAllCuts(): Flow<List<CutEntity>>

    @Query("SELECT * FROM cuts WHERE cutNumber = :cutNumber LIMIT 1")
    suspend fun getCutByNumber(cutNumber: String): CutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCut(cut: CutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuts(cuts: List<CutEntity>)

    @Update
    suspend fun updateCut(cut: CutEntity)

    @Delete
    suspend fun deleteCut(cut: CutEntity)

    @Query("DELETE FROM cuts WHERE id = :id")
    suspend fun deleteCutById(id: Long)

    @Query("SELECT COUNT(*) FROM cuts")
    suspend fun getCutsCount(): Int
}
