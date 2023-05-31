package com.example.delivery.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.delivery.fragments.client.ClientOrdersStatusFragment
import com.example.delivery.fragments.restaurant.RestaurantOrdersStatusFragment

class RestaurantTabPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var numberOfTabs: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return numberOfTabs
    }


    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                val bundle = Bundle()
                bundle.putString("status", "PAGO")
                val restaurantStatusFragment = RestaurantOrdersStatusFragment()
                restaurantStatusFragment.arguments = bundle
                return restaurantStatusFragment
            }

            1 -> {
                val bundle = Bundle()
                bundle.putString("status", "DESPACHADO")
                val restaurantStatusFragment = RestaurantOrdersStatusFragment()
                restaurantStatusFragment.arguments = bundle
                return restaurantStatusFragment
            }

            2 -> {
                val bundle = Bundle()
                bundle.putString("status", "ENCAMINHADO")
                val restaurantStatusFragment = RestaurantOrdersStatusFragment()
                restaurantStatusFragment.arguments = bundle
                return restaurantStatusFragment
            }

            3 -> {
                val bundle = Bundle()
                bundle.putString("status", "ENTREGUE")
                val restaurantStatusFragment = RestaurantOrdersStatusFragment()
                restaurantStatusFragment.arguments = bundle
                return restaurantStatusFragment
            }

            else -> return RestaurantOrdersStatusFragment()
        }
    }
}