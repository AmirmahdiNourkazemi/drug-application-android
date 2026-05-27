package com.approagency.drug.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavHostController
) {
    val items = listOf(
        Triple(MainRoute.HomeGraph, "دارو", Icons.Default.Search),
        Triple(MainRoute.SearchGraph, "سرچ", Icons.Rounded.Refresh),
        Triple(MainRoute.LabGraph, "آزمایش", Icons.Default.Build)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Surface(
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .navigationBarsPadding()
            .height(50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { item ->

                val selected = currentDestination
                    ?.hierarchy
                    ?.any { it.route == item.first.route } == true

                val color = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant

                val background = if (selected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else
                    Color.Transparent

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp , vertical = 4.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(background)
                        .clickable {
                            navController.navigate(item.first.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = item.third,
                            contentDescription = item.second,
                            tint = color,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = item.second,
                            color = color,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}