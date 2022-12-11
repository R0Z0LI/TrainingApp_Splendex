package hu.bme.aut.android.trainingapp.model

data class TrainingPlan(
    var name: String? = null,
    var trainingDays: ArrayList<TrainingDays>
    )
