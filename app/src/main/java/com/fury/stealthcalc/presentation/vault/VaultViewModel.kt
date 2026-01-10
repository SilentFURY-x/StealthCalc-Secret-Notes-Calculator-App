package com.fury.stealthcalc.presentation.vault

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.stealthcalc.data.Note
import com.fury.stealthcalc.data.NoteDao
import com.fury.stealthcalc.ui.theme.PrimaryOrange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val dao: NoteDao
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes = _notes.asStateFlow()

    init {
        getNotes()
    }

    private fun getNotes() {
        viewModelScope.launch {
            dao.getNotes().collectLatest {
                _notes.value = it
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            dao.deleteNote(note)
        }
    }

    // Temporary helper to add dummy data for testing UI
    fun addSampleNote() {
        viewModelScope.launch {
            dao.insertNote(
                Note(
                    title = "Secret Plan #${(1..100).random()}",
                    content = "This is a classified document.",
                    color = PrimaryOrange.toArgb()
                )
            )
        }
    }
}