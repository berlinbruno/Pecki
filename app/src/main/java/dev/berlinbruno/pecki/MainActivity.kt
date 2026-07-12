package dev.berlinbruno.pecki

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.berlinbruno.pecki.ui.navigation.Screen
import dev.berlinbruno.pecki.ui.theme.PeckiTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PeckiTheme {
                PeckiApp()
            }
        }
    }
}

enum class DrawerDestinations(
    val label: String,
    val icon: ImageVector,
    val route: Any,
) {
    INVESTMENTS("Investments", Icons.AutoMirrored.Filled.TrendingUp, Screen.Investments),
    BUDGETS("Budgets", Icons.Default.AccountBalanceWallet, Screen.Budgets),
    APPROVE_TRANSACTIONS("Approve Transactions", Icons.Default.CheckCircle, Screen.ApproveTransactions),
    SETTINGS("Settings", Icons.Default.Settings, Screen.Settings),
}

enum class HomeTabs(
    val label: String,
    val icon: ImageVector,
    val route: Any,
) {
    HOME("Home", Icons.Default.Home, Screen.Home),
    INSIGHTS("Insights", Icons.Default.PieChart, Screen.Reports),
    TRANSACTIONS("Transactions", Icons.AutoMirrored.Filled.List, Screen.Transactions),
}

@Composable
fun PeckiApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            MainAppScaffold(navController)
        }
        // Other top-level screens if they shouldn't have bottom bar
    }
}

@Composable
fun MainAppScaffold(rootNavController: NavHostController) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navigateToScreen: (Any) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { drawerState.close() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                onItemClick = { route ->
                    closeDrawer()
                    navigateToScreen(route)
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    onNavigate = navigateToScreen
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                composable<Screen.Home> { MainScreen("Dashboard", onMenuClick = openDrawer) }
                composable<Screen.Reports> { MainScreen("Reports", onMenuClick = openDrawer) }
                composable<Screen.Transactions> { MainScreen("Transactions", onMenuClick = openDrawer) }
                composable<Screen.Investments> { MainScreen("Investments", onMenuClick = openDrawer) }
                composable<Screen.Budgets> { MainScreen("Budgets", onMenuClick = openDrawer) }
                composable<Screen.ApproveTransactions> { MainScreen("Approve Transactions", onMenuClick = openDrawer) }
                composable<Screen.Settings> { MainScreen("Settings", onMenuClick = openDrawer) }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    navController: NavHostController,
    onItemClick: (Any) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    ModalDrawerSheet {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pecki",
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            NavigationDrawerItem(
                label = { Text("Home") },
                selected = currentDestination?.hierarchy?.any { it.hasRoute(Screen.Home::class) } == true,
                onClick = { onItemClick(Screen.Home) },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            DrawerDestinations.entries.forEach { item ->
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label) },
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                    onClick = { onItemClick(item.route) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    onNavigate: (Any) -> Unit
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        HomeTabs.entries.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                selected = selected,
                onClick = { onNavigate(tab.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(name: String, onMenuClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Greeting(name, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PeckiTheme {
        Greeting("Android")
    }
}
