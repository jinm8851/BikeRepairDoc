package myung.jin.bikerepairdoc


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner

import myung.jin.bikerepairdoc.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.Date


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


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
//spinner가 선택이 선택되면 "참고"로 초기화 그렇지않으면 선택단어 컨텐츠  변경
                with(binding) {
                    if ("선택" == parent?.getItemAtPosition(position).toString()) {
                        content.text = "참고"
                    } else {
                        content.text = parent?.getItemAtPosition(position).toString()

                    }
                    //컨텐츠가 변경되면 리퍼렌스 내용 변경
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
                        "참고"->reference.setText(R.string.reference)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //날짜 적용 함수
    fun dateSet() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("YYYY-MM-DD")
        val timestamp: String = sdf.format(date)
        binding.repairDate.setText(timestamp)
    }


}