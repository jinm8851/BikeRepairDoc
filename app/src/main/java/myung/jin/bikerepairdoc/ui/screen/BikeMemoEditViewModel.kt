package myung.jin.bikerepairdoc.ui.screen


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.ui.room.BikeMemoRepository

class BikeMemoEditViewModel(
    private val bikeMemoRepository: BikeMemoRepository
) : ViewModel() {

    var bikeUiState by mutableStateOf(BikeUiState())
        private set

    // private val bikeId: Int? = savedStateHandle[BikeMemoEditDestination.bikeMemoIdArg]

    /**
    BikeMemoEditScreen에서 LaunchedEffect를 사용하여 bikeMemoId가 변경될 때마다 viewModel.getBikeMemo(bikeMemoId)를 호출하도록 수정했습니다.
    BikeMemoEditViewModel에 getBikeMemo() 함수를 추가하여 bikeMemoId를 사용하여 데이터베이스에서 해당 아이템 정보를 가져와서 bikeUiState를 업데이트하도록 구현했습니다.
    */
    fun getBikeMemo(bikeMemoId: Int) {
        viewModelScope.launch {
            try {
                bikeUiState = bikeMemoRepository.getBikeMemoStream(bikeMemoId)
                    .filterNotNull() // null이 아닌 값만 필터링
                    .first()  // 첫 번째 값을 가져옴
                    .toBikeUiState(true)  // bikeUiState를 업데이트
            } catch (e: Exception) {
                Log.d("BikeMemoEditViewModel", "getBikeMemo: $e")
            }
            Log.d("BikeMemoEditViewModel", "getBikeMemo: ${bikeUiState.bikeDetails}")
        }
    }

    // 닉네임이 입력되었는지 확인하는 함수 유효성검사
    private fun validateInput(uiState: BikeDetails = bikeUiState.bikeDetails): Boolean {
        return with(uiState) {
            nickName.isNotBlank()
        }
    }

    // copy함수로 업데이트시킴
    fun updateUiState(bikeDetails: BikeDetails) {
        bikeUiState = bikeUiState.copy(
            bikeDetails = bikeDetails,
            isEntryValid = validateInput(bikeDetails)
        )
    }

    suspend fun updateBikeMemo() {
        if (validateInput()) {
            try {
                bikeMemoRepository.updateBikeMemo(bikeUiState.bikeDetails.toBikeMemoWithId())
                // 바이크메모에 아이디가 맞지 않아 업데이트가 안되었음 아이디를 불러와 복사해줌
                Log.d(
                    "BikeMemoEditViewModel",
                    "updateBikeMemo: ${bikeUiState.bikeDetails.toBikeMemoWithId()}"
                )

            } catch (e: Exception) {
                Log.d("BikeMemoEditViewModel", "updateBikeMemo: $e")

            }
        }

    }

    suspend fun deleteBikeMemo(bikeMemoId: Int) {
        bikeMemoRepository.deleteBikeMemoId(bikeMemoId)
    }
}