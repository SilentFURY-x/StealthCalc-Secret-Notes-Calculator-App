package com.fury.stealthcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.fury.stealthcalc.presentation.calculator.CalculatorScreen
import com.fury.stealthcalc.ui.theme.StealthCalcTheme
import dagger.hilt.android.AndroidEntryPoint
import com.fury.stealthcalc.presentation.Navigation

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StealthCalcTheme { // Use your theme wrapper
                // Replace CalculatorScreen() with Navigation()
                Navigation()
            }
        }
    }
}