package com.august.spiritscribe.showcase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.random.Random
import kotlin.system.measureTimeMillis


@OptIn(ExperimentalCoroutinesApi::class)
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

    @Test
    fun `test advanceTimeBy and runCurrent 2`() {
        val testDispatcher = StandardTestDispatcher()
        CoroutineScope(testDispatcher).launch {
            delay(2)
            print("Done")
        }
        CoroutineScope(testDispatcher).launch {
            delay(4)
            print("Done2")
        }
        CoroutineScope(testDispatcher).launch {
            delay(6)
            print("Done3")
        }
        for (i in 1..5) {
            print(".")
            // 시간을 흐르게 하고 그동안 일어났을 모든 연산을 수행함
            testDispatcher.scheduler.advanceTimeBy(1)
            // 1밀리초와 정확히 일치하는 시간에 예정된 연산을 재개한다
            testDispatcher.scheduler.runCurrent()

            // 1 회
            // 가상시간 1, 예정된 연산 없음 .
            // 가상시간 2, 예정된 연산 있음 .
                        // 가상시간 2, Done출력
            // 가상시간 3, 예정된 연산 없음 .
            // 가상시간 4, 예정된 연산 있음 .
                        // 가상시간 4, Done2 출력
            // 가상시간 5, 예정된 연산 없음
        }
        //.DoneDone2Done3.... => x
        //..Done..Don2. => o
    }

    @Test
    fun `가상시간은 실제 시간과 무관하다`() {
        val dispatcher = StandardTestDispatcher()
        CoroutineScope(dispatcher).launch {
            delay(1000)
            println("Coroutine done")
        }

        Thread.sleep(Random.nextLong(2000)) // 여기서 얼마나 기다리는지는 상관이 없다. 결과에 영향을 주지 않음

        val time = measureTimeMillis {
            println("${dispatcher.scheduler.currentTime} Before") //0
            dispatcher.scheduler.advanceUntilIdle()
            println("${dispatcher.scheduler.currentTime} After") //1000
        }
        println("Took $time ms")
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
