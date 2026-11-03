package com.playtank.scoredaily.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {

    // ── Writes ────────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ScoreRecordEntity)

    @Query("DELETE FROM score_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    // ── Reads for a specific day ──────────────────────────────────────────────

    /** All records for [dateString], ordered newest-first for display. */
    @Query("SELECT * FROM score_records WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getRecordsForDate(dateString: String): Flow<List<ScoreRecordEntity>>

    /** Sum of scoreDelta for [dateString]; returns null when there are no rows. */
    @Query("SELECT COALESCE(SUM(scoreDelta), 0) FROM score_records WHERE dateString = :dateString")
    fun getDailyTotal(dateString: String): Flow<Int>

    // ── Cumulative total ──────────────────────────────────────────────────────

    /**
     * Sum of all scoreDelta values for dates <= [upToDateString].
     * This is the running lifetime total as of (and including) the given day.
     */
    @Query("SELECT COALESCE(SUM(scoreDelta), 0) FROM score_records WHERE dateString <= :upToDateString")
    fun getCumulativeTotal(upToDateString: String): Flow<Int>
}
