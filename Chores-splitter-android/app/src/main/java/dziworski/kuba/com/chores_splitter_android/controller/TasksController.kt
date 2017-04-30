package dziworski.kuba.com.chores_splitter_android.controller

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bluelinelabs.conductor.Controller
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.Backend
import dziworski.kuba.com.chores_splitter_android.http.GetTaskDto
import dziworski.kuba.com.chores_splitter_android.http.GetUserDto
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TasksController : Controller() {
    lateinit var recyclerView: RecyclerView
    lateinit var usersSpinner : Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_tasks, container, false)
        recyclerView = view.findViewById(R.id.tasks_recycler_view) as RecyclerView
        usersSpinner = view.findViewById(R.id.users_spinner) as Spinner
        val spinnerAdapter = ArrayAdapter<GetUserDto>(view.context,R.layout.support_simple_spinner_dropdown_item, mutableListOf())
        usersSpinner.setAdapter(spinnerAdapter)
        RxGateway
            .usersFlowable
            .subscribeBy (
                    onNext = {
                        spinnerAdapter.clear()
                        spinnerAdapter.addAll(it.users)
                    }
            )
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(view.context))
        recyclerView.setAdapter(TaskItemAdapter(LayoutInflater.from(view.context),{usersSpinner.selectedItem.toString()}))

        return view
    }


    class TaskItemAdapter(val inflater: LayoutInflater,getUserId:() -> String) : RecyclerView.Adapter<TaskItemAdapter.ViewHolder>() {

        init {
            RxGateway
                .tasksFlowable
                .subscribeBy (
                    onNext = {
                        items = it.tasks
                        notifyDataSetChanged()
                    }
                )
        }
        var items : List<GetTaskDto> = listOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val row = inflater.inflate(R.layout.row_task,parent,false)
            return ViewHolder(row)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items.get(position))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val nameText = view.findViewById(R.id.row_task_name_txt) as TextView
            val pointsText = view.findViewById(R.id.row_task_points_text) as TextView
            val completedCheckBox = view.findViewById(R.id.row_task_completed_checkbox) as CheckBox

            fun bind(item: GetTaskDto) {
                nameText.text = item.chore.name
                pointsText.text = item.chore.points.toString()
            }
        }
    }

}