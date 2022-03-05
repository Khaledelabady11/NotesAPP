package com.example.notes
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.notes.databinding.ActivityMainBinding
import com.google.android.gms.common.SupportErrorDialogFragment
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_note.*
import kotlinx.android.synthetic.main.add_note.view.*
import kotlinx.android.synthetic.main.add_note.view.note_edt
import kotlinx.android.synthetic.main.add_note.view.title_edt
import kotlinx.android.synthetic.main.delete.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var noteRef:DatabaseReference?=null
    lateinit var noteList:ArrayList<Note>
    lateinit var picker:MaterialTimePicker
    lateinit var calendar:Calendar
    lateinit var binding:ActivityMainBinding
    lateinit var alarmManager:AlarmManager
    lateinit var pendingIntent: PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        noteList= ArrayList()
        calendar= Calendar.getInstance()
        var database:FirebaseDatabase= FirebaseDatabase.getInstance()
        noteRef=database.getReference("Notes")
        btnAdd.setOnClickListener {
            showDialog()
        }
           list.onItemClickListener= object :AdapterView.OnItemClickListener{
               override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                  var notes= noteList.get(p2)
                   var intent=Intent(this@MainActivity,Details::class.java)
                   intent.putExtra("titleKey",notes.title)
                   intent.putExtra("noteKey",notes.note)
                   startActivity(intent)

               }

           }
        list.onItemLongClickListener=
            AdapterView.OnItemLongClickListener { p0, p1, p2, p3 ->
                val alertBuilder=AlertDialog.Builder(this)
                val view =LayoutInflater.from(this).inflate(R.layout.delete,null)
                alertBuilder.setView(view)
                val alertDialog=alertBuilder.create()
                alertDialog.show()

                val myNote=noteList.get(p2)
                view.title_edt.setText(myNote.title)
                view.note_edt.setText(myNote.note)
                view.btn_update.setOnClickListener {
                    var childRef=noteRef!!.child(myNote.id!!)
                    var title=view.title_edt.text.toString()
                    var note=view.note_edt.text.toString()
                    var update=Note(myNote.id,title,note,getCurrentDate())
                    childRef.setValue(update)
                    alertDialog.dismiss()

                }
                view.btn_delete.setOnClickListener {
                    noteRef!!.child(myNote.id!!).removeValue()
                    alertDialog.dismiss()
                }

                false }

        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name:CharSequence="notesReminderChannel"
            val describtion="channel for Alarm Manager"
            val importance=NotificationManager.IMPORTANCE_HIGH
            val channel=NotificationChannel("foxAndroid",name,importance)
            channel.description=describtion
            val notificationManager=getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun showTimePicker() {
        picker=MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()
        picker.show(supportFragmentManager,"foxAndroid")
        picker.addOnPositiveButtonClickListener {
            if(picker.hour>12){
                String.format("%02d",picker.hour-12)+" : "+
                        String.format("%02d",picker.minute)+"PM"
            }
            else{
                String.format("%02d",picker.hour)+" : "+
                        String.format("%02d",picker.minute)+"AM"
            }

            calendar[Calendar.HOUR_OF_DAY]=picker.hour
            calendar[Calendar.MINUTE]=picker.minute
            calendar[Calendar.SECOND]=0
            calendar[Calendar.MILLISECOND]=0
            setAlarm()

        }

    }
    private fun setAlarm() {
        alarmManager=getSystemService(ALARM_SERVICE)as AlarmManager
        val intent=Intent(this,AlarmReciver::class.java)
        pendingIntent= PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent
        )
        Toast.makeText(this, "Alarm set Successfuly", Toast.LENGTH_SHORT).show()

    }


        fun showDialog(){
        val AlertBuilder=AlertDialog.Builder(this)
        val view=LayoutInflater.from(this).inflate(R.layout.add_note,null)
        AlertBuilder.setView(view)
        val alertDialog=AlertBuilder.create()
        alertDialog.show()
        view.btn_save.setOnClickListener {
            val title:String=view.title_edt.text.toString()
            val note:String=view.note_edt.text.toString()
            if(title.isNotEmpty()&&note.isNotEmpty()){
                var id =noteRef!!.push().key
                var model=Note(id,title,note,getCurrentDate())
                noteRef!!.child(id!!).setValue(model)
                alertDialog.dismiss()
            }
            else
                Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
        }


    }
    fun getCurrentDate():String{
        var calendar=Calendar.getInstance()
        var dateFormat=SimpleDateFormat("EEEE hh:mm a")
        var strDate=dateFormat.format(calendar.time)
        return strDate
    }
    override fun onStart() {
        super.onStart()
        noteRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear()
                for(n in snapshot.children){
                    var note=n.getValue(Note::class.java)
                    noteList.add(0,note!!)

                }
                Log.d("data", "onDataChange: $noteList")
                val adapter=noteAdapter(this@MainActivity,noteList)
                list.adapter=adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "No Internet", Toast.LENGTH_SHORT).show()
            }

        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.alarmMenu -> {showTimePicker()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}