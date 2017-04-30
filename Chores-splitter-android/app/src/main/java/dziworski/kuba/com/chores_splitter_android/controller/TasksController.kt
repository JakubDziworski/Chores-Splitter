package dziworski.kuba.com.chores_splitter_android.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.GetUserDto
import io.reactivex.rxkotlin.subscribeBy

class TasksController : Controller() {
    lateinit var usersSpinner : Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_tasks, container, false)
        val list = view.findViewById(R.id.tasks_list_container) as ViewGroup
        val tasksListRouter = getChildRouter(list).setPopsLastView(true);
        usersSpinner = view.findViewById(R.id.users_spinner) as Spinner
        val spinnerAdapter = ArrayAdapter<GetUserDto>(view.context,R.layout.support_simple_spinner_dropdown_item, mutableListOf())
        usersSpinner.setAdapter(spinnerAdapter)
        usersSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val user = parent.getItemAtPosition(position) as GetUserDto
                if(tasksListRouter.hasRootController()) tasksListRouter.popCurrentController()
                tasksListRouter.setRoot(RouterTransaction.with(TasksListController(user.id)))
            }
        }
        RxGateway
            .usersFlowable
            .subscribeBy (
                    onNext = {
                        spinnerAdapter.clear()
                        spinnerAdapter.addAll(it.users)
                    }
            )
        return view
    }


}