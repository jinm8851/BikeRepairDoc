package myung.jin.bikerepairdoc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myung.jin.bikerepairdoc.databinding.ItemRecyclerviewBinding


class RecyclerAdapter2(private var bikeMemoList: List<BikeMemo>, var onDeleteListener: OnDeleteListener):
    RecyclerView.Adapter<RecyclerAdapter2.Holder>(){





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
     val binding = ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
       return bikeMemoList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //날짜를 역순으로 리사이클뷰에 정렬
       val memo = bikeMemoList.sortedByDescending { it.date }.get(position)

        holder.setMemo(memo)
        holder.itemView.setOnLongClickListener {
            onDeleteListener.onDeleteListener(memo)
            return@setOnLongClickListener true
        }
    }

    class Holder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){

        fun setMemo(bikeMemo: BikeMemo){
            with(binding){
                itemRepairDate.text = bikeMemo.date
                itemKm.text = bikeMemo.km.toString()
                itemRePair.text = bikeMemo.refer
                itemAmount.text = bikeMemo.amount.toString()
                itemNote.text = bikeMemo.note
            }
        }
    }
}