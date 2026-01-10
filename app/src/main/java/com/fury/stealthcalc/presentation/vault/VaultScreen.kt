package com.fury.stealthcalc.presentation.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fury.stealthcalc.ui.theme.DarkBackground
import com.fury.stealthcalc.ui.theme.PrimaryOrange
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material.icons.filled.Delete
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun VaultScreen(
    navController: NavController,
    viewModel: VaultViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        // Scaffold handles system bars automatically if configured,
        // but let's be explicit with the modifier on the column inside content
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to Add Screen with default ID -1
                    navController.navigate("add_edit_note_screen?noteId=-1&noteColor=${PrimaryOrange.toArgb()}")
                },
                containerColor = PrimaryOrange
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(padding) // Scaffold padding handles bottom bar
                .consumeWindowInsets(padding) // Fixes potential glitch
                // Add status bar padding manually if needed, usually Scaffold handles it
                // but strictly for edge-to-edge with custom colors:
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Secret Notes",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // The List of Notes
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                items(notes, key = { it.id!! }) { note -> // IMPORTANT: Add key so Compose knows which item is which

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                viewModel.deleteNote(note) // Trigger Delete
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            // The "Red" background that appears when you drag
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) Color.Transparent else Color(0xFFFF1744),
                                label = "DismissColor"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp)) // Match note shape
                                    .background(color)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center // Center the trash icon
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        },
                        content = {
                            // The Actual Note
                            NoteItem(
                                note = note,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("add_edit_note_screen?noteId=${note.id}&noteColor=${note.color}")
                                    },
                                onDeleteClick = {} // We don't need the click anymore
                            )
                        }
                    )
                }
            }
        }
    }
}