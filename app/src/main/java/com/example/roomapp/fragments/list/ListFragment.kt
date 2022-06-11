package com.example.roomapp.fragments.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.roomapp.R
import com.example.roomapp.model.User
import com.example.roomapp.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_list.view.*
import java.lang.NumberFormatException

class ListFragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel
    private val user = User(0,"0")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]


        val adapter = ListAdapter(mUserViewModel,context)
        val recyclerView = view.viewPager2
        recyclerView.adapter = adapter


        mUserViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                mUserViewModel.addUser(user)
            }
        }

        mUserViewModel.readAllData.observe(viewLifecycleOwner) { user ->
            adapter.setData(user)
        }

        try {
            val i = activity?.intent?.action.toString()
            Log.e("TAG","Nav page $i")
            view.viewPager2.currentItem = i.toInt()
            view.viewPager2.doOnPreDraw {
                view.viewPager2.currentItem = i.toInt()
            }
        }catch (e:NumberFormatException){
            Log.d("TaGErr",e.message.toString())
        }
        // Add menu
        setHasOptionsMenu(false)

        return view
    }
}