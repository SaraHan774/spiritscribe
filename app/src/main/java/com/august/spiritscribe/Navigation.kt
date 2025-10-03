package com.august.spiritscribe

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.august.spiritscribe.ui.note.NewThreadScreen
import com.august.spiritscribe.ui.note.NoteDetailRoute
import com.august.spiritscribe.ui.note.NoteListRoute
import com.august.spiritscribe.ui.search.SearchScreen
import com.august.spiritscribe.ui.whiskey.AddWhiskeyScreen
import com.august.spiritscribe.ui.whiskey.AddWhiskeyNoteScreen
import com.august.spiritscribe.ui.whiskey.WhiskeyDetailRoute
import com.august.spiritscribe.ui.social.WhiskeySocialScreen
import com.august.spiritscribe.ui.feed.FeedScreen
import com.august.spiritscribe.ui.evolution.EvolutionScreen
import kotlinx.serialization.Serializable

@Serializable
data object AddWhiskey

@Serializable
data object AddNote

@Serializable
data class AddWhiskeyNote(val whiskeyId: String)

@Serializable
data class NoteDetail(val id: String)

@Serializable
data class WhiskeyDetail(
    val id: String
)

@Serializable
data object MyNoteList

@Serializable
data object Feed

@Serializable
data object Search

// List of routes where bottom navigation should be hidden
val hideBottomNavigationRoutes = listOf(
    AddWhiskey::class,
    AddNote::class,
    AddWhiskeyNote::class,
    NoteDetail::class,
    WhiskeyDetail::class
)

sealed class Screen(val route: String) {
    open val label: String = ""
    open val icon: ImageVector = Icons.Default.LocalBar

    object Note : Screen("feed") {
        override val icon: ImageVector = Icons.Filled.NoteAlt
        override val label: String = "노트"
    }

    object Search : Screen("search") {
        override val icon: ImageVector = Icons.Filled.Search
        override val label: String = "검색"
    }

    object Social : Screen("social") {
        override val icon: ImageVector = Icons.Filled.Group
        override val label: String = "소셜"
    }

    object Evolution : Screen("flavor_wheel") {
        override val icon: ImageVector = Icons.Filled.Science
        override val label: String = "진화"
    }

    // 상세 화면들
    object WhiskeyDetail : Screen("whiskey_detail/{whiskeyId}") {
        fun createRoute(whiskeyId: String) = "whiskey_detail/$whiskeyId"
    }

    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: String) = "note_detail/$noteId"
    }

    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: String) = "edit_note/$noteId"
    }

    object Settings : Screen("settings") {
        override val icon: ImageVector = Icons.Filled.Settings
        override val label: String = "Settings"
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    SharedTransitionLayout {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = Screen.Social.route
        ) {
            composable(Screen.Social.route) {
                WhiskeySocialScreen()
            }
            composable(Screen.Note.route) {
                FeedScreen(
                    onWhiskeyClick = { id ->
                        navController.navigate(Screen.WhiskeyDetail.createRoute(id))
                    },
                    onAddWhiskeyClick = {
                        navController.navigate(AddWhiskey)
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onWhiskeyClick = {}
                )
            }
            composable(Screen.Evolution.route) {
                EvolutionScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(
                route = Screen.WhiskeyDetail.route,
                arguments = listOf(
                    navArgument("whiskeyId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                WhiskeyDetailRoute(
                    whiskeyId = backStackEntry.arguments?.getString("whiskeyId") ?: "",
                    onAddNote = {
                        navController.navigateToAddWhiskeyNote(
                            backStackEntry.arguments?.getString("whiskeyId") ?: ""
                        )
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable<AddWhiskey> {
                AddWhiskeyRoute(
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable<AddWhiskeyNote> { navBackStackEntry: NavBackStackEntry ->
                val addNote: AddWhiskeyNote = navBackStackEntry.toRoute()
                AddWhiskeyNoteRoute(
                    whiskeyId = addNote.whiskeyId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(
                route = Screen.NoteDetail.route,
                arguments = listOf(
                    navArgument("noteId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                NoteDetailRoute(
                    id = backStackEntry.arguments?.getString("noteId") ?: "",
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                    onClickAddNote = {}
                )
            }
            composable(
                route = Screen.EditNote.route,
                arguments = listOf(
                    navArgument("noteId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                NewThreadScreen(
                    // onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.Settings.route) {
//                SettingsScreen(
//                    onNavigateBack = { navController.navigateUp() }
//                )
            }
        }
    }
}

// NavController extension functions
fun NavController.navigateToNoteDetail(id: String) {
    navigate(route = NoteDetail(id))
}

fun NavController.navigateToWhiskeyDetail(id: String) {
    navigate(route = WhiskeyDetail(id))
}

fun NavController.navigateToAddNote() {
    navigate(route = AddNote)
}

fun NavController.navigateToAddWhiskeyNote(whiskeyId: String) {
    navigate(route = AddWhiskeyNote(whiskeyId))
}

fun NavController.navigateToAddWhiskey() {
    navigate(route = AddWhiskey)
}

// NavGraphBuilder extension functions
fun NavGraphBuilder.feedDestination() {
    composable<Feed> {
        FeedScreen(
            onWhiskeyClick = { id -> /* TODO: Navigate to whiskey detail */ },
            onAddWhiskeyClick = { /* TODO: Navigate to add whiskey */ }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.noteDestination(
    navigateToNoteDetail: (String) -> Unit,
    navigateToWhiskeyDetail: (String) -> Unit,
    navigateToAddWhiskey: () -> Unit,
    navigateToAddNote: () -> Unit,
    navigateToAddWhiskeyNote: (String) -> Unit,
    onNavigateBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    composable<MyNoteList> {
        NoteListRoute(
            navigateToWhiskeyDetail = navigateToWhiskeyDetail,
            navigateToAddWhiskey = navigateToAddWhiskey,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable,
        )
    }
    composable<NoteDetail> { navBackStackEntry: NavBackStackEntry ->
        val detail: NoteDetail = navBackStackEntry.toRoute()
        NoteDetailRoute(
            id = detail.id,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this@composable,
            onClickAddNote = navigateToAddNote
        )
    }
    composable<WhiskeyDetail> { navBackStackEntry: NavBackStackEntry ->
        val detail: WhiskeyDetail = navBackStackEntry.toRoute()
        navBackStackEntry.savedStateHandle["id"] = detail.id
        WhiskeyDetailRoute(
            whiskeyId = detail.id,
            onAddNote = { navigateToAddWhiskeyNote(detail.id) },
            onNavigateBack = onNavigateBack,
            modifier = Modifier.fillMaxSize()
        )
    }
    composable<AddNote> {
        AddWhiskeyRoute(
            onNavigateBack = onNavigateBack
        )
    }
    composable<AddWhiskeyNote> { navBackStackEntry: NavBackStackEntry ->
        val addNote: AddWhiskeyNote = navBackStackEntry.toRoute()
        AddWhiskeyNoteRoute(
            whiskeyId = addNote.whiskeyId,
            onNavigateBack = onNavigateBack
        )
    }
    composable<AddWhiskey> {
        AddWhiskeyScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavGraphBuilder.searchDestination() {
    composable<Search> {
        SearchScreen(
            onWhiskeyClick = { id -> /* TODO: Navigate to whiskey detail */ }
        )
    }
}

// Route Composables
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
fun AddWhiskeyRoute(
    onNavigateBack: () -> Unit
) {
    AddWhiskeyScreen(
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun AddWhiskeyNoteRoute(
    whiskeyId: String,
    onNavigateBack: () -> Unit
) {
    AddWhiskeyNoteScreen(
        whiskeyId = whiskeyId,
        onNavigateBack = onNavigateBack
    )
}


@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        val items = listOf(
            Screen.Social,
            Screen.Note,
            Screen.Evolution,
            Screen.Search,
        )

        items.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = null,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}