package dev.berlinbruno.pecki

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
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
import dev.berlinbruno.pecki.ui.security.LockScreen
import dev.berlinbruno.pecki.ui.security.PinViewModel
import dev.berlinbruno.pecki.ui.security.SecurityViewModel
import dev.berlinbruno.pecki.ui.settings.SettingsScreen
import dev.berlinbruno.pecki.ui.settings.SettingsViewModel
import dev.berlinbruno.pecki.ui.transactions.CreateTransactionScreen
import dev.berlinbruno.pecki.ui.transactions.CreateTransactionViewModel
import dev.berlinbruno.pecki.ui.transactions.TransactionListScreen
import dev.berlinbruno.pecki.ui.transactions.management.CategoryManagementScreen
import dev.berlinbruno.pecki.ui.transactions.management.ModeManagementScreen
import dev.berlinbruno.pecki.ui.theme.Elevation
import dev.berlinbruno.pecki.ui.theme.PeckiTheme
import dev.berlinbruno.pecki.ui.theme.Spacing
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val securityViewModel: SecurityViewModel = hiltViewModel()
            val prefs by securityViewModel.securityPreferences.collectAsState(initial = null)

            val darkTheme = when (prefs?.themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            PeckiTheme(darkTheme = darkTheme) {
                PeckiApp(securityViewModel = securityViewModel)
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
fun PeckiApp(
    securityViewModel: SecurityViewModel = hiltViewModel(),
    pinViewModel: PinViewModel = hiltViewModel()
) {
    val isUnlocked by securityViewModel.isUnlocked.collectAsState()
    val prefs by securityViewModel.securityPreferences.collectAsState()

    if (prefs == null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        return
    }

    if (prefs?.securityEnabled == true && !isUnlocked) {
        LockScreen(securityViewModel, pinViewModel)
    } else {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Screen.Home
        ) {
            composable<Screen.Home> {
                MainAppScaffold(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    rootNavController: NavHostController,
    securityViewModel: SecurityViewModel = hiltViewModel(),
    pinViewModel: PinViewModel = hiltViewModel(),
    createTransactionViewModel: CreateTransactionViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val addTransactionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddTransactionSheet by remember { mutableStateOf(false) }

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
            if (showAddTransactionSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showAddTransactionSheet = false },
                    sheetState = addTransactionSheetState,
                    shape = MaterialTheme.shapes.extraLarge,
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = Elevation.level0
                ) {
                    CreateTransactionScreen(
                        viewModel = createTransactionViewModel,
                        onBackClick = {
                            scope.launch { addTransactionSheetState.hide() }.invokeOnCompletion {
                                if (!addTransactionSheetState.isVisible) {
                                    showAddTransactionSheet = false
                                }
                            }
                        }
                    )
                }
            }

            NavHost(
                navController = navController,
                startDestination = Screen.Home,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                composable<Screen.Home> { MainScreen("Dashboard", onMenuClick = openDrawer) }
                composable<Screen.Reports> { MainScreen("Reports", onMenuClick = openDrawer) }
                composable<Screen.Transactions> {
                    TransactionListScreen(
                        onMenuClick = openDrawer,
                        onAddTransactionClick = {
                            createTransactionViewModel.resetState()
                            showAddTransactionSheet = true
                        },
                        onEditTransactionClick = { transaction ->
                            createTransactionViewModel.loadTransaction(transaction)
                            showAddTransactionSheet = true
                        }
                    )
                }
                composable<Screen.Investments> { MainScreen("Investments", onMenuClick = openDrawer) }
                composable<Screen.Budgets> { MainScreen("Budgets", onMenuClick = openDrawer) }
                composable<Screen.ApproveTransactions> { MainScreen("Approve Transactions", onMenuClick = openDrawer) }
                composable<Screen.Settings> {
                    SettingsScreen(
                        securityViewModel = securityViewModel,
                        pinViewModel = pinViewModel,
                        settingsViewModel = settingsViewModel,
                        onMenuClick = openDrawer
                    )
                }
                composable<Screen.ManageCategories> {
                    CategoryManagementScreen(onBackClick = { navController.popBackStack() })
                }
                composable<Screen.ManageModes> {
                    ModeManagementScreen(onBackClick = { navController.popBackStack() })
                }
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
                title = {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@PreviewScreenSizes
@Composable
fun PeckiAppPreview() {
    PeckiTheme {
        PeckiApp()
    }
}
