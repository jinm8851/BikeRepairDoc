package myung.jin.bikerepairdoc.ui.screen


import android.icu.text.NumberFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import myung.jin.bikerepairdoc.R
import myung.jin.bikerepairdoc.ui.room.BikeMemo
import myung.jin.bikerepairdoc.ui.room.BikeMemoRepository

class MainScreenViewModel(
    private val bikeMemoRepository: BikeMemoRepository
) : ViewModel() {

    private val _bikeUiState = MutableStateFlow(BikeUiState()) // MutableStateFlow 선언
    val bikeUiState: StateFlow<BikeUiState> = _bikeUiState.asStateFlow() // StateFlow로 노출

    val _dateState: MutableStateFlow<String> = MutableStateFlow(currentDateString())
    val dateState: StateFlow<String> = _dateState.asStateFlow()

    // 바이크메모 의 모든 리스트를 불러옴
    /*var bikeLazyUiState: StateFlow<BikeLazyState> =
         bikeMemoRepository.getAllBikeMemoStream().map { BikeLazyState(it) }
             .stateIn(
                 scope = viewModelScope,
                 started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                 initialValue = BikeLazyState()
             )
 */
    //AI code
    private val _bikeLazyUiState = MutableStateFlow(BikeLazyState())
    val bikeLazyUiState: StateFlow<BikeLazyState> = _bikeLazyUiState.asStateFlow()

    // 처음 시작할때 bikeUiState를 업데이트하고 화면을 초기화시킴
    init {
        // AI code
        initializeBikeUiState()
        loadBikeMemoList()
    }

    // 바이크메모 첫번째 값을 불러와 UI를 날짜등을 넣고 나머지는 초기화시킴
    private fun initializeBikeUiState() {
        viewModelScope.launch {
            bikeMemoRepository.getLastBikeMemoStream().firstOrNull()?.let { lastBikeMemo ->
                _bikeUiState.value = lastBikeMemo.toBikeUiState()
                // km 와 amount을 0 으로 초기화
                bikeUiState.value.bikeDetails.km = 0
                bikeUiState.value.bikeDetails.amount = 0
                bikeUiState.value.bikeDetails.selectedOption = ""
                bikeUiState.value.bikeDetails.etc = ""
            } ?: run {
                // If no last bike memo, set default values
                _bikeUiState.value = BikeUiState(bikeDetails = BikeDetails())
                bikeUiState.value.bikeDetails.repairDate =
                    dateState.value.toString() // 아무것도 없을때 날짜넣기
            }
        }
    }

    // 바이크메모리스트를 불러옴
    private fun loadBikeMemoList() {
        _bikeLazyUiState.value = BikeLazyState()
        viewModelScope.launch {
            bikeMemoRepository.getAllBikeMemoStream().collect { bikeMemos ->
                _bikeLazyUiState.value = BikeLazyState(bikeMemos)
            }
        }
    }

    // bikeUiState를 onvalueChage를 통해 화면이 바뀔때마다 bikeUiState를 업데이트하는 함수
    fun updateUiState(bikeDetails: BikeDetails) {
        _bikeUiState.value = bikeUiState.value.copy(
            // _bikeUiState.value를 업데이트
            bikeDetails = bikeDetails,
            isEntryValid = validateInput(bikeDetails),
            //updateUiState 함수에서 bikeLazyUiState를 업데이트하지 않아도 bikeLazyUiState는
            // bikeMemoRepository.getAllBikeMemoStream()을 통해 자동으로 업데이트됩니다.
            // bikeLazyUiState = bikeLazyUiState.value
        )
    }

    // 입력값이 유효한지 확인하는 함수
    private fun validateInput(bikeDetails: BikeDetails = _bikeUiState.value.bikeDetails): Boolean {
        return bikeDetails.nickName.isNotBlank()
    }


    // 정지함수를 이용해서 bikeUiState 입력값이 있는지 확인하고 아이템유형으로 변환해서 룸 데이터항목을 삽입
    suspend fun saveBikeMemo() {
        if (validateInput()) {
            bikeMemoRepository.insertBikeMemo(_bikeUiState.value.bikeDetails.toBikeMemo())
        }
    }

    // 메모를 삭제하는 함수
    suspend fun deleteBikeMemo(bikeMemoId: Int) {
        bikeMemoRepository.deleteBikeMemoId(bikeMemoId)
    }
}


data class BikeLazyState(val bikeList: List<BikeMemo> = listOf())


// ui 상태
data class BikeUiState(
    val bikeDetails: BikeDetails = BikeDetails(),
    val isEntryValid: Boolean = false, // 입력값이 유효한지 확인하는 함수
    // val bikeLazyUiState: BikeLazyState = BikeLazyState()
)

data class BikeDetails(
    val no: Int = 0,
    var nickName: String = "",
    var startDate: String = "",
    var repairDate: String = "",
    var km: Int = 0,
    var selectedOption: String = "",
    var amount: Int = 0,
    var etc: String = "",
    var year: String = "",
)

// BikeUiState를  id 없이 BikeMemo로 변환하는 확장함수
fun BikeDetails.toBikeMemo(): BikeMemo = BikeMemo(
    //no = no,
    model = nickName,
    purchaseDate = startDate,
    date = repairDate,
    km = km, //toIntOrNull() ?: 0,
    refer = selectedOption, // 수리내역 스피너 드롭다운메뉴
    amount = amount,  //.toIntOrNull() ?: 0,
    note = etc,
    year = year,
)

// BikeUiState를 BikeMemo로 변환하는 확장 함수 (id 포함 BikeMemoEditScreen 에서 사용)
fun BikeDetails.toBikeMemoWithId(): BikeMemo = BikeMemo(
    no = no,
    model = nickName,
    purchaseDate = startDate,
    date = repairDate,
    km = km,
    refer = selectedOption,
    amount = amount,
    note = etc,
    year = year
)

// 금액을 그나라 통화로 변환하는 확장함수
fun BikeMemo.formatedAmount(): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}

// BikeMemo를 BikeUiState로 변환하는 확장함수
fun BikeMemo.toBikeUiState(isEntryValid: Boolean = false): BikeUiState = BikeUiState(
    bikeDetails = this.toBikeDetails(),
    isEntryValid = isEntryValid
)

// BikeMemo를 BikeDetails로 변환하는 확장함수
fun BikeMemo.toBikeDetails(): BikeDetails = BikeDetails(
    no = no,
    nickName = model,
    startDate = purchaseDate,
    repairDate = date,
    km = km,
    selectedOption = refer,
    amount = amount,
    etc = note,
    year = year,

    )


// 선택 변경
fun selectsSet(bikeDetails: BikeDetails): Int {
    val selectedOption = bikeDetails.selectedOption
    return when (selectedOption) {
        "오일" -> R.string.oil
        "오일 필터" -> R.string.oil_filter
        "앞 패드" -> R.string.front_pad
        "뒷 패드" -> R.string.back_pad
        "앞 타이어" -> R.string.front_tire
        "뒷 타이어" -> R.string.back_tire
        "벨트" -> R.string.belt
        "무브볼" -> R.string.move_ball
        "미션 오일" -> R.string.mission_oil
        "에어 클리너" -> R.string.air_cleaner
        "크러치 슈" -> R.string.shu
        "베어링" -> R.string.bearing
        "프러그" -> R.string.plug
        "앞 디스크" -> R.string.front_dist
        "뒷 디스크" -> R.string.back_disk
        "벨브 조정" -> R.string.valve
        "기타 구동계" -> R.string.drive_system
        "기타 엔진계" -> R.string.engine
        "기타 램프" -> R.string.lamp
        "기타" -> R.string.etc
        else -> R.string.reference
    }
}

// 선택 참조 변경
fun contentsSet(uiState: BikeDetails): Int {
    val selectedOption = uiState.selectedOption
    return when (selectedOption) {
        "오일" -> R.string.oil_info
        "오일 필터" -> R.string.oil_filter_info
        "앞 패드" -> R.string.brake_pad_info
        "뒷 패드" -> R.string.brake_pad_info
        "앞 타이어" -> R.string.tire_info
        "뒷 타이어" -> R.string.tire_info
        "벨트" -> R.string.belt_info
        "무브볼" -> R.string.move_ball_info
        "미션 오일" -> R.string.mission_oil_info
        "에어 클리너" -> R.string.air_cleaner_info
        "크러치 슈" -> R.string.shu_info
        "베어링" -> R.string.bearing_info
        "프러그" -> R.string.plug_info
        "앞 디스크" -> R.string.disk_info
        "뒷 디스크" -> R.string.disk_info
        "벨브 조정" -> R.string.valve_info
        "기타 구동계" -> R.string.drive_system_info
        "기타 엔진계" -> R.string.engine_info
        "기타 램프" -> R.string.lamp_info
        "기타" -> R.string.etc_info
        else -> R.string.reference_info
    }
}

// 오늘 날짜를 텍스트필드로 반환
fun currentDateString(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = currentDate.format(formatter)
    return formattedDate
}