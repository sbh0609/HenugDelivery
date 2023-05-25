package com.tuk.shdelivery.FragMent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.ScrollState
import com.tuk.shdelivery.Activity.HomeActivity
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.R
import com.tuk.shdelivery.Activity.createActivity
import com.tuk.shdelivery.Data.IconData
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.CategoryIconBinding
import com.tuk.shdelivery.databinding.FragmentHomeBinding
import com.tuk.shdelivery.databinding.MatchRoomBinding


val categoryMap = Data.category()

class HomeFragment : Fragment() {
    val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    var test = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //카테고리 생성
        createCategory()

        //리사이클러뷰 설정
        createRecyclerView()

        //툴바 리스너 달기
        createToolbarListener()

        //search 리스너 달기
        createSearchListener()

        //매칭방 생성 버튼 리스너 달기
        binding.createMatching.setOnClickListener { createMatching() }
        //새로고침 리스너 달기
        binding.swiper.setOnRefreshListener { reFresh() }

        //upscroll리스너 달기
        binding.scrollUpButton.setOnClickListener {
            binding.recycleView.smoothScrollToPosition(0);
        }
    }

    private fun createRecyclerView() {
        //데이터를 불러온다.
        var matchDataList = loadData()

        //어댑터 생성
        val adapter = CustomAdapter(activity as Activity, matchDataList)

        binding.recycleView.adapter = adapter

        //레이아웃 매니져 설정
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())

        //스크롤 리스너 설정
        createScrollListener()
    }

    private fun createScrollListener() {
        binding.recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var temp = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (temp == 1) {
                    super.onScrolled(recyclerView, dx, dy)
                    binding.createText.visibility = View.GONE
                    binding.scrollUpButton.visibility = View.INVISIBLE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                binding.scrollUpButton.visibility = View.VISIBLE
                binding.createText.visibility = View.VISIBLE
                temp = 1
            }
        })
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
        savedInstanceState: Bundle?,
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
            matchDataList.add(MatchRoomData("Title ${i}", "decription$i", i, "storeName", i, i))
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
            sample.add(
                MatchRoomData(
                    "Title ${i}",
                    getString(R.string.dumyText),
                    i,
                    "storeName",
                    i,
                    i
                )
            )
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

            true
        }
    }

    private fun createCategory() {
        var drawList = ArrayList<IconData>()
        for ((key, value) in categoryMap) {
            drawList.add(IconData(key, value))
        }

        //어댑터 생성
        val adapter = IconAdapter(drawList)

        binding.IconScroll.adapter = adapter

        //레이아웃 매니져 설정
        binding.IconScroll.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )

    }
}

//신경쓰지 않아도 되는 스크롤 뷰
class CustomAdapter(var activity: Activity, val listData: ArrayList<MatchRoomData>) :
    RecyclerView.Adapter<CustomAdapter.Holder>() {
    class Holder(var activity: Activity, var binding: MatchRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                ToastCustom.toast(
                    binding.root.context,
                    "${binding.title.text} ${binding.description.text} 매칭방 입장!"
                )
            }
        }

        fun setData(data: MatchRoomData) {
            binding.title.text = "${data.title}"
            binding.description.text = data.description
            binding.count.text = data.count.toString()
            binding.time.text = data.time.toString() + ":40"
            binding.tag.text = "족발/보쌈"
            binding.store.text = "아웃닭 산기대학로점"
            binding.tagImage.setImageResource(categoryMap["족발/보쌈"]!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = MatchRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = Holder(activity, binding)

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
        holder.setData(data)
        //홀더는 받은 데이터를 화면에 출력한다.
    }


}

class IconAdapter(val listData: ArrayList<IconData>) :
    RecyclerView.Adapter<IconAdapter.Holder>() {
    class Holder(var binding: CategoryIconBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                ToastCustom.toast(binding.root.context, "${binding.TextView.text} 메뉴 선택!")
            }
        }

        fun setData(data: IconData) {
            binding.ImageView.setImageResource(data.drawableId)
            binding.TextView.text = data.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            CategoryIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        holder.setData(data)
        //홀더는 받은 데이터를 화면에 출력한다.
    }
}
