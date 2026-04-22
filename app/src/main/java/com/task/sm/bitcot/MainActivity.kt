package com.task.sm.bitcot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.task.sm.bitcot.ui.navigation.BitcotNavHost
import com.task.sm.bitcot.ui.theme.BitcotTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitcotTheme {
                Scaffold { innerPadding ->
                    BitcotNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}