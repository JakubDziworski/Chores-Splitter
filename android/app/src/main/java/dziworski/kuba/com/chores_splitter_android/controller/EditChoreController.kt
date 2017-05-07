package dziworski.kuba.com.chores_splitter_android.controller

import android.os.Bundle
import android.view.View
import android.widget.TextView
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.EditChoreDto
import dziworski.kuba.com.chores_splitter_android.http.GetChoreDto

class EditChoreController : ChoresEditorController {

    constructor() : super()
    constructor(chore: GetChoreDto) : this(Bundles.withChore(chore))
    private constructor(bundle: Bundle) : super(bundle)


    override fun onViewBinded(root: View) {
        choreNameText.setText(Bundles.getChoreName(args),TextView.BufferType.EDITABLE)
        chorePointsPicker.value = Bundles.getChorePoints(args)
        choreIntervalPicker.value = Bundles.getChoreInterval(args) ?: 0
        choreIntervalCheckBox.isChecked = Bundles.getChoreInterval(args) != null
    }

    override fun okClicked() {
        editChore()
        super.okClicked()
    }

    fun editChore() {
        val chore = EditChoreDto(
                Bundles.getChoreId(args).toString(),
                choreNameText.text.toString(),
                chorePointsPicker.value,
                if(choreIntervalCheckBox.isChecked) choreIntervalPicker.value else null
        )
        RxGateway.editChore(chore)
    }


    companion object Bundles {
        val CHORE_ID_KEY = "ADD_EDIT_DIALOG_CHORE_ID"
        val CHORE_NAME_KEY = "ADD_EDIT_DIALOG_CHORE_NAME"
        val CHORE_INTERVAL_KEY = "ADD_EDIT_DIALOG_CHORE_INTERVAL"
        val CHORE_POINTS_KEY = "ADD_EDIT_DIALOG_CHORE_POINTS"
        fun withChore(chore: GetChoreDto): Bundle {
            val b = Bundle()
            b.putLong(CHORE_ID_KEY,chore.id)
            b.putString(CHORE_NAME_KEY,chore.name)
            b.putInt(CHORE_POINTS_KEY,chore.points)
            chore.interval?.let { b.putInt(CHORE_INTERVAL_KEY,chore.interval) }
            return b
        }
        fun getChoreId(bundle: Bundle) : Long {
            return bundle.getLong(CHORE_ID_KEY)
        }
        fun getChoreName(bundle: Bundle): String {
            return bundle.getString(CHORE_NAME_KEY)
        }
        fun getChorePoints(bundle: Bundle) : Int {
            return bundle.getInt(CHORE_POINTS_KEY)
        }
        fun getChoreInterval(bundle: Bundle): Int? {
            val interval = bundle.getInt(CHORE_INTERVAL_KEY)
            return if (interval < 1) null else interval
        }

    }

}