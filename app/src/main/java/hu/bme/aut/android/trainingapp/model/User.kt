package hu.bme.aut.android.trainingapp.model

data class User(
    var name: Name? = Name("",""),
    var userName: String? = "",
    var phoneNumber: String? = "",
    var age: String? = "",
    var sex: String? = "",
    var skip: String? = "",
    var trainings: ArrayList<Training> = arrayListOf<Training>(),
    var friends: ArrayList<Friend> = arrayListOf<Friend>(),
    var trainingDetails: TrainingDetails ?= TrainingDetails("", "", "","")
)