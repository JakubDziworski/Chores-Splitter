package dziworski.kuba.com.chores_splitter_android.controller.chores

import dziworski.kuba.com.chores_splitter_android.controller.common.ChoresEditorController

class EditChoreController : ChoresEditorController {

    constructor() : super()
    constructor(chore: dziworski.kuba.com.chores_splitter_android.http.GetChoreDto) : this(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.withChore(chore))
    private constructor(bundle: android.os.Bundle) : super(bundle)


    override fun onViewBinded(root: android.view.View) {
        choreNameText.setText(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.getChoreName(args), android.widget.TextView.BufferType.EDITABLE)
        chorePointsPicker.value = dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.getChorePoints(args)
        choreIntervalPicker.value = dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.getChoreInterval(args) ?: 0
        choreIntervalCheckBox.isChecked = dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.getChoreInterval(args) != null
    }

    override fun okClicked() {
        editChore()
        super.okClicked()
    }

    fun editChore() {
        val chore = dziworski.kuba.com.chores_splitter_android.http.EditChoreDto(
                Bundles.getChoreId(args).toString(),
                choreNameText.text.toString(),
                chorePointsPicker.value,
                if (choreIntervalCheckBox.isChecked) choreIntervalPicker.value else null
        )
        dziworski.kuba.com.chores_splitter_android.RxGateway.editChore(chore)
    }


    companion object Bundles {
        val CHORE_ID_KEY = "ADD_EDIT_DIALOG_CHORE_ID"
        val CHORE_NAME_KEY = "ADD_EDIT_DIALOG_CHORE_NAME"
        val CHORE_INTERVAL_KEY = "ADD_EDIT_DIALOG_CHORE_INTERVAL"
        val CHORE_POINTS_KEY = "ADD_EDIT_DIALOG_CHORE_POINTS"
        fun withChore(chore: dziworski.kuba.com.chores_splitter_android.http.GetChoreDto): android.os.Bundle {
            val b = android.os.Bundle()
            b.putLong(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.CHORE_ID_KEY,chore.id)
            b.putString(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.CHORE_NAME_KEY,chore.name)
            b.putInt(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.CHORE_POINTS_KEY,chore.points)
            chore.interval?.let { b.putInt(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.CHORE_INTERVAL_KEY,chore.interval) }
            return b
        }
        fun getChoreId(bundle: android.os.Bundle) : Long {
            return bundle.getLong(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.CHORE_ID_KEY)
        }
        fun getChoreName(bundle: android.os.Bundle): String {
            return bundle.getString(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.CHORE_NAME_KEY)
        }
        fun getChorePoints(bundle: android.os.Bundle) : Int {
            return bundle.getInt(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.CHORE_POINTS_KEY)
        }
        fun getChoreInterval(bundle: android.os.Bundle): Int? {
            val interval = bundle.getInt(dziworski.kuba.com.chores_splitter_android.controller.chores.EditChoreController.Bundles.CHORE_INTERVAL_KEY)
            return if (interval < 1) null else interval
        }

    }

}