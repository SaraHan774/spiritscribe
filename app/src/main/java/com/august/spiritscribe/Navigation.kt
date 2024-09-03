package com.august.spiritscribe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

sealed interface AppRoute

@Serializable
data object AddNote : AppRoute

@Serializable
data class NoteDetail(val id: String) : AppRoute

@Serializable
data object MyNoteList : AppRoute

@Serializable
data object Feed : AppRoute

@Serializable
data object Search : AppRoute

@Serializable
data object Profile : AppRoute

val topLevelDestinations: List<AppRoute> = listOf(
    MyNoteList, Feed, Search, Profile
)

//https://developer.android.com/guide/navigation/design/encapsulate
//In summary
//Encapsulate your navigation code for a related set of screens by placing it in a separate file
//Expose destinations by creating extension functions on NavGraphBuilder
//Expose navigation events by creating extension functions on NavController
//Use internal to keep screens and route types private

@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(navController, startDestination = MyNoteList) {
        noteDestination(
            navigateToNoteDetail = { id: String -> navController.navigateToNoteDetail(id) }
        )
        feedDestination()
        searchDestination()
        profileDestination()
    }
}

// NavController 확장함수로 네비 이벤트를 캡슐화
fun NavController.navigateToNoteDetail(id: String) {
    navigate(route = NoteDetail(id))
}

// NavGraphBuilder 확잠함수로 네비 목적지를 캡슐화
fun NavGraphBuilder.feedDestination() {
    composable<Feed> { FeedRoute() }
}

fun NavGraphBuilder.noteDestination(
    navigateToNoteDetail: (String) -> Unit,
) {
    composable<AddNote> { AddNoteRoute() }
    composable<MyNoteList> { NoteListRoute(navigateToNoteDetail = navigateToNoteDetail) }
    composable<NoteDetail> { navBackStackEntry: NavBackStackEntry ->
        // Type safe arguments !
        // TODO :  https://developer.android.com/guide/navigation/design/type-safety
        val detail: NoteDetail = navBackStackEntry.toRoute()
        NoteDetailRoute(detail.id)
    }
}

fun NavGraphBuilder.searchDestination() {
    composable<Search> { SearchRoute() }
}

fun NavGraphBuilder.profileDestination() {
    composable<Profile> { ProfileRoute() }
}

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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Note")
    }
}

@Composable
fun NoteListRoute(navigateToNoteDetail: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listOf(
            "DESTINATION_ID_ONE",
            "DESTINATION_ID_TWO",
            "DESTINATION_ID_THREE"
        ).forEachIndexed { index, item ->
            Column(modifier = Modifier
                .padding(8.dp)
                .wrapContentHeight()
                .fillMaxWidth()
                .clickable {
                    navigateToNoteDetail(item)
                }
                .background(Color.White)
                .padding(8.dp)
            ) {
                Text(text = item, style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}

@Composable
fun NoteDetailRoute(id: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Note Detail_$id")
    }
}

@Composable
fun SearchRoute(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Search")
    }
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