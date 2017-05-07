package dziworski.kuba.com.chores_splitter_android.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import dziworski.kuba.com.chores_splitter_android.R
import dziworski.kuba.com.chores_splitter_android.RxGateway
import dziworski.kuba.com.chores_splitter_android.http.GetUserDto
import io.reactivex.rxkotlin.subscribeBy

class UsersController : Controller() {
    private lateinit var usersSpinner : Spinner
    lateinit var userChangeListener: (GetUserDto) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_users, container, false)
        usersSpinner = view.findViewById(R.id.users_spinner) as Spinner
        val spinnerAdapter = ArrayAdapter<GetUserDto>(view.context,R.layout.support_simple_spinner_dropdown_item, mutableListOf())
        usersSpinner.setAdapter(spinnerAdapter)
        usersSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val user = parent.getItemAtPosition(position) as GetUserDto
                userChangeListener(user)
            }
        }
        RxGateway
            .usersFlowable
            .subscribeBy (
                    onNext = {
                        spinnerAdapter.clear()
                        spinnerAdapter.addAll(it.users)
                    }
            )
        return view
    }



}