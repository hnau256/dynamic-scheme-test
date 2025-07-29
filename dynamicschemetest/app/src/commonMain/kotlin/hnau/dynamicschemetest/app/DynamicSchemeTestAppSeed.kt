package hnau.dynamicschemetest.app

import hnau.common.app.model.app.AppSeed
import hnau.common.app.model.theme.Hue
import hnau.common.app.model.theme.ThemeBrightness

fun createDynamicSchemeTestAppSeed(
    defaultBrightness: ThemeBrightness? = null,
): AppSeed<RootModel, RootModel.Skeleton> = AppSeed(
    fallbackHue = Hue(0.8f),
    defaultBrightness = defaultBrightness,
    skeletonSerializer = RootModel.Skeleton.serializer(),
    createDefaultSkeleton = { RootModel.Skeleton() },
    createModel = { scope, appContext, skeleton ->
        RootModel(
            scope = scope,
            skeleton = skeleton,
        )
    },
    extractGoBackHandler = RootModel::goBackHandler,
)