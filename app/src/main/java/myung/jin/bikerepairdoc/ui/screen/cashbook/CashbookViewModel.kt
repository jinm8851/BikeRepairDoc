package myung.jin.bikerepairdoc.ui.screen.cashbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.ui.room.CashBook
import myung.jin.bikerepairdoc.ui.room.CashBookRepository
import myung.jin.bikerepairdoc.ui.room.ContentName
import myung.jin.bikerepairdoc.ui.screen.currentDateString
import myung.jin.bikerepairdoc.ui.screen.filterNumbers

class CashbookViewModel(private val repository: CashBookRepository) : ViewModel() {
    // UI 상태 관리
    private val _uiState = MutableStateFlow(CashbookUiState())
    val uiState: StateFlow<CashbookUiState> = _uiState.asStateFlow()

    /*   // 날짜
       private val _dateState: MutableStateFlow<String> = MutableStateFlow(currentDateString())
       val dateState: StateFlow<String> = _dateState.asStateFlow()*/

    // 1.드롬다운에 보여줄 컨텐츠 이름 목록
    val contentNames = repository.getAllContentName()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // 2.컨텐츠이름 추가 기능
    fun addContentName(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertContentName(ContentName(name = title))
        }
    }

    // 3.컨텐츠이름 삭제 기능
    fun deleteContentName(content: ContentName) {
        viewModelScope.launch {
            repository.deleteContentName(content.id)
            // 현재 선택된 텍스트가 삭제된 이름과 같다면 초기화
            if (_uiState.value.selectedText == content.name) {
                _uiState.update { it.copy(selectedText = "💖 선택 💖", isEntryValid = false) }
            }
        }
    }

    // 4. 입력 유효성을 검사하는 공통 함수 추가
    private fun validateInput(details: CashbookDetails, selectedText: String): Boolean {
        return details.content.isNotBlank() &&
                selectedText != "💖 선택 💖" &&
                (details.income > 0 || details.expense > 0)
    }

    // 저장된 모든 약 체크 기록을 가져옵니다.
    val cashBookList: StateFlow<List<CashBook>> =
        repository.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 합계 금액 (수입 - 지출)
    val totalAmount: StateFlow<Long> = cashBookList.map { list ->
        list.sumOf { it.income } - list.sumOf { it.expense }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    // 드롭다운에서 선택된 값에 따라 UiState를 업데이트합니다.
    fun updateSelectedText(selectedText: String) {
        _uiState.update { currentState ->
            val updatedDetails = currentState.cashBookDetails.copy(content = selectedText)
            currentState.copy(
                selectedText = selectedText,
                cashBookDetails = updatedDetails,
                isEntryValid = validateInput(updatedDetails, selectedText)
            )
        }
    }

    // 수입 금액 업데이트
    fun updateIncome(income: String) {
        val amount = income.filterNumbers().toLongOrNull() ?: 0L
        _uiState.update { currentState ->
            val updatedDetails = currentState.cashBookDetails.copy(income = amount)
            currentState.copy(
                cashBookDetails = updatedDetails,
                isEntryValid = validateInput(updatedDetails, currentState.selectedText)
            )
        }
    }

    // 지출 금액 업데이트
    fun updateExpense(expense: String) {
        val amount = expense.filterNumbers().toLongOrNull() ?: 0L
        _uiState.update { currentState ->
            val updatedDetails = currentState.cashBookDetails.copy(expense = amount)
            currentState.copy(
                cashBookDetails = updatedDetails,
                isEntryValid = validateInput(updatedDetails, currentState.selectedText)
            )
        }
    }

    // 날짜 업데이트
    fun updateDate(date: String) {
        _uiState.update { currentState ->
            val updatedDetails = currentState.cashBookDetails.copy(date = date)
            currentState.copy(
                cashBookDetails = updatedDetails,
                isEntryValid = validateInput(updatedDetails, currentState.selectedText)
            )
        }
    }

    // 데이터를 데이터베이스에 저장합니다.
    fun saveCashBook() {
        if (_uiState.value.isEntryValid) {
            viewModelScope.launch {
                // 저장 시 현재 설정된 날짜가 비어있으면 오늘 날짜로 설정
                val finalDetails = if (_uiState.value.cashBookDetails.date.isBlank()) {
                    _uiState.value.cashBookDetails.copy(date = currentDateString())
                } else {
                    _uiState.value.cashBookDetails
                }

                repository.insertCashBook(finalDetails.toCashBook())

                // 저장 후 상태 초기화 (날짜는 오늘 날짜로 유지)
                _uiState.update {
                    CashbookUiState(
                        cashBookDetails = CashbookDetails(date = currentDateString())
                    )
                }
            }
        }
    }

    fun deleteCashBook(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

}

data class CashbookUiState(
    val cashBookDetails: CashbookDetails = CashbookDetails(date = currentDateString()),
    val isEntryValid: Boolean = false,
    val selectedText: String = "💖 선택 💖",
)

data class CashbookDetails(
    val id: Long = 0,
    val date: String = "",
    val content: String = "",
    val income: Long = 0,
    val expense: Long = 0,
)

fun CashbookDetails.toCashBook(): CashBook = CashBook(
    date = date,
    content = content,
    income = income,
    expense = expense
)
