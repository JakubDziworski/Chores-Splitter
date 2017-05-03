package dziworski.kuba.com.chores_splitter_android

import android.util.Log
import android.widget.Toast
import dziworski.kuba.com.chores_splitter_android.http.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
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
            .subOnIoObservOnMainWithErrorHandling()

    val usersFlowable = tick
            .flatMap { backend.getUsers() }
            .subOnIoObservOnMainWithErrorHandling()

    val tasksFlowable = tick
            .flatMap { backend.getTasks() }
            .subOnIoObservOnMainWithErrorHandling()


    fun addChore(chore: AddChoreDto) {
        backend.addChore(chore)
                .subOnIoWithErrorHandling()
                .subscribe { choresChanged.onNext(Unit) }
    }

    fun editChore(chore: EditChoreDto) {
        backend.editChore(chore.choreId, chore)
                .subOnIoWithErrorHandling()
                .subscribe { choresChanged.onNext(Unit) }
    }

    fun addTask(task: AddTaskDto) {
        backend.addTask(task)
                .subOnIoWithErrorHandling()
                .subscribe{ tasksChanged.onNext(Unit) }
    }

    fun setTaskCompleted(completed: Boolean, taskId: Long) {
        val httpCallFlowable = if (completed) {
            backend.completeTask(taskId.toString())
        } else {
            backend.unCompleteTask(taskId.toString())
        }
        httpCallFlowable
                .subOnIoWithErrorHandling()
                .subscribe { tasksChanged.onNext(Unit) }
    }

    fun <T> Flowable<T>.subOnIoObservOnMainWithErrorHandling() : Flowable<T> {
        return this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .withErrorHandling()
    }

    fun <T> Flowable<T>.subOnIoWithErrorHandling() : Flowable<T> {
        return this
                .subscribeOn(Schedulers.io())
                .withErrorHandling()
    }

    fun <T> Flowable<T>.withErrorHandling() : Flowable<T> {
        return this.retry { exception: Throwable ->
            Log.e(RxGateway::class.toString(), exception.stackTrace.joinToString("\n"))
            Toast.makeText(
                    ChoresSplitterApp.instance,
                    "NETWORK ERROR $exception",
                    Toast.LENGTH_SHORT)
                    .show()
            false
        }
    }
}