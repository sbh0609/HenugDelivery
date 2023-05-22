package com.tuk.shdelivery

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.tuk.shdelivery.FragMent.ChatListFragment
import com.tuk.shdelivery.FragMent.HomeFragment
import com.tuk.shdelivery.FragMent.MypageFragment
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.HomeBinding

class HomeActivity : AppCompatActivity() {
    //바인딩 객체 생성
    val binding by lazy { HomeBinding.inflate(layoutInflater) }
    var listFragment = ArrayList<Fragment>()
    var search: SearchView? = null

    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000 // 뒤로가기 버튼을 연타 했을때 꺼질 간격
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        createFragMentList()

        createTabMenu()

        setTabListener()
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

        val titles = listOf<String>("홈", "채팅 리스트", "마이페이지")

        //탭메뉴에 들어갈 아이콘들
        var icons = arrayListOf<Drawable>()

        for (i in listOf(R.drawable.home, R.drawable.chaticon, R.drawable.person)) {
            icons.add((resources.getDrawable(i)))
        }

        for (i in 0..titles.size - 1) {
            val newTab = binding.tabLayout.newTab()
            newTab.setText(titles.get(i))
            newTab.setIcon(icons.get(i))
            binding.tabLayout.addTab(newTab)
        }

    }

    private fun setTabListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                when (tab?.position) {
                    0 -> transaction.show(listFragment.get(0)).commit()
                    1 -> transaction.show(listFragment.get(1)).commit()
                    2 -> transaction.show(listFragment.get(2)).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                when (tab?.position) {
                    0 -> transaction.hide(listFragment.get(0)).commit()
                    1 -> transaction.hide(listFragment.get(1)).commit()
                    2 -> transaction.hide(listFragment.get(2)).commit()
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onBackPressed() {
        if (search == null) {
            val layout = binding.viewPager.get(0) as ConstraintLayout
            val toolbar = layout.getViewById(R.id.toolbar) as androidx.appcompat.widget.Toolbar
            search = toolbar.menu.findItem(R.id.searchIcon).actionView as SearchView
        }

        //만약 입력상자가 켜져 있다면
        if (!search!!.isIconified) {
            search!!.clearFocus()
            search!!.isIconified = true
            search!!.isIconified = true
            //포커스 해제후 리턴
            return
        }

        val position = binding.tabLayout.selectedTabPosition
        if (position == 0) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime <= backPressedInterval) {
                super.onBackPressed()
            }
            backPressedTime = currentTime
            ToastCustom.toast(this, "한번더 누르면 종료됩니다.")
        }
        binding.tabLayout.getTabAt(0)?.select()
    }
}