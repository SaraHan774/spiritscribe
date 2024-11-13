package com.august.spiritscribe.sample

import app.cash.turbine.test
import com.august.spiritscribe.samples.Person
import com.august.spiritscribe.samples.PersonAPI
import com.august.spiritscribe.samples.PersonScreenState
import com.august.spiritscribe.samples.PersonViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test

// https://www.youtube.com/watch?app=desktop&v=-0PFpQIRyH4&ab_channel=CharfaouiYounes
class PersonViewModelTest {

    val testDispatcher = UnconfinedTestDispatcher()
    @Test
    fun `test`()  = runTest(testDispatcher) {
        val viewModel = PersonViewModel(
            FakePersonAPI(), testDispatcher, testDispatcher
        )

        viewModel.uiState.test {
            assertEquals(PersonScreenState.Loading(), awaitItem())
            advanceTimeBy(1000)
            assertEquals(PersonScreenState.Success(listOf(Person("Younes"), Person("Sara"))), awaitItem())
        }
    }

    class FakePersonAPI : PersonAPI {
        override fun getAllPersons(): Flow<List<Person>> {
            return flow {
                delay(1000)
                emit(
                    listOf(
                        Person("Younes"),
                        Person("Sara"),
                    )
                )
            }
        }
    }
}