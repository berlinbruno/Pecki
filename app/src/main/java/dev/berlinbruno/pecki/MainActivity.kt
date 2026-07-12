package dev.berlinbruno.pecki

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import dev.berlinbruno.pecki.ui.security.PinEntryScreen
import dev.berlinbruno.pecki.ui.security.PinViewModel
import dev.berlinbruno.pecki.ui.security.SecurityViewModel
import dev.berlinbruno.pecki.ui.theme.Elevation
import dev.berlinbruno.pecki.ui.theme.PeckiTheme
import dev.berlinbruno.pecki.ui.theme.Spacing
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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

@Composable
fun MainAppScaffold(
    rootNavController: NavHostController,
    securityViewModel: SecurityViewModel = hiltViewModel(),
    pinViewModel: PinViewModel = hiltViewModel()
) {
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
                composable<Screen.Settings> {
                    SettingsScreen(
                        securityViewModel = securityViewModel,
                        pinViewModel = pinViewModel,
                        onMenuClick = openDrawer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    securityViewModel: SecurityViewModel,
    pinViewModel: PinViewModel,
    onMenuClick: () -> Unit
) {
    val prefs by securityViewModel.securityPreferences.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPinSheet by remember { mutableStateOf(false) }
    var showTimeoutSheet by remember { mutableStateOf(false) }

    if (showPinSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPinSheet = false },
            sheetState = sheetState,
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = Elevation.level0
        ) {
            PinEntryScreen(
                title = "Setup PIN",
                subtitle = "Create a 4-digit PIN",
                viewModel = pinViewModel,
                isSetup = true,
                onSuccess = { showPinSheet = false }
            )
        }
    }

    if (showTimeoutSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimeoutSheet = false },
            sheetState = sheetState,
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = Elevation.level0
        ) {
            AutoLockTimeoutSheet(
                currentTimeout = prefs?.autoLockTimeoutMs ?: 0L,
                onTimeoutSelected = {
                    securityViewModel.setAutoLockTimeout(it)
                    showTimeoutSheet = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Settings",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            // Appearance Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = Elevation.level1),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(vertical = Spacing.medium)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Column {
                            Text(
                                text = "Appearance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Customize the app's look and feel",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.medium))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.medium))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = Spacing.small)
                        ) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(Spacing.medium))
                            Text(
                                text = "Theme",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        val options = listOf("System", "Light", "Dark")
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Spacing.small)
                        ) {
                            options.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                                    onClick = { securityViewModel.setThemeMode(index) },
                                    selected = (prefs?.themeMode ?: 0) == index,
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }
            }

            // Security Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = Elevation.level1),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(vertical = Spacing.medium)) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Column {
                            Text(
                                text = "App Security",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Keep your financial data private and secure",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.medium))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.medium))

                    // Enable PIN
                    ListItem(
                        headlineContent = { Text("Enable PIN Lock") },
                        supportingContent = { Text("Protect access with a 4-digit code") },
                        leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingContent = {
                            Switch(
                                checked = prefs?.securityEnabled == true,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        showPinSheet = true
                                    } else {
                                        securityViewModel.disableSecurity()
                                    }
                                }
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )

                    if (prefs?.securityEnabled == true) {
                        // Enable Biometric
                        ListItem(
                            headlineContent = { Text("Biometric Unlock") },
                            supportingContent = { Text("Use fingerprint or face recognition") },
                            leadingContent = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = prefs?.biometricEnabled == true,
                                    onCheckedChange = { securityViewModel.setBiometricEnabled(it) }
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )

                        // Change PIN
                        ListItem(
                            headlineContent = { Text("Change PIN") },
                            leadingContent = { Icon(Icons.Default.Password, contentDescription = null) },
                            modifier = Modifier.clickable { showPinSheet = true },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )

                        // Auto-lock
                        ListItem(
                            headlineContent = { Text("Auto-lock") },
                            supportingContent = {
                                val timeoutText = when (prefs?.autoLockTimeoutMs) {
                                    0L -> "Immediately"
                                    30000L -> "After 30 seconds"
                                    60000L -> "After 1 minute"
                                    300000L -> "After 5 minutes"
                                    else -> "Immediately"
                                }
                                Text(timeoutText)
                            },
                            leadingContent = { Icon(Icons.Default.Timer, contentDescription = null) },
                            modifier = Modifier.clickable { showTimeoutSheet = true },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )

                        Spacer(modifier = Modifier.height(Spacing.small))

                        Button(
                            onClick = { securityViewModel.lockNow() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.medium),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Text("Lock Now")
                        }
                    }
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

@Composable
fun AutoLockTimeoutSheet(
    currentTimeout: Long,
    onTimeoutSelected: (Long) -> Unit
) {
    val options = listOf(
        0L to "Immediately",
        30000L to "After 30 seconds",
        60000L to "After 1 minute",
        300000L to "After 5 minutes"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.medium)
    ) {
        Text(
            text = "Auto-lock Timeout",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(Spacing.medium)
        )
        options.forEach { (timeout, label) ->
            ListItem(
                headlineContent = { 
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge
                    ) 
                },
                trailingContent = {
                    RadioButton(
                        selected = currentTimeout == timeout,
                        onClick = null
                    )
                },
                modifier = Modifier.clickable { onTimeoutSelected(timeout) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.large))
    }
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
