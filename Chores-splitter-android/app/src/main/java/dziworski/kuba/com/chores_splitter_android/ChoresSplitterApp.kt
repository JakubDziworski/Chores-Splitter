package dziworski.kuba.com.chores_splitter_android

import android.app.Application
import android.content.Context

class ChoresSplitterApp() : Application() {

    companion object {
        lateinit var instance: Context
            private set
    }

    init {
        instance = this
    }
}

