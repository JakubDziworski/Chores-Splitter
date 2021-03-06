package dziworski.kuba.com.chores_splitter_android.controller.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.ButterKnife
import com.bluelinelabs.conductor.Controller
import dziworski.kuba.com.chores_splitter_android.R

/**
 * Created by jdziworski on 30.04.17.
 */
abstract class ChoresEditorController : Controller {

    lateinit protected var choreNameText : EditText
    lateinit protected var choreIntervalCheckBox : CheckBox
    lateinit protected var chorePointsPicker : NumberPicker
    lateinit protected var choreIntervalPicker : NumberPicker
    lateinit protected var cancelBtn : Button
    lateinit protected var okBtn : Button

    constructor() : super()
    constructor(bundle: Bundle) : super(bundle)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val root = inflater.inflate(R.layout.controller_chores_editor,container,false)
        ButterKnife.bind(this, root)
        choreNameText = root.findViewById(R.id.add_modify_chore_name) as EditText
        choreIntervalCheckBox = root.findViewById(R.id.add_modify_chore_interval_enabled) as CheckBox
        chorePointsPicker = root.findViewById(R.id.add_modify_chore_points_picker) as NumberPicker
        choreIntervalPicker = root.findViewById(R.id.add_modify_chore_interval_picker) as NumberPicker
        choreIntervalPicker.maxValue = 365
        choreIntervalPicker.minValue = 0
        chorePointsPicker.maxValue = 500
        chorePointsPicker.minValue = 0
        cancelBtn = root.findViewById(R.id.add_modify_chore_cancel_btn) as Button
        okBtn = root.findViewById(R.id.add_modify_chore_ok_btn) as Button
        okBtn.setOnClickListener { okClicked() }
        cancelBtn.setOnClickListener { cancelClicked() }
        setupIntervalPickerDisabling()
        onViewBinded(root)
        return root
    }

    fun setupIntervalPickerDisabling() {
        choreIntervalPicker.isEnabled  = choreIntervalCheckBox.isChecked
        choreIntervalPicker.alpha  = if (choreIntervalCheckBox.isChecked) 1.0f else 0.1f
        choreIntervalCheckBox.setOnCheckedChangeListener{ btn,isChecked ->
            choreIntervalPicker.isEnabled = isChecked
            choreIntervalPicker.alpha = if (isChecked) 1.0f else 0.1f
        }
    }

    open fun onViewBinded(root: View) {

    }

    open fun okClicked() {
        router.popController(this)
    }

    open fun cancelClicked() {
        router.popController(this)
    }

    override fun handleBack(): Boolean {
        router.popController(this)
        return true
    }
}