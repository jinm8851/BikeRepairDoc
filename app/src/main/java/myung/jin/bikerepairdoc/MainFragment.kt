package myung.jin.bikerepairdoc


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import myung.jin.bikerepairdoc.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.Date


class MainFragment : Fragment(), OnDeleteListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    val bikeList = mutableListOf<BikeMemo>()
    lateinit var spinerRefer: String
    lateinit var helper: RoomHelper
    lateinit var bikeAdapter: RecyclerAdapter
    lateinit var bikeMemoDao: BikeMemoDao
    var totalAmount1 = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//날짜 적용
        dateSet()
//스피너 선택 밎 적용
        spinnerSelected()
        //헬퍼에 바이크메모 적용
        helper = Room.databaseBuilder(requireContext(), RoomHelper::class.java, "bike_memo")
            .fallbackToDestructiveMigration()
            .build()

        bikeMemoDao = helper.bikeMemoDao()

        bikeAdapter = RecyclerAdapter(bikeList, this)

        refreshAdapter()

        with(binding) {
            mainRecyclerView.adapter = bikeAdapter
            mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            save.setOnClickListener {

                //null check 후 메모 리스트를 만들어서 바이크리스트에 적용 후 인서트
                var bikeName1 = bikeName.text.toString()
                if (bikeName1.isEmpty()) {
                    Toast.makeText(requireContext(), "애칭이 비어있습니다.", Toast.LENGTH_LONG).show()
                    bikeName1 = ""
                    content.text = ""
                }
                //구입날짜나 다른것이 비어있으면 참고를 빈 문자로 변경
                var startDate1 = startDate.text.toString()
                if (startDate1.isEmpty()) {
                    startDate1=""
                    content.text = ""
                }
                var repairDate1 = repairDate.text.toString()
                if (repairDate1.isEmpty()) {
                    repairDate1 = ""
                    content.text = ""
                }
                //텍스트를 받아 인트로 변환
                var km2 = km.text.toString()
                var km1: Int
                if (km2.isNotEmpty()) {
                    km1 = km2.toInt()
                } else {
                    km1 = 0
                    content.text = ""
                }
                var content1 = content.text.toString()
                if (content1.isEmpty()) {
                    content1 = ""
                }

                var amount2 = amount.text.toString()
                var amount1: Int
                if (amount2.isNotEmpty()) {
                    amount1 = amount2.toInt()
                } else {
                    amount1 = 0
                    content.text = ""
                }
                var note1 = note.text.toString()
                if (note1.isEmpty()) {
                    note1 = ""
                    content.text = ""
                }
                //금액을 더해 토탈금액에 적용
                totalAmount1 += amount1

                totalAmount.text = totalAmount1.toString()

                val memo = BikeMemo(
                    bikeName1,
                    startDate1,
                    repairDate1,
                    km1,
                    content1,
                    amount1,
                    note1,
                    totalAmount1
                )
                insertBikeMemo(memo)


                 
            }
        }

    }


    private fun insertBikeMemo(memo: BikeMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            bikeMemoDao.insert(memo)

            refreshAdapter()
        }
    }


    private fun refreshAdapter() {
        CoroutineScope(Dispatchers.IO).launch {
            var date = binding.repairDate.text.toString()
            bikeList.clear()
            bikeList.addAll(bikeMemoDao.getDate(date))
            withContext(Dispatchers.Main) {
                bikeAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun deleteBikememo(bikeMemo: BikeMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            bikeMemoDao.delete(bikeMemo)
            refreshAdapter()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //날짜 적용 함수
    private fun dateSet() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("YYYY-MM-DD")
        val timestamp: String = sdf.format(date)
        binding.repairDate.setText(timestamp)
    }

    private fun spinnerSelected() {
        // 스피너 적용
        val spinner: Spinner = binding.planetsSpinner
        //프레그먼트에서 컨텍스트를 얻을때는 requireContext() 로 얻을수 있습니다.
        ArrayAdapter.createFromResource(
            requireContext(),  //컨텍스트를 불러올수 있는 메서드
            R.array.spinner_item,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //스피너가 선택됬을때 실행메서드
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//spinner 가 선택이 선택되면 "참고"로 초기화 그렇지않으면 선택단어 컨텐츠  변경

                with(binding) {
                    if ("선택" == parent?.getItemAtPosition(position).toString()) {
                        content.text = "참고"
                    } else {
                        content.text = parent?.getItemAtPosition(position).toString()
                        spinerRefer = parent?.getItemAtPosition(position).toString()
                    }
                    //컨텐츠가 변경되면 리퍼렌스 내용 변경
                    contentSelect()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    fun contentSelect() {

        //컨텐츠가 변경되면 리퍼렌스 내용 변경
        with(binding) {
            when (content.text.toString()) {
                "오일" -> reference.setText(R.string.oil)
                "오일필터" -> reference.setText(R.string.oilfillter)
                "앞브레이크패드" -> reference.setText(R.string.brakepad)
                "뒷브레이크패드" -> reference.setText(R.string.brakepad)
                "앞타이어" -> reference.setText(R.string.tire)
                "뒷타이어" -> reference.setText(R.string.tire)
                "벨트" -> reference.setText(R.string.belt)
                "무브볼" -> reference.setText(R.string.moveball)
                "미션오일" -> reference.setText(R.string.missionoil)
                "에어클리너" -> reference.setText(R.string.aircleaner)
                "슈" -> reference.setText(R.string.shu)
                "베어링" -> reference.setText(R.string.bearing)
                "프러그" -> reference.setText(R.string.plug)
                "벨브조절" -> reference.setText(R.string.valve)
                "기타구동계" -> reference.setText(R.string.drivesystem)
                "기타엔진계" -> reference.setText(R.string.engine)
                "기타램프" -> reference.setText(R.string.lamp)
                "기타" -> reference.setText(R.string.etc)
                "참고" -> reference.setText(R.string.reference)
            }
        }
    }

    override fun onDeleteListener(bikeMemo: BikeMemo) {
        deleteBikememo(bikeMemo)
    }




}