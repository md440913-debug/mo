package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AuditDao
import com.example.data.dao.CutDao
import com.example.data.dao.StockTransactionDao
import com.example.data.entity.AuditEntity
import com.example.data.entity.CutEntity
import com.example.data.entity.StockTransactionEntity

@Database(
    entities = [CutEntity::class, StockTransactionEntity::class, AuditEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cutDao(): CutDao
    abstract fun stockTransactionDao(): StockTransactionDao
    abstract fun auditDao(): AuditDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qassat_warehouse_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
