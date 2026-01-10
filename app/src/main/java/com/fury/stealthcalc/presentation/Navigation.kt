package com.fury.stealthcalc.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fury.stealthcalc.presentation.calculator.CalculatorScreen
import com.fury.stealthcalc.presentation.calculator.CalculatorUiEvent
import com.fury.stealthcalc.presentation.calculator.CalculatorViewModel
import com.fury.stealthcalc.presentation.vault.VaultScreen
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "calculator_screen") {

        // Screen 1: Calculator
        composable("calculator_screen") {
            val viewModel: CalculatorViewModel = hiltViewModel()

            // Listen for the "Navigate" event from ViewModel
            LaunchedEffect(true) {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is CalculatorUiEvent.NavigateToVault -> {
                            navController.navigate("vault_screen")
                        }
                    }
                }
            }

            CalculatorScreen(
                viewModel = viewModel,
                onNavigateToVault = {
                    // We handle navigation inside the LaunchedEffect above
                    // so this callback might not be strictly needed, but good for preview
                }
            )
        }

        // Screen 2: Vault
        composable("vault_screen") {
            VaultScreen()
        }
    }
}