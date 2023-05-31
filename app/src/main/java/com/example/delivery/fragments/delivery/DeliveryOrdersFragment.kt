package com.example.delivery.fragments.delivery

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.delivery.R
import com.example.delivery.adapters.DeliveryTabPagerAdapter
import com.example.delivery.adapters.RestaurantTabPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class DeliveryOrdersFragment : Fragment() {

    var myView: View? = null
    var viewpager: ViewPager2? = null
    var tabLayout: TabLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        myView = inflater.inflate(R.layout.fragment_delivery_orders, container, false)

        viewpager = myView?.findViewById(R.id.viewpager)
        tabLayout = myView?.findViewById(R.id.tab_layout)

        tabLayout?.setSelectedTabIndicatorColor(Color.BLACK)
        tabLayout?.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        tabLayout?.tabTextColors = ContextCompat.getColorStateList(requireContext(),R.color.black)
       // tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
        //tabLayout?.isInlineLabel = true

        var numberOfTabs = 3
        val adapter =
            DeliveryTabPagerAdapter(requireActivity().supportFragmentManager,lifecycle,numberOfTabs)
        viewpager?.adapter = adapter
        viewpager?.isUserInputEnabled=true

        TabLayoutMediator(tabLayout!!,viewpager!!){
                tab,position ->

            when(position){
                0->{
                    tab.text = "DESPACHADO"
                }
                1->{
                    tab.text = "ENCAMINHADO"
                }
                2->{
                    tab.text = "ENTREGUE"
                }
            }
        }.attach()


        return myView


    }


}