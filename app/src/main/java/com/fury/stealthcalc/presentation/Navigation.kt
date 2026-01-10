package com.fury.stealthcalc.presentation

import android.annotation.SuppressLint
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
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.platform.LocalContext

@SuppressLint("ContextCastToActivity")
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "calculator_screen") {

        // 1. Calculator
        composable("calculator_screen") {
            val viewModel: CalculatorViewModel = hiltViewModel()
            val context = LocalContext.current as FragmentActivity

            // 1. Setup the Biometric Prompt
            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = remember {
                BiometricPrompt(context, executor, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        // 2. On Success, tell ViewModel to Navigate
                        viewModel.onBiometricSuccess()
                    }
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        // Optional: Handle error (e.g., show Toast)
                    }
                })
            }

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Stealth Access")
                .setSubtitle("Authenticate to access the Vault")
                .setNegativeButtonText("Cancel")
                .build()

            // 3. Listen for events
            LaunchedEffect(true) {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is CalculatorUiEvent.ShowBiometricPrompt -> {
                            biometricPrompt.authenticate(promptInfo)
                        }
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