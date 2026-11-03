package com.playtank.scoredaily.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.playtank.scoredaily.data.ScoreDatabase
import com.playtank.scoredaily.data.ScoreRecordEntity
import com.playtank.scoredaily.data.ScoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class ScoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScoreRepository by lazy {
        val db = ScoreDatabase.getInstance(application)
        ScoreRepository(db.scoreDao())
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // ── Selected date ─────────────────────────────────────────────────────────

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun goToPreviousDay() { _selectedDate.value = _selectedDate.value.minusDays(1) }
    fun goToNextDay()     { _selectedDate.value = _selectedDate.value.plusDays(1) }
    fun goToToday()       { _selectedDate.value = LocalDate.now() }
    fun selectDate(date: LocalDate) { _selectedDate.value = date }

    // flatMapLatest re-subscribes the downstream flows whenever the date changes.

    // ── Records for selected day ──────────────────────────────────────────────

    val records = _selectedDate
        .flatMapLatest { date ->
            repository.getRecordsForDate(date.format(dateFormatter))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ── Daily subtotal ────────────────────────────────────────────────────────

    val dailyTotal: StateFlow<Int> = _selectedDate
        .flatMapLatest { date ->
            repository.getDailyTotal(date.format(dateFormatter))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    // ── Cumulative total (all history up to and including selected date) ───────

    val cumulativeTotal: StateFlow<Int> = _selectedDate
        .flatMapLatest { date ->
            repository.getCumulativeTotal(date.format(dateFormatter))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    // ── Mutations ─────────────────────────────────────────────────────────────

    /**
     * Adds a score record for the currently selected date.
     *
     * @param delta  Signed score change (positive or negative).
     * @param reason Optional free-text reason.
     */
    fun addScore(delta: Int, reason: String?) {
        val date = _selectedDate.value
        val record = ScoreRecordEntity(
            timestamp = System.currentTimeMillis(),
            dateString = date.format(dateFormatter),
            scoreDelta = delta,
            reason = reason?.trim()?.ifBlank { null }
        )
        viewModelScope.launch { repository.addRecord(record) }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch { repository.deleteRecord(id) }
    }
}
