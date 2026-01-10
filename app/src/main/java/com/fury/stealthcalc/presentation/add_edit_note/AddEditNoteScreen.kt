package com.fury.stealthcalc.presentation.add_edit_note

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fury.stealthcalc.ui.theme.DarkBackground
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value
    val currentColor = viewModel.noteColor.value

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 1. Smooth Color Animation
    val animatedBackground by animateColorAsState(
        targetValue = Color(currentColor),
        animationSpec = tween(durationMillis = 500),
        label = "ColorAnimation"
    )

    // 2. Determine Text Contrast (Black text for light notes, White for dark notes)
    val isDarkNote = animatedBackground.luminance() < 0.5f
    val contentColor = if (isDarkNote) Color.White else Color.Black
    val placeholderColor = if (isDarkNote) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f)

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold    (
        topBar = {
            // Transparent Top Bar
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = contentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AddEditNoteEvent.SaveNote) },
                // FAB is always opposite color of the note for visibility
                containerColor = if (isDarkNote) Color.White else Color.Black,
                contentColor = if (isDarkNote) Color.Black else Color.White
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save Note")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = animatedBackground, // Background goes edge-to-edge
        contentWindowInsets = WindowInsets.systemBars // Scaffold will handle padding for inner content
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()) // Allow scrolling for long notes
                .padding(horizontal = 24.dp)
        ) {

            // 3. Elegant Color Picker Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // A nice palette of professional colors
                val colors = listOf(
                    Color(0xFF1F1F1F), // The "Stealth" Dark
                    Color(0xFF5D4037), // Deep Brown
                    Color(0xFF303F9F), // Midnight Blue
                    Color(0xFFEF9A9A), // Pastel Red
                    Color(0xFF80DEEA), // Pastel Cyan
                    Color(0xFFFFF59D), // Pastel Yellow
                    Color(0xFFA5D6A7)  // Pastel Green
                )

                colors.forEach { color ->
                    val isSelected = currentColor == color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) contentColor else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.onEvent(AddEditNoteEvent.ChangeColor(color.toArgb()))
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Custom Title Field (No ugly underlines)
            TransparentHintTextField(
                text = titleState,
                hint = "Title...",
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                textStyle = MaterialTheme.typography.headlineMedium.copy(color = contentColor),
                hintColor = placeholderColor,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Custom Content Field
            TransparentHintTextField(
                text = contentState,
                hint = "Type your secret here...",
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredContent(it)) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = contentColor, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5),
                hintColor = placeholderColor,
                modifier = Modifier.fillMaxHeight() // Fill rest of screen
            )
        }
    }
}

// Helper Composable for clean text input
@Composable
fun TransparentHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle = LocalTextStyle.current,
    hintColor: Color = Color.Gray,
    singleLine: Boolean = false
) {
    Box(modifier = modifier) {
        androidx.compose.foundation.text.BasicTextField(
            value = text,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = textStyle,
            modifier = Modifier.fillMaxWidth()
        )
        if (text.isEmpty()) {
            Text(
                text = hint,
                style = textStyle,
                color = hintColor
            )
        }
    }
}