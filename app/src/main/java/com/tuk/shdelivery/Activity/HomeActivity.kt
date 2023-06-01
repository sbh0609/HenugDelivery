package com.tuk.shdelivery.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.FragMent.ChatListFragment
import com.tuk.shdelivery.FragMent.HomeFragment
import com.tuk.shdelivery.FragMent.MypageFragment
import com.tuk.shdelivery.R
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.ActivityHomeBinding
import com.tuk.shdelivery.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), CoroutineScope {

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    var user: User? = null
    val Udao = UserDao()

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

        //유저 초기화
        userInit()

        //탭 메뉴 넣기
        createTabMenu()

        //탭 리스너 달기
        setTabListener()
    }

    private fun userInit() {
        val userid = intent.getStringExtra("userid")
        user?.userId = userid!!
        //유저 정보 확인
        launch {
            var result = Udao.getUser(userid!!)
            //새로운 유저라면
            if (result == null) {
                val newUser = User(
                    intent.getStringExtra("userid")!!,
                    intent.getStringExtra("userName")!!,
                    0,
                    0
                )
                user = newUser
                Udao.addUser(newUser)
            }
            //이미 있는 유저라면 User초기화, intent에 넣기
            user = result

            intent.putExtra("user", user)

            //마이페이지 유저이름, 포인트 설정
            val mypageFragment = listFragment.get(2) as MypageFragment
            mypageFragment.binding.userName.text = user?.userName
            mypageFragment.binding.point.text = user?.userPoint.toString() + "P"
        }
    }

    private fun createFragMentList() {
        arrayOf(HomeFragment(), ChatListFragment(), MypageFragment()).map {
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
                transaction.show(listFragment.get(tab?.position!!)).commit()
                tab?.icon?.setColorFilter(getColor(R.color.orange), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.hide(listFragment.get(tab?.position!!)).commit()
                tab?.icon?.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            var newCreateData = data?.getSerializableExtra("createData") as MatchRoomData

            val fragment = listFragment.get(0) as HomeFragment

            fragment.adapter?.listData?.add(newCreateData)
            fragment.adapter?.notifyDataSetChanged()
        }
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            val fragment = listFragment.get(1) as ChatListFragment

            fragment.binding.btn.performClick()
            binding.tabLayout.getTabAt(1)!!.select()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {

        val position = binding.tabLayout.selectedTabPosition
        if (position == 0) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime > backPressedInterval) {
                backPressedTime = currentTime
                ToastCustom.toast(this, "한번더 누르면 종료됩니다.")
            } else {
                super.onBackPressed()
            }
        }
        binding.tabLayout.getTabAt(0)?.select()
    }
}