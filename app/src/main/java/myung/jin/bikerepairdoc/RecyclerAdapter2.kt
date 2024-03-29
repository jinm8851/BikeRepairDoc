package myung.jin.bikerepairdoc

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myung.jin.bikerepairdoc.databinding.ItemRecyclerviewBinding
import java.util.Random


class RecyclerAdapter2(private var bikeMemoList: List<BikeMemo>, var onDeleteListener: OnDeleteListener):
    RecyclerView.Adapter<RecyclerAdapter2.Holder>(){

    //날짜별 색상을 저장 하는 맵 (날짜 키,색상 값)
    private val dateColorMap = mutableMapOf<String,Int>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //날짜를 역 순으로 리사이클뷰에 정렬
        val memo = bikeMemoList.sortedByDescending { it.date }.get(position)

        /*
         내생각 메모.데이트를 변수에 담아 중간 숫자 두개만 날짜로 변경헤서 홀더에 적용 dayOfMonth를 한번만
         가져와서 한가지 색으로만 표현됨
         날짜에서 일자부분을 추출
         val dayOfMonth = memo.date.substring(8,10)
         val backgroundColor = generateBackgroundDolor(dayOfMonth)
        */

        //메모에서 날짜 변수에 담기
        val date = memo.date

        //같은 날짜별로 색상을 가져오거나 새로 생성합니다
        val backgroundColor = dateColorMap.getOrPut(date){
            generateRandomColor() //날짜 별로 랜덤 생상 생성

        }

        //setMemo 에서 backgroundColor 맵을 날짜 별로 적용시킴
        holder.setMemo(memo,backgroundColor)

        holder.itemView.setOnLongClickListener {
            //삭제 다이얼로그 실행
            showDeleteDialog(holder.itemView.context,memo,position)
            return@setOnLongClickListener true

        }
    }

    //삭제다이얼로그 작성함수 홀더에 포지션까지 같이 파라미터로 받아서넘기면 notifyItemRemoved(sellect)를 사용할수있음
    private fun showDeleteDialog(context: Context, bikeMemo: BikeMemo, position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle(R.string.deletion)
        alertDialogBuilder.setMessage(R.string.wantDeletion)

        alertDialogBuilder.setPositiveButton(R.string.yes) { _, _ ->
            // "예" 버튼을 눌렀을 때의 동작 수행
            onDeleteListener.onDeleteListener(bikeMemo)
          //  notifyDataSetChanged() // 데이터셋이 변경되었음을 알려서 RecyclerView를 갱신
            // 홀더에 포지션까지 같이 파라미터로 받아서넘기면 notifyItemRemoved(position)를 사용할수있음
            notifyItemRemoved(position)
        }

        alertDialogBuilder.setNegativeButton(R.string.no) { dialog, _ ->
            // "아니오" 버튼을 눌렀을 때의 동작 수행
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun getItemCount(): Int {
       return bikeMemoList.size
    }



    class Holder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){

        fun setMemo(bikeMemo: BikeMemo,backgroundColor: Int){
            with(binding){
                itemRepairDate.text = bikeMemo.date
                itemKm.text = bikeMemo.km.toString()
                itemRePair.text = bikeMemo.refer
                itemAmount.text = bikeMemo.amount.toString()
                itemNote.text = bikeMemo.note

                //날짜와 랜덤색으로 저장된 배경색 설정
               linearLayout.setBackgroundColor(backgroundColor)
            }
        }
    }

    //랜덤으로 색상 설정
    private fun generateRandomColor(): Int {
        val random = Random()
        return Color.argb(32, random.nextInt(256), random.nextInt(256), random.nextInt(256))

    }
    //추출한 일짜로 컬러변경
    /*private fun generateBackgroundDolor(dayOfMonth: String): Int {
        val colorHex = "#32$dayOfMonth$dayOfMonth$dayOfMonth"
        return Color.parseColor(colorHex)
    }*/
}