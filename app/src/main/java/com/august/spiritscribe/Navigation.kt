package com.august.spiritscribe

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.august.spiritscribe.ui.note.NoteDetailRoute
import com.august.spiritscribe.ui.note.DebugScreenNoteListRoute
import com.august.spiritscribe.ui.note.NoteListRoute
import com.august.spiritscribe.ui.poc.NewThreadScreen
import com.august.spiritscribe.ui.search.SearchRoute
import kotlinx.serialization.Serializable


@Serializable
data object AddNote

@Serializable
data class NoteDetail(val id: String)

@Serializable
data object MyNoteList

@Serializable
data object Feed

@Serializable
data object Search

@Serializable
data object Profile


data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Note", MyNoteList, Icons.Filled.Create),
    TopLevelRoute("Feed", Feed, Icons.Filled.Menu),
    TopLevelRoute("Search", Search, Icons.Filled.Search),
    TopLevelRoute("Profile", Profile, Icons.Filled.AccountCircle)
)


//https://developer.android.com/guide/navigation/design/encapsulate
//In summary
//Encapsulate your navigation code for a related set of screens by placing it in a separate file
//Expose destinations by creating extension functions on NavGraphBuilder
//Expose navigation events by creating extension functions on NavController
//Use internal to keep screens and route types private

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = MyNoteList
        ) {
            noteDestination(
                navigateToNoteDetail = { id: String -> navController.navigateToNoteDetail(id) },
                sharedTransitionScope = this@SharedTransitionLayout,
                navigateToAddNote = { navController.navigateToAddNote() }
            )
            feedDestination()
            searchDestination()
            profileDestination()
        }
    }
}

// NavController 확장함수로 네비 이벤트를 캡슐화
fun NavController.navigateToNoteDetail(id: String) {
    navigate(route = NoteDetail(id))
}

fun NavController.navigateToAddNote() {
    navigate(route = AddNote)
}

// NavGraphBuilder 확잠함수로 네비 목적지를 캡슐화
fun NavGraphBuilder.feedDestination() {
    composable<Feed> { FeedRoute() }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.noteDestination(
    navigateToNoteDetail: (String) -> Unit,
    navigateToAddNote: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    composable<MyNoteList> {
        NoteListRoute(
            navigateToNoteDetail = navigateToNoteDetail,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable,
        )
    }
    composable<NoteDetail> { navBackStackEntry: NavBackStackEntry ->
        // Type safe arguments !
        // TODO :  https://developer.android.com/guide/navigation/design/type-safety
        val detail: NoteDetail = navBackStackEntry.toRoute()
        NoteDetailRoute(
            detail.id,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable,
            onClickAddNote = navigateToAddNote
        )
    }
    composable<AddNote> { AddNoteRoute() }
}

fun NavGraphBuilder.searchDestination() {
    composable<Search> { SearchRoute() }
}

fun NavGraphBuilder.profileDestination() {
    composable<Profile> { ProfileRoute() }
}


//// Routes /////

@Composable
fun FeedRoute() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun AddNoteRoute() {
    NewThreadScreen()
}

@Composable
fun ProfileRoute(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile")
    }
}