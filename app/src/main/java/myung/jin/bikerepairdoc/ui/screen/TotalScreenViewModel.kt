package myung.jin.bikerepairdoc.ui.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import myung.jin.bikerepairdoc.ui.room.BikeMemoRepository

class TotalScreenViewModel(
    private val bikeMemoRepository: BikeMemoRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    /**
     * stateIn(...)
    이것이 코드의 핵심이며, StateFlow가 생성되는 곳입니다.
    stateIn은 일반 Flow를 StateFlow로 변환하는 함수입니다.
    StateFlow는 수집자에게 업데이트를 방출하는 단일 업데이트 가능 데이터 값을 가진 읽기 전용 상태를 나타내는 핫 플로우입니다.
    stateIn은 세 가지 인수를 사용합니다.
    scope: 흐름이 수집될 코루틴 범위입니다. 여기서는 viewModelScope로, ViewModel의 수명 주기에 연결된 범위입니다.
    started: 흐름의 공유를 시작하는 방법을 지정합니다. SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)는 구독자가 하나 이상 있는 동안만 흐름이 공유된다는 것을 의미합니다. TIMEOUT_MILLIS는 밀리초 단위의 시간 초과 값입니다.
    initialValue: StateFlow의 초기 값입니다. 여기서는 BikeLazyState()로, 빈 상태를 나타냅니다.
    stateIn은 StateFlow를 반환하며, 이는 totalUiState에 할당됩니다.
     */
    val totalUiState: StateFlow<BikeLazyState> =
        bikeMemoRepository.getAllBikeMemoStream().map { BikeLazyState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BikeLazyState()
            )

    suspend fun deleteBikeMemo(bikeMemoId: Int) {
        bikeMemoRepository.deleteBikeMemoId(bikeMemoId)
    }
}

