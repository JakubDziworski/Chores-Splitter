package dziworski.kuba.com.chores_splitter_android.http

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface BackendService {
    @GET("users")
    fun getUsers() : Flowable<GetUsersDto>

    @GET("tasks")
    fun getTasks() : Flowable<GetTasksDto>

    @POST("tasks")
    fun addTask(@Body addTaskDto: AddTaskDto) : Flowable<TaskId>
    
    @GET("tasks/user/{userId}")
    fun getTasksForUser(@Path("userId")userId:String) : Flowable<GetTasksDto>

    @PUT("tasks/{taskId}/set-completed")
    fun completeTask(@Path("taskId")taskId:String) : Completable

    @PUT("tasks/{taskId}/set-uncompleted")
    fun unCompleteTask(@Path("taskId")taskId:String) : Completable

    @GET("chores")
    fun getChores() : Flowable<GetChoresDto>

    @POST("chores")
    fun addChore(@Body addChoreDto: AddChoreDto) : Flowable<ChoreId>

    @PUT("chores/{choreId}")
    fun editChore(@Path("choreId") choreId:String, @Body editChoreDto: EditChoreDto) : Flowable<ChoreId>

    @POST("penalties")
    fun addPenalty(@Body addPenaltyDto: AddPenaltyDto) : Flowable<PenaltyId>

    @GET("penalties")
    fun getPenalties() : Flowable<GetPenaltiesDto>

}

object Backend {
    val Tag = this::class.toString()
    val instance : BackendService by lazy {
        Log.i(Tag,"Creating backend service")
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        Retrofit.Builder()
                .baseUrl("http://10.7.69.193:8090/api/v1/")
//                .baseUrl("http://10.0.2.2:8090/api/v1/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(BackendService::class.java)
    }
}

data class GetUserDto(val id: Long,val  name: String,val  email: String,val points: Int) {
    override fun toString(): String {
        return "$name $points points"
    }
}
data class UserId(val userId: Long)
data class GetUsersDto(val users: List<GetUserDto>)

data class ChoreId(val choreId: Long)
data class GetChoreDto(val id: Long,val  name: String, val points: Int,val  interval: Int?)
data class GetChoresDto(val chores: List<GetChoreDto>)
data class AddChoreDto(val name: String, val points: Int, val interval: Int?)
data class EditChoreDto(val choreId: String, val name: String, val points: Int, val interval: Int?)

data class TaskId(val taskId:Long)
data class GetTaskDto(val id: Long, val chore:GetChoreDto, val userId:Long, val assignedAt: Long, val completedAt: Long?)
data class GetTasksDto(val tasks:List<GetTaskDto>)
data class AddTaskDto(val choreId: ChoreId,val userId: UserId)

data class PenaltyId(val penaltyId:Long)
data class AddPenaltyDto(val userId: Long,val points:Int,val reason:String)
data class GetPenaltyDto(val id:Long,val userId:Long,val points:Int,val reason:String)
data class GetPenaltiesDto(val penalties:List<GetPenaltyDto>)