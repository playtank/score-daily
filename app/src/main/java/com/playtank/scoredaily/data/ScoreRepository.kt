package com.playtank.scoredaily.data

import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for score data.
 *
 * Separating the repository from the DAO means the ViewModel never touches Room
 * directly and a test double can be dropped in without touching the database.
 */
class ScoreRepository(private val dao: ScoreDao) {

    fun getRecordsForDate(dateString: String): Flow<List<ScoreRecordEntity>> =
        dao.getRecordsForDate(dateString)

    fun getDailyTotal(dateString: String): Flow<Int> =
        dao.getDailyTotal(dateString)

    fun getCumulativeTotal(upToDateString: String): Flow<Int> =
        dao.getCumulativeTotal(upToDateString)

    suspend fun addRecord(record: ScoreRecordEntity) = dao.insert(record)

    suspend fun deleteRecord(id: Long) = dao.deleteById(id)
}
