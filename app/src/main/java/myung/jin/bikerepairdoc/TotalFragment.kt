package myung.jin.bikerepairdoc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.*
import myung.jin.bikerepairdoc.databinding.FragmentTotalBinding


class TotalFragment : Fragment(), OnDeleteListener {

    private var _binding: FragmentTotalBinding? = null
    private val totalBinding get() = _binding!!
    private var bikeList2 = mutableListOf<BikeMemo>()
    private lateinit var helper: RoomHelper
    private lateinit var bikeAdapter2: RecyclerAdapter2
    private lateinit var bikeMemoDao: BikeMemoDao
    private var tAmount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTotalBinding.inflate(inflater, container, false)
        return totalBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        helper = Room.databaseBuilder(requireContext(), RoomHelper::class.java, "bike_memo")
            .fallbackToDestructiveMigration().build()
        bikeMemoDao = helper.bikeMemoDao()

        bikeAdapter2 = RecyclerAdapter2(bikeList2, this)





        with(totalBinding) {
            totalRecycler.adapter = bikeAdapter2
            totalRecycler.layoutManager = LinearLayoutManager(requireContext())

            searchButton.setOnClickListener {

                searchMemo()

            }
        }


    }

    //챗 GPT 가 만들어준 코드 간략하고 알기 쉬움
    @SuppressLint("NotifyDataSetChanged")
    private fun searchMemo() {
        //검색한 글짜를 입력받음
        val searchE = totalBinding.searchEdit.text.toString()
        // 아무것도 없이 비어있으면 다시 전부 불러옴
        if (searchE.isEmpty()) {
            refreshMemo()
            return
        }
// 코루틴스코프를 사용해서 when문을 이용해 날짜나 숫자 그밖에 나머지를 검색함
        // filteredBikes를 이용해 bikeMemoDao 를 조건에 맞게 저장고 메인에서 리스트를 업데이트함 (정규직사용)
        CoroutineScope(Dispatchers.IO).launch {
            val filteredBikes = when {
                searchE.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> {
                    // Search by date
                    bikeMemoDao.getDate(searchE)
                }
                searchE.matches(Regex("\\d{4}")) -> {
                    // Search by year
                    bikeMemoDao.getYear(searchE)
                }
                else -> {
                    // Search by model
                    bikeMemoDao.getModel(searchE)
                }
            }

            withContext(Dispatchers.Main) {
                bikeList2.clear()
                bikeList2.addAll(filteredBikes)

                //리스트에서 어마운트 합을 구함
                tAmount = bikeList2.sumOf { it.amount }
                bikeAdapter2.notifyDataSetChanged()
                totalBinding.totalTotalAmount.text = tAmount.toString()
            }
        }
    }

    //화면 이동시 다시 실행
    @SuppressLint("NotifyDataSetChanged")
    private fun refreshMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            val filteredBikes = bikeMemoDao.getAll()

            withContext(Dispatchers.Main) {
                bikeList2.clear()
                bikeList2.addAll(filteredBikes)

                // 합계금액 적용

                tAmount = bikeList2.sumOf { it.amount }
                bikeAdapter2.notifyDataSetChanged()
                totalBinding.totalTotalAmount.text = tAmount.toString()

            }
        }
    }

    // 화면 전환시 리즘에서 처리해야 화면 이동시 작동함
    override fun onResume() {
        super.onResume()
        refreshMemo()
    }

    // 바인딩 해제
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //바이크메모에서 데이터를 가져와 적용 길게 누르면 삭제

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteListener(bikeMemo: BikeMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            //토탈금액 계산
            val amount = bikeMemo.amount
            tAmount -= amount
            bikeMemoDao.delete(bikeMemo)
            bikeList2.remove(bikeMemo)
            withContext(Dispatchers.Main) {
                bikeAdapter2.notifyDataSetChanged()
                // 합계금액을 리스크에서 뽑아와 저장
                totalBinding.totalTotalAmount.text = tAmount.toString()
            }
        }
    }
}


//내가 짠 코드를 챗gpt를 통해서 간략하고 정확화게 변경함
//    private fun searchMemo() {
//        var searchE= totalBinding.searchEdit.text.toString()
//
//        if (searchE.isNotEmpty()){
//            CoroutineScope(Dispatchers.IO).launch {
//                var filteredBikes = bikeMemoDao.getModel(searchE)
//
//                bikeList2.clear()
//                bikeList2.addAll(filteredBikes)
//
//                withContext(Dispatchers.Main) {
//
//                    // 합계금액 적용
//                    var dAmout: Int = 0
//                    for (bike in bikeList2) {
//                        dAmout += bike.amount
//                    }
//                    tAmout = dAmout
//                    bikeAdapter2.notifyDataSetChanged()
//                    totalBinding.totalTotalAmount.text = tAmout.toString()
//                }
//            }
////날짜로 찾기  작성중
//            CoroutineScope(Dispatchers.IO).launch {
//                var filteredBikes = bikeMemoDao.getDate(searchE)
//
//                bikeList2.clear()
//                bikeList2.addAll(filteredBikes)
//
//                withContext(Dispatchers.Main) {
//
//                    // 합계금액 적용
//                    var dAmout: Int = 0
//                    for (bike in bikeList2) {
//                        dAmout += bike.amount
//                    }
//                    tAmout = dAmout
//                    bikeAdapter2.notifyDataSetChanged()
//                    totalBinding.totalTotalAmount.text = tAmout.toString()
//                }
//            }
//
//            // 년도로 찾기
//            val searchE= totalBinding.searchEdit.text.toString()
//            CoroutineScope(Dispatchers.IO).launch {
//                var filteredBikes = bikeMemoDao.getYear(searchE)
//
//                bikeList2.clear()
//                bikeList2.addAll(filteredBikes)
//
//                withContext(Dispatchers.Main) {
//
//                    // 합계금액 적용
//                    var dAmout: Int = 0
//                    for (bike in bikeList2) {
//                        dAmout += bike.amount
//                    }
//                    tAmout = dAmout
//                    bikeAdapter2.notifyDataSetChanged()
//                    totalBinding.totalTotalAmount.text = tAmout.toString()
//                }
//            }
//        }else {
//        // 발견되지 안는경우
//            refreshMemo()
//        }
//                      Log.d("str", "$searchE")
//    }
