package myung.jin.bikerepairdoc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import myung.jin.bikerepairdoc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    class MyFragmentPagerAdapter(activiy: FragmentActivity): FragmentStateAdapter(activiy){
        val fragment: List<Fragment>
        init {
            fragment = listOf(MainFragment(),TotalFragment())
        }

        override fun getItemCount(): Int {
            return fragment.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragment[position]
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MyFragmentPagerAdapter(this)
        binding.viewpager.adapter = adapter
    }


}