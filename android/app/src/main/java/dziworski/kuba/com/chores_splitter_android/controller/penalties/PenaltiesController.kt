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
import dziworski.kuba.com.chores_splitter_android.controller.common.UsersController
import dziworski.kuba.com.chores_splitter_android.http.GetPenaltyDto
import dziworski.kuba.com.chores_splitter_android.http.GetUserDto
import dziworski.kuba.com.penaltys_splitter_android.controller.AddPenaltyController
import io.reactivex.disposables.CompositeDisposable

class PenaltiesController : Controller() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addPenaltyBtn : ImageButton
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        retainViewMode = RetainViewMode.RETAIN_DETACH
        val view = inflater.inflate(R.layout.controller_penalties, container, false)

        recyclerView = view.findViewById(R.id.penalties_list_recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(view.context))
        addPenaltyBtn = view.findViewById(R.id.add_penalty_btn) as ImageButton
        val penaltiesAdapter = PenaltiesItemAdapter(LayoutInflater.from(applicationContext))
        recyclerView.adapter = penaltiesAdapter


        val usersControllerContainer = view.findViewById(R.id.penalties_users_controller) as ViewGroup
        val usersRouter = getChildRouter(usersControllerContainer).setPopsLastView(true)
        val usersController = UsersController()
        usersController.userChangeListener = { user: GetUserDto ->
            penaltiesAdapter.userId = user.id
            addPenaltyBtn.setOnClickListener {
                router.pushController(RouterTransaction.with(AddPenaltyController(user.id)))
            }
        }
        usersRouter.setRoot(RouterTransaction.with(usersController))

        return view
    }

    override fun onDestroyView(view: View) {
        disposable.clear()
    }

    inner class PenaltiesItemAdapter(val inflater: LayoutInflater) : RecyclerView.Adapter<PenaltiesItemAdapter.ViewHolder>() {

        var userId: Long? = null
            get
            set(value) {
                field = value
                notifyDataSetChanged()
            }


        fun penaltiesForCurrentUser(): List<GetPenaltyDto> {
            return items.filter { it.userId == userId }
        }

        var items: List<GetPenaltyDto> = listOf()

        init {
            disposable.add(RxGateway
                    .penaltiesFlowable
                    .map { it.penalties }
                    .subscribe {
                        items = it
                        notifyDataSetChanged()
                    })
        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val row = inflater.inflate(R.layout.row_penalty, parent, false)
            return ViewHolder(row)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(penaltiesForCurrentUser().get(position))
        }

        override fun getItemCount(): Int {
            return penaltiesForCurrentUser().size
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