package hnau.dynamicschemetest.app

import dynamiccolor.Variant
import hnau.common.app.model.app.AppModel
import hnau.common.app.projector.app.AppProjector
import hnau.common.app.projector.utils.theme.DynamicSchemeConfig
import kotlinx.coroutines.CoroutineScope

fun createAppProjector(
    scope: CoroutineScope,
    model: AppModel<RootModel, RootModel.Skeleton>,
): AppProjector<RootModel, RootModel.Skeleton, RootProjector> = AppProjector(
    scope = scope,
    model = model,
    schemeConfig = DynamicSchemeConfig(
        variant = Variant.MONOCHROME
    ),
    createProjector = { scope, model, _ ->
        RootProjector(
            scope = scope,
            model = model,
        )
    },
    content = { rootProjector ->
        rootProjector.Content()
    }
)