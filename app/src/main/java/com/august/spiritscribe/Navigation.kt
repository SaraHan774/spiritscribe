package com.august.spiritscribe

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Home
@Serializable
object AddNote
@Serializable
data class NoteDetail(val id: String)
@Serializable
object NoteList

//https://developer.android.com/guide/navigation/design/encapsulate
//In summary
//Encapsulate your navigation code for a related set of screens by placing it in a separate file
//Expose destinations by creating extension functions on NavGraphBuilder
//Expose navigation events by creating extension functions on NavController
//Use internal to keep screens and route types private

@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(navController, startDestination = Home) {
        homeDestination()
        noteDestination(
            navigateToNoteDetail = { id: String -> navController.navigateToNoteDetail(id) }
        )
    }
}

// NavController 확장함수로 네비 이벤트를 캡슐화
fun NavController.navigateToNoteDetail(id: String) {
    navigate(route = NoteDetail(id))
}

// NavGraphBuilder 확잠함수로 네비 목적지를 캡슐화
fun NavGraphBuilder.homeDestination() {
    composable<Home> { HomeRoute() }
}

fun NavGraphBuilder.noteDestination(
    navigateToNoteDetail: (String) -> Unit,
) {
    composable<AddNote> { AddNoteRoute() }
    composable<NoteList> { NoteListRoute(navigateToNoteDetail = navigateToNoteDetail) }
}

@Composable
fun HomeRoute() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SideEffect { Log.d("===", "Home Route") }
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
        SideEffect { Log.d("===", "Add Note Route") }
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
        SideEffect { Log.d("===", "Note List Route") }
        Text("Note List")
    }
}

@Composable
fun NoteDetailRoute(id: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SideEffect { Log.d("===", "Note Detail Route") }
        Text("Note Detail")
    }
}
