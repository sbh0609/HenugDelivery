package com.tuk.shdelivery.FragMent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.HomeActivity
import com.tuk.shdelivery.R
import com.tuk.shdelivery.categoryActivity
import com.tuk.shdelivery.databinding.FragmentHomeBinding
import com.tuk.shdelivery.databinding.MatchRoomBinding
import java.lang.Thread.sleep

class HomeFragment : Fragment() {
    val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        //툴바 리스너 달기
        createToolbarListener()

        //search 리스너 달기
        createSearchListener()

        //매칭방 생성 버튼 리스너 달기
        binding.createMatching.setOnClickListener {createMatching()}
        //새로고침 리스너 달기
        binding.swiper.setOnRefreshListener { reFresh() }
    }

    /**서치 리스너 달기*/
    private fun createSearchListener() {
        val search = binding.toolbar.menu.findItem(R.id.searchIcon).actionView as SearchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                ToastCustom.toast(activity!!, "$query 검색!")

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // 검색어가 변경될 때마다 호출됨
                // 여기서 필요한 작업을 수행합니다. (예: 실시간 검색 결과 업데이트)
                Log.d("Search", newText)
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //데이터를 불러온다.
        var matchDataList = loadData()

        //어댑터 생성
        val adapter = RecycleAdapter(matchDataList)
        binding.recycleView.adapter = adapter

        //레이아웃 매니져 설정
        binding.recycleView.layoutManager = LinearLayoutManager(activity)

        return binding.root
    }

    /**DB에서 데이터 불러오는 함수*/
    fun loadData(): ArrayList<MatchRoomData> {
        var matchDataList = ArrayList<MatchRoomData>()

        for (no in 1..10) {
            var st = "Dumy $no"
            var num = no
            matchDataList.add(MatchRoomData(st, no))
        }
        Toast.makeText(this.context, "DB에서 매칭방 데이터 불러옴", Toast.LENGTH_SHORT).show()
        return matchDataList
    }

    /**새로고침 함수*/
    fun reFresh(): Unit {
        //데이터를 불러온다.
        var listData = (binding.recycleView.adapter as RecycleAdapter).listData


        for (no in 10..20) {
            var st = "Dumy $no"
            var num = no
            listData.add(MatchRoomData(st, no))
        }

        //어댑터 생성
        RecycleAdapter(listData)
        binding.recycleView.adapter?.notifyDataSetChanged()

        ToastCustom.toast(requireActivity(),"새로고침 완료!!")

        binding.swiper.isRefreshing = false
    }

    /**매칭방 생성 함수*/
    fun createMatching(): Unit {
        ToastCustom.toast(requireActivity(),"매칭방 액티비티 출력")
        var intent = Intent(activity,createActivity::class.java)

        startActivity(intent)
    }
    private fun createToolbarListener() {
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem->
            when(item.itemId){
                R.id.categoryIcon->{
                    var intent = Intent(activity,categoryActivity::class.java)

                    startActivityForResult(intent,0)

                   ToastCustom.toast(requireActivity(), "카테고리 액티비티 출력")
                }
            }
            true
        }
    }

}

//신경쓰지 않아도 되는 스크롤 뷰
class RecycleAdapter(val listData: ArrayList<MatchRoomData>) :
    RecyclerView.Adapter<RecycleAdapter.Holder>() {

    class Holder(val binding: MatchRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: MatchRoomData) {
            Log.d("병학","데이터셋 넣기")
            binding.textView.text = "${data.name}  ${data.id}"
            binding.textView.setOnClickListener {
                Toast.makeText(
                    binding.root.context,
                    "${binding.textView.text.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    //매칭방이 생성될때 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = MatchRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    //생성이 안되어있던 매칭방이 올라가서 생성될때 호출되는 함수
    override fun onBindViewHolder(holder: Holder, position: Int) {
        //사용할 데이터를 꺼내고
        val data = listData.get(position)
        //홀더에 데이터를 전달한다.
        holder.setData(data)
        //홀더는 받은 데이터를 화면에 출력한다.
    }
}
