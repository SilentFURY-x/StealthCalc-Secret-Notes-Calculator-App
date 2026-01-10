package com.fury.stealthcalc.presentation.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import net.objecthunter.exp4j.ExpressionBuilder // Using the math library we added
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    // State for what is currently typed
    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()

    // State for the calculation result (history or current answer)
    private val _result = MutableStateFlow("")
    val result = _result.asStateFlow()

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

        // 1. CHECK FOR SECRET CODE
        // CHECK FOR SECRET CODE)
        if (currentExpression == "69/67") {
            _input.value = "" // Clear screen
            viewModelScope.launch {
                _uiEvent.send(CalculatorUiEvent.ShowBiometricPrompt) // Trigger fingerprint, not nav
            }
            return
        }

        // 2. Perform Math
        try {
            val expression = ExpressionBuilder(currentExpression).build()
            val res = expression.evaluate()
            // If it's a whole number, remove the ".0"
            val longRes = res.toLong()
            if (res == longRes.toDouble()) {
                _result.value = longRes.toString()
            } else {
                _result.value = res.toString()
            }
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    fun onBiometricSuccess() {
        viewModelScope.launch {
            _uiEvent.send(CalculatorUiEvent.NavigateToVault)
        }
    }
}

// Define actions to keep code clean
sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
    object Decimal : CalculatorAction()
}