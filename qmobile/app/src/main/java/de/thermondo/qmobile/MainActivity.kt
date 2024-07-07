package de.thermondo.qmobile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddToPhotos
import androidx.compose.material.icons.sharp.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.thermondo.qmobile.ui.theme.QmobileTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

const val BASE_URL = "http://192.168.0.172:3993/api/evaluate"
const val EVALUATE = "api/evaluate"

class MainActivity : ComponentActivity() {

  private val pickMedia =
      registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
          viewModel.selectImage(uri)
        } else {
          Log.d("PhotoPicker", "No media selected")
        }
      }

  private val viewModel: AppViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val navController = rememberNavController()
      QmobileTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          NavHost(
              modifier = Modifier.padding(innerPadding),
              navController = navController,
              startDestination = "home"
          ) { composable("home") { HomeScreen() } }
        }
      }
    }
  }

  @Composable
  fun HomeScreen() {
    var prompt by remember { mutableStateOf("This image has a cat") }
    var endpoint_full_path by remember { mutableStateOf(BASE_URL) }

    val isInSync by viewModel.isInSync.collectAsState()
    val evaluation by viewModel.evaluation.collectAsState()

    Surface {
      val borderColor =
          when {
            viewModel.selectedImageUri.toString().isNotEmpty() -> Color.Blue.copy(alpha = 0.1f)
            else -> Color.LightGray.copy(alpha = 0.1f)
          }

      Column(
          Modifier.fillMaxSize().padding(top = 50.dp).verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box {
          Box(
              modifier =
                  Modifier.padding(16.dp)
                      .size(310.dp)
                      .border(10.dp, borderColor, RoundedCornerShape(20.dp))
                      .align(Alignment.Center)
                      .clip(RoundedCornerShape(20.dp))
                      .clickable { viewModel.pickImage(pickMedia) },
          ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Sharp.AddToPhotos,
                tint = Color.LightGray,
                contentDescription = null,
            )
          }

          ImageUri(
              modifier =
                  Modifier.padding(16.dp)
                      .size(300.dp)
                      .clip(RoundedCornerShape(20.dp))
                      .align(Alignment.Center),
              uri = viewModel.selectedImageUri.toString()
          )

          if (isInSync) {
            Box(
                modifier =
                    Modifier.padding(16.dp)
                        .size(300.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .align(Alignment.Center)
                        .background(Color.Black.copy(alpha = 0.5f))
            ) {}
          }
        }

        when {
          viewModel.selectedImageUri != Uri.EMPTY -> {
            Spacer(modifier = Modifier.size(16.dp))
            if (isInSync) {
              Text(text = "Evaluating...")
            } else {

              OutlinedTextField(
                  value = endpoint_full_path,
                  onValueChange = { endpoint_full_path = it }
              )
              Spacer(modifier = Modifier.size(16.dp))
              OutlinedTextField(value = prompt, onValueChange = { prompt = it })
              Spacer(modifier = Modifier.size(16.dp))
              Button(onClick = { viewModel.evaluate(endpoint_full_path, prompt) }) {
                Icon(imageVector = Icons.Sharp.Upload, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Evaluate")
              }
            }
          }
          else -> {
            Text(text = "Select an image to evaluate")
          }
        }

        if (evaluation.response.isNotEmpty()) {
          Spacer(modifier = Modifier.size(16.dp))
          Text(modifier = Modifier.padding(16.dp), text = "Score: ${evaluation.score}")
          Spacer(modifier = Modifier.size(16.dp))
          Text(
              modifier = Modifier.padding(16.dp),
              textAlign = TextAlign.Center,
              text = "Reason for the score\n${evaluation.response}"
          )
          Spacer(modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}
