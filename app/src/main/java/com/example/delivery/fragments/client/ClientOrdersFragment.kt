package com.example.delivery.fragments.client

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.delivery.R
import com.example.delivery.adapters.TabPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ClientOrdersFragment : Fragment() {


    var myView: View? = null
    var viewpager: ViewPager2? = null
    var tabLayout: TabLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        myView = inflater.inflate(R.layout.fragment_client_orders, container, false)


        viewpager = myView?.findViewById(R.id.viewpager)
        tabLayout = myView?.findViewById(R.id.tab_layout)

        tabLayout?.setSelectedTabIndicatorColor(Color.BLACK)
        tabLayout?.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        tabLayout?.tabTextColors = ContextCompat.getColorStateList(requireContext(),R.color.black)
        tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout?.isInlineLabel = true

        var numberOfTabs = 4
        val adapter =TabPagerAdapter(requireActivity().supportFragmentManager,lifecycle,numberOfTabs)
        viewpager?.adapter = adapter
        viewpager?.isUserInputEnabled=true

        TabLayoutMediator(tabLayout!!,viewpager!!){
            tab,position ->

            when(position){
                0->{
                    tab.text = "PAGO"
                }
                1->{
                    tab.text = "DESPACHADO"
                }
                2->{
                    tab.text = "ENCAMINHADO"
                }
                3->{
                    tab.text = "ENTREGUE"
                }
            }
        }.attach()


        return myView


    }


}