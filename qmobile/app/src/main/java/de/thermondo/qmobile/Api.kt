package de.thermondo.qmobile

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.InputStream

interface Api {

    @Multipart
    @POST(EVALUATE)
    suspend fun evaluate(
        @Part image: MultipartBody.Part,
        @Part("prompt") prompt: RequestBody
    ): EvaluationResponse
}