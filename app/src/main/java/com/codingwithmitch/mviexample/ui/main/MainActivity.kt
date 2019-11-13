package com.codingwithmitch.mviexample.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.mviexample.R
import com.codingwithmitch.mviexample.ui.DataStateListener
import com.codingwithmitch.mviexample.util.DataState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DataStateListener {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        showMainFragment()
    }

    fun showMainFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,
                MainFragment(), "MainFragment")
            .commit()
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        handleDataStateChange(dataState)
    }

    private fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let {

            // Handle loading
            showLoading(it.loading)

            // Handle message
            dataState.message?.let { message ->
                showToast(message)
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showLoading(isVisible: Boolean) {
        progress_bar?.let { progressBar ->
            if (isVisible) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
}























