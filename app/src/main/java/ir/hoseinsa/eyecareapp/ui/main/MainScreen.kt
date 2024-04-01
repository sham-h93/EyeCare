package ir.hoseinsa.eyecareapp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ir.hoseinsa.eyecareapp.ui.components.EyeCareTopBar
import ir.hoseinsa.eyecareapp.ui.theme.EyeCareAppTheme

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EyeCareTopBar(title = "Eye Care")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    EyeCareAppTheme {
        MainScreen()
    }
}