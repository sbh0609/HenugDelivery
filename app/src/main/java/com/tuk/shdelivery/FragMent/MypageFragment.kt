package com.tuk.shdelivery.FragMent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tuk.shdelivery.Activity.ChargeActivity
import com.tuk.shdelivery.Activity.HomeActivity
import com.tuk.shdelivery.databinding.FragmentMypageBinding
import com.tuk.shdelivery.Data.MatchDao
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.custom.DeliverTime
import java.util.*


class MypageFragment : Fragment() {
    val intent by lazy { requireActivity().intent }
    val binding by lazy { FragmentMypageBinding.inflate(layoutInflater) }
    var matchDao = MatchDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //충전 버튼 설정
        binding.charge.setOnClickListener {
            intent.setClass(requireContext(), ChargeActivity::class.java)
            activity?.startActivityForResult(intent, 2)
        }

        //방삭제 버튼 설정
        binding.deliteRoom.setOnClickListener {
            val user = intent.getSerializableExtra("user") as User
            //자기가 만든 매칭방일 경우
            if(user.userId == user.participateMatchId){
                deliteMatchRoom(user){
                    ((activity as HomeActivity).listFragment[0] as HomeFragment).reFresh()
                }
            }
            //본인이 만든 매칭방이 아닐경우
            else{
                Toast.makeText(context, "본인이 만든 매칭방만 삭제 가능합니다." , Toast.LENGTH_SHORT).show()
            }

        }

        //처음 프로필 세팅
        enterSetProfile(intent.getSerializableExtra("user") as User)

        //마이페이지 유저이름, 포인트 설정
        updateProfile()
    }

    public fun updateProfile() {
        val user = intent.getSerializableExtra("user") as User
        binding.userName.text = user.userName
        binding.point.text = user.userPoint.toString() + "P"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    fun deliteMatchRoom(user: User, callback: () -> Unit) {
        matchDao.removeMatchRoom(user) {
            ((activity as HomeActivity).listFragment[1] as ChatListFragment).settingChatRoom()
            exitSetProfile()
            callback()
        }
    }

    fun exitSetProfile() {
        binding.layoutMatchRoom.root.visibility = View.GONE
        binding.deliteRoom.visibility = View.GONE
    }

    fun enterSetProfile(user: User) {
        //매칭방에 입장중이라면
        if (user.participateMatchId != "") {
            matchDao.getParticipatingMatch(user) {
                var match = it
                binding.layoutMatchRoom.root.visibility = View.VISIBLE
                //본인이 만든 방이면 삭제 버튼 활성화
                if (user.userId == user.participateMatchId) {
                    binding.deliteRoom.visibility = View.VISIBLE
                } else binding.deliteRoom.visibility = View.GONE

                val category = Data.category()

                val diffMillis = match.deliveryTime - match.createTime

                binding.layoutMatchRoom.tag.text = "${match.menu}"
                binding.layoutMatchRoom.description.text = match.description
                binding.layoutMatchRoom.count.text = match.count.toString()
                binding.layoutMatchRoom.tagImage.setImageResource(category[match.menu]!!)
                binding.layoutMatchRoom.store.text = match.storeName
                binding.layoutMatchRoom.deliveryTime.text = DeliverTime.getHourMinute(diffMillis)
                binding.layoutMatchRoom.createTime.text = DeliverTime(
                    Calendar.getInstance()
                        .apply { timeInMillis = match.createTime }).getCreateTime()
                binding.layoutMatchRoom.goneCreateTime.text = DeliverTime.setCalendar(
                    Calendar.getInstance().apply { timeInMillis = match.createTime })
                binding.layoutMatchRoom.goneDeliveryTime.text = DeliverTime.setCalendar(
                    Calendar.getInstance().apply { timeInMillis = match.deliveryTime })
            }
        }
        //입장중이지 않다면
        if(user.participateMatchId == ""){
            exitSetProfile()
        }
    }
}