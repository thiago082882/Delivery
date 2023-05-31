package com.example.delivery.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.delivery.fragments.client.ClientOrdersStatusFragment

class TabPagerAdapter(
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
                val clientStatusFragment = ClientOrdersStatusFragment()
                clientStatusFragment.arguments = bundle
                return clientStatusFragment
            }

            1 -> {
                val bundle = Bundle()
                bundle.putString("status", "DESPACHADO")
                val clientStatusFragment = ClientOrdersStatusFragment()
                clientStatusFragment.arguments = bundle
                return clientStatusFragment
            }

            2 -> {
                val bundle = Bundle()
                bundle.putString("status", "ENCAMINHADO")
                val clientStatusFragment = ClientOrdersStatusFragment()
                clientStatusFragment.arguments = bundle
                return clientStatusFragment
            }

            3 -> {
                val bundle = Bundle()
                bundle.putString("status", "ENTREGUE")
                val clientStatusFragment = ClientOrdersStatusFragment()
                clientStatusFragment.arguments = bundle
                return clientStatusFragment
            }

            else -> return ClientOrdersStatusFragment()
        }
    }
}