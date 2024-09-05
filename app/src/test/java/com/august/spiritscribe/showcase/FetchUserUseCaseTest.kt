package com.august.spiritscribe.showcase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class FetchUserUseCaseTest {

    private lateinit var repo: UserDataRepository
    private lateinit var useCase: FetchUserUseCase


    @Before
    fun setUp() {
        repo = FakeUserDataRepository()
        useCase = FetchUserUseCase(repo)
    }

    @Test
    fun `should construct User`() = runBlocking {
        // given

        //when
        val result = useCase.fetchUserData()

        //then
        val expectedUser = User(
            name = "Ben",
            friends = listOf(Friend(id = "some-friend-1", name = "Sara")),
            profile = Profile("123", "description", "")
        )
        assertEquals(expectedUser, result)
    }

    @Test
    fun `test 순차적으로 두개 호출하는 경우`() = runBlocking {
        val profile = repo.getProfile()
        val friends = repo.getFriends()
        assert(profile.id == "123")
        assert(friends.size == 1)
    }

    @Test
    fun `test 병렬적으로 두개 호출하는 경우`() = runBlocking {
        val profile = async { repo.getProfile() }
        val friends = async { repo.getFriends() }
        assert(profile.await().id == "123")
        assert(friends.await().size == 1)
    }

    @Test
    fun `test scheduler`() {
        // 코루틴에서 TestCoroutineScheduler 를 사용하려면 이를 지원하는 디스패처를 사용해야 함.
        // 일반적으로 StandardTestDispatcher 를 사용함
        val scheduler = TestCoroutineScheduler()
        println(scheduler.currentTime) // 0
        scheduler.advanceTimeBy(1000)
        println(scheduler.currentTime) // 1000
        scheduler.advanceTimeBy(1000)
        println(scheduler.currentTime) // 2000 -- 하지만 총 테스트 실행 시간은 11ms 이다
    }

    @Test
    fun `test scheduler and dispatcher`() {
        val scheduler = TestCoroutineScheduler()
        // 다른 디스패처와 달리, 코루틴이 실행되어야 할 스레드를 결정할 때만 사용되는게 아니라,
        // 테스트 디스패처로 시작된 코루틴은 가상 시간만큼 진행되기 전까지 실행되지 않음
        val dispatcher = StandardTestDispatcher(scheduler)
        CoroutineScope(dispatcher).launch {
            println("some work")
            delay(1000)
            println("some work 2")
            delay(1000)
            println("Coroutine done")
        }

        println("[${scheduler.currentTime}] Before")
        scheduler.advanceUntilIdle()
        println("[${scheduler.currentTime}] After")
    }

    @Test
    fun `test advanceTimeBy and runCurrent`() {
        val testDispatcher = StandardTestDispatcher()
        CoroutineScope(testDispatcher).launch {
            delay(1)
            println("done1")
        }
        CoroutineScope(testDispatcher).launch {
            delay(3)
            println("done2")
        }
        testDispatcher.scheduler.advanceTimeBy(2) // 2밀리초를 흐르게 하면 그 전에 지연된 모든 코루틴이 재개된다
        testDispatcher.scheduler.runCurrent() // 2밀리초와 정확히 일치하는 시간에 예정된 연산을 재개한다
    }
}

class FakeUserDataRepository : UserDataRepository {
    override suspend fun getName(): String {
        return "Ben"
    }

    override suspend fun getFriends(): List<Friend> {
        delay(500)
        return listOf(Friend("some-friend-1", "Sara"))
    }

    override suspend fun getProfile(): Profile {
        delay(1000)
        return Profile("123", "description", "")
    }
}
