package com.tuk.shdelivery.FragMent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuk.shdelivery.Activity.createActivity
import com.tuk.shdelivery.Data.IconData
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.R
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.custom.DeliverTime
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.CategoryIconBinding
import com.tuk.shdelivery.databinding.FragmentHomeBinding
import com.tuk.shdelivery.databinding.MatchRoomBinding
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    var adapter : CustomAdapter? = null
    var datalist = ArrayList<MatchRoomData>()
    val categoryMap = Data.category()
    var test = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //카테고리 생성
        createCategory()

        //리사이클러뷰 설정
        createRecyclerView()

        //search 리스너 달기
        createSearchListener()

        //매칭방 생성 버튼 리스너 달기
        binding.createMatching.setOnClickListener { createMatching() }
        //새로고침 리스너 달기
        binding.swiper.setOnRefreshListener { reFresh() }

        //upscroll리스너 달기
        binding.scrollUpButton.setOnClickListener {
            binding.recycleView.smoothScrollToPosition(0);
            binding.scrollUpButton.visibility = View.INVISIBLE
        }
    }
    fun createMatching(): Unit {
        ToastCustom.toast(requireContext(), "매칭방 액티비티 출력")
        var intent = Intent(activity, createActivity::class.java)

        activity?.startActivityForResult(intent, 0)
    }

    private fun createRecyclerView() {
        //데이터를 불러온다.
        var matchDataList = loadData()



        //어댑터 생성
        adapter = CustomAdapter(matchDataList)

        binding.recycleView.adapter = adapter

        //백업
        datalist = adapter?.listData!!

        //레이아웃 매니져 설정
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())

        //스크롤 리스너 설정
        createScrollListener()
    }

    private fun createScrollListener() {
        binding.scrollUpButton.visibility = View.INVISIBLE
        binding.recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var temp = 0
            var direc = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (temp == 1) {
                    super.onScrolled(recyclerView, dx, dy)
                    binding.scrollUpButton.visibility = View.INVISIBLE
                    direc = dy
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                binding.scrollUpButton.visibility = View.VISIBLE
                temp = 1
                //맨위로 올라왔을땐 스크롤업 버튼 안보이게
                if (direc < 0) {
                    binding.scrollUpButton.visibility = View.INVISIBLE
                }
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
            val calendar = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            calendar2.add(Calendar.MINUTE,i)
            matchDataList.add(MatchRoomData("치킨", calendar2, "dumy~", i, calendar, "산기대학로 노랑통닭"))
        }

        ToastCustom.toast(requireActivity(), "DB에서 매칭방 데이터 불러옴")

        return matchDataList
    }

    /**새로고침 함수*/
    public fun reFresh(): Unit {
        //데이터를 불러온다.

        adapter?.listData?.clear()

        val sample = loadData()


        for (i in 1..test) {
            val calendar = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            calendar2.add(Calendar.MINUTE,i)
            sample.add(
                MatchRoomData(
                    "치킨",
                    calendar2,
                    "dumy~",
                    i,
                    calendar,
                    "산기대학로 노랑통닭"
                )
            )
        }

        test = test + 1

        for (data in sample) {
            adapter?.listData?.add(data)
        }

        adapter?.notifyDataSetChanged()

        //백업
        datalist = adapter?.listData!!

        ToastCustom.toast(requireActivity(), "새로고침 완료!!")

        binding.scrollUpButton.visibility = View.INVISIBLE

        binding.swiper.isRefreshing = false
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



    //신경쓰지 않아도 되는 스크롤 뷰
    inner class CustomAdapter(var listData: ArrayList<MatchRoomData>) :
        RecyclerView.Adapter<CustomAdapter.Holder>() {
        inner class Holder(var bd: MatchRoomBinding) :
            RecyclerView.ViewHolder(bd.root) {
            init {
                bd.root.setOnClickListener {
                    ToastCustom.toast(
                        context!!,
                        "${bd.tag.text} ${bd.description.text} 매칭방 입장!"
                    )
                }
            }

            fun setData(data: MatchRoomData) {
                val diffMillis = data.deliveryTime.timeInMillis - data.createTime.timeInMillis

                bd.tag.text = "${data.category}"
                bd.description.text = data.description
                bd.count.text = data.count.toString()
                bd.tagImage.setImageResource(categoryMap[data.category]!!)
                bd.store.text = data.storeName
                bd.deliveryTime.text = DeliverTime.getHourMinute(diffMillis)
                bd.createTime.text = DeliverTime(data.createTime).getCreateTime()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val b =
                MatchRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val holder = Holder(b)

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


    inner class IconAdapter(val listData: ArrayList<IconData>) :
        RecyclerView.Adapter<IconAdapter.Holder>() {
        inner class Holder(var bd: CategoryIconBinding) : RecyclerView.ViewHolder(bd.root) {
            init {
                bd.root.setOnClickListener {

                    binding.toolbar.title = bd.TextView.text
                    ToastCustom.toast(context!!, "${bd.TextView.text} 메뉴 선택!")
                    adapter?.listData = datalist?.filter { it.category == bd.TextView.text } as ArrayList<MatchRoomData>
                    adapter?.notifyDataSetChanged()
                }
            }

            fun setData(data: IconData) {
                bd.ImageView.setImageResource(data.drawableId)
                bd.TextView.text = data.name
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val b =
                CategoryIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val holder = Holder(b)

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
}


