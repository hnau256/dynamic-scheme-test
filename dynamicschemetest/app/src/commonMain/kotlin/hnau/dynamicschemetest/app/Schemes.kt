package hnau.dynamicschemetest.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import hnau.common.app.model.theme.ThemeBrightness
import hnau.common.app.projector.uikit.utils.Dimens

@Composable
fun RowScope.Schemes(
    model: RootModel,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(Dimens.chipsSeparation),
    ) {
        ThemeBrightness
            .entries
            .fastForEach {brightness ->
                val schemesByVariants by model
                    .schemes[brightness]
                    .collectAsState()
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.chipsSeparation),
                ) {
                    schemesByVariants.fastForEach {(variant, scheme) ->
                        MaterialTheme(
                            colorScheme = scheme,
                        ) {
                            Scheme(
                                title = variant.name,
                            )
                        }
                    }
                }
            }
    }
}