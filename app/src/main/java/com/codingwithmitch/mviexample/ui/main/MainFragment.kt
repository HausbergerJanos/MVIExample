package com.codingwithmitch.mviexample.ui.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingwithmitch.mviexample.R
import com.codingwithmitch.mviexample.model.BlogPost
import com.codingwithmitch.mviexample.ui.DataStateListener
import com.codingwithmitch.mviexample.ui.main.state.MainStateEvent
import com.codingwithmitch.mviexample.ui.main.state.MainStateEvent.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.ClassCastException

class MainFragment : Fragment(), BlogRecyclerAdapter.Interaction {

    lateinit var viewModel: MainViewModel

    lateinit var dataStateHandler: DataStateListener

    lateinit var blogRecyclerAdapter: BlogRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        subscribeObservers()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            blogRecyclerAdapter = BlogRecyclerAdapter(this@MainFragment)
            adapter = blogRecyclerAdapter
        }
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        Log.d("DEBUG-->", "Clicked: $position")
        Log.d("DEBUG-->", "Clicked: $item")
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            println("DEBUG: DataState: ${dataState}")

            // Handle loading and error message
            dataStateHandler.onDataStateChange(dataState)

            // Handle Data<T>
            dataState.data?.let { event ->
                event.getContentIfNotHandled()?.let { mainViewState ->
                    mainViewState.blogPosts?.let{
                        // set BlogPosts data
                        viewModel.setBlogListData(it)
                    }

                    mainViewState.user?.let{
                        // set User data
                        viewModel.setUser(it)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->
            viewState.blogPosts?.let {
                // set BlogPosts to RecyclerView
                blogRecyclerAdapter.submitList(it)
            }

            viewState.user?.let{
                // set User data to widgets
                println("DEBUG: Setting User data: ${viewState.user}")
            }
        })
    }

    fun triggerGetUserEvent(){
        viewModel.setStateEvent(GetUserEvent("1"))
    }

    fun triggerGetBlogsEvent(){
        viewModel.setStateEvent(GetBlogPostsEvent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_get_blogs-> triggerGetBlogsEvent()

            R.id.action_get_user-> triggerGetUserEvent()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            Log.d("DEBUG-->", "$context must implement DataStateListener")
        }
    }
}














