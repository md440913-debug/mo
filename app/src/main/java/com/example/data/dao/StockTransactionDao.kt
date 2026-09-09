package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.StockTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockTransactionDao {
    @Query("SELECT * FROM stock_transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<StockTransactionEntity>>

    @Query("SELECT * FROM stock_transactions WHERE cutNumber = :cutNumber ORDER BY id DESC")
    fun getTransactionsForCut(cutNumber: String): Flow<List<StockTransactionEntity>>

    @Query("SELECT * FROM stock_transactions WHERE transactionType = :type ORDER BY id DESC")
    fun getTransactionsByType(type: String): Flow<List<StockTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: StockTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<StockTransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: StockTransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: StockTransactionEntity)

    @Query("DELETE FROM stock_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("SELECT COALESCE(SUM(quantityPieces), 0) FROM stock_transactions WHERE cutNumber = :cutNumber AND transactionType = 'IN'")
    suspend fun getTotalInPiecesForCut(cutNumber: String): Int

    @Query("SELECT COALESCE(SUM(quantityPieces), 0) FROM stock_transactions WHERE cutNumber = :cutNumber AND transactionType = 'OUT'")
    suspend fun getTotalOutPiecesForCut(cutNumber: String): Int

    @Query("SELECT COALESCE(SUM(quantityPieces), 0) FROM stock_transactions WHERE transactionType = 'IN'")
    suspend fun getTotalInPiecesAll(): Int

    @Query("SELECT COALESCE(SUM(quantityPieces), 0) FROM stock_transactions WHERE transactionType = 'OUT'")
    suspend fun getTotalOutPiecesAll(): Int

    @Query("SELECT COUNT(*) FROM stock_transactions")
    suspend fun getTransactionsCount(): Int
}
