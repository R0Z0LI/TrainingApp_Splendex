package hu.bme.aut.android.trainingapp.home.plans

import hu.bme.aut.android.trainingapp.model.Exercise
import hu.bme.aut.android.trainingapp.model.TrainingDays

interface PlansDataHolder {
    fun getTrainingDays() : ArrayList<TrainingDays>
    fun getExercises() : ArrayList<Exercise>
}