package dziworski.kuba.com.chores_splitter_android.controller

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.GetChoreDto
import io.reactivex.rxkotlin.subscribeBy

class ChoresController : Controller() {

    private lateinit var recyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val root = inflater.inflate(R.layout.controller_chores, container, false) as ViewGroup
        setupAddChoreBtn(root)
        recyclerView = root.findViewById(R.id.chores_recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(root.context))
        recyclerView.adapter = ChoreItemAdapter(LayoutInflater.from(applicationContext))
        return root
    }

    fun setupAddChoreBtn(root:ViewGroup) {
        val addChoreBtn = root.findViewById(R.id.add_chore_btn) as ImageButton
        addChoreBtn.setOnClickListener {
            router.pushController(RouterTransaction.with(AddChoreController()))
        }
    }

    inner class ChoreItemAdapter(val inflater: LayoutInflater) : RecyclerView.Adapter<ChoreItemAdapter.ViewHolder>() {

        init {
            RxGateway
                    .choresFlowable
                    .subscribeBy (
                            onNext = {
                                items = it.chores
                                notifyDataSetChanged()
                            }
                    )
        }
        var items : List<GetChoreDto> = listOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val row = inflater.inflate(R.layout.row_chore,parent,false)
            return ViewHolder(row)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items.get(position))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val nameText = view.findViewById(R.id.row_chore_name_txt) as TextView
            val pointsText = view.findViewById(R.id.row_chore_points_txt) as TextView
            val intervalText = view.findViewById(R.id.row_chore_interval_txt) as TextView
            val editBtn = view.findViewById(R.id.row_chore_edit_btn) as ImageButton

            fun bind(item: GetChoreDto) {
                nameText.text = item.name
                pointsText.text = item.points.toString()
                intervalText.text = item.interval?.toString().orEmpty()
                editBtn.setOnClickListener {
                    router.pushController(RouterTransaction.with(EditChoreController(item)))
                }
            }

        }
    }
}