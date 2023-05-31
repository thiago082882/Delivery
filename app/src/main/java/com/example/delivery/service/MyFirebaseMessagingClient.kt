package com.example.delivery.service

import com.example.delivery.channel.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingClient: FirebaseMessagingService() {


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val title = data["title"]
        val body = data["body"]
        val idNotification = data["id_notification"]

        if (!title.isNullOrBlank() && !body.isNullOrBlank() && !idNotification.isNullOrBlank()) {
            showNotification(title, body, idNotification)
        }
    }


    private fun showNotification(title: String, body: String, idNotification: String) {
        val helper = NotificationHelper(baseContext)
        val builder = helper.getNotification(title, body)
        val id = idNotification.toInt()
        helper.getManager().notify(id, builder.build())
    }

}