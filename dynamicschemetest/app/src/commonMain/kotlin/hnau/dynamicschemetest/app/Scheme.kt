package hnau.dynamicschemetest.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hnau.common.app.projector.uikit.shape.HnauShape
import hnau.common.app.projector.uikit.utils.Dimens
import hnau.common.kotlin.foldBoolean

@Composable
fun RowScope.Scheme(
    title: String,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .weight(1f)
            .padding(
                horizontal = Dimens.smallSeparation,
                vertical = Dimens.extraSmallSeparation,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.smallSeparation),
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
        )
        Line(
            start = {
                Rect(
                    title = "1",
                    container = MaterialTheme.colorScheme.primary,
                    content = MaterialTheme.colorScheme.onPrimary,
                )
            },
            end = {
                Rect(
                    title = "1C",
                    container = MaterialTheme.colorScheme.primaryContainer,
                    content = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        )
        Line(
            start = {
                Rect(
                    title = "2",
                    container = MaterialTheme.colorScheme.secondary,
                    content = MaterialTheme.colorScheme.onSecondary,
                )
            },
            end = {
                Rect(
                    title = "2C",
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        )
        Line(
            start = {
                Rect(
                    title = "3",
                    container = MaterialTheme.colorScheme.tertiary,
                    content = MaterialTheme.colorScheme.onTertiary,
                )
            },
            end = {
                Rect(
                    title = "3C",
                    container = MaterialTheme.colorScheme.tertiaryContainer,
                    content = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        )
        Line(
            start = {
                Rect(
                    title = "S",
                    container = MaterialTheme.colorScheme.surface,
                    content = MaterialTheme.colorScheme.onSurface,
                )
            },
            end = {
                Rect(
                    title = "SC",
                    container = MaterialTheme.colorScheme.surfaceContainer,
                    content = MaterialTheme.colorScheme.onSurface,
                )
            }
        )
        Line(
            start = {
                Rect(
                    title = "E",
                    container = MaterialTheme.colorScheme.error,
                    content = MaterialTheme.colorScheme.onError,
                )
            },
            end = {
                Rect(
                    title = "EC",
                    container = MaterialTheme.colorScheme.errorContainer,
                    content = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        )

        Line(
            start = {
                Rect(
                    title = "O",
                    container = MaterialTheme.colorScheme.outline,
                    content = MaterialTheme.colorScheme.outline,
                    outlineContainer = true,
                )
            },
            end = {
                Rect(
                    title = "I",
                    container = MaterialTheme.colorScheme.inverseSurface,
                    content = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        )
    }
}

@Composable
private fun ColumnScope.Line(
    start: @Composable RowScope.() -> Unit,
    end: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalArrangement = Arrangement.spacedBy(Dimens.smallSeparation),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        start()
        end()
    }
}

@Composable
private fun RowScope.Rect(
    title: String,
    container: Color,
    content: Color,
    outlineContainer: Boolean = false,
) {
    Box(
        modifier = outlineContainer
            .foldBoolean(
                ifTrue = {
                    Modifier.border(
                        width = 2.dp,
                        color = container,
                        shape = HnauShape(),
                    )
                },
                ifFalse = {
                    Modifier.background(
                        color = container,
                        shape = HnauShape(),
                    )
                }
            )
            .weight(1f)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            color = content,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
        )
    }
}