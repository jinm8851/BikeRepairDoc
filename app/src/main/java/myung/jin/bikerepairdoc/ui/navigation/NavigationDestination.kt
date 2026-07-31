package myung.jin.bikerepairdoc.ui.navigation

interface NavigationDestination {
    // 컴포저블의 경로를 정의하는 고유한 이름
    val route: String

    // 화면에 표시될 제목을 포함하는 문자열 리소스 ID
    val titleRes: Int
}
