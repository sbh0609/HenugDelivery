package com.tuk.shdelivery.FragMent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.Qna.setOnClickListener {
            val url =
                "https://open.kakao.com/o/s6Vru8nf" // yourChatRoom을 실제 카카오톡 오픈채팅방의 링크로 변경해주세요.

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        //충전 버튼 설정
        binding.charge.setOnClickListener {
            intent.setClass(requireContext(), ChargeActivity::class.java)
            activity?.startActivityForResult(intent, 2)
        }

        //방삭제 버튼 설정
        binding.deliteRoom.setOnClickListener {
            val user = intent.getSerializableExtra("user") as User
            //자기가 만든 매칭방일 경우
            if (user.userId == user.participateMatchId) {
                deliteMatchRoom(user) {
                    ((activity as HomeActivity).listFragment[0] as HomeFragment).reFresh()
                }
            }
            //본인이 만든 매칭방이 아닐경우
            else {
                Toast.makeText(context, "본인이 만든 매칭방만 삭제 가능합니다.", Toast.LENGTH_SHORT).show()
            }

        }

        val user = intent.getSerializableExtra("user") as User
        UserDao.userListener(user) { user ->
            if(user != null){
                intent.putExtra("user",user)
                //처음 프로필 세팅
                SetProfile(user)

                //마이페이지 유저이름, 포인트 설정
                updateProfile(user)
            }
        }
    }

    fun updateProfile(user: User) {
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

        //배달이 시작됐으면 못지움
        val matchRoomData = intent.getSerializableExtra("matchRoomData") as MatchRoomData
        if (matchRoomData.id != "start") {
            MatchDao.removeMatchRoom(user) {
                callback()
            }
        } else {
            Toast.makeText(context, "배달을 완료하세요!! \n문제발생시 문의사항으로 연락주세요", Toast.LENGTH_SHORT).show()
        }
    }

    fun SetProfile(user: User) {
        //매칭방에 입장중이라면
        if (user.participateMatchId != "") {
            MatchDao.getParticipatingMatch(user.participateMatchId) {
                if (it != null) {
                    var match = it
                    binding.layoutMatchRoom.root.visibility = View.VISIBLE
                    //본인이 만든 방이면 삭제 버튼 활성화
                    if (user.userId == user.participateMatchId) {
                        binding.deliteRoom.visibility = View.VISIBLE
                    } else binding.deliteRoom.visibility = View.GONE

                    val category = Data.category()

                    val diffMillis = match.deliveryTime - Calendar.getInstance().timeInMillis

                    binding.layoutMatchRoom.tag.text = "${match.menu}"
                    binding.layoutMatchRoom.description.text = match.description
                    binding.layoutMatchRoom.count.text = match.count.toString()
                    binding.layoutMatchRoom.tagImage.setImageResource(category[match.menu]!!)
                    binding.layoutMatchRoom.store.text = match.storeName
                    binding.layoutMatchRoom.deliveryTime.text =
                        DeliverTime.getHourMinute(diffMillis)
                    binding.layoutMatchRoom.createTime.text = DeliverTime(
                        Calendar.getInstance()
                            .apply { timeInMillis = match.createTime }).getCreateTime()
                    binding.layoutMatchRoom.goneCreateTime.text = DeliverTime.setCalendar(
                        Calendar.getInstance().apply { timeInMillis = match.createTime })
                    binding.layoutMatchRoom.goneDeliveryTime.text = DeliverTime.setCalendar(
                        Calendar.getInstance().apply { timeInMillis = match.deliveryTime })
                }
            }
        }
        //입장중이지 않다면
        if (user.participateMatchId == "") {
            binding.layoutMatchRoom.root.visibility = View.GONE
            binding.deliteRoom.visibility = View.GONE
        }

        updateProfile(user)
    }
}