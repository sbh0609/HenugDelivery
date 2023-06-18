package com.tuk.shdelivery.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.tuk.shdelivery.Data.Chat
import com.tuk.shdelivery.Data.MatchDao
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.FragMent.ChatListFragment
import com.tuk.shdelivery.FragMent.HomeFragment
import com.tuk.shdelivery.FragMent.MypageFragment
import com.tuk.shdelivery.R
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.ActivityHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class HomeActivity : AppCompatActivity(), CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //바인딩 객체 생성
    val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    var listFragment = ArrayList<Fragment>()


    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000 // 뒤로가기 버튼을 연타 했을때 꺼질 간격
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //프래그먼트 설정
        createFragMentList()

        //탭 메뉴 넣기
        createTabMenu()

        //탭 리스너 달기
        setTabListener()
    }


    private fun createFragMentList() {
        arrayOf(HomeFragment(), ChatListFragment(), MypageFragment()).map {  // use myPageFragment here
            listFragment.add(it)
        }
        for (i in listFragment) {
            supportFragmentManager.beginTransaction().add(R.id.viewPager, i).commit()
            supportFragmentManager.beginTransaction().hide(i).commit()
        }
        supportFragmentManager.beginTransaction().show(listFragment.get(0)).commit()
    }

    fun createTabMenu(): Unit {

        val titles = listOf<String>("홈", "채팅", "마이페이지")

        //탭메뉴에 들어갈 아이콘들
        var icons = arrayListOf<Drawable>()

        for (i in listOf(
            R.drawable.vector_home,
            R.drawable.vector_chaticon,
            R.drawable.vector_person
        )) {
            icons.add((resources.getDrawable(i)))
        }

        for (i in 0..titles.size - 1) {
            val newTab = binding.tabLayout.newTab()
            newTab.setText(titles.get(i))
            newTab.setIcon(icons.get(i))
            binding.tabLayout.addTab(newTab)
        }
        binding.tabLayout.getTabAt(0)?.icon?.setColorFilter(
            getColor(R.color.orange),
            PorterDuff.Mode.SRC_IN
        )

    }

    private fun setTabListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.show(listFragment[tab?.position!!]).commitAllowingStateLoss()
                tab.icon?.setColorFilter(getColor(R.color.orange), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.hide(listFragment[tab?.position!!]).commitAllowingStateLoss()
                tab?.icon?.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, fetchIntent: Intent?) {

//        //create데이터가 왔다면
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            var newCreateData = fetchIntent?.getSerializableExtra("createData") as MatchRoomData
            var user = (intent.getSerializableExtra("user") as User)
            user.participateMatchId = newCreateData.id
            intent.putExtra("user", user)
            val fragment1 = listFragment.get(1) as ChatListFragment
            val fragment0 = listFragment.get(0) as HomeFragment
            val updateFields = mapOf("participateMatchId" to newCreateData.id)
            UserDao.updateUserFields(user.userId, updateFields) {
                var chat = Chat("입장", user.userName, "님이 입장하였습니다.")
                MatchDao.sendMessageToFirebase(chat, user.participateMatchId) {
                    fragment1.enterChatRoom() {
                        fragment0.reFresh()
                        binding.tabLayout.getTabAt(1)!!.select()
                    }
                }
            }
        }
        //매칭방을 입장한 뒤라면
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val mypage = listFragment.get(2) as MypageFragment
            //!!참여중인 매칭방 id로 매칭방 가져온다음 매칭방에 참여중 표시
            val user = fetchIntent?.getSerializableExtra("user") as User
            val fragment1 = listFragment.get(1) as ChatListFragment
            val fragment0 = listFragment.get(0) as HomeFragment

            intent.putExtra("user", user)
            intent.putExtra("selectChatRoom", fetchIntent.getSerializableExtra("selectChatRoom"))
            //입장 메세지 보내고 들어가기
            var chat = Chat("입장", user.userName, "님이 입장하였습니다.")
            MatchDao.sendMessageToFirebase(chat, user.participateMatchId) {
                fragment1.enterChatRoom() {
                    fragment0.reFresh()
                    binding.tabLayout.getTabAt(1)!!.select()
                }
            }
        }
        //포인트를 충전한 뒤라면
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            //프로필 새로고침
            val user = fetchIntent?.getSerializableExtra("user") as User
            intent.putExtra("user", user)
            val fragment = listFragment.get(2) as MypageFragment

            binding.tabLayout.getTabAt(2)!!.select()

            Toast.makeText(
                this,
                fetchIntent.getLongExtra("inputPoint", 0L).toString() + "P 충전 되었습니다.",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        super.onActivityResult(requestCode, resultCode, intent)
    }


    override fun onBackPressed() {

        val position = binding.tabLayout.selectedTabPosition
        if (position == 0) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime > backPressedInterval) {
                backPressedTime = currentTime
                ToastCustom.toast(this, "한번더 누르면 종료됩니다.")
            } else {
                // 모든 액티비티를 종료
                finishAffinity()

                // 앱 프로세스를 종료
                System.exit(0)
            }
        }
        binding.tabLayout.getTabAt(0)?.select()
    }
}