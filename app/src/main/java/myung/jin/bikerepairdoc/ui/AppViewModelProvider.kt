package myung.jin.bikerepairdoc.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import myung.jin.bikerepairdoc.BikeMemoApplication
import myung.jin.bikerepairdoc.ui.screen.BikeMemoEditViewModel
import myung.jin.bikerepairdoc.ui.screen.MainScreenViewModel
import myung.jin.bikerepairdoc.ui.screen.TotalScreenViewModel
import myung.jin.bikerepairdoc.ui.screen.authScreen.AuthViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MainScreenViewModel(
                bikeMemoApplication().appContainer.bikeMemoRepository
            )
        }

        initializer {
            TotalScreenViewModel(
                bikeMemoApplication().appContainer.bikeMemoRepository
            )
        }
        initializer {
            BikeMemoEditViewModel(
                bikeMemoApplication().appContainer.bikeMemoRepository
            )
        }
        initializer {
            AuthViewModel(
                bikeMemoApplication().appContainer.bikeMemoRepository,
            )
        }
    }
}

fun CreationExtras.bikeMemoApplication(): BikeMemoApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BikeMemoApplication)