package dziworski.kuba.com.chores_splitter_android.controller

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.bluelinelabs.conductor.Controller
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.GetTaskDto
import io.reactivex.rxkotlin.subscribeBy

class TasksListController : Controller {

    constructor(userId:Long) : this(Boundles.putUserId(userId))
    constructor(bundle:Bundle) : super(bundle)

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_tasks_list, container, false)
        recyclerView = view.findViewById(R.id.tasks_list_recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(view.context))
        recyclerView.adapter = TaskItemAdapter(Boundles.getUserId(args),LayoutInflater.from(applicationContext))
        return view
    }


    inner class TaskItemAdapter(val userId:Long,val inflater: LayoutInflater) : RecyclerView.Adapter<TaskItemAdapter.ViewHolder>() {

        init {
            RxGateway
                    .tasksFlowable
                    .map { it.tasks.filter { it.user.id == userId } }
                    .subscribeBy (
                            onNext = {
                                items = it
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

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val nameText = view.findViewById(R.id.row_task_name_txt) as TextView
            val pointsText = view.findViewById(R.id.row_task_points_text) as TextView
            val completedCheckBox = view.findViewById(R.id.row_task_completed_checkbox) as CheckBox

            fun bind(item: GetTaskDto) {
                nameText.text = item.chore.name
                pointsText.text = item.chore.points.toString() + " points"
                completedCheckBox.isChecked = item.completed
                completedCheckBox.setOnClickListener {
                    RxGateway.setTaskCompleted(completedCheckBox.isChecked,item.id)
                }
            }
        }
    }

    companion object Boundles {
        val USER_ID_KEY = "TASK_LIST_USER_ID"

        fun getUserId(bundle:Bundle): Long {
            return bundle.getLong(USER_ID_KEY)
        }
        fun putUserId(value: Long) : Bundle {
            val bundle = Bundle()
            bundle.putLong(USER_ID_KEY,value)
            return bundle
        }
    }
}
