package dev.berlinbruno.pecki.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.ui.theme.Elevation
import dev.berlinbruno.pecki.ui.theme.Spacing

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = Elevation.level1),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        content = content
    )
}

@Composable
fun SettingsSection(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    trailing: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.medium))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryManagementCard(
    categories: List<Category>,
    onAddCategoryClick: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onResetCategories: () -> Unit,
    maxCategories: Int = 40,
    modifier: Modifier = Modifier
) {
    val debitCategories = categories.filter { it.type == TransactionType.DEBIT }
    val creditCategories = categories.filter { it.type == TransactionType.CREDIT }
    val bothCategories = categories.filter { it.type == null }
    val isLimitReached = categories.size >= maxCategories

    SettingsCard(modifier = modifier) {
        SettingsSection(
            title = "Manage Categories (${categories.size}/$maxCategories)",
            subtitle = "Customize how you group your transactions",
            icon = Icons.Default.Category,
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    IconButton(
                        onClick = onResetCategories,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = "Reset", modifier = Modifier.size(20.dp))
                    }
                    FilledTonalIconButton(
                        onClick = onAddCategoryClick,
                        modifier = Modifier.size(36.dp),
                        enabled = !isLimitReached
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(20.dp),
                            tint = if (isLimitReached) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        )

        Column(modifier = Modifier.padding(start = Spacing.medium, end = Spacing.medium, bottom = Spacing.medium)) {
            if (debitCategories.isNotEmpty()) {
                CategoryTypeSection(
                    title = "Debit",
                    categories = debitCategories,
                    onRemoveCategory = onDeleteCategory,
                    onEditCategory = onEditCategory
                )
            }

            if (creditCategories.isNotEmpty()) {
                if (debitCategories.isNotEmpty()) Spacer(modifier = Modifier.height(Spacing.medium))
                CategoryTypeSection(
                    title = "Credit",
                    categories = creditCategories,
                    onRemoveCategory = onDeleteCategory,
                    onEditCategory = onEditCategory
                )
            }

            if (bothCategories.isNotEmpty()) {
                if (debitCategories.isNotEmpty() || creditCategories.isNotEmpty()) Spacer(modifier = Modifier.height(Spacing.medium))
                CategoryTypeSection(
                    title = "Both Types",
                    categories = bothCategories,
                    onRemoveCategory = onDeleteCategory,
                    onEditCategory = onEditCategory
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryTypeSection(
    title: String,
    categories: List<Category>,
    onRemoveCategory: (Category) -> Unit,
    onEditCategory: (Category) -> Unit,
    initiallyExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$title (${categories.size})",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(Spacing.small))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    categories.forEach { category ->
                        CategoryTag(
                            name = category.name,
                            color = category.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary,
                            onRemove = { onRemoveCategory(category) },
                            onEdit = { onEditCategory(category) },
                            isRemovable = true,
                            isEditable = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.small))
            }
        }
    }
}

@Composable
fun CategoryTag(
    name: String,
    color: Color,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    isRemovable: Boolean,
    isEditable: Boolean
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        contentColor = color,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = Modifier.then(if (isEditable) Modifier.clickable { onEdit() } else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(
                start = 12.dp,
                end = if (isRemovable) 4.dp else 12.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (isRemovable) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(14.dp),
                        tint = color.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModeManagementCard(
    modes: List<Mode>,
    onAddModeClick: () -> Unit,
    onEditMode: (Mode) -> Unit,
    onDeleteMode: (Mode) -> Unit,
    onResetModes: () -> Unit,
    maxModes: Int = 15,
    modifier: Modifier = Modifier
) {
    val isLimitReached = modes.size >= maxModes

    SettingsCard(modifier = modifier) {
        SettingsSection(
            title = "Payment Modes (${modes.size}/$maxModes)",
            subtitle = "Customize your payment methods",
            icon = Icons.Default.Wallet,
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    IconButton(
                        onClick = onResetModes,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = "Reset", modifier = Modifier.size(20.dp))
                    }
                    FilledTonalIconButton(
                        onClick = onAddModeClick,
                        modifier = Modifier.size(36.dp),
                        enabled = !isLimitReached
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(20.dp),
                            tint = if (isLimitReached) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.medium, end = Spacing.medium, bottom = Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            modes.forEach { mode ->
                CategoryTag(
                    name = mode.name,
                    color = MaterialTheme.colorScheme.primary,
                    onRemove = { onDeleteMode(mode) },
                    onEdit = { onEditMode(mode) },
                    isRemovable = true,
                    isEditable = true
                )
            }
        }
    }
}

@Composable
fun SettingsSegmentedItem(
    title: String,
    subtitle: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { onOptionSelected(index) },
                    selected = index == selectedIndex,
                    label = { Text(label) }
                )
            }
        }
    }
}

@Composable
fun SettingsNavigationItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = modifier.clickable { onClick() },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = modifier,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsButtonItem(
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit,
    buttonColors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(Spacing.medium))
        Button(
            onClick = onClick,
            colors = buttonColors,
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(horizontal = Spacing.medium)
        ) {
            Text(text = buttonText)
        }
    }
}

@Composable
fun DataManagementCard(
    onResetSyncClick: () -> Unit,
    onClearDataClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsCard(modifier = modifier) {
        SettingsSection(
            title = "Data Management",
            subtitle = "Maintain and clean your local database",
            icon = Icons.Default.Build
        )

        SettingsButtonItem(
            title = "Reset Sync Progress",
            subtitle = "Clear markers and re-scan message history",
            buttonText = "Reset",
            onClick = onResetSyncClick
        )

        SettingsButtonItem(
            title = "Clear All Transactions",
            subtitle = "Permanently delete all transaction records",
            buttonText = "Clear",
            onClick = onClearDataClick,
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        )
        
        Spacer(modifier = Modifier.height(Spacing.small))
    }
}
