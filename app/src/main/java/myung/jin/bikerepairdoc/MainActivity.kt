package myung.jin.bikerepairdoc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import myung.jin.bikerepairdoc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 뷰페이져2 관련 크레스
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

    // EditText가 아닌 다를 영역을 터치했을 때, 키보드가 내려가게 된다
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        return super.dispatchTouchEvent(ev)
    }


}