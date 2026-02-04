package fgori.ft_hanguots

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast


class SmsReceiver: BroadcastReceiver() {

    private val prefixList : List<String> = listOf(
        "+39",  // Italia
        "+44",  // Regno Unito
        "+33",  // Francia
        "+49",  // Germania
        "+34",  // Spagna
        "+41",  // Svizzera
        "+43",  // Austria
        "+31",  // Paesi Bassi
        "+32",  // Belgio
        "+351", // Portogallo
        "+46",  // Svezia
        "+47",  // Norvegia
        "+45",  // Danimarca
        "+358", // Finlandia
        "+30",  // Grecia
        "+353", // Irlanda
        "+48",  // Polonia
        "+40",  // Romania
        "+7",   // Russia

        // Americhe
        "+1",   // USA & Canada
        "+52",  // Messico
        "+54",  // Argentina
        "+55",  // Brasile
        "+56",  // Cile
        "+57",  // Colombia
        "+51",  // Perù

        // Asia
        "+81",  // Giappone
        "+82",  // Corea del Sud
        "+86",  // Cina
        "+91",  // India

        // Oceania
        "+61",  // Australia
        "+64",  // Nuova Zelanda

        // Africa
        "+20",  // Egitto
        "+27",  // Sudafrica
        "+212", // Marocco
        "+234"  // Nigeri
    )
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            try {
                val messages: Array<SmsMessage> =
                    Telephony.Sms.Intents.getMessagesFromIntent(intent)
                var mess = StringBuilder()
                var sender = filterNumber(messages[0].displayOriginatingAddress)
                for (message in messages) {
                    mess.append(message.messageBody)
                }
                    saveSmsToDatabase(context, sender, mess.toString())
                    val Updatepackage = Intent("com.fgori.ft_hanguots.UPDATE_CHAT")
                    Updatepackage.setPackage(context.packageName)
                    context.sendBroadcast(Updatepackage)

            } catch (e: Exception)
            {
                println("Errore in SmsReceiver: ${e.message}")
            }
        }
    }

    private fun filterNumber(number: String): String {
        if (number.startsWith("+")) {
            for (prefix in prefixList) {
                if (number.startsWith(prefix)) {
                    return number.substring(prefix.length)

                }
            }
        }
        return number
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