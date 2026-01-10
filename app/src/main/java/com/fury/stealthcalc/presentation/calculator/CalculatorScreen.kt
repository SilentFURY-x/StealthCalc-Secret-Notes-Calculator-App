package com.fury.stealthcalc.presentation.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.stealthcalc.ui.theme.*
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel(),
    onNavigateToVault: () -> Unit
) {
    val input by viewModel.input.collectAsState()
    val result by viewModel.result.collectAsState()
    val history by viewModel.history.collectAsState()

    // Scroll states to auto-scroll to latest
    val historyListState = rememberLazyListState()
    val inputScrollState = rememberScrollState()

    // Auto-scroll history when new item added
    LaunchedEffect(history.size) {
        if(history.isNotEmpty()) {
            historyListState.animateScrollToItem(history.size - 1)
        }
    }

    // Auto-scroll input when typing
    LaunchedEffect(input) {
        inputScrollState.animateScrollTo(inputScrollState.maxValue)
    }

    val cyberGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cyberGradient)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {

        // 1. HISTORY SECTION (Scrollable list at top)
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Takes up available space at top
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            state = historyListState,
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            items(history) { item ->
                Text(
                    text = item,
                    color = HistoryText,
                    fontSize = 20.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // 2. INPUT & RESULT DISPLAY
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Result (Small, above input)
            Text(
                text = result,
                color = HistoryText,
                fontSize = 32.sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Light,
                maxLines = 1
            )

            // Main Input (Large, Horizontal Scrollable)
            Text(
                text = input.ifEmpty { "0" },
                color = TextWhite,
                fontSize = 56.sp, // Slightly smaller to fit better
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                maxLines = 1, // Prevents vertical overlap
                modifier = Modifier.horizontalScroll(inputScrollState) // Fixes glitchy wrapping
            )
        }

        // 3. OPTIMIZED KEYPAD GRID
        // We use Column with weights so buttons fill height evenly
        Column(
            modifier = Modifier.weight(1.5f), // Controls height ratio of keypad
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: C, DEL, %, /
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton(symbol = "C", Modifier.weight(1f), AccentGray, TextBlack) { viewModel.onAction(CalculatorAction.Clear) }

                // DEL Button (Using Icon)
                CalculatorIconButton(Modifier.weight(1f), AccentGray, TextBlack, onClick = { viewModel.onAction(CalculatorAction.Delete) }) {
                    Icon(imageVector = Icons.Default.Backspace, contentDescription = "Delete")
                }

                CalculatorButton(symbol = "%", Modifier.weight(1f), AccentGray, TextBlack) { viewModel.onAction(CalculatorAction.Operation("%")) }
                CalculatorButton(symbol = "/", Modifier.weight(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Operation("/")) }
            }

            // Row 2: 7, 8, 9, *
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton(symbol = "7", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("7")) }
                CalculatorButton(symbol = "8", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("8")) }
                CalculatorButton(symbol = "9", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("9")) }
                CalculatorButton(symbol = "*", Modifier.weight(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Operation("*")) }
            }

            // Row 3: 4, 5, 6, -
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton(symbol = "4", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("4")) }
                CalculatorButton(symbol = "5", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("5")) }
                CalculatorButton(symbol = "6", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("6")) }
                CalculatorButton(symbol = "-", Modifier.weight(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Operation("-")) }
            }

            // Row 4: 1, 2, 3, +
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton(symbol = "1", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("1")) }
                CalculatorButton(symbol = "2", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("2")) }
                CalculatorButton(symbol = "3", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("3")) }
                CalculatorButton(symbol = "+", Modifier.weight(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Operation("+")) }
            }

            // Row 5: 0, ., =
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorButton(symbol = "0", Modifier.weight(2f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Number("0")) } // Weight 2f makes it double width
                CalculatorButton(symbol = ".", Modifier.weight(1f), SecondaryGray, TextWhite) { viewModel.onAction(CalculatorAction.Decimal) }
                CalculatorButton(symbol = "=", Modifier.weight(1f), PrimaryOrange, TextWhite) { viewModel.onAction(CalculatorAction.Calculate) }
            }
        }
    }
}