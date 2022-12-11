package hu.bme.aut.android.trainingapp.home.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.databinding.FragmentFriendsBinding
import hu.bme.aut.android.trainingapp.home.UserDataHolder
import hu.bme.aut.android.trainingapp.model.Friend
import hu.bme.aut.android.trainingapp.model.TrainingDetails
import hu.bme.aut.android.trainingapp.model.User

class FriendsFragment : Fragment(R.layout.fragment_friends), FriendAdapter.OnItemClickListener{

    private lateinit var database: DatabaseReference
    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : FragmentFriendsBinding
    private lateinit var user : User
    private var friendList : ArrayList<Friend> = arrayListOf()
    private var users : ArrayList<User> = arrayListOf()
    private var userDataHolder: UserDataHolder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDataHolder = if(activity is UserDataHolder){
            activity as UserDataHolder?
        } else{
            throw RuntimeException(
                "Activity must implement CurrencyDataHolder interface!"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendRecyclerView = binding.recyclerView
        friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        friendRecyclerView.setHasFixedSize(true)
        database = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        user = userDataHolder?.getUser()!!
        friendList.clear()
        val friends = user?.friends
        if (friends != null) {
            for(User in friends){
                friendList.add(User)
            }
        }
        initRecycleView()
        loadData()

        binding.appCompatButton.setOnClickListener {
            for(User in users) {
                if(binding.editText.text.toString() == User.userName){
                    val friend = Friend(User.name?.firstName, User.name?.secondName, User.userName, TrainingDetails(User.trainingDetails?.distanceTraveled, User.trainingDetails?.caloriesBurnt, User.trainingDetails?.hoursSpent, User.trainingDetails?.trainingsDone))
                    user.friends.add(friend)
                    friendList.add(friend)
                    initRecycleView()
                    updateData()
                }
            }
        }
    }

    private fun initRecycleView(){
        friendRecyclerView.adapter = FriendAdapter(friendList, this)
    }

    private fun updateData() {
        val uid = auth.currentUser?.uid
        if(uid != null){
            database.child(uid).setValue(user).addOnCompleteListener{
                if(it.isSuccessful){

                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadData() {
        database.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    users.clear()
                        for(userSnapshot in snapshot.children){
                            val user = userSnapshot.getValue(User::class.java)!!
                            if(user.userName != userDataHolder?.getUser()?.userName){
                                users.add(user)
                            }
                        }
                    }
                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun onItemClick(position: Int) {
        val intent = Intent(requireContext(), FriendDetailsActivity::class.java)
        intent.putExtra("First", friendList[position].firstName)
        intent.putExtra("Second", friendList[position].secondName)
        intent.putExtra("User", friendList[position].userName)
        intent.putExtra("CaloriesBurnt", friendList[position].trainingDetails?.caloriesBurnt)
        intent.putExtra("DistanceTraveled", friendList[position].trainingDetails?.distanceTraveled)
        intent.putExtra("HoursSpent", friendList[position].trainingDetails?.hoursSpent)
        intent.putExtra("TrainingsDone", friendList[position].trainingDetails?.trainingsDone)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        val friend = friendList.removeAt(position)
        user.friends.remove(friend)
        updateData()
        friendRecyclerView.adapter?.notifyItemRemoved(position)
    }

}