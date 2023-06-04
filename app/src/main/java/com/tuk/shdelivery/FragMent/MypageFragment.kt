package com.tuk.shdelivery.FragMent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tuk.shdelivery.Activity.ChargeActivity
import com.tuk.shdelivery.R
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.FragmentMypageBinding
import com.tuk.shdelivery.Activity.settingActivity
import com.tuk.shdelivery.Data.MatchDao
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.MyPageViewModel
import com.tuk.shdelivery.MyPageViewModelFactory
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.custom.DeliverTime
import java.util.*


class MypageFragment : Fragment() {
    val intent by lazy { requireActivity().intent }
    val binding by lazy { FragmentMypageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //충전 버튼 설정
        binding.charge.setOnClickListener {
            intent.setClass(requireContext(),ChargeActivity::class.java)
            startActivity(intent)
        }
        //마이페이지 유저이름, 포인트 설정
        val user = intent.getSerializableExtra("user") as User
        binding.userName.text = user.userName
        binding.point.text = user.userPoint.toString() + "P"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = activity?.intent?.getSerializableExtra("user") as User?
        val userId = user?.userId
        val matchDao = MatchDao()  // Initialize matchDao
        val viewModelFactory = MyPageViewModelFactory(matchDao, userId!!)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(MyPageViewModel::class.java)

        MatchDao().getParticipatingMatch(user){
            var match = it
            // Check if participateMatchId is empty
            if (user?.participateMatchId == "") {
                binding.layoutMatchRoom.root.visibility = View.GONE
            } else {
                binding.layoutMatchRoom.root.visibility = View.VISIBLE

                val category = Data.category()

                val diffMillis = match.deliveryTime - match.createTime

                binding.layoutMatchRoom.tag.text = "${match.menu}"
                binding.layoutMatchRoom.description.text = match.description
                binding.layoutMatchRoom.count.text = match.count.toString()
                binding.layoutMatchRoom.tagImage.setImageResource(category[match.menu]!!)
                binding.layoutMatchRoom.store.text = match.storeName
                binding.layoutMatchRoom.deliveryTime.text = DeliverTime.getHourMinute(diffMillis)
                binding.layoutMatchRoom.createTime.text = DeliverTime(
                    Calendar.getInstance().apply { timeInMillis = match.createTime }).getCreateTime()
                binding.layoutMatchRoom.goneCreateTime.text = DeliverTime.setCalendar(
                    Calendar.getInstance().apply { timeInMillis = match.createTime })
                binding.layoutMatchRoom.goneDeliveryTime.text = DeliverTime.setCalendar(
                    Calendar.getInstance().apply { timeInMillis = match.deliveryTime })
            }
        }
        return binding.root
    }
    companion object {
        fun newInstance(userId: String): MypageFragment {
            val fragment = MypageFragment()
            val bundle = Bundle()
            bundle.putString("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }
}