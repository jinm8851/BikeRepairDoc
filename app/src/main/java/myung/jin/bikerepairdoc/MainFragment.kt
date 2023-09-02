package myung.jin.bikerepairdoc


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import myung.jin.bikerepairdoc.databinding.FragmentMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainFragment : Fragment(), OnDeleteListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val bikeList = mutableListOf<BikeMemo>()
    private lateinit var helper: RoomHelper
    private lateinit var bikeAdapter: RecyclerAdapter
    private lateinit var bikeMemoDao: BikeMemoDao

    private val bikeListMS = mutableListOf<BikeMemo>()
    // 년도만 추출
    private lateinit var year: String
    private var totalAmount1 = 0
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

            //마지막 입력한 모델명 이나 구입 날짜를 가져와 EditText 에 넣어 주는 코드
            textSet()


            save.setOnClickListener {
                //수리날짜 연도로 저장
                reDateSet()
                checkMemo()
                // 버튼을 누른 후 금액 입력란을 빈칸으로 초기화
                amount.setText("")
                note.setText("")

            }
        }


    }

    private fun textSet() {
        // 모델 넘버와 구입 날짜 앱 재 실행시 사라 지는 문제 해결 중
        // 바이크 리스트 를 전부 불러와 모델과 날짜를 입력 하고 바이크 리스트 를 지워 버림
        CoroutineScope(Dispatchers.IO).launch {
            bikeListMS.clear()
            bikeListMS.addAll(bikeMemoDao.getAll())
            withContext(Dispatchers.Main) {

                if (bikeListMS.isNotEmpty() && bikeListMS.last().model.isNotEmpty()) {
                    val modelText: String = bikeListMS.last().model
                    binding.bikeName.setText(modelText)

                } else {
                    binding.bikeName.setText("")
                }
                if (bikeListMS.isNotEmpty() && bikeListMS.last().purchaseDate.isNotEmpty()) {
                    val pDate: String = bikeListMS.last().purchaseDate
                    binding.stDated.setText(pDate)

                } else {
                    binding.stDated.setText("")
                }

            }

        }

    }

    //버튼을 눌렀을때 앱의 값을 불러와 널체크 후 값입력 메모를 만들고 인서트
    private fun checkMemo() {
        with(binding) {
            //null check 후 메모 리스트를 만들어서 바이크리스트에 적용 후 인서트
            var bikeName1 = bikeName.text.toString()
            if (bikeName1.isEmpty()) {
                Toast.makeText(requireContext(), R.string.nickem, Toast.LENGTH_LONG).show()
                bikeName1 = ""
            }

            //구입날짜나 다른것이 비어있으면 참고를 빈 문자로 변경
            var startDate1 = stDated.text.toString()
            if (startDate1.isEmpty()) {
                Toast.makeText(requireContext(), R.string.purem, Toast.LENGTH_LONG)
                    .show()
                startDate1 = ""
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

            if (year.isEmpty()) {
                year = ""
            }
// 바이크 메모에 생성자 로 넘겨줌
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
    @SuppressLint("notifyDataSetChanged")
    private fun refreshAdapter() {
        CoroutineScope(Dispatchers.IO).launch {
            val date = binding.repairDate.text.toString()
            bikeList.clear()
            bikeList.addAll(bikeMemoDao.getDate(date))

//토탈금액 앱을 다시켜면 0이 되는 문제 해결중 (수리 날짜를 바꿘다 다시 불러오면 합계가 안맞음)
            // bikeList 의 마지막인 a 가 널이면 0을 로 초기화
            //for 문으로 해결 바이크리스트에서 어마운트만빼서 더해 합계에 넣음
 //           var dAmount = 0
//            for (bike in bikeList){
//                dAmount += bike.amount
//                Log.d("테스트","$dAmount+$totalAmount1")
//            }
            // 챗 gpt 코드로 변경

           val dAmount = bikeList.sumOf { it.amount }

           // totalAmount1 = dAmount
                    //  Log.d("테스트","$dAmount+$totalAmount1")
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

    //날짜 적용 함수 (chat gpt 가 만들어준 함수)
    fun dateSet() {
        val currentDate = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        binding.repairDate.setText(currentDate.format(formatter))
    }
    /*private fun dateSet() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        //날자로 저장
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val timestamp: String = sdf.format(date)
        binding.repairDate.setText(timestamp)

    }*/

    // 수리날짜 년도로 저장 입력 날짜를 받아 앞에서 4자리까지 잘라서 넣음
    private fun reDateSet() {
        val redate: String = binding.repairDate.text.toString()

        year = redate.substring(0, 4)
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

                val selecedItem = parent?.getItemAtPosition(position).toString()


                with(binding) {
                    if (getText(R.string.select) == selecedItem) {
                        content.text = getText(R.string.reference)
                    } else {
                        content.text = selecedItem

                    }
                    //컨텐츠가 변경되면 리퍼렌스 내용 변경
                    contentSelect()
                    //스피너 처음 표시되는 컬러 색상 변경은 테마에서 변경

                }

            }

            //아무것도 선택되지 않았을때 적용
            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.reference.setText(R.string.referenceinfo)
            }

        }
    }

    fun contentSelect() {

        //컨텐츠가 변경되면 리퍼렌스 내용 변경
        with(binding) {
            when (content.text.toString()) {
                getText(R.string.oil) -> reference.setText(R.string.oilinfo)
                getText(R.string.oilfilter) -> reference.setText(R.string.oilfillterinfo)
                getText(R.string.fpad) -> reference.setText(R.string.brakepadinfo)
                getText(R.string.rpad) -> reference.setText(R.string.brakepadinfo)
                getText(R.string.ftire) -> reference.setText(R.string.tireinfo)
                getText(R.string.rtire) -> reference.setText(R.string.tireinfo)
                getText(R.string.belt) -> reference.setText(R.string.beltinfo)
                getText(R.string.moveball) -> reference.setText(R.string.moveballinfo)
                getText(R.string.missionoil) -> reference.setText(R.string.missionoilinfo)
                getText(R.string.aircleaner) -> reference.setText(R.string.aircleanerinfo)
                getText(R.string.shu) -> reference.setText(R.string.shuinfo)
                getText(R.string.bearing) -> reference.setText(R.string.bearinginfo)
                getText(R.string.plug) -> reference.setText(R.string.pluginfo)
                getText(R.string.valve) -> reference.setText(R.string.valveinfo)
                getText(R.string.drivesystem) -> reference.setText(R.string.drivesysteminfo)
                getText(R.string.engine) -> reference.setText(R.string.engineinfo)
                getText(R.string.lamp) -> reference.setText(R.string.lampinfo)
                getText(R.string.etc) -> reference.setText(R.string.etcinfo)
                getText(R.string.reference) -> reference.setText(R.string.referenceinfo)
                getText(R.string.fdisk) -> reference.setText(R.string.diskinfo)
                getText(R.string.rdisk) -> reference.setText(R.string.diskinfo)
                getText(R.string.select) -> reference.setText(R.string.referenceinfo)
                else -> {
                    reference.setText(R.string.referenceinfo)
                }
            }
        }
    }

    // 리사이클러 뷰를 클릭했을때 룸 삭제 및 리사이클러 재 설정
    override fun onDeleteListener(bikeMemo: BikeMemo) {
        val dMemo = bikeMemo.amount
        totalAmount1 -= dMemo
        binding.totalAmount.text = totalAmount1.toString()
        deleteBikememo(bikeMemo)
    }


    //라이프사이클을 통한 두 프레그먼트간에 데이터 교환 리즘에서 재설정을 해야 화면에 보임
    override fun onResume() {
        super.onResume()
        refreshAdapter()
    }
}

