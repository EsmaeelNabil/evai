package de.thermondo.qmobile.di

import de.thermondo.qmobile.Api
import de.thermondo.qmobile.AppFileUtils
import de.thermondo.qmobile.AppViewModel
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val appModule = module {
  viewModel { AppViewModel(get(), get()) }

  single { AppFileUtils(get()) }
}

val networkModule = module {
  single {
    OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        )
        .callTimeout(1000, TimeUnit.MINUTES)
        .build()
  }

  single {
    Retrofit.Builder()
        .client(get())
        .addConverterFactory(
            Json.asConverterFactory("application/json; charset=UTF8".toMediaType())
        )
        .baseUrl("http://will.be.overriden.com/")
        .build()
  }

  single<Api> { get<Retrofit>().create(Api::class.java) }
}

