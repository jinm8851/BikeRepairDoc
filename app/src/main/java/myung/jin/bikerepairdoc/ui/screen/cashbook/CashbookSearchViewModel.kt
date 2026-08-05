package myung.jin.bikerepairdoc.ui.screen.cashbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import myung.jin.bikerepairdoc.ui.room.CashBook
import myung.jin.bikerepairdoc.ui.room.CashBookRepository

class CashbookSearchViewmodel(
    private val repository: CashBookRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 모든 가계부 내역 가져오기
    private val allCashBookList: StateFlow<List<CashBook>> =
        repository.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 검색어에 따라 실시간으로 필터링된 결과
    val filteredCashBookList: StateFlow<List<CashBook>> =
        combine(allCashBookList, _searchQuery) { list, query ->
            if (query.isBlank()) {
                list
            } else {
                list.filter { item ->
                    item.date.contains(query, ignoreCase = true) ||
                    item.content.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 필터링된 결과의 합계 금액 (수입 - 지출)
    val filteredTotalAmount: StateFlow<Long> = 
        filteredCashBookList.map { list ->
            list.sumOf { it.income } - list.sumOf { it.expense }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteCashBook(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}
