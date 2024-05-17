package com.example.mypermission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypermission.Adapter.notificationAdapter
import com.example.mypermission.databinding.FragmentNotificationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NotificationFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
            binding=FragmentNotificationBinding.inflate(layoutInflater,container,false)
        val notifications = listOf("Alert: Exceeded limit! 2 hours of continuous screen time may cause eye strain.","You've spent 2 hours on Instagram today,Consider a break!", "Mindful moment: Take a deep breath and relax.","Great job limiting social media! Keep it up!" )
        val notificationImages = listOf(R.drawable.alert,R.drawable.breaktime,R.drawable.meditate,R.drawable.thumbsup)
        val adapter = notificationAdapter(
            ArrayList(notifications),
            ArrayList(notificationImages)
        )
        binding.notificationRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {
    }
}