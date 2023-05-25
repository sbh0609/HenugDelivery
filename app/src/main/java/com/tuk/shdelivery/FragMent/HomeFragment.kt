package com.tuk.shdelivery.FragMent

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.R
import com.tuk.shdelivery.Activity.categoryActivity
import com.tuk.shdelivery.Activity.createActivity
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.FragmentHomeBinding
import com.tuk.shdelivery.databinding.MatchRoomBinding

class HomeFragment : Fragment() {
    val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    var test = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //데이터를 불러온다.
        var matchDataList = loadData()

        //어댑터 생성
        val adapter = CustomAdapter(matchDataList)

        binding.recycleView.adapter = adapter

        //레이아웃 매니져 설정
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())

        //툴바 리스너 달기
        createToolbarListener()

        //search 리스너 달기
        createSearchListener()

        //매칭방 생성 버튼 리스너 달기
        binding.createMatching.setOnClickListener { createMatching() }
        //새로고침 리스너 달기
        binding.swiper.setOnRefreshListener { reFresh() }
    }

    /**서치 리스너 달기*/
    private fun createSearchListener() {
        val search = binding.toolbar.menu.findItem(R.id.searchIcon).actionView as SearchView
        search.maxWidth = Int.MAX_VALUE
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                ToastCustom.toast(activity!!, "$query 검색!")
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                // 검색어가 변경될 때마다 호출됨
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    /**DB에서 데이터 불러오는 함수*/
    fun loadData(): ArrayList<MatchRoomData> {
        var matchDataList = ArrayList<MatchRoomData>()

        /**
         * 여기에 DB데이터 가져오는 코드
         * */

        for (i in 1..1) {
            matchDataList.add(MatchRoomData("Dumy ${i}", i))
        }

        ToastCustom.toast(requireActivity(), "DB에서 매칭방 데이터 불러옴")

        return matchDataList
    }

    /**새로고침 함수*/
    fun reFresh(): Unit {
        val adapter = binding.recycleView.adapter as CustomAdapter
        //데이터를 불러온다.

        adapter.listData.clear()

        val sample = loadData()
        for (i in 1..test) {
            sample.add(MatchRoomData("Dumy ${i}", i))
        }

        test = test + 1

        for (data in sample) {
            adapter.listData.add(data)
        }

        adapter?.notifyDataSetChanged()

        ToastCustom.toast(requireActivity(), "새로고침 완료!!")

        binding.swiper.isRefreshing = false
    }

    /**매칭방 생성 함수*/
    fun createMatching(): Unit {
        ToastCustom.toast(requireActivity(), "매칭방 액티비티 출력")
        var intent = Intent(activity, createActivity::class.java)

        startActivity(intent)
    }

    private fun createToolbarListener() {
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.categoryIcon -> {
                    var intent = Intent(activity, categoryActivity::class.java)
                    intent.putExtra("width",binding.toolbar.width)
                    startActivityForResult(intent, 0)

                    ToastCustom.toast(requireActivity(), "카테고리 액티비티 출력")
                }
            }
            true
        }
    }
}

//신경쓰지 않아도 되는 스크롤 뷰
class CustomAdapter(val listData: ArrayList<MatchRoomData>) :
    RecyclerView.Adapter<CustomAdapter.Holder>() {
    class Holder(var binding: MatchRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.button3.setOnClickListener {
                var view = it as Button
                ToastCustom.toast(binding.button3.context,"${view.text} ${view.id} 매칭방 생성!")
            }
        }
        fun setData(data: MatchRoomData) {
            binding.button3.text = "${data.name}  ${data.id}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = MatchRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = Holder(binding)

        holder.setIsRecyclable(false);
        return holder
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //사용할 데이터를 꺼내고
        val data = listData.get(position)
        //홀더에 데이터를 전달한다.
        holder.setData(data,)
        //홀더는 받은 데이터를 화면에 출력한다.
    }
}
