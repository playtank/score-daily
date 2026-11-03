package com.playtank.scoredaily.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a single score change event.
 *
 * @param id          Auto-generated primary key.
 * @param timestamp   Unix epoch millis at the moment the record was created.
 * @param dateString  "yyyy-MM-dd" representation of the local date — used for
 *                    efficient per-day querying without date arithmetic in SQL.
 * @param scoreDelta  The signed change in score (e.g. +5 or -5).
 * @param reason      Optional free-text description of why the score changed.
 */
@Entity(
    tableName = "score_records",
    indices = [Index(value = ["dateString"])]
)
data class ScoreRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val dateString: String,
    val scoreDelta: Int,
    val reason: String? = null
)
