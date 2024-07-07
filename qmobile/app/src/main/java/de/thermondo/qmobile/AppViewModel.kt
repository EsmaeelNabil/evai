package de.thermondo.qmobile

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AppViewModel(private val api: Api, private val fileUtils: AppFileUtils) : ViewModel() {

  private var _evaluationResult = MutableStateFlow(EvaluationResponse())
  val evaluation
    get() = _evaluationResult.asStateFlow()

  private val selectedImage = mutableStateOf<Uri>(Uri.EMPTY)
  val selectedImageUri
    get() = selectedImage.value

  private val _isInSync = MutableStateFlow(false)
  val isInSync
    get() = _isInSync.asStateFlow()

  fun selectImage(uri: Uri) {
    selectedImage.value = uri
  }

  private fun getRequestPartsPair(prompt: String) =
      Pair(
          fileUtils
              .createFileFromUri(selectedImageUri, "image.jpg")
              ?.asRequestBody("image/*".toMediaType())
              ?.let { MultipartBody.Part.createFormData("image", "image.jpg", it) }!!,
          prompt.toRequestBody("text/plain".toMediaType())
      )

  private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    _isInSync.value = false
    Log.e("AppViewModel", "Error: ${exception.message}")
  }

  fun evaluate(url: String, prompt: String) = perform {
    val (imagePart, promptBody) = getRequestPartsPair(prompt)
    val eval = api.evaluate(url, imagePart, promptBody)
    _evaluationResult.update { it.copy(response = eval.response, score = eval.score) }
  }

  private fun perform(call: suspend () -> Unit) =
      viewModelScope.launch(Dispatchers.Main + exceptionHandler) {
        _isInSync.value = true
        call()
        _isInSync.value = false
      }

  fun pickImage(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
    pickMedia.launch(PickVisualMediaRequest())
  }
}

