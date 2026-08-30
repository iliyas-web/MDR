package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TimetableSlotEntity
import com.example.data.model.CurrentUser
import com.example.data.model.Role
import com.example.data.repository.ClashCheckResult
import com.example.ui.theme.AcademiaBluePrimary
import com.example.ui.theme.AcademiaElectricBlue
import com.example.ui.theme.AcademiaError
import com.example.ui.theme.AcademiaSkyBlue
import com.example.ui.theme.AcademiaSuccess
import com.example.ui.theme.AcademiaWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademiaTopBar(
    user: CurrentUser,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onSwitchRole: (Role) -> Unit,
    onLogout: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AcademiaBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "App Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MDR 1225 TECH",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AcademiaElectricBlue,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "ACADEMIA &bull; ${user.role.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            // Role Switcher Button
            Box {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (user.role) {
                        Role.ADMIN -> AcademiaBluePrimary.copy(alpha = 0.15f)
                        Role.STAFF -> AcademiaSuccess.copy(alpha = 0.15f)
                        Role.STUDENT -> AcademiaWarning.copy(alpha = 0.15f)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showRoleMenu = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("role_switcher_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = user.role.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = when (user.role) {
                                Role.ADMIN -> AcademiaElectricBlue
                                Role.STAFF -> AcademiaSuccess
                                Role.STUDENT -> AcademiaWarning
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Role",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showRoleMenu,
                    onDismissRequest = { showRoleMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Admin (Principal/Dean)") },
                        onClick = {
                            showRoleMenu = false
                            onSwitchRole(Role.ADMIN)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Staff (Dr. R. Vignesh)") },
                        onClick = {
                            showRoleMenu = false
                            onSwitchRole(Role.STAFF)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Student (Aarav Sharma - CSE)") },
                        onClick = {
                            showRoleMenu = false
                            onSwitchRole(Role.STUDENT)
                        }
                    )
                }
            }

            // Theme Toggle
            IconButton(
                onClick = onToggleDarkTheme,
                modifier = Modifier.testTag("theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Logout
            IconButton(
                onClick = onLogout,
                modifier = Modifier.testTag("logout_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ClashConflictAlertDialog(
    clash: ClashCheckResult,
    onDismiss: () -> Unit,
    onForceSchedule: () -> Unit
) {
    if (clash is ClashCheckResult.NoClash) return

    val (title, description, existingSlot) = when (clash) {
        is ClashCheckResult.StaffConflict -> Triple(
            "Faculty Schedule Conflict!",
            "Faculty member '${clash.staffName}' is already occupied during Period ${clash.existingSlot.periodNumber} in ${clash.existingSlot.roomNumber} teaching ${clash.existingSlot.subjectCode} (${clash.existingSlot.department} Sem ${clash.existingSlot.semester}).",
            clash.existingSlot
        )
        is ClashCheckResult.RoomConflict -> Triple(
            "Room Booking Conflict!",
            "Hall '${clash.roomNumber}' is already reserved during Period ${clash.existingSlot.periodNumber} for ${clash.existingSlot.subjectCode} by ${clash.existingSlot.staffName} (${clash.existingSlot.department} Sec ${clash.existingSlot.section}).",
            clash.existingSlot
        )
        is ClashCheckResult.ClassConflict -> Triple(
            "Class Timetable Overlap!",
            "Class '${clash.classDesc}' already has a lecture scheduled in Period ${clash.existingSlot.periodNumber} (${clash.existingSlot.subjectCode} - ${clash.existingSlot.subjectName} in ${clash.existingSlot.roomNumber}).",
            clash.existingSlot
        )
        else -> Triple("Conflict Detected", "A scheduling conflict exists.", null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = AcademiaError,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = AcademiaError
            )
        },
        text = {
            Column {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = AcademiaError.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Auto-Clash Prevention Policy: MDR 1225 TECH requires unique faculty, room, and student slots to ensure zero timetable clashes.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("clash_resolve_dismiss_button")
            ) {
                Text("Pick Another Slot / Room")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onForceSchedule,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AcademiaError),
                modifier = Modifier.testTag("clash_override_button")
            ) {
                Text("Override Anyway")
            }
        }
    )
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(actionLabel)
            }
        }
    }
}
