package com.usuhackathon.trashtalk.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.usuhackathon.trashtalk.ui.theme.Ubuntu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionScreen(
    imageBitmap: Bitmap?,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Basic Clean", "Deep Clean", "Dishes", "Laundry", "Trash", "Vacuuming")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Task", fontFamily = Ubuntu) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Preview
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Task Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image Captured", fontFamily = Ubuntu, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dropdown Menu for Task Type
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Task Type", fontFamily = Ubuntu) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category, fontFamily = Ubuntu) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes TextField
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Notes", fontFamily = Ubuntu) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = LocalTextStyle.current.copy(fontFamily = Ubuntu),
                maxLines = 4
            )

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("SUBMIT", fontFamily = Ubuntu)
            }
        }
    }
}