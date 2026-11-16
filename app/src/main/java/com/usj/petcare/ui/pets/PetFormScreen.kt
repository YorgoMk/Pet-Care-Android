package com.usj.petcare.ui.pets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.usj.petcare.model.Species
import com.usj.petcare.ui.theme.PetCareTheme
import com.usj.petcare.ui.view_model.AppViewModelProvider
import com.usj.petcare.ui.view_model.PetFormErrors
import com.usj.petcare.ui.view_model.PetFormUiState
import com.usj.petcare.ui.view_model.PetFormViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun PetFormRoute(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: PetFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }

    PetFormScreen(
        uiState = uiState,
        onBack = onBack,
        onNameChange = viewModel::onNameChange,
        onSpeciesChange = viewModel::onSpeciesChange,
        onBreedChange = viewModel::onBreedChange,
        onBirthDateChange = viewModel::onBirthDateChange,
        onWeightChange = viewModel::onWeightChange,
        onNotesChange = viewModel::onNotesChange,
        onSave = viewModel::save
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PetFormScreen(
    uiState: PetFormUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onSpeciesChange: (Species) -> Unit,
    onBreedChange: (String) -> Unit,
    onBirthDateChange: (LocalDate?) -> Unit,
    onWeightChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit pet" else "Add pet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text("Name *") },
                singleLine = true,
                isError = uiState.errors.name != null,
                supportingText = uiState.errors.name?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Species", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Species.entries.forEach { species ->
                    FilterChip(
                        selected = uiState.species == species,
                        onClick = { onSpeciesChange(species) },
                        label = { Text("${species.emoji} ${species.label}") }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.breed,
                onValueChange = onBreedChange,
                label = { Text("Breed (optional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.birthDate?.format(BirthDateFormatter) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Birth date (optional)") },
                isError = uiState.errors.birthDate != null,
                supportingText = uiState.errors.birthDate?.let { { Text(it) } },
                trailingIcon = {
                    if (uiState.birthDate != null) {
                        IconButton(onClick = { onBirthDateChange(null) }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear birth date")
                        }
                    } else {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = "Pick birth date")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.weightText,
                onValueChange = onWeightChange,
                label = { Text("Weight (optional)") },
                suffix = { Text("kg") },
                singleLine = true,
                isError = uiState.errors.weight != null,
                supportingText = uiState.errors.weight?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotesChange,
                label = { Text("Notes (optional)") },
                placeholder = { Text("Food, habits, anything worth remembering") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(if (uiState.isEditing) "Save changes" else "Save pet")
            }
        }
    }

    if (showDatePicker) {
        BirthDatePickerDialog(
            initialDate = uiState.birthDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                onBirthDateChange(it)
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthDatePickerDialog(
    initialDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val todayMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayMillis
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let {
                        onDateSelected(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    } ?: onDismiss()
                }
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = state)
    }
}

@Preview(showBackground = true)
@Composable
private fun PetFormPreview() {
    PetCareTheme {
        PetFormScreen(
            uiState = PetFormUiState(
                name = "",
                weightText = "abc",
                errors = PetFormErrors(name = "Please enter a name", weight = "Enter a number, for example 4.5")
            ),
            onBack = {}, onNameChange = {}, onSpeciesChange = {}, onBreedChange = {},
            onBirthDateChange = {}, onWeightChange = {}, onNotesChange = {}, onSave = {}
        )
    }
}
