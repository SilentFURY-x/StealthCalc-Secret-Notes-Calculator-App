package com.fury.stealthcalc.presentation.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.stealthcalc.ui.theme.*
import androidx.compose.ui.graphics.Brush

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel(),
    onNavigateToVault: () -> Unit // Callback for later
) {
    val input by viewModel.input.collectAsState()
    val result by viewModel.result.collectAsState()

    val cyberGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027), // Deep Blue/Black
            Color(0xFF203A43),
            Color(0xFF2C5364)  // Lighter Teal/Grey
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cyberGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // 1. History / Result Display
        Text(
            text = result,
            color = HistoryText,
            fontSize = 40.sp,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Light
        )

        // 2. Main Input Display
        Text(
            text = input.ifEmpty { "0" },
            color = TextWhite,
            fontSize = 64.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )

        // 3. Keypad Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorButton(symbol = "C", Modifier.weight(1f).aspectRatio(1f), AccentGray, TextBlack) { viewModel.onAction(CalculatorAction.Clear) }
            CalculatorButton(symbol = "(", Modifier.weight(1f).aspectRatio(1f), AccentGray, TextBlack) { viewModel.onAction(CalculatorAction.Operation("(")) }
            CalculatorButton(symbol = ")", Modifier.weight(1f).aspectRatio(1f), AccentGray, TextBlack) { viewModel.onAction(CalculatorAction.Operation(")")) }
            CalculatorButton(symbol = "/", Modifier.weight(1f).aspectRatio(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Operation("/")) }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorButton(symbol = "7", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("7")) }
            CalculatorButton(symbol = "8", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("8")) }
            CalculatorButton(symbol = "9", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("9")) }
            CalculatorButton(symbol = "*", Modifier.weight(1f).aspectRatio(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Operation("*")) }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorButton(symbol = "4", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("4")) }
            CalculatorButton(symbol = "5", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("5")) }
            CalculatorButton(symbol = "6", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("6")) }
            CalculatorButton(symbol = "-", Modifier.weight(1f).aspectRatio(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Operation("-")) }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorButton(symbol = "1", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("1")) }
            CalculatorButton(symbol = "2", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("2")) }
            CalculatorButton(symbol = "3", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("3")) }
            CalculatorButton(symbol = "+", Modifier.weight(1f).aspectRatio(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Operation("+")) }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorButton(symbol = "0", Modifier.weight(2f).aspectRatio(2f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("0")) }
            CalculatorButton(symbol = ".", Modifier.weight(1f).aspectRatio(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Decimal) }
            CalculatorButton(symbol = "=", Modifier.weight(1f).aspectRatio(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Calculate) }
        }
    }
}