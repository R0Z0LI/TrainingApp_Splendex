package hu.bme.aut.android.trainingapp.home.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.model.Friend

class FriendAdapter(private val friendList : ArrayList<Friend>, private val listener: OnItemClickListener) : RecyclerView.Adapter<FriendAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = friendList[position]

        holder.first.text = currentItem.firstName
        holder.second.text = currentItem.secondName
        holder.user.text = currentItem.userName
    }


    override fun getItemCount(): Int {
        return friendList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener{
        val first : TextView = itemView.findViewById(R.id.tvFirst)
        val second : TextView = itemView.findViewById(R.id.tvSecond)
        val user : TextView = itemView.findViewById(R.id.tvUser)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(position)
            }
            return true
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

}