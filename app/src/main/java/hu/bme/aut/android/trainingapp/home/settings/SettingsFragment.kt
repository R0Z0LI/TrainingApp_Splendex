package hu.bme.aut.android.trainingapp.home.settings

import android.animation.LayoutTransition
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import hu.bme.aut.android.trainingapp.R
import hu.bme.aut.android.trainingapp.auth.login.DetailsActivity
import hu.bme.aut.android.trainingapp.databinding.FragmentSettingsBinding
import hu.bme.aut.android.trainingapp.home.HomeActivity
import hu.bme.aut.android.trainingapp.home.settings.language.MyPreference
import hu.bme.aut.android.trainingapp.home.settings.notification.*
import hu.bme.aut.android.trainingapp.home.settings.notification.Notification
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class SettingsFragment : Fragment(R.layout.fragment_settings){

    private lateinit var binding: FragmentSettingsBinding
    private val languageList = arrayOf("en","hu")
    private lateinit var myPreference: MyPreference
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    companion object {
        const val CHANNEL_ID = "dummy_channel"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    @RequiresApi(33)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvNotifications.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.cvSecurity.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.cvAppearance.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.cvAbout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.rlNotification.setOnClickListener{
            if(binding.llNotification.visibility == View.GONE){
                TransitionManager.beginDelayedTransition(binding.cvNotifications, AutoTransition())
                binding.llNotification.visibility = View.VISIBLE
            } else{
                TransitionManager.beginDelayedTransition(binding.cvNotifications, AutoTransition())
                binding.llNotification.visibility = View.GONE
            }
        }

        binding.rlSecurity.setOnClickListener {
            if(binding.rlExpanding.visibility == View.GONE){
                TransitionManager.beginDelayedTransition(binding.cvSecurity, AutoTransition())
                binding.rlExpanding.visibility = View.VISIBLE
            }else{
                TransitionManager.beginDelayedTransition(binding.cvSecurity, AutoTransition())
                binding.rlExpanding.visibility = View.GONE
            }
        }

        binding.rlAppearance.setOnClickListener{
            if(binding.llAppearance.visibility == View.GONE){
                TransitionManager.beginDelayedTransition(binding.cvAppearance, AutoTransition())
                binding.llAppearance.visibility = View.VISIBLE
            } else{
                TransitionManager.beginDelayedTransition(binding.cvAppearance, AutoTransition())
                binding.llAppearance.visibility = View.GONE
            }
        }

        binding.rlAbout.setOnClickListener{
            if(binding.llAbout.visibility == View.GONE){
                TransitionManager.beginDelayedTransition(binding.cvAbout, AutoTransition())
                binding.llAbout.visibility = View.VISIBLE
            } else{
                TransitionManager.beginDelayedTransition(binding.cvAbout, AutoTransition())
                binding.llAbout.visibility = View.GONE
            }
        }

        binding.btnProfile.setOnClickListener{
            startActivity(Intent(requireContext(), DetailsActivity::class.java))
        }

        myPreference = MyPreference(requireContext())

        binding.spLanguage.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,languageList)

        val lang = myPreference.getLoginCount()
        val index = languageList.indexOf(lang)
        if(index >= 0){
            binding.spLanguage.setSelection(index)
        }

        binding.tvLanguage.setOnClickListener {
            myPreference.setLoginCount(languageList[binding.spLanguage.selectedItemPosition])
            startActivity(Intent(requireContext(), HomeActivity::class.java))
        }

        createNotificationChannel()

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                showDummyNotification()
                Toast.makeText(requireContext(), "Notification permission from App Settings", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(requireContext(), "Please grant Notification permission from App Settings", Toast.LENGTH_SHORT).show()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(requireContext(), AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context?.startActivity(intent)
                }
            }

        }

        binding.btnSubmit.setOnClickListener{
            scheduleNotification()
        }


        binding.swKeepOnScreen.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        var isNightModeOn = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.swKeepOnScreen.isChecked = isNightModeOn
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun scheduleNotification() {
        val intent = Intent(requireContext().applicationContext, Notification::class.java)
        val title = binding.titleET.text.toString()
        val message = binding.messageET.text.toString()
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)


        val pendingIntent = PendingIntent.getBroadcast(
            requireContext().applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext().applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext().applicationContext)

        AlertDialog.Builder(requireContext())
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                "\n Message: " + message +
                "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID,name, importance)
        channel.description = desc
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showDummyNotification() {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Congratulations! 🎉🎉🎉")
            .setContentText("You have post a notification to Android 13!!!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(1, builder.build())
        }
    }


}