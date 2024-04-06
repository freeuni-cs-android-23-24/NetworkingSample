package ge.freeuni.networkingsample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import ge.freeuni.networkingsample.networking.Dependencies
import ge.freeuni.networkingsample.networking.TriviaQuestionEntity
import ge.freeuni.networkingsample.ui.theme.NetworkingSampleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NetworkingSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var state by remember {
                        mutableStateOf<MainScreenState>(MainScreenState.Empty)
                    }
                    val coroutineScope = rememberCoroutineScope()
                    MainScreen(
                        state,
                        onLoadQuestionsClicked = {
                            state = MainScreenState.Loading
                            coroutineScope.launch {
                                runCatching {
                                    Dependencies.apiService.getQuestions(10, type = "boolean")
                                }.onSuccess { apiResponse ->
                                    state = MainScreenState.Content(questions = apiResponse.results)
                                }.onFailure {
                                    TODO()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

sealed interface MainScreenState {
    data object Loading : MainScreenState
    data object Empty : MainScreenState
    data class Content(val questions: List<TriviaQuestionEntity>) : MainScreenState
}

@Composable
fun MainScreen(state: MainScreenState, onLoadQuestionsClicked: () -> Unit) {
    when (state) {
        is MainScreenState.Content -> MainScreenContent(questions = state.questions)
        MainScreenState.Empty -> MainScreenEmpty(onLoadQuestionsClicked = onLoadQuestionsClicked)
        MainScreenState.Loading -> MainScreenLoading()
    }
}

@Composable
fun MainScreenContent(questions: List<TriviaQuestionEntity>) {
    val scroll = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scroll)) {
        questions.forEach {
            TriviaQuestionListItem(question = it)
        }
    }
}

@Composable
fun TriviaQuestionListItem(question: TriviaQuestionEntity) {
    Card(modifier = Modifier
        .padding(4.dp).fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = question.question
        )
    }
}

@Composable
fun MainScreenLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            CircularProgressIndicator()
            Text(text = "Loading...")
        }
    }
}

@Composable
fun MainScreenEmpty(onLoadQuestionsClicked: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { onLoadQuestionsClicked() }) {
            Text(text = "Load Questions")
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420"
)
@Composable
fun MainScreenPreview() {
    NetworkingSampleTheme {
        MainScreen(
            state = MainScreenState.Empty,
            onLoadQuestionsClicked = {}
        )
    }
}