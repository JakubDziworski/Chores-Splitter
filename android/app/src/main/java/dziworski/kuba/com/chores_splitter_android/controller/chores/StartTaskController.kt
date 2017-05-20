package dziworski.kuba.com.chores_splitter_android.controller.chores

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bluelinelabs.conductor.Controller
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.controller.common.TasksListController
import dziworski.kuba.com.chores_splitter_android.http.AddTaskDto
import dziworski.kuba.com.chores_splitter_android.http.ChoreId
import dziworski.kuba.com.chores_splitter_android.http.GetChoreDto
import dziworski.kuba.com.chores_splitter_android.http.UserId

class StartTaskController : Controller {
    private lateinit var recyclerView : RecyclerView

    constructor() : super()
    constructor(userId:Long) : this(TasksListController.putUserId(userId))
    constructor(bundle: Bundle) : super(bundle)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val root = inflater.inflate(R.layout.controller_start_task, container, false) as ViewGroup
        recyclerView = root.findViewById(R.id.chores_list_uneditable_recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(root.context))
        recyclerView.adapter = ChoreItemAdapter(LayoutInflater.from(applicationContext))
        return root
    }

    override fun handleBack(): Boolean {
        return router.popCurrentController()
    }

    inner class ChoreItemAdapter(val inflater: LayoutInflater) : RecyclerView.Adapter<ChoreItemAdapter.ViewHolder>() {

        init {
            RxGateway.choresFlowable
                    .subscribe {
                        items = it.chores
                        notifyDataSetChanged()
                    }
        }
        var items : List<GetChoreDto> = listOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val row = inflater.inflate(R.layout.row_chore_uneditable,parent,false)
            return ViewHolder(row)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items.get(position))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

            val nameText = view.findViewById(R.id.row_chore_uneditable_name_txt) as TextView
            val pointsText = view.findViewById(R.id.row_chore_uneditable_points_txt) as TextView
            val intervalText = view.findViewById(R.id.row_chore_uneditable_interval_txt) as TextView

            fun bind(item: GetChoreDto) {
                nameText.text = item.name
                pointsText.text = item.points.toString() + " points"
                intervalText.text = if(item.interval != null) "Every " + item.interval.toString() + " days" else ""
                val addTask =  {
                    val userId = TasksListController.getUserId(args)
                    val task = AddTaskDto(ChoreId(item.id), UserId(userId))
                    RxGateway.addTask(task)
                }
                view.setOnClickListener{
                    addTask()
                    router.popCurrentController()
                }
            }

        }
    }
}