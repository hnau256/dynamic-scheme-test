@file:UseSerializers(
    MutableStateFlowSerializer::class,
)

package hnau.dynamicschemetest.app

import androidx.compose.material3.ColorScheme
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

    companion object {

        private val hueConfig = SliderValue.Config(
            min = 0f,
            max = 1f,
            default = 0.1f,
        )

        private val chromaConfig = SliderValue.Config(
            min = 0f,
            max = 100f,
            default = 48f,
        )

        private val contrastLevelConfig = SliderValue.Config(
            min = 0f,
            max = 2f,
            default = 1f,
        )

        private val toneConfig: ThemeBrightnessValues<SliderValue.Config> =
            ThemeBrightnessValues { brightness ->
                SliderValue.Config(
                    min = 0f,
                    max = 100f,
                    default = when (brightness) {
                        ThemeBrightness.Light -> 40f
                        ThemeBrightness.Dark -> 80f
                    },
                )
            }
    }

    @Serializable
    data class Skeleton(
        val hue: MutableStateFlow<Float> =
            hueConfig.default.toMutableStateFlowAsInitial(),

        val contrastLevel: MutableStateFlow<Float> =
            contrastLevelConfig.default.toMutableStateFlowAsInitial(),

        val chroma: MutableStateFlow<Float> =
            chromaConfig.default.toMutableStateFlowAsInitial(),

        val tones: ThemeBrightnessValues<MutableStateFlow<Float>> = ThemeBrightnessValues { brightness ->
            toneConfig[brightness].default.toMutableStateFlowAsInitial()
        },

        val specVersion: MutableStateFlow<ColorSpec.SpecVersion> =
            ColorSpec.SpecVersion.SPEC_2025.toMutableStateFlowAsInitial(),

        val platform: MutableStateFlow<DynamicScheme.Platform> =
            DynamicScheme.Platform.PHONE.toMutableStateFlowAsInitial(),
    )

    val hue = SliderValue(
        value = skeleton.hue,
        config = hueConfig,
    )

    val chroma = SliderValue(
        value = skeleton.chroma,
        config = chromaConfig,
    )

    val contrastLevel = SliderValue(
        value = skeleton.contrastLevel,
        config = contrastLevelConfig,
    )

    val tones: ThemeBrightnessValues<SliderValue> = ThemeBrightnessValues { brightness ->
        SliderValue(
            value = skeleton.tones[brightness],
            config = toneConfig[brightness],
        )
    }

    val specVersion: MutableStateFlow<ColorSpec.SpecVersion>
        get() = skeleton.specVersion

    val platform: MutableStateFlow<DynamicScheme.Platform>
        get() = skeleton.platform

    private val tonesValues: StateFlow<ThemeBrightnessValues<Double>> = ThemeBrightness
        .entries
        .fold(
            initial = MutableStateFlow(emptyMap<ThemeBrightness, Double>()) as StateFlow<Map<ThemeBrightness, Double>>,
        ) { acc, brightness ->
            acc.combineStateWith(
                scope = scope,
                other = skeleton.tones[brightness],
            ) { acc, tone ->
                acc + (brightness to tone.toDouble())
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
            .mapState(scope) { hue ->
                hue.let(::Hue)
            }
            .combineStateWith(
                scope = scope,
                other = skeleton.contrastLevel,
            ) { hue, contrastLevel ->
                hue to contrastLevel.toDouble()
            }
            .combineStateWith(
                scope = scope,
                other = skeleton.chroma,
            ) { (hue, contrastLevel), chroma ->
                Triple(hue, contrastLevel, chroma.toDouble())
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
                configsByVariants.map { (variant, config) ->
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