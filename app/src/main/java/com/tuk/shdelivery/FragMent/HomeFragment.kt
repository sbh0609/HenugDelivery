package com.tuk.shdelivery.FragMent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuk.shdelivery.Activity.MatchActivity
import com.tuk.shdelivery.Activity.createActivity
import com.tuk.shdelivery.Data.IconData
import com.tuk.shdelivery.Data.MatchDao
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.R
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.custom.DeliverTime
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.CategoryIconBinding
import com.tuk.shdelivery.databinding.FragmentHomeBinding
import com.tuk.shdelivery.databinding.LayoutMatchRoomBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class HomeFragment() : Fragment(), CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    val intent by lazy { requireActivity().intent }
    val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    var adapter: CustomAdapter? = null
    var datalist = ArrayList<MatchRoomData>()
    val categoryMap = Data.category()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //카테고리 생성
        createCategory()

        //리사이클러뷰 설정
        createRecyclerView()

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
        intent.setClass(requireContext(), createActivity::class.java)
        requireActivity().startActivityForResult(intent, 0)
    }

    private fun createRecyclerView() {
        binding.toolbar.title = "전체"
        //데이터를 불러온다.
        var matchDao = MatchDao()

        launch {
            val lst = async { loadData() }.await()

            Log.d("lst", lst.toString())

            adapter = CustomAdapter(lst)

            binding.recycleView.adapter = adapter

            //레이아웃 매니져 설정
            binding.recycleView.layoutManager = LinearLayoutManager(requireContext())

            binding.recycleView.adapter!!.notifyDataSetChanged()
        }
        //스크롤 리스너 설정
        createScrollListener()
    }

    private fun createScrollListener() {
        binding.hint.setOnTouchListener { _, _ ->
            binding.hint.visibility = View.GONE
            true
        }
        binding.IconScroll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                binding.hint.visibility = View.GONE
            }
        })


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    /**DB에서 데이터 불러오는 함수*/
    suspend fun loadData(): ArrayList<MatchRoomData> = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<ArrayList<MatchRoomData>>()

        adapter?.listData?.clear()

        var matchDao = MatchDao()
        var result = ArrayList<MatchRoomData>()
        matchDao.fetchChatrooms {
            result = it as ArrayList<MatchRoomData>

            //백업
            datalist = result

            deferred.complete(result)
        }
        deferred.await()
    }

    /**새로고침 함수*/
    fun reFresh(): Unit {
        adapter?.listData?.clear()

        launch {
            val loadData = loadData()
            //!!여기도 refresh부분
            for (data in loadData) {
                adapter?.listData?.add(data)
            }

            adapter?.notifyDataSetChanged()

            ToastCustom.toast(requireActivity(), "새로고침 완료")

            binding.scrollUpButton.visibility = View.INVISIBLE

            binding.swiper.isRefreshing = false
        }
    }


    private fun createCategory() {
        var drawList = ArrayList<IconData>()
        drawList.add(IconData("전체", R.drawable.icon_all))
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
        inner class Holder(var bd: LayoutMatchRoomBinding) :
            RecyclerView.ViewHolder(bd.root) {
            init {
                bd.root.setOnClickListener {
                    //!!! 여기도 수정 id, data라는 key값?
                    intent.setClass(requireContext(), MatchActivity::class.java)
                    val data = MatchRoomData(
                        bd.matchId.text.toString(),
                        bd.tag.text.toString(),
                        DeliverTime.getCalendar(bd.goneDeliveryTime.text.toString()).timeInMillis,
                        bd.description.text.toString(),
                        bd.count.text.toString().toInt(),
                        DeliverTime.getCalendar(bd.goneCreateTime.text.toString()).timeInMillis,
                        bd.store.text.toString()
                    )
                    intent.putExtra("selectMatchData", data)
                    activity?.startActivityForResult(intent, 1)
                }
            }

            fun setData(data: MatchRoomData) {
                val diffMillis = data.deliveryTime - data.createTime

                bd.tag.text = "${data.menu}"
                bd.description.text = data.description
                bd.count.text = data.count.toString()
                bd.tagImage.setImageResource(categoryMap[data.menu]!!)
                bd.store.text = data.storeName
                bd.deliveryTime.text = DeliverTime.getHourMinute(diffMillis)
                bd.createTime.text = DeliverTime(
                    Calendar.getInstance().apply { timeInMillis = data.createTime }).getCreateTime()
                bd.goneCreateTime.text = DeliverTime.setCalendar(
                    Calendar.getInstance().apply { timeInMillis = data.createTime })
                bd.goneDeliveryTime.text = DeliverTime.setCalendar(
                    Calendar.getInstance().apply { timeInMillis = data.deliveryTime })
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val b =
                LayoutMatchRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                    adapter?.listData =
                        if (bd.TextView.text != "전체")
                            datalist?.filter { it.menu == bd.TextView.text } as ArrayList<MatchRoomData>
                        else
                            datalist
                    adapter?.notifyDataSetChanged()
                }
            }

            fun setData(data: IconData) {
                bd.ImageView.setImageResource(data.drawableId)
                if (data.name == "전체") {
                    bd.TextView.visibility = View.INVISIBLE
                }
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


