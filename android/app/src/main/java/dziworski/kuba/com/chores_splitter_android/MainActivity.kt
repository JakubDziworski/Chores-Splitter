package dziworski.kuba.com.chores_splitter_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import dziworski.kuba.com.chores_splitter_android.controller.HomeController


class MainActivity : AppCompatActivity() {


    private var router: Router? = null

    override fun onBackPressed() {
        if (router!!.handleBack()) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = findViewById(R.id.controller_container) as ViewGroup
        router = Conductor.attachRouter(this, container, savedInstanceState)
        router!!.setRoot(RouterTransaction.with(HomeController()))
    }
}
