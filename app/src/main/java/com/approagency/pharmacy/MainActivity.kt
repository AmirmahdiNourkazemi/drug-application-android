package com.approagency.pharmacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import com.approagency.pharmacy.navigation.AppNavGraph
import com.approagency.pharmacy.ui.theme.DrugTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrugTheme {

                AppNavGraph(
//                        navController = navController,
//                        modifier = Modifier.padding(innerPadding)
                )
//                Scaffold(modifier = Modifier.fillMaxSize(),
//                    ) { innerPadding ->
//                    AppNavGraph(
////                        navController = navController,
////                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
            }
        }
    }
}
