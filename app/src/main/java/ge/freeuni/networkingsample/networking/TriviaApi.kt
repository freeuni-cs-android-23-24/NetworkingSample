package ge.freeuni.networkingsample.networking

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaServiceApi {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("type") type: String
    ): TriviaApiResponseEntity
}

object Dependencies {
    @OptIn(ExperimentalSerializationApi::class)
    val json by lazy {
        Json {
            ignoreUnknownKeys = true
            namingStrategy = JsonNamingStrategy.SnakeCase
        }
    }
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(
                json.asConverterFactory(MediaType.parse("application/json; charset=UTF8")!!)
            )
            .build()
    }

    val apiService by lazy {
        retrofit.create<TriviaServiceApi>()
    }
}