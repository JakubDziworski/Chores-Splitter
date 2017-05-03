package dziworski.kuba.com.chores_splitter_android.controller

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.GetPenaltyDto
import dziworski.kuba.com.chores_splitter_android.http.GetUserDto

class PenaltiesController : Controller() {

    private lateinit var recyclerView: RecyclerView
    private var userId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_penalties, container, false)

        recyclerView = view.findViewById(R.id.penalties_list_recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(view.context))
        recyclerView.adapter = PenaltiesItemAdapter(LayoutInflater.from(applicationContext))

        val usersControllerContainer = view.findViewById(R.id.penalties_users_controller) as ViewGroup
        val usersRouter = getChildRouter(usersControllerContainer);
        val usersController = UsersController()
        usersController.userChangeListener = { user: GetUserDto ->
            userId = user.id
        }
        usersRouter.pushController(RouterTransaction.with(usersController))

        return view
    }

    inner class PenaltiesItemAdapter(val inflater: LayoutInflater) : RecyclerView.Adapter<PenaltiesItemAdapter.ViewHolder>() {

        init {
            RxGateway
                    .penaltiesFlowable
                    .map { it.penalties.filter { it.userId == userId } }
                    .subscribe {
                        items = it
                        notifyDataSetChanged()
                    }
        }

        var items: List<GetPenaltyDto> = listOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val row = inflater.inflate(R.layout.row_penalty, parent, false)
            return ViewHolder(row)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items.get(position))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val reasonText = view.findViewById(R.id.row_penalty_reason_txt) as TextView
            val pointsText = view.findViewById(R.id.row_penalty_points_txt) as TextView

            fun bind(item: GetPenaltyDto) {
                reasonText.text = item.reason
                pointsText.text = item.points.toString() + " points"
            }
        }
    }

}