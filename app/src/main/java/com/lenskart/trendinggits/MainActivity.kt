package com.lenskart.trendinggits

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lenskart.trendinggits.network.RetrofitServiceKt
import com.lenskart.trendinggits.data.Repo
import com.lenskart.trendinggits.data.UserAdapter
import com.lenskart.trendinggits.repository.UserRepositoryKt
import com.lenskart.trendinggits.viewmodel.MyViewModelFactory
import com.lenskart.trendinggits.viewmodel.UserListViewModelKt

class MainActivity : AppCompatActivity(), UserAdapter.OnUserItemClickedListener {


    private lateinit var mViewModel: UserListViewModelKt
    private lateinit var userRecycler: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private var userKtList: List<Repo> = ArrayList<Repo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userRecycler = findViewById<RecyclerView>(R.id.recyclerview)
        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.setHasFixedSize(true)

        val retrofitService: RetrofitServiceKt = RetrofitServiceKt.Companion.getInstance()
        val userRepositoryKt = UserRepositoryKt(retrofitService)
        mViewModel = ViewModelProvider(this, MyViewModelFactory(userRepositoryKt)).get(
            UserListViewModelKt::class.java
        )

        loadUserList()

    }

    private fun loadUserList() {
        mViewModel.userKtList.observe(this,
            Observer { userListSet: List<Repo> ->
                userAdapter =
                    UserAdapter(userListSet, this)
                userKtList = userListSet
                userRecycler.setAdapter(userAdapter)
                userAdapter.notifyDataSetChanged()
            })
        mViewModel.errorMessage.observe(this,
            Observer { it: String? ->
                Log.e(
                    "********** getErrorMessage",
                    it!!
                )
            })
        mViewModel.loadingStatus.observe(this, Observer { it: Boolean ->
            Log.e("********** getLoading", "is Loading $it")
            if (it) {
                //     contentLoadingProgressBar.setVisibility(View.GONE)
            } else {
                //   contentLoadingProgressBar.setBackgroundColor(resources.getColor(android.R.color.system_accent1_10))
            }
        })
        mViewModel.intiUserList()
    }

    override fun onUserItemClicked(position: Int) {
        TODO("Not yet implemented")
    }


}