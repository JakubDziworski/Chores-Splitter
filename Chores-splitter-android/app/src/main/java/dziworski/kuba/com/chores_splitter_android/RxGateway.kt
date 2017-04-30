package dziworski.kuba.com.chores_splitter_android

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
    val choresFlowable : Flowable<GetChoresDto>  = tick.mergeWith(choresChanged)
            .flatMap { backend.getChores() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    fun usersTasksFlowable(userId: Long) : Flowable<GetTasksDto> = tick
            .map { userId.toString() }
            .flatMap { backend.getTasksForUser(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    val usersFlowable = tick
            .flatMap { backend.getUsers() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    val tasksFlowable = tick
            .flatMap { backend.getTasks() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun addChore(chore: AddChoreDto) {
        backend.addChore(chore)
                .subscribeOn(Schedulers.io())
                .subscribe{choresChanged.onNext(Unit)}
    }

    fun editChore(chore: EditChoreDto) {
        backend.editChore(chore.choreId,chore)
                .subscribeOn(Schedulers.io())
                .subscribe{choresChanged.onNext(Unit)}
    }

    fun addTask() {
        tasksChanged.onNext(Unit)
    }
}