package dziworski.kuba.com.chores_splitter_android.controller

import android.view.View
import android.widget.TextView
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.AddChoreDto

class AddChoreController() : ChoresEditorController() {

    override fun okClicked() {
        createChore()
        super.okClicked()
    }

    override fun onViewBinded(root: View) {
        val title = root.findViewById(R.id.add_modify_chore_title_txt) as TextView
        title.text = "Add Chore"
    }

    private fun createChore() {
        val chore = AddChoreDto(
                choreNameText.text.toString(),
                chorePointsPicker.value,
                if(choreIntervalCheckBox.isChecked) choreIntervalPicker.value else null
        )
        RxGateway.addChore(chore)
    }

}