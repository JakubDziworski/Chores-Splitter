package dziworski.kuba.com.chores_splitter_android.controller.chores

import dziworski.kuba.com.chores_splitter_android.controller.common.ChoresEditorController

class AddChoreController() : ChoresEditorController() {

    override fun okClicked() {
        createChore()
        super.okClicked()
    }

    override fun onViewBinded(root: android.view.View) {
        val title = root.findViewById(dziworski.kuba.com.chores_splitter_android.R.id.add_modify_chore_title_txt) as android.widget.TextView
        title.text = "Add Chore"
    }

    private fun createChore() {
        val chore = dziworski.kuba.com.chores_splitter_android.http.AddChoreDto(
                choreNameText.text.toString(),
                chorePointsPicker.value,
                if (choreIntervalCheckBox.isChecked) choreIntervalPicker.value else null
        )
        dziworski.kuba.com.chores_splitter_android.RxGateway.addChore(chore)
    }

}