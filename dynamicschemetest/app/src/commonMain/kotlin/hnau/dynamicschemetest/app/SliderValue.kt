package hnau.dynamicschemetest.app

import kotlinx.coroutines.flow.MutableStateFlow

data class SliderValue(
    val value: MutableStateFlow<Float>,
    val config: Config,
) {

    data class Config(
        val min: Float,
        val max: Float,
        val default: Float,
    )
}