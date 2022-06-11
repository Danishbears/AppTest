package com.example.roomapp.fragments.list

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomapp.MainActivity
import com.example.roomapp.R
import com.example.roomapp.model.User
import com.example.roomapp.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.custom_row.view.*

class ListAdapter(
    private val mUserViewModel: UserViewModel,
    private val context: Context?
) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var fragmentList = emptyList<User>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val addButton: Button = itemView.buttonAdd
        val deleteButton:Button = itemView.buttonDelete
        val notifyButton:Button = itemView.notificationButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun getItemCount(): Int = fragmentList.size


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = fragmentList[holder.adapterPosition]

        holder.itemView.id_txt.text = position.inc().toString()

        if(item == fragmentList[0]){
            holder.deleteButton.visibility = View.INVISIBLE
        }else{
            holder.deleteButton.visibility = View.VISIBLE
        }

        val intent = Intent(context, MainActivity::class.java).apply {

            putExtra("notificationFragment",fragmentList[position].id)
        }

        val pendingIntent = TaskStackBuilder.create(context).run{
            addNextIntentWithParentStack(intent)
            getPendingIntent(fragmentList[position].id, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        createNotificationChannel()
        val notification = createNotification(pendingIntent,position)

        val notificationManager = NotificationManagerCompat.from(context!!)

        holder.addButton.setOnClickListener {
            insertDataToDatabase(position)
            notifyItemInserted(fragmentList.size.inc())
        }

        holder.deleteButton.setOnClickListener {
            deleteData()
            notificationManager.cancel(fragmentList.lastIndex)
        }

        holder.notifyButton.setOnClickListener {
            notificationManager.notify(position,notification)
        }

    }

    private fun createNotification(pendingIntent: PendingIntent, position: Int): Notification {
        return NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setContentTitle("You create a notification")
            .setContentText("Notification number ${position.inc()}")
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.nod))
            .setSmallIcon(androidx.appcompat.R.drawable.abc_btn_radio_to_on_mtrl_015)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun deleteData() {
        mUserViewModel.deleteUser(fragmentList[fragmentList.lastIndex])

        notifyItemRemoved(fragmentList.lastIndex)
    }

    private fun insertDataToDatabase(position: Int) {
        // Create User Object
        val fragment = User(
            fragmentList.size.inc(),
            "${position.inc()}"
        )
        // Add Data to Database
        mUserViewModel.addUser(fragment)
    }


    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

        }
    }

    companion object{
        const val CHANNEL_ID = "channelId"
        const val CHANNEL_NAME = "channelName"
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(fragment: List<User>) {
            this.fragmentList = fragment
            notifyDataSetChanged()
    }
}