package hu.bme.aut.android.trainingapp.home.plans

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.PlanItemBinding
import hu.bme.aut.android.trainingapp.model.TrainingDays

class TrainingDaysAdapter(private val listener: OnTrainingDaySelectedListener) : RecyclerView.Adapter<TrainingDaysAdapter.TrainingDaysViewHolder>() {
    private val trainingDays : ArrayList<TrainingDays> = ArrayList()


    interface OnTrainingDaySelectedListener{
        fun onTrainingDaySelectedListener(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingDaysViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_item, parent, false)
        return TrainingDaysViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingDaysViewHolder, position: Int) {
        val item = trainingDays[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return trainingDays.size
    }

    fun update(newTrainingDays: List<TrainingDays>) {
        trainingDays.clear()
        for (trainingDay in newTrainingDays) {
            trainingDays.add(trainingDay)
        }
        notifyDataSetChanged()
    }

    inner class TrainingDaysViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var binding = PlanItemBinding.bind(itemView)
        var item : TrainingDays? = null

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onTrainingDaySelectedListener(position)
            }
        }

        fun bind(newTrainingDay: TrainingDays?){
            item = newTrainingDay
            binding.tvDay.text = item?.day
            binding.tvType.text = item?.type
            binding.tvEstimatedDuration.text = item?.estimatedDuration
        }
    }
}