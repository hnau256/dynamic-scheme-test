@file:UseSerializers(
    MutableStateFlowSerializer::class,
)

package hnau.dynamicschemetest.app

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.util.lerp
import dynamiccolor.ColorSpec
import dynamiccolor.DynamicScheme
import dynamiccolor.Variant
import hnau.common.app.model.goback.GoBackHandler
import hnau.common.app.model.goback.NeverGoBackHandler
import hnau.common.app.model.theme.Hue
import hnau.common.app.model.theme.ThemeBrightness
import hnau.common.app.model.theme.ThemeBrightnessValues
import hnau.common.app.projector.utils.theme.DynamicSchemeConfig
import hnau.common.app.projector.utils.theme.buildColorScheme
import hnau.common.kotlin.coroutines.combineStateWith
import hnau.common.kotlin.coroutines.mapState
import hnau.common.kotlin.coroutines.toMutableStateFlowAsInitial
import hnau.common.kotlin.serialization.MutableStateFlowSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

class RootModel(
    scope: CoroutineScope,
    private val skeleton: Skeleton,
) {

    @Serializable
    data class Skeleton(
        val hue: MutableStateFlow<Float> = 0.5f.toMutableStateFlowAsInitial(),
        val contrastLevel: MutableStateFlow<Float> = 0.5f.toMutableStateFlowAsInitial(),
        val chroma: MutableStateFlow<Float> = 0.5f.toMutableStateFlowAsInitial(),
        val tones: ThemeBrightnessValues<MutableStateFlow<Float>> = ThemeBrightnessValues {
            0.5f.toMutableStateFlowAsInitial()
        },

        val specVersion: MutableStateFlow<ColorSpec.SpecVersion> =
            ColorSpec.SpecVersion.SPEC_2025.toMutableStateFlowAsInitial(),

        val platform: MutableStateFlow<DynamicScheme.Platform> =
            DynamicScheme.Platform.PHONE.toMutableStateFlowAsInitial(),
    )

    val hue: MutableStateFlow<Float>
        get() = skeleton.hue

    val chroma: MutableStateFlow<Float>
        get() = skeleton.chroma

    val contrastLevel: MutableStateFlow<Float>
        get() = skeleton.contrastLevel

    val tones: ThemeBrightnessValues<MutableStateFlow<Float>>
        get() = skeleton.tones

    private val tonesValues: StateFlow<ThemeBrightnessValues<Double>> = ThemeBrightness
        .entries
        .fold(
            initial = MutableStateFlow(emptyMap<ThemeBrightness, Double>()) as StateFlow<Map<ThemeBrightness, Double>>,
        ) { acc, brightness ->
            acc.combineStateWith(
                scope = scope,
                other = skeleton.tones[brightness],
            ) { acc, toneFraction ->
                val tone = lerp(0f, 100f, toneFraction).toDouble()
                acc + (brightness to tone)
            }
        }
        .mapState(scope) { tones ->
            ThemeBrightnessValues { brightness ->
                tones.getValue(brightness)
            }
        }

    private val baseConfig: StateFlow<Pair<Hue, List<Pair<Variant, DynamicSchemeConfig>>>> =
        skeleton
            .hue
            .mapState(scope) { hueFraction ->
                lerp(0f, 1f, hueFraction).let(::Hue)
            }
            .combineStateWith(
                scope = scope,
                other = skeleton.contrastLevel,
            ) { hue, contrastLevelFraction ->
                val contrastLevel = lerp(0f, 2f, contrastLevelFraction).toDouble()
                hue to contrastLevel
            }
            .combineStateWith(
                scope = scope,
                other = skeleton.chroma,
            ) { (hue, contrastLevel), chromaFraction ->
                val chroma = lerp(0f, 100f, chromaFraction).toDouble()
                Triple(hue, contrastLevel, chroma)
            }
            .combineStateWith(
                scope = scope,
                other = skeleton.specVersion,
            ) { values, specVersion ->
                values to specVersion
            }
            .combineStateWith(
                scope = scope,
                other = skeleton.platform,
            ) { (values, specVersion), platform ->
                Triple(values, specVersion, platform)
            }
            .combineStateWith(
                scope = scope,
                other = tonesValues,
            ) { (values, specVersion, platform), tones ->
                val (hue, contrastLevel, chroma) = values
                hue to Variant.entries.map { variant ->
                    variant to DynamicSchemeConfig(
                        contrastLevel = contrastLevel,
                        chroma = chroma,
                        tone = tones,
                        specVersion = specVersion,
                        platform = platform,
                        variant = variant,
                    )
                }
            }

    val schemes: ThemeBrightnessValues<StateFlow<List<Pair<Variant, ColorScheme>>>> =
        ThemeBrightnessValues { brightness ->
            baseConfig.mapState(scope) { (hue, configsByVariants) ->
                configsByVariants.map {(variant, config) ->
                    variant to buildColorScheme(
                        hue = hue,
                        config = config,
                        brightness = brightness,
                    )
                }
            }
        }

    val goBackHandler: GoBackHandler
        get() = NeverGoBackHandler
}