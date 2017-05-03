package dziworski.kuba.com.chores_splitter_android

import android.util.Log
import android.widget.Toast
import dziworski.kuba.com.chores_splitter_android.http.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


object RxGateway {
    val backend: BackendService by lazy {
        Backend.instance
    }

    private val tick: Flowable<Unit> = Flowable.interval(0, 1, TimeUnit.MINUTES).map { it -> Unit }
    private val choresChanged: PublishProcessor<Unit> = PublishProcessor.create<Unit>()
    private val tasksChanged: PublishProcessor<Unit> = PublishProcessor.create<Unit>()
    val choresFlowable: Flowable<GetChoresDto> = tick.mergeWith(choresChanged)
            .flatMap { backend.getChores() }
            .setUpFlowable()

    fun usersTasksFlowable(userId: Long): Flowable<GetTasksDto> = tick
            .map { userId.toString() }
            .flatMap { backend.getTasksForUser(it) }
            .setUpFlowable()

    val usersFlowable = tick
            .flatMap { backend.getUsers() }
            .setUpFlowable()

    val tasksFlowable = tick
            .flatMap { backend.getTasks() }
            .setUpFlowable()


    fun addChore(chore: AddChoreDto) {
        backend.addChore(chore)
                .setUpFlowable()

    }

    fun editChore(chore: EditChoreDto) {
        backend.editChore(chore.choreId, chore)
                .setUpFlowable()
                .subscribe { choresChanged.onNext(Unit) }
    }

    fun addTask(task: AddTaskDto) {
        backend.addTask(task)
                .setUpFlowable()
                .subscribeBy(
                        onNext = { tasksChanged.onNext(Unit) },
                        onError = { err -> }
                )
    }

    fun setTaskCompleted(completed: Boolean, taskId: Long) {
        val httpCallFlowable = if (completed) {
            backend.completeTask(taskId.toString())
        } else {
            backend.unCompleteTask(taskId.toString())
        }
        httpCallFlowable
                .setUpFlowable()
                .subscribe { tasksChanged.onNext(Unit) }
    }

    fun <T> Flowable<T>.setUpFlowable() : Flowable<T> {
        return this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext { exception : Throwable ->
                    Toast.makeText(
                        ChoresSplitterApp.instance,
                        "NETWORK ERROR $exception",
                        Toast.LENGTH_SHORT)
                        .show()
                    Flowable.empty()
                }
    }
}