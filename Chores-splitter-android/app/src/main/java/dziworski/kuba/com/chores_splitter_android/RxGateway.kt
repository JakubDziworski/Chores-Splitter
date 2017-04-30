package dziworski.kuba.com.chores_splitter_android

import dziworski.kuba.com.chores_splitter_android.http.Backend
import dziworski.kuba.com.chores_splitter_android.http.BackendService
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object RxGateway {
    val backend : BackendService by lazy {
        Backend.instance
    }

    private val tick: Flowable<Unit> = Flowable.interval(0,1, TimeUnit.SECONDS).map{it -> Unit}
    private val choresChanged: PublishProcessor<Unit> = PublishProcessor.create<Unit>()
    private val tasksChanged: PublishProcessor<Unit> = PublishProcessor.create<Unit>()
    val choresFlowable = tick.mergeWith(choresChanged)
    val usersTasksFlowable = tick.mergeWith(tasksChanged)
    val usersFlowable = tick
            .flatMap{ backend.getUsers()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    val tasksFlowable = tick
            .flatMap{backend.getTasks()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun addChore() {
        choresChanged.onNext(Unit)
    }

    fun addTask() {
        tasksChanged.onNext(Unit)
    }
}