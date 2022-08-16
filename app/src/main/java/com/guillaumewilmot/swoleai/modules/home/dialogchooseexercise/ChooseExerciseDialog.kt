package com.guillaumewilmot.swoleai.modules.home.dialogchooseexercise

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentDialog
import com.guillaumewilmot.swoleai.databinding.DialogDefaultWithActionBinding
import com.guillaumewilmot.swoleai.ui.compose.components.list.exercise.ListOfExercise
import com.guillaumewilmot.swoleai.ui.compose.components.listitem.exercise.ListItemOfExercise
import com.guillaumewilmot.swoleai.ui.compose.components.listitem.exercise.ListItemOfExerciseListViewDataModelPreviewParameterProvider
import com.guillaumewilmot.swoleai.ui.compose.theme.SwoleAiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChooseExerciseDialog : ParentDialog<DialogDefaultWithActionBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            SwoleAiTheme {
                ChooseExerciseDialogLayout()
            }
        }
    }

    /**
     * COMPOSE
     */

    @Composable
    fun ChooseExerciseDialogLayout(viewModel: ChooseExerciseDialogViewModel = viewModel()) {
        val exercisesState: State<List<ListItemOfExercise.ViewDataModel>> = viewModel.exercises
            .subscribeAsState(
                initial = listOf()
            )
        val showLoaderState: State<Boolean> = viewModel.loaderIsLoading
            .subscribeAsState(
                initial = false
            )

        ChooseExerciseDialogLayout(
            exercises = exercisesState.value,
            showLoader = showLoaderState.value,
            onExerciseClicked = { index ->
                viewModel.onExerciseClicked(index)
                                },
            onExerciseInfoClicked = { index ->
                viewModel.onExerciseInfoClicked(index)
                                    },
        )
    }

    @Composable
    fun ChooseExerciseDialogLayout(
        exercises: List<ListItemOfExercise.ViewDataModel>,
        showLoader: Boolean,
        onExerciseClicked: (Int) -> Unit = {},
        onExerciseInfoClicked: (Int) -> Unit = {},
    ) {
        Surface {
            Column {
                Row(
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(id = R.dimen.marginSmall),
                            top = dimensionResource(id = R.dimen.marginSmall),
                            end = dimensionResource(id = R.dimen.marginSmall)
                        )
                ) {
                    Text(
                        text = stringResource(R.string.app_dialog_choose_exercise_title_text),
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier.weight(1f)
                    )
                    if (showLoader) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colors.onSurface,
                            strokeWidth = dimensionResource(id = R.dimen.thicknessLoader),
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.sizeLoader))
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

                ListOfExercise.View(
                    exercises = exercises,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { index ->
                        onExerciseClicked(index)
                    },
                    onClickInfo = { index ->
                        onExerciseInfoClicked(index)
                    }
                )
            }
        }
    }

    /**
     * PREVIEWS
     */

    @Composable
    @Preview(name = "ChooseExerciseDialogPreviewLight", uiMode = UI_MODE_NIGHT_NO)
    @Preview(name = "ChooseExerciseDialogPreviewDark", uiMode = UI_MODE_NIGHT_YES)
    fun ChooseExerciseDialogPreview(
        @PreviewParameter(ListItemOfExerciseListViewDataModelPreviewParameterProvider::class)
        exercises: List<ListItemOfExercise.ViewDataModel>
    ) {
        SwoleAiTheme {
            ChooseExerciseDialogLayout(
                exercises = exercises,
                showLoader = true
            )
        }
    }
}

