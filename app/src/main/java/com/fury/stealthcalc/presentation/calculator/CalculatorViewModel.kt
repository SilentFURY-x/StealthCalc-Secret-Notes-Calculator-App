package com.fury.stealthcalc.presentation.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()

    private val _result = MutableStateFlow("")
    val result = _result.asStateFlow()

    // History State
    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history = _history.asStateFlow()

    private val _uiEvent = Channel<CalculatorUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> _input.update { it + action.number }
            is CalculatorAction.Operation -> _input.update { it + action.operation }
            is CalculatorAction.Clear -> {
                _input.value = ""
                _result.value = ""
            }
            is CalculatorAction.Delete -> {
                // Handle DEL (Backspace)
                if (_input.value.isNotEmpty()) {
                    _input.update { it.dropLast(1) }
                }
            }
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Decimal -> _input.update { it + "." }
        }
    }

    private fun performCalculation() {
        val currentExpression = _input.value
        if (currentExpression.isBlank()) return

        // CHECK FOR SECRET CODE (67/67 or 69/67)
        if (currentExpression == "69/67") {
            _input.value = ""
            viewModelScope.launch {
                // Trigger Biometric Prompt
                _uiEvent.send(CalculatorUiEvent.ShowBiometricPrompt)
            }
            return
        }

        try {
            val expression = ExpressionBuilder(currentExpression).build()
            val res = expression.evaluate()
            val longRes = res.toLong()

            val finalResult = if (res == longRes.toDouble()) {
                longRes.toString()
            } else {
                res.toString()
            }

            _result.value = finalResult

            // Add to History (Only valid calculations)
            val historyEntry = "$currentExpression = $finalResult"
            _history.update { list ->
                (list + historyEntry).takeLast(10) // Keep last 10
            }

        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    // The function Navigation.kt calls on success
    fun onBiometricSuccess() {
        viewModelScope.launch {
            _uiEvent.send(CalculatorUiEvent.NavigateToVault)
        }
    }

    fun clearInput() {
        _input.value = ""
        _result.value = ""
    }
}

// Ensure this sealed class is at the bottom
sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
    object Decimal : CalculatorAction()
}