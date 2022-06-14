package com.lenskart.trendinggits

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lenskart.trendinggits.data.Repo
import com.lenskart.trendinggits.data.UserAdapter
import com.lenskart.trendinggits.network.RetrofitServiceKt
import com.lenskart.trendinggits.repository.UserRepositoryKt
import com.lenskart.trendinggits.viewmodel.MyViewModelFactory
import com.lenskart.trendinggits.viewmodel.UserListViewModelKt
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), UserAdapter.OnUserItemClickedListener {


    private lateinit var mViewModel: UserListViewModelKt
    private lateinit var userRecycler: RecyclerView
    private lateinit var contentLoadingProgressBar: ContentLoadingProgressBar
    private lateinit var retryBtn: AppCompatButton
    private  var userAdapter: UserAdapter? = null
     private  val userKtList  = ArrayList<Repo>()
    private val userKtListSearch  = ArrayList<Repo>()



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

        mViewModel.errorMessage.observe(
            this
        ) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        mViewModel.loadingStatus.observe(this) {
            contentLoadingProgressBar.visibility = View.GONE
            if (it) {
                retryBtn.visibility = View.GONE
                userKtList.addAll( mViewModel.getOriginalList())
                userKtListSearch.addAll( userKtList)
                userAdapter =
                    UserAdapter(userKtListSearch, this)
                userRecycler.adapter = userAdapter

                mViewModel.userKtListSearched.observe(this,
                    Observer { key: String ->
                        searchFilter(key)
                    })

            } else {
                retryBtn.visibility = View.VISIBLE
            }
        }
        mViewModel.intiUserList()
    }

    override fun onUserItemClicked(position: Int) {
        Toast.makeText(this,   userKtListSearch.get(position).repoName, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Type.."

        val oldSearchKey: String? = mViewModel.userKtListSearched.value
        if (oldSearchKey!= null && oldSearchKey.isNotEmpty()) {
            searchView.setQuery(oldSearchKey, false)
        }
        searchFilter(mViewModel.userKtListSearched.value)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mViewModel.userKtListSearched.postValue(newText)
                return true
            }
        })

        return true
    }

    private fun searchFilter(key: String?) {
          val filteredList = ArrayList<Repo>()
        if (key == null || key.isEmpty()) {
            filteredList.addAll(userKtList)
        } else {
            val filterPattern =
                key.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
            for (item in userKtList) {
                if (item.repoName.lowercase(Locale.getDefault()).contains(filterPattern)) {
                    filteredList.add(item)
                }
            }
        }


        userKtListSearch.clear()
        userKtListSearch.addAll(filteredList)
        userAdapter?.notifyDataSetChanged()
    }
}