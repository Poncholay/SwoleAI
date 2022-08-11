package com.guillaumewilmot.swoleai.modules.home.dialogchooseexercise

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentDialog
import com.guillaumewilmot.swoleai.databinding.DialogDefaultWithActionBinding
import com.guillaumewilmot.swoleai.model.ExerciseBookModel
import com.guillaumewilmot.swoleai.ui.compose.components.ListItemExercise
import com.guillaumewilmot.swoleai.ui.compose.theme.SwoleAiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChooseExerciseDialog : ParentDialog<DialogDefaultWithActionBinding>() {

    private val viewModel: ChooseExerciseDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            SwoleAiTheme {
                ChooseExerciseDialogLayout(viewModel)
            }
        }
    }

    /**
     * COMPOSE
     */

    @Composable
    fun ChooseExerciseDialogLayout(viewModel: ChooseExerciseDialogViewModel) {
        val exerciseBookState: State<ExerciseBookModel> = viewModel.exerciseBook
            .subscribeAsState(
                initial = ExerciseBookModel(id = 0, exercises = listOf())
            )
        val loaderState: State<Boolean> = viewModel.loaderIsLoading
            .subscribeAsState(
                initial = false
            )

        ChooseExerciseDialogLayout(
            exerciseBook = exerciseBookState.value,
            showLoader = loaderState.value
        )
    }

    @Composable
    fun ChooseExerciseDialogLayout(
        exerciseBook: ExerciseBookModel,
        showLoader: Boolean
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
                ExerciseList(
                    exerciseBook = exerciseBook,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun ExerciseList(
        exerciseBook: ExerciseBookModel,
        modifier: Modifier
    ) {
        val exerciseListState: LazyListState = rememberLazyListState()

        LazyColumn(
            state = exerciseListState,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.marginSmall))
        ) {
            this.items(exerciseBook.exercises) { exercise ->
                ListItemExercise(exercise)
            }
        }
    }

    /**
     * PREVIEWS
     */

    @Composable
    @Preview(name = "ChooseExerciseDialogPreviewLight", uiMode = UI_MODE_NIGHT_NO)
    @Preview(name = "ChooseExerciseDialogPreviewDark", uiMode = UI_MODE_NIGHT_YES)
    fun ChooseExerciseDialogPreviewLight(
        @PreviewParameter(ExerciseBookModelPreviewParameterProvider::class)
        exerciseBook: ExerciseBookModel
    ) {
        SwoleAiTheme {
            ChooseExerciseDialogLayout(
                exerciseBook = exerciseBook,
                showLoader = true
            )
        }
    }
}

