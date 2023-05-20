package com.tuk.shdelivery.FragMent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.HomeActivity
import com.tuk.shdelivery.R
import com.tuk.shdelivery.databinding.FragmentHomeBinding
import com.tuk.shdelivery.databinding.MatchRoomBinding
import java.lang.Thread.sleep

class HomeFragment : Fragment() {
    val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.swiper.setOnRefreshListener {

            //데이터를 불러온다.
            var listData = (binding.recycleView.adapter as RecycleAdapter).listData


            for (no in 10..20){
                var st = "Dumy $no"
                var num = no
                listData.add(MatchRoomData(st, no))
            }

            //어댑터 생성
            val adapter = RecycleAdapter(listData)
            binding.recycleView.adapter?.notifyDataSetChanged()

            Toast.makeText(this.context,"새로고침 완료!!",Toast.LENGTH_SHORT).show()
            binding.swiper.isRefreshing = false
        }
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

    fun loadData() : ArrayList<MatchRoomData>{
        var matchDataList = ArrayList<MatchRoomData>()

        for (no in 1..10){
            var st = "Dumy $no"
            var num = no
            matchDataList.add(MatchRoomData(st, no))
        }

        return matchDataList
    }

    fun setData(matchRoomDataList: ArrayList<MatchRoomData>): Unit {
        for(i in matchRoomDataList){

        }
    }

}

//신경쓰지 않아도 되는 스크롤 뷰
class RecycleAdapter(val listData : ArrayList<MatchRoomData>) : RecyclerView.Adapter<RecycleAdapter.Holder>() {

    class Holder(val binding: MatchRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: MatchRoomData) {
            binding.textView.text = "${data.name}  ${data.id}"
            binding.textView.setOnClickListener{
                Toast.makeText(binding.root.context,"${binding.textView.text.toString()}",Toast.LENGTH_SHORT).show()
            }
        }

    }
    //매칭방이 생성될때 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = MatchRoomBinding.inflate(LayoutInflater.from(parent.context),parent, false)
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
