package com.usj.petcare.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usj.petcare.data.repository.BreedRepository
import com.usj.petcare.model.Breed
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

data class BreedUiState(
    val query: String = "",
    val breeds: List<Breed> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

class BreedViewModel(
    private val repository: BreedRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BreedUiState())
    val uiState: StateFlow<BreedUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun searchBreeds(query: String = _uiState.value.query) {
        val searchQuery = query.trim()

        if (searchQuery.isBlank()) {
            _uiState.update {
                it.copy(
                    breeds = emptyList(),
                    error = null,
                    isLoading = false,
                    hasSearched = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    query = searchQuery,
                    isLoading = true,
                    error = null,
                    hasSearched = true
                )
            }

            try {
                val results = repository.searchBreeds(searchQuery)

                _uiState.update {
                    it.copy(
                        breeds = results,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        breeds = emptyList(),
                        isLoading = false,
                        error = "No internet connection. Please check your connection and try again."
                    )
                }
            } catch (exception: HttpException) {
                _uiState.update {
                    it.copy(
                        breeds = emptyList(),
                        isLoading = false,
                        error = httpErrorMessage(exception.code())
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        breeds = emptyList(),
                        isLoading = false,
                        error = "Something went wrong. Please try again."
                    )
                }
            }
        }
    }

    fun retrySearch() {
        searchBreeds(_uiState.value.query)
    }

    companion object {
        fun httpErrorMessage(code: Int): String = when (code) {
            401, 403 -> "The breed service rejected the API key (error $code). Check DOG_API_KEY in local.properties."
            in 500..599 -> "The breed service is not responding (error $code). Please try again later."
            else -> "The breed search failed (error $code). Please try again."
        }
    }
}
