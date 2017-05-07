package dziworski.kuba.com.chores_splitter_android

import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by jdziworski on 07.05.17.
 */
class RxGatewayTest {
    @Test
    fun test() {
        val tick = Flowable.interval(0, 1, TimeUnit.SECONDS).map { Unit }
        val publisher: PublishProcessor<Unit> = PublishProcessor.create<Unit>()

        tick.mergeWith(publisher).subscribe { onNext ->
            println("got $onNext")
        }

        publisher.onNext(Unit)

        Thread.sleep(5000)
    }
}