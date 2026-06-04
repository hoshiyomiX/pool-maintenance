package com.poolmaintenance.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.poolmaintenance.app.R
import com.poolmaintenance.app.ui.icons.AppIcons
import com.poolmaintenance.app.ui.map.MapScreen
import com.poolmaintenance.app.ui.reminder.ReminderScreen
import com.poolmaintenance.app.ui.stats.StatsScreen

sealed class Screen(val route: String) {
    data object Reminder : Screen("reminder")
    data object Map : Screen("map")
    data object Stats : Screen("stats")
}

data class BottomNavItem(
    val screen: Screen,
    val labelResId: Int,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Reminder, R.string.tab_reminder, Icons.Filled.Notifications),
    BottomNavItem(Screen.Map, R.string.tab_map, AppIcons.Map),
    BottomNavItem(Screen.Stats, R.string.tab_stats, AppIcons.BarChart)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelResId)) },
                        label = { Text(stringResource(item.labelResId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Reminder.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Reminder.route) {
                ReminderScreen()
            }
            composable(Screen.Map.route) {
                MapScreen()
            }
            composable(Screen.Stats.route) {
                StatsScreen()
            }
        }
    }
}
