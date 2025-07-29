package hnau.dynamicschemetest.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import hnau.common.app.model.theme.ThemeBrightness
import hnau.common.app.projector.uikit.utils.Dimens
import hnau.common.app.projector.utils.collectAsMutableAccessor
import hnau.common.app.projector.utils.horizontalDisplayPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow


class RootProjector(
    scope: CoroutineScope,
    private val model: RootModel,
) {

    @Composable
    fun Content() {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .width(512.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(Dimens.separation)
            ) {
                Fraction(
                    title = "Hue",
                    value = model.hue,
                    modifier = Modifier.horizontalDisplayPadding().fillMaxWidth(),
                )
                Fraction(
                    title = "Chroma",
                    value = model.chroma,
                    modifier = Modifier.horizontalDisplayPadding().fillMaxWidth(),
                )
                Fraction(
                    title = "Contrast level",
                    value = model.contrastLevel,
                    modifier = Modifier.horizontalDisplayPadding().fillMaxWidth(),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.separation),
                    modifier = Modifier.horizontalDisplayPadding().fillMaxWidth(),
                ) {
                    ThemeBrightness
                        .entries
                        .fastForEach { brightness ->
                            Fraction(
                                title = "Tone for ${brightness.name}",
                                value = model.tones[brightness],
                                modifier = Modifier.weight(1f),
                            )
                        }
                }
            }
            Schemes(
                model = model,
            )
        }
    }

    @Composable
    private fun Title(
        text: String,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
        )
    }

    @Composable
    private fun Fraction(
        modifier: Modifier = Modifier,
        title: String,
        value: MutableStateFlow<Float>,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.smallSeparation),
            modifier = modifier,
        ) {
            Title(
                text = title,
            )
            var current by value.collectAsMutableAccessor()
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = current,
                onValueChange = { current = it }
            )
        }
    }


}