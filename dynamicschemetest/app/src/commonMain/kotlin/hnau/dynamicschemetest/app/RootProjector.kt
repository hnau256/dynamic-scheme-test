package hnau.dynamicschemetest.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import dynamiccolor.ColorSpec
import dynamiccolor.DynamicScheme
import hnau.common.app.model.theme.ThemeBrightness
import hnau.common.app.projector.uikit.utils.Dimens
import hnau.common.app.projector.utils.collectAsMutableAccessor
import hnau.common.app.projector.utils.horizontalDisplayPadding
import hnau.common.app.projector.utils.verticalDisplayPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow


class RootProjector(
    scope: CoroutineScope,
    private val model: RootModel,
) {

    @Composable
    fun Content() {
        Row(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .width(512.dp)
                    .fillMaxHeight()
                    .verticalDisplayPadding(),
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
                Choose(
                    title = "Specification version",
                    value = model.specVersion,
                    all = ColorSpec.SpecVersion.entries,
                    extractTitle = { it.name },
                    modifier = Modifier.horizontalDisplayPadding().fillMaxWidth(),
                )
                Choose(
                    title = "Platform",
                    value = model.platform,
                    all = DynamicScheme.Platform.entries,
                    extractTitle = { it.name },
                    modifier = Modifier.horizontalDisplayPadding().fillMaxWidth(),
                )
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
    private fun <T> Choose(
        title: String,
        value: MutableStateFlow<T>,
        all: List<T>,
        extractTitle: (T) -> String,
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
        ) {
            Title(
                text = title,
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                var selected by value.collectAsMutableAccessor()
                all.forEachIndexed { i, item ->
                    SegmentedButton(
                        selected = item == selected,
                        onClick = { selected = item },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = i,
                            count = all.size,
                        ),
                        label = {
                            Text(
                                text = extractTitle(item),
                            )
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun Fraction(
        title: String,
        value: SliderValue,
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
        ) {
            var current by value.value.collectAsMutableAccessor()
            Title(
                text = "$title ($current)",
            )
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = current,
                onValueChange = { current = it },
                valueRange = value.config.min..value.config.max,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = value.config.min.toString(),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = value.config.max.toString(),
                )
            }
        }
    }


}