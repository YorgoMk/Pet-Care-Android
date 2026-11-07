package com.usj.petcare.ui.pets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.usj.petcare.model.Pet
import com.usj.petcare.model.PetAge
import com.usj.petcare.model.Species
import com.usj.petcare.ui.theme.PetCareTheme
import com.usj.petcare.ui.view_model.AppViewModelProvider
import com.usj.petcare.ui.view_model.PetDetailUiState
import com.usj.petcare.ui.view_model.PetDetailViewModel
import java.time.LocalDate

@Composable
fun PetDetailRoute(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: PetDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // After a delete there is nothing left to show, so return to the list.
    LaunchedEffect(uiState) {
        if (uiState == PetDetailUiState.Deleted) onBack()
    }

    PetDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onEdit = onEdit,
        onConfirmDelete = viewModel::deletePet
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    uiState: PetDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Small UI-only state: survives rotation, does not belong in the database.
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val pet = (uiState as? PetDetailUiState.Content)?.pet

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(pet?.name ?: "Pet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (pet != null) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit pet")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete pet")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            when (uiState) {
                PetDetailUiState.Loading, PetDetailUiState.Deleted ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                PetDetailUiState.NotFound ->
                    Text("This pet no longer exists.", Modifier.align(Alignment.Center))

                is PetDetailUiState.Content -> PetProfile(uiState.pet)
            }
        }
    }

    if (showDeleteDialog && pet != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete ${pet.name}?") },
            text = { Text("This removes the profile from this phone. It cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onConfirmDelete()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PetProfile(pet: Pet) {
    val today = remember { LocalDate.now() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpeciesAvatar(pet.species, size = 96.dp)
        Spacer(Modifier.height(12.dp))
        Text(pet.name, style = MaterialTheme.typography.headlineMedium)
        Text(
            pet.speciesAndBreed(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(vertical = 8.dp)) {
                InfoRow("Age", pet.birthDate?.let { PetAge.describe(it, today) } ?: "Unknown")
                HorizontalDivider()
                InfoRow("Birth date", pet.birthDate?.format(BirthDateFormatter) ?: "Not set")
                HorizontalDivider()
                InfoRow("Weight", pet.weightKg?.let { "$it kg" } ?: "Not set")
            }
        }

        if (pet.notes.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Notes", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    Text(pet.notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun PetDetailPreview() {
    PetCareTheme {
        PetDetailScreen(
            uiState = PetDetailUiState.Content(
                Pet(1, "Luna", Species.DOG, "Golden Retriever", LocalDate.of(2022, 3, 14), 28.5, "Allergic to chicken.")
            ),
            onBack = {},
            onEdit = {},
            onConfirmDelete = {}
        )
    }
}
