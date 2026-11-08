
package com.usj.petcare.ui.pets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.usj.petcare.model.Pet
import com.usj.petcare.model.PetAge
import com.usj.petcare.model.Species
import com.usj.petcare.ui.theme.PetCareTheme
import com.usj.petcare.ui.view_model.AppViewModelProvider
import com.usj.petcare.ui.view_model.PetListUiState
import com.usj.petcare.ui.view_model.PetListViewModel
import java.time.LocalDate

/**
 * Connects the screen to its ViewModel.
 * The screen below stays free of ViewModel code.
 */
@Composable
fun PetListRoute(
    onAddPet: () -> Unit,
    onOpenPet: (Long) -> Unit,
    onOpenBreedGuide: () -> Unit,
    viewModel: PetListViewModel = viewModel(
        factory = AppViewModelProvider.Factory
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PetListScreen(
        uiState = uiState,
        onAddPet = onAddPet,
        onOpenPet = onOpenPet,
        onOpenBreedGuide = onOpenBreedGuide
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    uiState: PetListUiState,
    onAddPet: () -> Unit,
    onOpenPet: (Long) -> Unit,
    onOpenBreedGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("My pets")
                },
                actions = {
                    IconButton(
                        onClick = onOpenBreedGuide
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Pets,
                            contentDescription = "Open Breed Guide"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddPet,
                icon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text("Add pet")
                }
            )
        }
    ) { innerPadding ->

        when (uiState) {

            PetListUiState.Loading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            PetListUiState.Empty -> EmptyPets(
                Modifier.padding(innerPadding)
            )

            is PetListUiState.Content -> PetList(
                pets = uiState.pets,
                onOpenPet = onOpenPet,
                contentPadding = innerPadding
            )
        }
    }
}

@Composable
private fun PetList(
    pets: List<Pet>,
    onOpenPet: (Long) -> Unit,
    contentPadding: PaddingValues
) {
    val today = remember {
        LocalDate.now()
    }

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            // Leave room so the last card is not hidden behind the Add Pet button.
            bottom = contentPadding.calculateBottomPadding() + 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // key = pet.id lets Compose track each pet when the list changes.
        items(pets, key = { it.id }) { pet ->
            PetCard(
                pet = pet,
                today = today,
                onClick = {
                    onOpenPet(pet.id)
                }
            )
        }
    }
}

@Composable
private fun PetCard(
    pet: Pet,
    today: LocalDate,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeciesAvatar(pet.species)

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    pet.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    pet.speciesAndBreed(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                pet.birthDate?.let {
                    Text(
                        PetAge.describe(it, today),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun EmptyPets(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🐾",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "No pets yet",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Tap \"Add pet\" to create your first pet profile.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PetListPreview() {
    PetCareTheme {
        PetListScreen(
            uiState = PetListUiState.Content(
                listOf(
                    Pet(
                        1,
                        "Luna",
                        Species.DOG,
                        "Golden Retriever",
                        LocalDate.of(2022, 3, 14),
                        28.5
                    ),
                    Pet(
                        2,
                        "Milo",
                        Species.CAT,
                        null,
                        LocalDate.of(2024, 6, 1)
                    )
                )
            ),
            onAddPet = {},
            onOpenPet = {},
            onOpenBreedGuide = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PetListEmptyPreview() {
    PetCareTheme {
        PetListScreen(
            uiState = PetListUiState.Empty,
            onAddPet = {},
            onOpenPet = {},
            onOpenBreedGuide = {}
        )
    }
}
