/*
 *MIT License
 *
 *Copyright (c) 2018 Rosário Pereira Fernandes
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy
 *of this software and associated documentation files (the "Software"), to deal
 *in the Software without restriction, including without limitation the rights
 *to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *copies of the Software, and to permit persons to whom the Software is
 *furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all
 *copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *SOFTWARE.
 */
package io.github.rosariopfernandes.firepesa.messaging

import android.content.pm.PackageManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.rosariopfernandes.firepesa.R

/**
 * NotificationService class
 * @author Rosário Pereira Fernandes
 */
class NotificationService : FirebaseMessagingService() {

    /**
     * This method will be called when a new Notification arrives from FCM.
     * It will then display the notification.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            displayNotification(it)
        }
        Log.d("NotificationService", "New Notification arrived")
    }

    /**
     * This method will be called when the FCM device token changes
     */
    override fun onNewToken(token: String) {
        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            FirebaseDatabase.getInstance().getReference("firePesa/consumers")
                    .child(it.uid).child("tokens/android")
                    .setValue(token)
        }
        Log.i("InstanceIdService", "FCM Token Refreshed")
    }

    /**
     * Displays the notification
     */
    private fun displayNotification(message:RemoteMessage.Notification){
        val mBuilder = NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_payment_black_24dp)
                .setContentTitle(message.title)
                .setContentText(message.body)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val app = getPackageManager().getApplicationInfo(getPackageName(),
                PackageManager.GET_META_DATA)
        val bundle = app.metaData
        val smallIcon =
                bundle.getInt("com.google.firebase.messaging.default_notification_icon")
        val color =
                bundle.getInt("com.google.firebase.messaging.default_notification_color")
        if(smallIcon>0) {
            mBuilder.setSmallIcon(smallIcon)
        }
        if(color>0) {
            mBuilder.color = ContextCompat.getColor(this, color)
        }
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(25884, mBuilder.build())
    }
}