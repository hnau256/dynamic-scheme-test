package hnau.dynamicschemetest.app

import hnau.common.app.model.app.AppModel
import hnau.common.app.projector.app.AppProjector
import kotlinx.coroutines.CoroutineScope

fun createAppProjector(
    scope: CoroutineScope,
    model: AppModel<RootModel, RootModel.Skeleton>,
): AppProjector<RootModel, RootModel.Skeleton, RootProjector> = AppProjector(
    scope = scope,
    model = model,
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