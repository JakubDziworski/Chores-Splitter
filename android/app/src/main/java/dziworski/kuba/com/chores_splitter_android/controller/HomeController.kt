package dziworski.kuba.com.chores_splitter_android.controller

import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.controller.chores.ChoresController
import dziworski.kuba.com.chores_splitter_android.controller.tasks.TasksController

class HomeController :  Controller() {

    lateinit var viewPager:ViewPager
    lateinit var tabLayout:TabLayout

    val pagerAdapter : PagerAdapter = object: RouterPagerAdapter(this) {
        val tabs = listOf("tasks" to TasksController(),"chores" to ChoresController(),"penalties" to PenaltiesController())

        override fun getCount(): Int {
            return tabs.size
        }

        override fun configureRouter(router: Router, position: Int) {
            router.setRoot(RouterTransaction.with(tabs.get(position).second))
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabs.get(position).first
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_main, container, false)
        viewPager = view.findViewById(R.id.main_view_pager) as ViewPager
        tabLayout = view.findViewById(R.id.main_tab_layout) as TabLayout
        viewPager.setAdapter(pagerAdapter)
        viewPager.offscreenPageLimit = pagerAdapter.count
        tabLayout.setupWithViewPager(viewPager)
        setupErrorHandling()
        return view
    }

    private fun setupErrorHandling() {
        RxGateway.errorsFlowable.subscribe { throwable ->
            Log.e(RxGateway::class.toString(), throwable.stackTrace.joinToString("\n"))
            Toast.makeText(
                    applicationContext,
                    "NETWORK ERROR $throwable",
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onDetach(view: View) {
        RxGateway.tickEnabled = false
    }

    override fun onAttach(view: View) {
        RxGateway.tickEnabled = true
    }
}

