package dziworski.kuba.com.chores_splitter_android.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import dziworski.kuba.com.chores_splitter_android.R

class ChoresController : Controller() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_chores, container, false)
        return view
    }
}