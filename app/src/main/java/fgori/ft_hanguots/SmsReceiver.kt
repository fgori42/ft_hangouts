package fgori.ft_hanguots

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast


class SmsReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            val messages: Array<SmsMessage> = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val sender = message.displayOriginatingAddress
                val body = message.messageBody

                saveSmsToDatabase(context, sender, body)
                context.sendBroadcast(Intent("REFRESH_DATA"))
            }
        }
    }
    private fun saveSmsToDatabase(context: Context, sender: String, body: String)
    {
        val dbHelper = DatabaseHelper(context)
        val contact = dbHelper.isNumberInDatabase(sender)
        if (contact != 0)
        {
            val message = Message(MsgDir.IN, body, contact, System.currentTimeMillis())
            dbHelper.addMessage(message)
        } else{
            val newContact = Contact(sender, sender)
            dbHelper.addContact(newContact)
            val newContactId = dbHelper.isNumberInDatabase(sender)
            val message = Message(MsgDir.IN, body, newContactId, System.currentTimeMillis())
            dbHelper.addMessage(message)
        }
    }
}