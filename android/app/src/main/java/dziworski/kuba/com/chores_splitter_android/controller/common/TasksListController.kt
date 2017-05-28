package dziworski.kuba.com.chores_splitter_android.controller.common

import android.os.Bundle
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
import org.joda.time.LocalDate
import org.zakariya.stickyheaders.SectioningAdapter
import org.zakariya.stickyheaders.StickyHeaderLayoutManager

class TasksListController : Controller {

    constructor(userId:Long) : this(putUserId(userId))
    constructor(bundle: Bundle) : super(bundle)

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_tasks_list, container, false)
        recyclerView = view.findViewById(R.id.tasks_list_recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(StickyHeaderLayoutManager())
        recyclerView.adapter = TaskItemAdapter(getUserId(args), LayoutInflater.from(applicationContext))
        return view
    }


    inner class TaskItemAdapter(val userId:Long,val inflater: LayoutInflater) : SectioningAdapter() {

        init {
            RxGateway.tasksFlowable
                    .map { it.tasks.filter { it.userId == userId } }
                    .subscribeBy (
                            onNext = {
                                sections = it
                                        .groupBy { LocalDate(it.assignedAt) }
                                        .map { (date,tasks) ->
                                            Section(date,tasks)
                                        }
                                notifyAllSectionsDataSetChanged()
                            }
                    )
        }
        var sections : List<Section> = listOf()


        override fun getNumberOfSections(): Int {
            return sections.size
        }

        override fun getNumberOfItemsInSection(sectionIndex: Int): Int {
            return sections[sectionIndex].tasks.count()
        }

        override fun doesSectionHaveFooter(sectionIndex: Int): Boolean {
            return false
        }

        override fun doesSectionHaveHeader(sectionIndex: Int): Boolean {
            return true
        }

        override fun onCreateItemViewHolder(parent: ViewGroup, itemUserType: Int): SectioningAdapter.ItemViewHolder {
            val row = inflater.inflate(R.layout.row_task,parent,false)
            return ItemViewHolder(row)
        }

        override fun onCreateHeaderViewHolder(parent: ViewGroup, headerUserType: Int): SectioningAdapter.HeaderViewHolder {
            val row = inflater.inflate(R.layout.row_tasks_header_date,parent,false)
            return HeaderViewHolder(row)
        }

        override fun onBindItemViewHolder(viewHolder: SectioningAdapter.ItemViewHolder, sectionIndex: Int, taskIndex: Int, itemUserType: Int) {
            (viewHolder as ItemViewHolder).bind(sections[sectionIndex].tasks[taskIndex])
        }

        override fun onBindHeaderViewHolder(viewHolder: SectioningAdapter.HeaderViewHolder, sectionIndex: Int, headerUserType: Int) {
            (viewHolder as HeaderViewHolder).bind(sections[sectionIndex])
        }


        inner class HeaderViewHolder(view: View) : SectioningAdapter.HeaderViewHolder(view) {
            val titleTextView = view.findViewById(R.id.row_tasks_header_date_txt) as TextView

            fun bind(section:Section) {
                titleTextView.text = section.date.toString()
            }
        }

        inner class ItemViewHolder(view: View) : SectioningAdapter.ItemViewHolder(view) {

            val nameText = view.findViewById(R.id.row_task_name_txt) as TextView
            val pointsText = view.findViewById(R.id.row_task_points_text) as TextView
            val completedCheckBox = view.findViewById(R.id.row_task_completed_checkbox) as CheckBox

            fun bind(item: GetTaskDto) {
                nameText.text = item.chore.name
                pointsText.text = item.chore.points.toString() + " points"
                completedCheckBox.isChecked = item.completedAt != null
                completedCheckBox.setOnClickListener {
                    RxGateway.setTaskCompleted(completedCheckBox.isChecked, item.id)
                }
            }
        }

        inner class Section(val date:LocalDate,val tasks:List<GetTaskDto>)
    }

    companion object Boundles {
        val USER_ID_KEY = "TASK_LIST_USER_ID"

        fun getUserId(bundle: Bundle): Long {
            return bundle.getLong(USER_ID_KEY)
        }
        fun putUserId(value: Long) : Bundle {
            val bundle = Bundle()
            bundle.putLong(USER_ID_KEY,value)
            return bundle
        }
    }
}
