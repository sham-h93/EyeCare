package example.hotaku.eyecareapp.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import example.hotaku.eyecareapp.ui.theme.EyeCareAppTheme
import example.hotaku.eyecareapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EyeCareTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationIconClick: () -> Unit = {},
    actions: (@Composable RowScope.() -> Unit)? = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text =title )
        },
        modifier = modifier.fillMaxWidth(),
        navigationIcon = navigationIcon?.let {
            {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        imageVector = it,
                        contentDescription = null
                    )
                }
            }
        } ?: {},
        actions = actions ?: {},
    )
}

@Preview
@Composable
private fun EyeCareTopBarPreview() {
    EyeCareAppTheme {
        EyeCareTopBar(
            title = stringResource(id = R.string.app_name),
            navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack
        )
    }
}