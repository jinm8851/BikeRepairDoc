package myung.jin.bikerepairdoc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myung.jin.bikerepairdoc.databinding.Item1RecyclerBinding

class RecyclerAdapter(private val bikeMemoList: List<BikeMemo>, var onDeleteListener: OnDeleteListener) :
    RecyclerView.Adapter<RecyclerAdapter.Holder>() {
// 바인딩 전달
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = Item1RecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
       // 1.사용할 데이터를 꺼낸고
        val memo = bikeMemoList.get(position)

        // 2. 홀더에 데이터를 전달
        holder.setMemo(memo)
        holder.itemView.setOnLongClickListener {
            onDeleteListener.onDeleteListener(memo)
            return@setOnLongClickListener true
        }
    }
//리스트 갯수 전달
    override fun getItemCount(): Int {
        return bikeMemoList.size
    }
//리사이클러에 표시될 아이템
    class Holder(val binding: Item1RecyclerBinding) : RecyclerView.ViewHolder(binding.root){

        fun setMemo(bikeMemo: BikeMemo) {
            with(binding){
                item1Spiner.text = bikeMemo.refer
                item1Note.text =bikeMemo.note
                item1Amount.text = bikeMemo.amount.toString()
            }
        }
    }
}