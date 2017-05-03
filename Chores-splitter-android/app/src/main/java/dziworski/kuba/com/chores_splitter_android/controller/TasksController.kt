package dziworski.kuba.com.chores_splitter_android.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.GetUserDto
import io.reactivex.rxkotlin.subscribeBy

class TasksController : Controller() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_tasks, container, false)

        val taskListControllerContainer = view.findViewById(R.id.tasks_list_container) as ViewGroup
        val tasksListRouter = getChildRouter(taskListControllerContainer);
        val addTaskBtn = view.findViewById(R.id.add_task_btn) as ImageButton

        val usersControllerContainer = view.findViewById(R.id.tasks_users_controller) as ViewGroup
        val usersRouter = getChildRouter(usersControllerContainer);
        val usersController = UsersController()
        usersController.userChangeListener = { user : GetUserDto ->
            if(tasksListRouter.hasRootController()) tasksListRouter.popCurrentController()
            tasksListRouter.setRoot(RouterTransaction.with(TasksListController(user.id)))
            addTaskBtn.setOnClickListener {
                router.pushController(RouterTransaction.with(StartTaskController(user.id)))
            }
        }
        usersRouter.pushController(RouterTransaction.with(usersController))
        return view
    }


}