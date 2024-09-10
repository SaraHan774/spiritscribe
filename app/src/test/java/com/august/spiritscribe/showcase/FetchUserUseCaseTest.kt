package com.august.spiritscribe.showcase

import android.service.autofill.UserData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.math.exp
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

    @Test
    fun runTestUsecase() = runTest {
        println("scope = ${this.backgroundScope}") // StandardTestDispatcher
        println("scheduler = ${this.testScheduler}") // TestCoroutineScheduler
    }

    @Test
    fun test2() = runTest {
        assertEquals(0, currentTime)
        coroutineScope {
            launch { delay(1000) }
            launch { delay(1500) }
            launch { delay(2000) }
        }
        assertEquals(2000, currentTime) // 병렬 처리 하는 코드 이렇게 쉽게 테스트 가능함
    }
}

class FakeUserDataRepository : UserDataRepository {
    override suspend fun getName(): String {
        delay(1000)
        return "Ben"
    }

    override suspend fun getFriends(): List<Friend> {
        delay(1000)
        return listOf(Friend("some-friend-1", "Sara"))
    }

    override suspend fun getProfile(): Profile {
        delay(1000)
        return Profile("123", "description", "")
    }
}

class FetchUserUseCase(
    private val repo: UserDataRepository
) {
    suspend fun fetchUserData(): User = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends() }
        val profile = async { repo.getProfile() }
        User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
class FetchUserDataTest {

    @Test
    fun `should load data concurrently`() = runTest {
        // given
        val userRepo = FakeUserDataRepository()
        val useCase = FetchUserUseCase(repo = userRepo)

        // when
        useCase.fetchUserData()

        // then
        assertEquals(1000, currentTime)
    }

    @Test
    fun `should construct User`() = runTest {
        //given
        val userRepo = FakeUserDataRepository()
        val useCase = FetchUserUseCase(repo = userRepo)

        // when
        val result = useCase.fetchUserData()

        // then
        val expectedUser = User(
            name = "Ben",
            friends = listOf(Friend("some-friend-1", "Sara")),
            profile = Profile("123", "description", "")
        )
        assertEquals(expectedUser, result)
    }
}

class BackgroundScopeTest {

    @Test
    fun `should increment counter`() = runTest {
        var i = 0
        launch { // test scope 에다가 런칭 해버리면 계속해서 기다리게 됨. 종료 안돼 !
            while (true) {
                delay(1000)
                i++
            }
        }
        delay(1001)
        assertEquals(1, i)
        delay(1000)
        assertEquals(2, i)
        coroutineContext.job.cancelChildren() // 명시적으로 돌아가는 애들을 캔슬 해주어야 테스트 종료됨
    }

    @Test
    fun `should increment counter2` () = runTest {
        var i = 0
        backgroundScope.launch {
            while (true) {
                delay(1000)
                i++
            }
        }
        delay(1001)
        assertEquals(1, i)
        delay(1000)
        assertEquals(2, i) // backgroundScope - 테스트가 기다릴 필요 없는 모든 프로세스 시작할때 용이
    }
}

// 특정 함수가 구조화된 동시성을 지키고 있는지 테스트하려면, 중단 함수로부터 컨텍스트를 받은 뒤, 컨텍스트가 기대한 값을 가지고 있는지와 잡이
// 적절한 상태인지 확인하는 것이 가장 쉬운 방법이다.

class CancelContextPassingTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should map async and keep elements order`() = runTest {
        val transforms = listOf(
            suspend { delay(3000); "A" },
            suspend { delay(2000); "B" },
            suspend { delay(4000); "C" },
            suspend { delay(1000); "D" },
        )
        val res = transforms.mapAsync { it() }
        assertEquals(listOf("A", "B", "C", "D"), res)
        assertEquals(4000, currentTime)
    }

    @Test
    fun `should support context propagation`() = runTest {
        var ctx: CoroutineContext? = null
        val name1 = CoroutineName("Name 1")
        withContext(name1) {
            listOf("A").mapAsync {
                ctx = currentCoroutineContext()
                it
            }
            assertEquals(name1, ctx?.get(CoroutineName))
        }

        val name2 = CoroutineName("Some Name2")
        withContext(name2) {
            listOf(1, 2, 3).mapAsync {
                ctx = currentCoroutineContext()
                it
            }
            assertEquals(name2, ctx?.get(CoroutineName))
        }
    }

    @Test
    fun `should support cancellation`() = runTest {
        var job: Job? = null
        val parentJob = launch {
            listOf("A").mapAsync {
                job = currentCoroutineContext().job
                delay(Long.MAX_VALUE)
            }
        }
        delay(1000)
        parentJob.cancel() // 부모 잡이 캔슬됨
        assertEquals(true, job?.isCancelled) // 자식도 캔슬 됨
    }


}

suspend fun <T, R> Iterable<T>.mapAsync(transformation: suspend (T) -> R) : List<R> = coroutineScope {
    this@mapAsync.map { async { transformation(it) } }.awaitAll() // 만약 async 를 외부 스코프에서 시작하면 위의 테스트들은 실패한다.
}

class UnconfinedTest {

    // StandardTestDispatcher - scheduler.advance() 하기 전까지 연산 안됨
    // UnconfinedTestDispatcher - 코루틴을 시작했을때 첫번째 지연이 일어나기까지 모든 연산을 즉시 수행
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test(){
        val dispa = StandardTestDispatcher()
        CoroutineScope(dispa).launch {
            print("A")
            delay(1)
            print("B")
        }
        dispa.scheduler.advanceUntilIdle() // 없으면 위의 런치는 실행 안됨
        CoroutineScope(UnconfinedTestDispatcher()).launch {
            print("***C***")
            delay(1)
            print("D")
        }
    }
}

class MockTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should load data concurrently`() = runTest {
        // given
        val userRepo = mockk<UserDataRepository>()
        // coEvery - suspend 함수 모킹 (coroutine 이란 뜻인가?)
        coEvery { userRepo.getName() }.coAnswers {
            delay(600)
            "Ben"
        }
        coEvery { userRepo.getFriends() }.coAnswers {
            delay(800)
            listOf(Friend("id1", "Sara"))
        }
        coEvery { userRepo.getProfile() }.coAnswers {
            delay(800)
            Profile("id", "description", "https://google.com")
        }
        val useCase = FetchUserUseCase(userRepo)
        useCase.fetchUserData()
        assertEquals(800, currentTime)
    }
}