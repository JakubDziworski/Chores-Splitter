package dziworski.kuba.com.chores_splitter_android.http

import android.util.Log
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface BackendService {
    @GET("users")
    fun getUsers() : Flowable<GetUsersDto>

    @GET("tasks")
    fun getTasks() : Flowable<GetTasksDto>
    
    @GET("tasks/user/{userId}")
    fun getTasksForUser(@Path("userId")userId:String) : Flowable<GetTasksDto>

    @GET("chores")
    fun getChores() : Flowable<GetChoresDto>
}

object Backend {
    val Tag = this::class.toString()
    val instance : BackendService by lazy {
        Log.i(Tag,"Creating backend service")
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8090/api/v1/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(BackendService::class.java)
    }
}

data class GetUserDto(val id: Long,val  name: String,val  email: String) {
    override fun toString(): String {
        return name
    }
}
data class GetUsersDto(val users: List<GetUserDto>)

data class GetChoreDto(val id: Long,val  name: String, val points: Int,val  interval: Int?)
data class GetChoresDto(val chores: List<GetChoreDto>)

data class GetTaskDto(val id: Long, val chore:GetChoreDto, val user:GetUserDto, val assignedAt: Long, val completed: Boolean)
data class GetTasksDto(val tasks:List<GetTaskDto>)