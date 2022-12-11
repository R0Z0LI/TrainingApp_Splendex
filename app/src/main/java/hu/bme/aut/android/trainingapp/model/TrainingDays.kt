package hu.bme.aut.android.trainingapp.model

data class TrainingDays(
    var day: String? = null,
    var type: String? = null,
    var estimatedDuration: String? = null,
    var training: Training,
    var exercise: ArrayList<Exercise> = arrayListOf<Exercise>()
)