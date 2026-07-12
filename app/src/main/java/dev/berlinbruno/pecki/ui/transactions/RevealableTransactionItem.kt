package dev.berlinbruno.pecki.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.Transaction
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.ui.theme.CornerRadius
import dev.berlinbruno.pecki.ui.theme.Spacing
import dev.berlinbruno.pecki.utils.formatCompactCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// Swipe action dimensions
private const val EDIT_DELETE_ACTION_WIDTH = 160
private const val DISAPPROVE_ACTION_WIDTH = 80

// Animation constants
private const val DRAG_THRESHOLD = 0.5f
private const val VELOCITY_THRESHOLD = 100

// Elevation constants
private const val ITEM_ELEVATION = 1
private const val SELECTED_ELEVATION = 4

// Vibrant Colors
private val VibrantGreen = Color(0xFF00C853)
private val VibrantRed = Color(0xFFD50000)

enum class DragValue {
    START,
    CENTER,
    END
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RevealableTransactionItem(
    transaction: Transaction,
    categories: List<Category>,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onLongClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onDisapprove: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val editDeleteActionWidthPx = with(density) { -EDIT_DELETE_ACTION_WIDTH.dp.toPx() }
    val disapproveActionWidthPx = with(density) { DISAPPROVE_ACTION_WIDTH.dp.toPx() }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.CENTER,
            positionalThreshold = { distance: Float -> distance * DRAG_THRESHOLD },
            velocityThreshold = { with(density) { VELOCITY_THRESHOLD.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec
        )
    }

    LaunchedEffect(state, isSelectionMode) {
        if (isSelectionMode) {
            state.animateTo(DragValue.CENTER)
        }
        state.updateAnchors(
            DraggableAnchors {
                if (!isSelectionMode) {
                    DragValue.START at disapproveActionWidthPx
                }
                DragValue.CENTER at 0f
                if (!isSelectionMode) {
                    DragValue.END at editDeleteActionWidthPx
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(CornerRadius.medium))
    ) {
        if (!isSelectionMode) {
            SwipeActionsBackground(
                onEdit = onEdit,
                onDelete = onDelete,
                onDisapprove = onDisapprove,
                state = state,
                scope = scope
            )
        }

        TransactionItemForeground(
            transaction = transaction,
            categories = categories,
            isSelected = isSelected,
            isSelectionMode = isSelectionMode,
            state = state,
            onClick = onClick,
            onLongClick = onLongClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeActionsBackground(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDisapprove: () -> Unit,
    state: AnchoredDraggableState<DragValue>,
    scope: CoroutineScope
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Disapprove Action (Left side, revealed by swiping right)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(DISAPPROVE_ACTION_WIDTH.dp)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .clickable {
                    scope.launch { state.animateTo(DragValue.CENTER) }
                    onDisapprove()
                }
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Block,
                contentDescription = "Disapprove",
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        // Edit and Delete Actions (Right side, revealed by swiping left)
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .width(EDIT_DELETE_ACTION_WIDTH.dp)
                .align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.End
        ) {
            // Edit Action
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        scope.launch { state.animateTo(DragValue.CENTER) }
                        onEdit()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Delete Action
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .clickable {
                        scope.launch { state.animateTo(DragValue.CENTER) }
                        onDelete()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TransactionItemForeground(
    transaction: Transaction,
    categories: List<Category>,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    state: AnchoredDraggableState<DragValue>,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val cardShape = remember(state.offset) {
        val offset = if (state.offset.isNaN()) 0f else state.offset
        val corner = CornerRadius.medium
        when {
            offset > 1f -> RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = corner, bottomEnd = corner)
            offset < -1f -> RoundedCornerShape(topStart = corner, bottomStart = corner, topEnd = 0.dp, bottomEnd = 0.dp)
            else -> RoundedCornerShape(corner)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = if (state.offset.isNaN()) 0 else state.offset.roundToInt(),
                    y = 0
                )
            }
            .anchoredDraggable(state, Orientation.Horizontal, enabled = !isSelectionMode)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) SELECTED_ELEVATION.dp else ITEM_ELEVATION.dp,
        shape = cardShape
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = isSelectionMode,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    modifier = Modifier.padding(end = Spacing.medium)
                )
            }
            TransactionItemContent(transaction, categories)
        }
    }
}

@Composable
private fun TransactionItemContent(
    transaction: Transaction,
    categories: List<Category>
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yy", Locale.getDefault()) }
    val category = categories.find { it.id == transaction.categoryId }
    val isCredit = transaction.type == TransactionType.CREDIT
    
    val categoryColor = category?.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Transaction Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isCredit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = categoryColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(Spacing.medium))

        // Details (Title & Category)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = category?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Other",
                style = MaterialTheme.typography.labelSmall,
                color = categoryColor
            )
        }

        Spacer(modifier = Modifier.width(Spacing.small))

        // Amount & Date
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatCompactCurrency(transaction.amount, transaction.currency),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isCredit) VibrantGreen else VibrantRed,
                maxLines = 1
            )
            Text(
                text = dateFormat.format(Date(transaction.dateTime)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
