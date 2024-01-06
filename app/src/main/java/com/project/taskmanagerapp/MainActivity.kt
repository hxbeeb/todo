package com.project.taskmanagerapp

import android.annotation.SuppressLint
import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                TaskApp()
            }

        }
    }
}

data class Task(val id: Int, val text: String, val isDone: Boolean = false)

@Composable
fun MainScreen() {
    // Your main screen content

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable

fun TaskApp() {
    var navigateToMain by remember { mutableStateOf(false) }

    val navigateTo = {
        navigateToMain = true
    }

    if (navigateToMain) {
        MainScreen()

    } else {
        SplashScreen(navigateToMainScreen = navigateTo)
    }

    var newTaskText by remember { mutableStateOf("") }
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var isAddingTask by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current;

    Scaffold(

        floatingActionButton = {
            FloatingActionButton(onClick = {
                isAddingTask = true
                focusManager.clearFocus()
                keyboardController?.show()
            }) {
                Text(text = "+", fontSize = 30.sp)

            }
        }, content = { paddingValues ->


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {


                Text(text = "Task Manager", fontSize = 30.sp)
                Spacer(modifier = Modifier.height(20.dp))
                if(tasks.isEmpty())
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {


                        IconButton(onClick = { isAddingTask = true
                            focusManager.clearFocus()
                            keyboardController?.show() }) {
                            Icon(Icons.Default.AddCircle, contentDescription =null ,Modifier.size(50.dp))

                        }
                        Text(text = "No items added")
                    }
                LazyColumn {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskChecked = { taskId, isChecked ->
                                tasks =
                                    tasks.map { if (it.id == taskId) it.copy(isDone = isChecked) else it }
                            },
                            onDeleteTask = { taskId ->
                                tasks = tasks.filterNot { it.id == taskId }
                            }
                        )
                    }

                }

                // Add Task Section
                if (isAddingTask) {
                    AddTaskSection(
                        onAddTask = {
                            if (newTaskText.isNotBlank()) {
                                tasks = tasks + Task(id = tasks.size + 1, text = newTaskText)
                                newTaskText = ""
                                isAddingTask = false
                                keyboardController?.hide()
                            }
                        },
                        onCancel = {
                            newTaskText = ""
                            isAddingTask = false
                            keyboardController?.hide()
                        },
                        onTextChanged = { newText ->
                            newTaskText = newText
                        }
                    )
                } else {
//                    FloatingActionButton(
//                        onClick = {
//                            isAddingTask = true
//                            focusManager.clearFocus()
//                            keyboardController?.show()
//                        },
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .align(Alignment.End),
//                    ) {
//                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
//                    }
                    // FAB to Add Task

                }
            }


        })


}


@Composable
fun TaskItem(task: Task, onTaskChecked: (Int, Boolean) -> Unit, onDeleteTask: (Int) -> Unit) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for marking tasks as done
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { isChecked ->
                    onTaskChecked(task.id, isChecked)
                },
                modifier = Modifier
                    .padding(end = 16.dp)
            )

            // Task text
            Text(
                text = task.text,

                style = MaterialTheme.typography.bodyLarge.copy(textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None),
                modifier = Modifier
                    .weight(1f)
            )

            // Delete button
            IconButton(
                onClick = { onDeleteTask(task.id) },
                modifier = Modifier.size(24.dp)
            ) {

                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}


@Composable
fun AddTaskSection(
    onAddTask: () -> Unit,
    onCancel: () -> Unit,
    onTextChanged: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }


    Dialog(onDismissRequest = { }) {



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .padding(20.dp)
            .background(Color.White)

            .padding(20.dp),
    ) {

        OutlinedTextField(value = text, onValueChange = {
            text = it
            onTextChanged(it)
        },
            label = { Text("Enter Task") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onAddTask()
                }
            ),

            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)

        )



        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel, modifier = Modifier.padding(end = 10.dp)) {
                Text("Cancel")
            }
            Button(onClick = onAddTask) {
                Text(text = "Add")
            }
        }

    }
}

}


@Composable
fun SplashScreen(
    navigateToMainScreen: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Simulating a delay for splash screen visibility (you can replace this with your logic)
        delay(2000)
        navigateToMainScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your logo drawable
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )
    }
}


