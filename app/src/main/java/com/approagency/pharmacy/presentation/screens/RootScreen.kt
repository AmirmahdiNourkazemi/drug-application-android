package com.approagency.pharmacy.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootScreen(navHostController: NavHostController, modifier: Modifier){
    val pages = listOf(
       "دارو" to Icons.Default.Search,
        "آزمایش" to Icons.Default.Build,
        "سرچ" to Icons.Rounded.Refresh
    )
    var seletedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold (
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
                topBar = { CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl){
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("دستیار سلامت" , textAlign = TextAlign.Right , style = MaterialTheme.typography.titleLarge , fontWeight = FontWeight.W700 )
                }
            )
        }},
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp).padding(bottom = 2.dp).clip(MaterialTheme.shapes.extraLarge),
                tonalElevation = 8.dp, // subtle shadow
                windowInsets = WindowInsets.navigationBars
                    .only(WindowInsetsSides.Bottom)
            ) {
                pages.forEachIndexed { index, item ->
                    val selected = seletedTab == index

                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        selected = selected,
                        onClick = { seletedTab = index },

                        icon = {
                            Icon(
                                imageVector = item.second,
                                contentDescription = item.first,
                                modifier = Modifier.size(16.dp)
                            )
                        },

                        label = {
                            Text(
                                item.first,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },

                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) {
            padding -> when (seletedTab) {
        0 -> HomeScreen( navController = navHostController,Modifier.padding(padding).consumeWindowInsets(padding))
        1 -> LabScreen(Modifier.padding(padding))
        2 -> SearchScreen(
            navHostController,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
        )
    }
    }
}
