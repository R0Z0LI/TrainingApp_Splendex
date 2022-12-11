package hu.bme.aut.android.trainingapp.home.plans.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.ExerciseItemBinding
import hu.bme.aut.android.trainingapp.model.Exercise

class ExerciseAdapter(private val listener: OnExerciseSelectedListener) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>(){
    private val exercises : ArrayList<Exercise> = arrayListOf()


    interface OnExerciseSelectedListener{
        fun onExerciseSelectedListener(position: Int)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val item =exercises[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    fun update(newExercises: List<Exercise>) {
        exercises.clear()
        for (exercise in newExercises) {
            exercises.add(exercise)
        }
        notifyDataSetChanged()
    }


    inner class ExerciseViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var binding = ExerciseItemBinding.bind(itemView)
        var item : Exercise? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(newExercise: Exercise){
            item = newExercise
            binding.tvType.text = item?.type
            binding.tvDur.text = item?.duration
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onExerciseSelectedListener(position)
            }
        }

    }
}