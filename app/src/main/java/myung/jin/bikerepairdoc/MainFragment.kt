package myung.jin.bikerepairdoc



import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log


import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText

import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import myung.jin.bikerepairdoc.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment(), OnDeleteListener  {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
     private val bikeList = mutableListOf<BikeMemo>()
    lateinit var helper: RoomHelper
    lateinit var bikeAdapter: RecyclerAdapter
    lateinit var bikeMemoDao: BikeMemoDao

// 년도만 추출
    lateinit var year: String
    var totalAmount1 = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        with(binding) {
            mainRecyclerView.adapter = bikeAdapter
            mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            // 모델 넘버와 구입 날짜 앱 재 실행시 사라지는 문제 해결 중
//            if (bikeList.isNotEmpty() && bikeList.last().model.isNotEmpty()){
//
//            val modelText: String = bikeList.last().model
//            bikeName.setText(modelText)
//            }else{ bikeName.setText("")}


            save.setOnClickListener {

                reDateSet()
                checkMemo()
                // 버튼을 누른 후 금액 입력란을 빈칸으로 초기화
                amount.setText("")

            }
        }


    }

    //버튼을 눌렀을때 앱의 값을 불러와 널체크 후 값입력 메모를 만들고 인서트
    private fun checkMemo() {
        with(binding){
            //null check 후 메모 리스트를 만들어서 바이크리스트에 적용 후 인서트
            var bikeName1 = bikeName.text.toString()
            if (bikeName1.isEmpty()) {
                Toast.makeText(requireContext(), "애칭이 비어있습니다. 애칭을 입력해주세요", Toast.LENGTH_LONG).show()
                bikeName1 = ""
            }

            //구입날짜나 다른것이 비어있으면 참고를 빈 문자로 변경
            var startDate1 = startDate.text.toString()
            if (startDate1.isEmpty()) {
                Toast.makeText(requireContext(),"구입날짜가 비어있습니다. 구입날짜를 입력해주세요",Toast.LENGTH_LONG).show()
                startDate1=""
            }

            var repairDate1 = repairDate.text.toString()
            if (repairDate1.isEmpty()) {
                repairDate1 = ""
            }

            //텍스트를 받아 인트로 변환
            val km2 = km.text.toString()
            val km1: Int = if (km2.isNotEmpty()) {
                km2.toInt()
            } else {
                0
            }

            var content1 = content.text.toString()
            if (content1.isEmpty()) {
                content1 = ""
            }

            val amount2 = amount.text.toString()
            val amount1: Int = if (amount2.isNotEmpty()) {
                amount2.toInt()
            } else {
                0
            }

            var note1 = note.text.toString()
            if (note1.isEmpty()) {
                note1 = ""
            }

            if (year.isEmpty()) year = ""
// 바이크 메모에 생성자로 넘겨줌
            val memo = BikeMemo(
                bikeName1,
                startDate1,
                repairDate1,
                km1,
                content1,
                amount1,
                note1,
                year
            )
            insertBikeMemo(memo)
        }
    }
//바이크 메모를 룸에 입력
    private fun insertBikeMemo(memo: BikeMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            bikeMemoDao.insert(memo)

            refreshAdapter()
        }
    }

//바이크메모를 룸에서 수리 날짜로 불러와 바이크리스트에 입력
//린트는 개발자가 완벽히 알맞은 코드나 충돌 가능성이 있는 코드를 사용할때 @SuppressLint(...)를 붙여 사용할 수 있게 해줍니다.
//
//@SuppressLint("NewApi")는 해당 프로젝트의 설정 된 minSdkVersion 이후에 나온 API를 사용할때  warning을 없애고 개발자가 해당 APi를 사용할 수 있게 합니다.
    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter() {
        CoroutineScope(Dispatchers.IO).launch {
            val date = binding.repairDate.text.toString()
            bikeList.clear()
            bikeList.addAll(bikeMemoDao.getDate(date))

//토탈금액 앱을 다시켜면 0이 되는 문제 해결중 (수리 날짜를 바꿘다 다시 불러오면 합계가 안맞음)
            // bikeList 의 마지막인 a 가 널이면 0을 로 초기화
            //for 문으로 해결 바이크리스트에서 어마운트만빼서 더해 합계에 넣음
//            for (bike in bikeList){
//                dAmount += bike.amount
//            }
            // 챗 gpt 코드로 변경
            val dAmount = bikeList.sumOf { it.amount }
            totalAmount1=dAmount
  //          Log.d("테스트","$dAmount+$totalAmount1")
            withContext(Dispatchers.Main) {
                bikeAdapter.notifyDataSetChanged()
                //합계금액을 날짜 리스트에서 뽑아와 저장
            binding.totalAmount.text = dAmount.toString()
            }
        }
    }



// 바이크 메모 삭제
    private fun deleteBikememo(bikeMemo: BikeMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            bikeMemoDao.delete(bikeMemo)
            refreshAdapter()
        }
    }

//바인딩 해제
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //날짜 적용 함수
    @SuppressLint("SimpleDateFormat")
    private fun dateSet() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        //날자로 저장
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val timestamp: String = sdf.format(date)
        binding.repairDate.setText(timestamp)

    }

       // 수리날짜 년도로 저장 입력 날짜를 받아 앞에서 4자리까지 잘라서 넣음
    private fun reDateSet(){
        val redate: String = binding.repairDate.text.toString()

        year = redate.substring(0,4)
    }

     fun spinnerSelected() {
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

                    }
                    //컨텐츠가 변경되면 리퍼렌스 내용 변경
                    contentSelect()
                }
            }
            //아무것도 선택되지 않았을때 적용
            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.reference.setText(R.string.reference)
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
                else -> {reference.setText(R.string.reference)}
            }
        }
    }
// 리사이클러 뷰를 클릭했을때 룸 삭제 및 리사이클러 재 설정
    override fun onDeleteListener(bikeMemo: BikeMemo) {
        val dMemo = bikeMemo.amount
        totalAmount1-=dMemo
        binding.totalAmount.text = totalAmount1.toString()
        deleteBikememo(bikeMemo)
    }



    //라이프사이클을 통한 두 프레그먼트간에 데이터 교환 리즘에서 재설정을 해야 화면에 보임
    override fun onResume() {
        super.onResume()
        refreshAdapter()
    }
}

