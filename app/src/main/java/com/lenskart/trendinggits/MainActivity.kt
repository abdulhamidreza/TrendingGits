package com.lenskart.trendinggits

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.ContentLoadingProgressBar
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
    private lateinit var contentLoadingProgressBar: ContentLoadingProgressBar
    private lateinit var retryBtn: AppCompatButton
    private lateinit var userAdapter: UserAdapter
    private var userKtList: List<Repo> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userRecycler = findViewById(R.id.recyclerview)
        contentLoadingProgressBar = findViewById(R.id.loadProgressBar)
        retryBtn = findViewById(R.id.retryBtn)
        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.setHasFixedSize(true)

        val retrofitService: RetrofitServiceKt = RetrofitServiceKt.Companion.getInstance()
        val userRepositoryKt = UserRepositoryKt(retrofitService)
        mViewModel = ViewModelProvider(this, MyViewModelFactory(userRepositoryKt)).get(
            UserListViewModelKt::class.java
        )

        retryBtn.setOnClickListener {
            retryBtn.visibility = View.GONE
            loadUserList()
        }

        loadUserList()

    }

    private fun loadUserList() {
        contentLoadingProgressBar.visibility = View.VISIBLE
        mViewModel.userKtList.observe(this,
            Observer { userListSet: List<Repo> ->
                userAdapter =
                    UserAdapter(userListSet, this)
                userKtList = userListSet
                userRecycler.setAdapter(userAdapter)
                userAdapter.notifyDataSetChanged()
            })
        mViewModel.errorMessage.observe(
            this
        ) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        mViewModel.loadingStatus.observe(this) {
            contentLoadingProgressBar.visibility = View.GONE
            if (it) {
                retryBtn.visibility = View.GONE
            }else{
                retryBtn.visibility = View.VISIBLE
            }
        }
        mViewModel.intiUserList()
    }

    override fun onUserItemClicked(position: Int) {
        TODO("Not yet implemented")
    }


}