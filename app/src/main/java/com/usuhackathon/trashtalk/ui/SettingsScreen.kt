package com.usuhackathon.trashtalk.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.usuhackathon.trashtalk.data.Chore
import com.usuhackathon.trashtalk.ui.theme.Ubuntu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val state = viewModel.state
    var showAddDialog by remember { mutableStateOf(false) }
    var editingChore by remember { mutableStateOf<Chore?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontFamily = Ubuntu) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.leagueID.isNotEmpty()) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Chore")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Manage Chores",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = Ubuntu
                )

                if (state.leagueID.isEmpty()) {
                    Text("Join a league to manage chores.")
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(state.chores) { chore ->
                            ChoreManagementRow(
                                chore = chore,
                                onEdit = { editingChore = chore },
                                onDelete = { viewModel.deleteChore(chore.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("LOG OUT", fontFamily = Ubuntu)
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showAddDialog) {
        ChoreDialog(
            title = "Add Chore",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desc, pts ->
                viewModel.addChore(name, desc, pts)
                showAddDialog = false
            }
        )
    }

    if (editingChore != null) {
        ChoreDialog(
            title = "Edit Chore",
            nameInitial = editingChore!!.name,
            descInitial = editingChore!!.description,
            ptsInitial = editingChore!!.points.toString(),
            onDismiss = { editingChore = null },
            onConfirm = { name, desc, pts ->
                viewModel.updateChore(editingChore!!.id, name, desc, pts)
                editingChore = null
            }
        )
    }
}

@Composable
fun ChoreManagementRow(chore: Chore, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(chore.name, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(chore.description, style = MaterialTheme.typography.bodySmall)
                Text("${chore.points} pts", color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
        }
    }
}

@Composable
fun ChoreDialog(
    title: String,
    nameInitial: String = "",
    descInitial: String = "",
    ptsInitial: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf(nameInitial) }
    var desc by remember { mutableStateOf(descInitial) }
    var pts by remember { mutableStateOf(ptsInitial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pts,
                    onValueChange = { pts = it },
                    label = { Text("Points") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, desc, pts.toIntOrNull() ?: 0) },
                enabled = name.isNotBlank() && pts.toIntOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
