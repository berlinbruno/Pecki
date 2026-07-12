package dev.berlinbruno.pecki.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.berlinbruno.pecki.data.security.SecurityPreferences
import dev.berlinbruno.pecki.ui.components.settings.*
import dev.berlinbruno.pecki.ui.security.PinEntryScreen
import dev.berlinbruno.pecki.ui.security.PinViewModel
import dev.berlinbruno.pecki.ui.security.SecurityViewModel
import dev.berlinbruno.pecki.ui.theme.Elevation
import dev.berlinbruno.pecki.ui.theme.Spacing
import dev.berlinbruno.pecki.ui.transactions.management.CategoryForm
import dev.berlinbruno.pecki.ui.transactions.management.CategoryManagementViewModel
import dev.berlinbruno.pecki.ui.transactions.management.ModeForm
import dev.berlinbruno.pecki.ui.transactions.management.ModeManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    securityViewModel: SecurityViewModel,
    pinViewModel: PinViewModel,
    settingsViewModel: SettingsViewModel,
    categoryViewModel: CategoryManagementViewModel = hiltViewModel(),
    modeViewModel: ModeManagementViewModel = hiltViewModel(),
    onMenuClick: () -> Unit
) {
    val prefs by securityViewModel.securityPreferences.collectAsState()
    val categories by settingsViewModel.categories.collectAsState()
    val modes by settingsViewModel.modes.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPinSheet by remember { mutableStateOf(false) }
    var showTimeoutSheet by remember { mutableStateOf(false) }

    // Forms
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    var showCategorySheet by remember { mutableStateOf(false) }
    
    val modeUiState by modeViewModel.uiState.collectAsState()
    var showModeSheet by remember { mutableStateOf(false) }

    LaunchedEffect(categoryUiState.isSaved) {
        if (categoryUiState.isSaved) {
            showCategorySheet = false
            categoryViewModel.clearSaved()
        }
    }

    LaunchedEffect(modeUiState.isSaved) {
        if (modeUiState.isSaved) {
            showModeSheet = false
            modeViewModel.clearSaved()
        }
    }

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

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showCategorySheet = false 
                categoryViewModel.resetState()
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            CategoryForm(viewModel = categoryViewModel)
        }
    }

    if (showModeSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showModeSheet = false 
                modeViewModel.resetState()
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            ModeForm(viewModel = modeViewModel)
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
                .padding(Spacing.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            AppearanceCard(prefs = prefs, securityViewModel = securityViewModel)

            SecurityCard(
                prefs = prefs, 
                securityViewModel = securityViewModel, 
                onPinToggle = { enabled ->
                    if (enabled) showPinSheet = true else securityViewModel.disableSecurity()
                },
                onTimeoutClick = { showTimeoutSheet = true },
                onChangePinClick = { showPinSheet = true }
            )

            var showResetCategoriesConfirm by remember { mutableStateOf(false) }
            CategoryManagementCard(
                categories = categories,
                onAddCategoryClick = {
                    categoryViewModel.resetState()
                    showCategorySheet = true
                },
                onEditCategory = { category ->
                    categoryViewModel.loadCategory(category)
                    showCategorySheet = true
                },
                onDeleteCategory = { settingsViewModel.deleteCategory(it) },
                onResetCategories = { showResetCategoriesConfirm = true },
                maxCategories = CategoryManagementViewModel.MAX_CATEGORIES
            )

            var showResetModesConfirm by remember { mutableStateOf(false) }
            ModeManagementCard(
                modes = modes,
                onAddModeClick = {
                    modeViewModel.resetState()
                    showModeSheet = true
                },
                onEditMode = { mode ->
                    modeViewModel.loadMode(mode)
                    showModeSheet = true
                },
                onDeleteMode = { settingsViewModel.deleteMode(it) },
                onResetModes = { showResetModesConfirm = true },
                maxModes = ModeManagementViewModel.MAX_MODES
            )

            // Data Management Card
            var showClearDataConfirm by remember { mutableStateOf(false) }
            DataManagementCard(
                onResetSyncClick = { /* Logic for resetting SMS sync */ },
                onClearDataClick = { showClearDataConfirm = true }
            )

            if (showClearDataConfirm) {
                AlertDialog(
                    onDismissRequest = { showClearDataConfirm = false },
                    title = { Text("Clear All Transactions?") },
                    text = { Text("This will permanently delete every transaction record. Your categories and modes will be preserved.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                settingsViewModel.clearAllData()
                                showClearDataConfirm = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text("Clear All") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDataConfirm = false }) { Text("Cancel") }
                    }
                )
            }

            if (showResetCategoriesConfirm || showResetModesConfirm) {
                AlertDialog(
                    onDismissRequest = { 
                        showResetCategoriesConfirm = false
                        showResetModesConfirm = false 
                    },
                    title = { Text("Reset to Defaults?") },
                    text = { Text("This will restore the original system defaults. Your transactions will remain.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (showResetCategoriesConfirm) settingsViewModel.restoreDefaults()
                                if (showResetModesConfirm) settingsViewModel.restoreDefaults()
                                showResetCategoriesConfirm = false
                                showResetModesConfirm = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text("Reset") }
                    },
                    dismissButton = {
                        TextButton(onClick = { 
                            showResetCategoriesConfirm = false
                            showResetModesConfirm = false 
                        }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

@Composable
fun AppearanceCard(
    prefs: SecurityPreferences?,
    securityViewModel: SecurityViewModel
) {
    SettingsCard {
        SettingsSection(
            title = "Appearance",
            subtitle = "Customize the app's look and feel",
            icon = Icons.Default.Palette
        )

        SettingsSegmentedItem(
            title = "App Theme",
            subtitle = "Choose between light, dark, or system default",
            options = listOf("System", "Light", "Dark"),
            selectedIndex = prefs?.themeMode ?: 0,
            onOptionSelected = { securityViewModel.setThemeMode(it) }
        )

        SettingsSegmentedItem(
            title = "Time Range",
            subtitle = "Default period for transaction insights",
            options = listOf("This Month", "Last 30 Days"),
            selectedIndex = prefs?.timeRangeType ?: 0,
            onOptionSelected = { securityViewModel.setTimeRangeType(it) }
        )

        SettingsNavigationItem(
            title = "Currency",
            subtitle = "Base currency for all accounts (${prefs?.currencyCode ?: "€"})",
            onClick = { /* TODO: Show Currency Picker */ }
        )
        
        Spacer(modifier = Modifier.height(Spacing.small))
    }
}

@Composable
fun SecurityCard(
    prefs: SecurityPreferences?,
    securityViewModel: SecurityViewModel,
    onPinToggle: (Boolean) -> Unit,
    onTimeoutClick: () -> Unit,
    onChangePinClick: () -> Unit
) {
    SettingsCard {
        SettingsSection(
            title = "App Security",
            subtitle = "Keep your financial data private and secure",
            icon = Icons.Default.Security
        )

        SettingsSwitchItem(
            title = "Enable PIN Lock",
            subtitle = "Protect access with a 4-digit code",
            checked = prefs?.securityEnabled == true,
            onCheckedChange = onPinToggle
        )

        if (prefs?.securityEnabled == true) {
            SettingsSwitchItem(
                title = "Biometric Unlock",
                subtitle = "Use fingerprint or face recognition",
                checked = prefs.biometricEnabled,
                onCheckedChange = { securityViewModel.setBiometricEnabled(it) }
            )

            val timeoutText = when (prefs.autoLockTimeoutMs) {
                0L -> "Immediately"
                30000L -> "After 30 seconds"
                60000L -> "After 1 minute"
                300000L -> "After 5 minutes"
                else -> "Immediately"
            }
            SettingsNavigationItem(
                title = "Auto-lock",
                subtitle = timeoutText,
                onClick = onTimeoutClick
            )

            SettingsButtonItem(
                title = "Change PIN",
                subtitle = "Update your 4-digit security code",
                buttonText = "Change",
                onClick = onChangePinClick
            )

            Button(
                onClick = { securityViewModel.lockNow() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(Spacing.small))
                Text("Lock Now")
            }
        }
    }
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
