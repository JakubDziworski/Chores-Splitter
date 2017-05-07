package dziworski.kuba.com.chores_splitter_android

import android.os.Handler
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

    var tickEnabled = true

    private val tick: Flowable<Unit> = Flowable
            .interval(0, 30, TimeUnit.SECONDS)
            .map { Unit }
            .filter { tickEnabled }

    private val choresChanged: PublishProcessor<Unit> = PublishProcessor.create<Unit>()
    private val tasksChanged: PublishProcessor<Unit> = PublishProcessor.create<Unit>()
    private val penaltesChanged: PublishProcessor<Unit> = PublishProcessor.create<Unit>()
    private val usersChanged: PublishProcessor<Unit> = PublishProcessor.create<Unit>()
    private val errorOccurred = PublishProcessor.create<Throwable>()

    val errorsFlowable: Flowable<Throwable> = errorOccurred
            .observeOn(AndroidSchedulers.mainThread())

    val choresFlowable: Flowable<GetChoresDto> = tick
            .mergeWith(choresChanged)
            .flatMap { backend.getChores() }
            .subOnIoObservOnMainWithErrorHandling()

    val usersFlowable = tick
            .mergeWith(usersChanged)
            .flatMap { backend.getUsers() }
            .subOnIoObservOnMainWithErrorHandling()

    val tasksFlowable = tick
            .mergeWith(tasksChanged)
            .flatMap { backend.getTasks() }
            .subOnIoObservOnMainWithErrorHandling()

    val penaltiesFlowable = tick
            .mergeWith(penaltesChanged)
            .flatMap { backend.getPenalties() }
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
                .subscribe { tasksChanged.onNext(Unit) }
    }

    fun addPenalty(penaltyDto: AddPenaltyDto) {
        backend.addPenalty(penaltyDto)
                .subOnIoWithErrorHandling()
                .subscribe {
                    penaltesChanged.onNext(Unit)
                    usersChanged.onNext(Unit)
                }
    }

    fun setTaskCompleted(completed: Boolean, taskId: Long) {
        val httpCallFlowable = if (completed) {
            backend.completeTask(taskId.toString())
        } else {
            backend.unCompleteTask(taskId.toString())
        }
        httpCallFlowable
                .subOnIoWithErrorHandling()
                .subscribe {
                    tasksChanged.onNext(Unit)
                    usersChanged.onNext(Unit)
                }
    }

    fun <T> Flowable<T>.subOnIoObservOnMainWithErrorHandling(): Flowable<T> {
        return this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .withErrorHandling()
    }

    fun <T> Flowable<T>.subOnIoWithErrorHandling(): Flowable<T> {
        return this
                .subscribeOn(Schedulers.io())
                .withErrorHandling()
    }

    fun <T> Flowable<T>.withErrorHandling(): Flowable<T> {
        return this.onErrorResumeNext{ throwable: Throwable ->
                    errorOccurred.onNext(throwable)
                    Flowable.empty<T>()
                }
    }
}