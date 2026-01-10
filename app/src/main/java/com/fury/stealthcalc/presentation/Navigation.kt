package com.fury.stealthcalc.presentation

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.stealthcalc.presentation.add_edit_note.AddEditNoteScreen
import com.fury.stealthcalc.presentation.calculator.CalculatorScreen
import com.fury.stealthcalc.presentation.calculator.CalculatorUiEvent
import com.fury.stealthcalc.presentation.calculator.CalculatorViewModel
import com.fury.stealthcalc.presentation.vault.VaultScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "calculator_screen") {

        // 1. Calculator
        composable("calculator_screen") {
            val viewModel: CalculatorViewModel = hiltViewModel()
            LaunchedEffect(true) {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is CalculatorUiEvent.NavigateToVault -> {
                            navController.navigate("vault_screen")
                        }
                    }
                }
            }
            CalculatorScreen(viewModel = viewModel, onNavigateToVault = {})
        }

        // 2. Vault List
        composable(
            "vault_screen",
            enterTransition = { slideInVertically(initialOffsetY = { it }) }, // Slides up from bottom like a secret panel
            exitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
            VaultScreen(
                navController = navController // Pass navController to VaultScreen
            )
        }

        // 3. Add/Edit Note Screen
        composable(
            route = "add_edit_note_screen?noteId={noteId}&noteColor={noteColor}",
            arguments = listOf(
                navArgument(name = "noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(name = "noteColor") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            val color = it.arguments?.getInt("noteColor") ?: -1
            AddEditNoteScreen(navController = navController, noteColor = color)
        }
    }
}