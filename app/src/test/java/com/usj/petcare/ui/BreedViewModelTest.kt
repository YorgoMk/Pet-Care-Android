package com.usj.petcare.ui

import com.usj.petcare.data.remote.dto.BreedDto
import com.usj.petcare.data.repository.BreedRepository
import com.usj.petcare.fakes.FakeDogApiService
import com.usj.petcare.ui.view_model.BreedViewModel
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class BreedViewModelTest {
    private val api = FakeDogApiService(
        listOf(BreedDto(id = 1, name = "Beagle"), BreedDto(id = 2, name = "Golden Retriever"))
    )
    private val viewModel by lazy { BreedViewModel(BreedRepository(api)) }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody(null)))

    @Test
    fun startsWithNothingSearched() {
        val state = viewModel.uiState.value
        assertFalse(state.hasSearched)
        assertTrue(state.breeds.isEmpty())
    }

    @Test
    fun successfulSearchShowsResults() = runTest {
        viewModel.updateQuery("golden")
        viewModel.searchBreeds()

        val state = viewModel.uiState.value
        assertEquals(listOf("Golden Retriever"), state.breeds.map { it.name })
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun noMatchIsEmptyNotAnError() = runTest {
        viewModel.searchBreeds("unicorn")

        val state = viewModel.uiState.value
        assertTrue(state.hasSearched)
        assertTrue(state.breeds.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun blankQueryDoesNotCallTheApi() = runTest {
        viewModel.searchBreeds("   ")
        assertTrue(api.queries.isEmpty())
        assertFalse(viewModel.uiState.value.hasSearched)
    }

    @Test
    fun noConnectionShowsErrorThenRetryRecovers() = runTest {
        api.error = IOException("offline")
        viewModel.searchBreeds("beagle")
        assertTrue(viewModel.uiState.value.error!!.startsWith("No internet connection"))

        api.error = null
        viewModel.retrySearch()
        assertNull(viewModel.uiState.value.error)
        assertEquals(listOf("Beagle"), viewModel.uiState.value.breeds.map { it.name })
    }

    @Test
    fun rejectedKeyExplainsWhatToCheck() = runTest {
        api.error = httpError(401)
        viewModel.searchBreeds("beagle")
        assertTrue(viewModel.uiState.value.error!!.contains("DOG_API_KEY"))
    }

    @Test
    fun serverErrorShowsTheStatusCode() = runTest {
        api.error = httpError(503)
        viewModel.searchBreeds("beagle")
        assertTrue(viewModel.uiState.value.error!!.contains("503"))
    }
}
