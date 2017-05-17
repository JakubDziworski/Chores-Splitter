package dziworski.kuba.com.penaltys_splitter_android.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.ButterKnife
import com.bluelinelabs.conductor.Controller
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.controller.common.TasksListController
import dziworski.kuba.com.chores_splitter_android.http.AddPenaltyDto

class AddPenaltyController : Controller {

    lateinit protected var penaltyReasonText: EditText
    lateinit protected var penaltyPointsPicker : NumberPicker

    constructor(userId:Long) : this(TasksListController.putUserId(userId))
    constructor(bundle:Bundle) : super(bundle)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val root = inflater.inflate(R.layout.controller_add_penalty,container,false)
        ButterKnife.bind(this, root)
        penaltyReasonText = root.findViewById(R.id.add_penalty_name) as EditText
        penaltyPointsPicker = root.findViewById(R.id.add_penalty_points_spinner) as NumberPicker
        penaltyPointsPicker.maxValue = 500
        penaltyPointsPicker.minValue = 0
        val cancelBtn = root.findViewById(R.id.add_penalty_cancel_btn) as Button
        val okBtn = root.findViewById(R.id.add_penalty_ok_btn) as Button
        val userId : Long = AddPenaltyController.getUserId(args)
        okBtn.setOnClickListener { okClicked(userId) }
        cancelBtn.setOnClickListener { cancelClicked() }
        return root
    }

    fun okClicked(userId: Long) {
        val chore = AddPenaltyDto(
                userId,
                penaltyPointsPicker.value,
                penaltyReasonText.text.toString()
        )
        RxGateway.addPenalty(chore)
        router.popController(this)
    }

    fun cancelClicked() {
        router.popController(this)
    }

    override fun handleBack(): Boolean {
        router.popController(this)
        return true
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