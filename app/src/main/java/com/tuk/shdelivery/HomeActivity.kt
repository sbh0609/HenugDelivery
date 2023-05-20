package com.tuk.shdelivery

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tuk.shdelivery.FragMent.ChatListFragment
import com.tuk.shdelivery.FragMent.CreateFragment
import com.tuk.shdelivery.FragMent.HomeFragment
import com.tuk.shdelivery.FragMent.MypageFragment
import com.tuk.shdelivery.databinding.HomeBinding

class HomeActivity : AppCompatActivity() {
    //바인딩 객체 생성
    val binding by lazy { HomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //프래그먼트 리스트 생성
        val fragMentList = listOf(CreateFragment(), HomeFragment(), ChatListFragment(), MypageFragment())
        //어뎁터를 이용한 뷰 페이저와 프래그 먼트 연결
        binding.viewPager.adapter = fragMentAdapter(fragMentList, this)

        //탭메뉴에 들어갈 제목들
        val titles = listOf<String>("매칭방 생성", "홈", "매칭방", "MyPage")
        //탭메뉴에 들어갈 아이콘들
        var icons = arrayListOf<Drawable>()
        for (i in listOf(R.drawable.add, R.drawable.home, R.drawable.chaticon, R.drawable.person)) {
            icons.add((resources.getDrawable(i)))
        }
        //탭 레이아웃에 탭 메뉴들 추가
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles.get(position)
            tab.icon = icons.get(position)
        }.attach()
    }
}

//뷰 페이저와 프래그먼트를 연결할 어댑터 클래스 생성
//연결용으로 별 내용없음
class fragMentAdapter(var fragMentList: List<Fragment>, fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return fragMentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragMentList.get(position)
    }
}