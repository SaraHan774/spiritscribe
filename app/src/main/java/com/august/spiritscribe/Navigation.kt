package com.august.spiritscribe

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.august.spiritscribe.ui.search.SearchRoute
import com.august.spiritscribe.ui.search.SearchScreen
import com.august.spiritscribe.ui.whiskey.AddWhiskeyScreen
import com.august.spiritscribe.ui.whiskey.WhiskeyDetailRoute
import com.august.spiritscribe.ui.profile.ProfileScreen
import com.august.spiritscribe.ui.feed.FeedScreen
import com.august.spiritscribe.ui.flavor.FlavorWheelScreen
import com.august.spiritscribe.ui.flavor.FlavorWheelViewModel
import kotlinx.serialization.Serializable

@Serializable
data object AddWhiskey

@Serializable
data object AddNote

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

@Serializable
data object Profile

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Note", MyNoteList, Icons.Filled.Create),
    TopLevelRoute("Feed", Feed, Icons.Filled.Menu),
    TopLevelRoute("Search", Search, Icons.Filled.Search),
    TopLevelRoute("Profile", Profile, Icons.Filled.AccountCircle)
)

// List of routes where bottom navigation should be hidden
val hideBottomNavigationRoutes = listOf(
    AddWhiskey::class,
    AddNote::class,
    NoteDetail::class,
    WhiskeyDetail::class
)

sealed class Screen(val route: String) {
    open val label: String = ""
    open val icon: ImageVector = Icons.Default.LocalBar
    object Feed : Screen("feed") {
        override val icon: ImageVector = Icons.Filled.Home
        override val label: String = "Feed"
    }
    
    object Search : Screen("search") {
        override val icon: ImageVector = Icons.Filled.Search
        override val label: String = "Search"
    }
    
    object Create : Screen("create") {
        override val icon: ImageVector = Icons.Filled.Add
        override val label: String = "Add Note"
    }
    
    object Profile : Screen("profile") {
        override val icon: ImageVector = Icons.Filled.Person
        override val label: String = "Profile"
    }
    
    object FlavorWheel : Screen("flavor_wheel") {
        override val icon: ImageVector = Icons.Filled.LocalBar
        override val label: String = "Flavor"
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
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = Screen.Feed.route
        ) {
            composable(Screen.Feed.route) {
                FeedScreen(
                    onNoteClick = { id ->
                        navController.navigate(Screen.NoteDetail.createRoute(id))
                    },
                    onUserClick = {}
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onWhiskeyClick = {}
                )
            }
            composable(Screen.Create.route) {
                AddNoteRoute(
//                    noteId = backStackEntry.arguments?.getString("noteId") ?: "",
//                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable(Screen.FlavorWheel.route) {
                val viewModel = hiltViewModel<FlavorWheelViewModel>()
                val flavorProfile = viewModel.flavorProfile.collectAsState()
                
                FlavorWheelScreen(
                    flavorProfile = flavorProfile.value,
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
                    onAddNote = {},
                    // onNavigateBack = { navController.navigateUp() }
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

fun NavController.navigateToAddWhiskey() {
    navigate(route = AddWhiskey)
}

// NavGraphBuilder extension functions
fun NavGraphBuilder.feedDestination() {
    composable<Feed> {
        FeedScreen(
            onNoteClick = { id -> /* TODO: Navigate to note detail */ },
            onUserClick = { userId -> /* TODO: Navigate to user profile */ }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.noteDestination(
    navigateToNoteDetail: (String) -> Unit,
    navigateToWhiskeyDetail: (String) -> Unit,
    navigateToAddWhiskey: () -> Unit,
    navigateToAddNote: () -> Unit,
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
            onAddNote = navigateToAddNote,
            modifier = Modifier.fillMaxSize()
        )
    }
    composable<AddNote> { AddNoteRoute() }
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

fun NavGraphBuilder.profileDestination() {
    composable<Profile> { ProfileRoute() }
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
fun AddNoteRoute() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NewThreadScreen()
    }
}

@Composable
fun ProfileRoute(modifier: Modifier = Modifier) {
    ProfileScreen(
        onEditProfile = { /* TODO: Implement edit profile navigation */ },
        modifier = modifier
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
            Screen.Feed,
            Screen.Search,
            Screen.Create,
            Screen.FlavorWheel,
            Screen.Profile
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