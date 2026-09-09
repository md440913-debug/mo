package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.AuditEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditDao {
    @Query("SELECT * FROM inventory_audits ORDER BY id DESC")
    fun getAllAudits(): Flow<List<AuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: AuditEntity): Long

    @Update
    suspend fun updateAudit(audit: AuditEntity)

    @Delete
    suspend fun deleteAudit(audit: AuditEntity)

    @Query("DELETE FROM inventory_audits WHERE id = :id")
    suspend fun deleteAuditById(id: Long)
}
