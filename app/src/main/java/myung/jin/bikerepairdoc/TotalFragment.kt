package myung.jin.bikerepairdoc

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import myung.jin.bikerepairdoc.databinding.FragmentTotalBinding


class TotalFragment : Fragment(),OnDeleteListener {

    private var _binding: FragmentTotalBinding? = null
    val totalBinding get() = _binding!!

    val bikeList2 = mutableListOf<BikeMemo>()


    lateinit var helper: RoomHelper
    lateinit var bikeAdapter2: RecyclerAdapter2
    lateinit var bikeMemoDao: BikeMemoDao

    private lateinit var model: String
    private lateinit var year : String

    var tAmout :Int =0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTotalBinding.inflate(inflater, container, false)
        return totalBinding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)






        helper = Room.databaseBuilder(requireContext(), RoomHelper::class.java, "bike_memo")
            .fallbackToDestructiveMigration()
            .build()
        bikeMemoDao = helper.bikeMemoDao()

        bikeAdapter2 = RecyclerAdapter2(bikeList2,this)



        totalBinding.totalRecycler.adapter = bikeAdapter2
        totalBinding.totalRecycler.layoutManager = LinearLayoutManager(requireContext())

        totalBinding.searchButton.setOnClickListener {

            searchMemo(bikeList2)

        }

    }
    //작성중
    private fun searchMemo(bikeMemo: MutableList<BikeMemo>) {
        var searchE  = totalBinding.searchEdit.text.toString()
        Log.d("str","$searchE")
        for (bike in bikeMemo){
            if (bike.model == searchE) {

                CoroutineScope(Dispatchers.IO).launch {

                    val filteredBikes = bikeMemoDao.getModel(searchE)
                    for (list in bikeList2){
                        Log.d("list","${list.model}")
                    }
                    // 합계금액 적용
                    var dAmout: Int = 0
                    for (bike in bikeList2){
                        dAmout += bike.amount
                    }
                    tAmout = dAmout

                    withContext(Dispatchers.Main){
                    bikeList2.clear()
                    bikeList2.addAll(filteredBikes)
                        bikeAdapter2.notifyDataSetChanged()
                        totalBinding.totalTotalAmount.text = tAmout.toString()

                    }
                }
            }
        }

    }

//    private fun searchMemo(bikeMemo: MutableList<BikeMemo>, str: String) {
//        bikeMemo.find { it.model == str }?.let { bike ->
//            CoroutineScope(Dispatchers.IO).launch {
//                bikeList2.apply {
//                    clear()
//                    addAll(bikeMemoDao.getModel(str))
//                }
//
//                tAmout = bikeList2.sumBy { it.amount }
//
//                withContext(Dispatchers.Main) {
//                    bikeAdapter2.notifyDataSetChanged()
//                    totalBinding.totalTotalAmount.text = tAmout.toString()
//                }
//            }
//        }
//    }



    //화면 이동시 다시 실행
    private fun refreshMemo() {
        CoroutineScope(Dispatchers.IO).launch {

            bikeList2.clear()
            bikeList2.addAll(bikeMemoDao.getAll())
            // 합계금액 적용
            var dAmout: Int = 0
            for (bike in bikeList2){
                dAmout += bike.amount
            }
            tAmout = dAmout
            withContext(Dispatchers.Main) {
                bikeAdapter2.notifyDataSetChanged()
                totalBinding.totalTotalAmount.text = tAmout.toString()
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
   override  fun onDeleteListener(bikeMemo: BikeMemo) {
        CoroutineScope(Dispatchers.IO).launch {

            val amount = bikeMemo.amount
            tAmout-=amount


            bikeMemoDao.delete(bikeMemo)
                bikeList2.remove(bikeMemo)
            withContext(Dispatchers.Main) {
                bikeAdapter2.notifyDataSetChanged()
                // 합계금액을 리스크에서 뽑아와 저장
                totalBinding.totalTotalAmount.text = tAmout.toString()
            }
        }
    }
}