package com.lolo.io.onelist.dialogs

import android.annotation.SuppressLint
import android.app.*
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.lolo.io.onelist.*
import com.lolo.io.onelist.Notification
import com.lolo.io.onelist.model.Item
import com.lolo.io.onelist.util.shake
import kotlinx.android.synthetic.main.dialog_edit_item.*
import kotlinx.android.synthetic.main.dialog_edit_item.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("InflateParams")
fun editItemDialog(activity: Activity, item: Item, onDoneEditing: (_: Item) -> Any?): AlertDialog {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_item, null).apply {
        item_title.setText(item.title)
        item_title.setSelection(item_title.text.length)
        item_comment.setText(item.comment)
        tv_date.setText(item.date)
        tv_time.setText(item.time)
        item_title.requestFocus()
    }

    val dialog = AlertDialog.Builder(activity).run {
        setView(view)
        create()
    }.apply {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        setCanceledOnTouchOutside(true)
        setOnCancelListener {
            onDoneEditing(item)
        }
    }

    view.apply {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute =  c.get(Calendar.MINUTE)

        btn_addReminder.setOnClickListener{
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute->
                tv_time.setText(""+hour+":"+minute)
            }
            TimePickerDialog(this.context,timeSetListener,hour,minute,true).show()

            val dateSetListener = DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                tv_date.setText(""+mDay+"/"+mMonth+"/"+mYear)
            }
            val dpd = DatePickerDialog(this.context,dateSetListener,year,month,day)
            dpd.show()
        }

        validateEdit.setOnClickListener {
            if (view.item_title.text.toString().isEmpty()) {
                dialog.item_title.shake()
            } else {
                val newItem = Item(view.item_title.text.toString(), view.item_comment.text.toString(), item.done, item.commentDisplayed, date = view.tv_date.text.toString(), time = view.tv_time.text.toString())
                onDoneEditing(newItem)
//                createnNotificationManager()
                val name = "Notif Channel"
                val desc = "Dexcription for Channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId,name,importance)
                channel.description = desc
                val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

//                scheduleNotification

                val intent = Intent(context.applicationContext, Notification::class.java)
                val message = view.item_title.text.toString()
                val title = "Your task is Overdue"
                intent.putExtra(messageExtra, message)
                intent.putExtra(titleExtra, title)

                val pendingIntent = PendingIntent.getBroadcast(
                    context.applicationContext,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                c.set(year,month,day, hour,minute)
                val time = c.timeInMillis
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    pendingIntent
                )
                dialog.dismiss()
//                val date = Date(time)

//                val dateFormat = android.text.format.DateFormat.getLongDateFormat(context.applicationContext)
//                val timeFormat = android.text.format.DateFormat.getTimeFormat(context.applicationContext)
//                AlertDialog.Builder(this.context)
//                    .setTitle("Your Task is Overdue")
//                    .setMessage(title+"\n"+message+"\n"+dateFormat.format(date)+" "+timeFormat.format(date))
//                    .show()
            }
        }
        cancelEdit.setOnClickListener {
            onDoneEditing(item)
            dialog.dismiss()
        }
    }
    return dialog
}