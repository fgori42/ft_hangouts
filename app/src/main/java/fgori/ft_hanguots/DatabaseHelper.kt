package fgori.ft_hanguots

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 1

        // --- Tabella Contatti ---
        const val TABLE_CONTACTS = "contacts"
        const val COLUMN_ID = "_id" // Nome standard per la chiave primaria
        const val COLUMN_NAME = "name"
        const val COLUMN_SURNAME = "surname"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_IMAGE_URI = "image_uri"

        // --- Tabella Messaggi ---
        const val TABLE_MESSAGES = "messages"
        const val COLUMN_MESSAGE_ID = "_id"
        const val COLUMN_MESSAGE_CONTENT = "content"
        const val COLUMN_MESSAGE_DIRECTION = "direction"
        const val COLUMN_MESSAGE_TIMESTAMP = "timestamp"
        const val COLUMN_MESSAGE_CONTACT_ID = "contact_id" // Chiave Esterna
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        // --- 1. Creazione Tabella Contatti ---
        val CREATE_CONTACTS_TABLE = "CREATE TABLE $TABLE_CONTACTS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT NOT NULL," +
                "$COLUMN_PHONE TEXT NOT NULL," +
                "$COLUMN_SURNAME TEXT," +
                "$COLUMN_EMAIL TEXT," +
                "$COLUMN_ADDRESS TEXT," +
                "$COLUMN_IMAGE_URI TEXT)"
        db.execSQL(CREATE_CONTACTS_TABLE)
        
        // --- 2. Creazione Tabella Messaggi (CON TIMESTAMP COME NUMERO) ---
        val CREATE_MESSAGES_TABLE = "CREATE TABLE $TABLE_MESSAGES (" +
                "$COLUMN_MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_MESSAGE_CONTENT TEXT NOT NULL," +
                "$COLUMN_MESSAGE_DIRECTION INTEGER NOT NULL," +
                "$COLUMN_MESSAGE_TIMESTAMP INTEGER NOT NULL," +
                "$COLUMN_MESSAGE_CONTACT_ID INTEGER NOT NULL," +
                "FOREIGN KEY($COLUMN_MESSAGE_CONTACT_ID) REFERENCES $TABLE_CONTACTS($COLUMN_ID))"
        db.execSQL(CREATE_MESSAGES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES") // Prima i messaggi
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS") // Poi i contatti
        onCreate(db)
    }


    fun addContact(contact: Contact) {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NAME, contact.getValue("name"))
            put(COLUMN_SURNAME, contact.getValue("surname"))
            put(COLUMN_PHONE, contact.getValue("phone"))
            put(COLUMN_EMAIL, contact.getValue("email"))
            put(COLUMN_ADDRESS, contact.getValue("address"))
            put(COLUMN_IMAGE_URI, contact.getValue("img"))
        }

        db.insert(TABLE_CONTACTS, null, values)

        db.close()
    }

    fun addMessage(message: Message) {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_MESSAGE_CONTENT, message.content)
            put(COLUMN_MESSAGE_DIRECTION, message.direction.ordinal)
            put(COLUMN_MESSAGE_TIMESTAMP, message.timeStamp)
            // Usa la proprietà 'other' dall'oggetto Message per il contactId
            put(COLUMN_MESSAGE_CONTACT_ID, message.other)
        }

        db.insert(TABLE_MESSAGES, null, values)

        db.close()
    }
    
    fun getContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        // Ordiniamo i contatti per nome in ordine ascendente
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS ORDER BY $COLUMN_NAME ASC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        // Itera su ogni riga del cursore e crea un oggetto Contact
        if (cursor.moveToFirst()) {
            do {
                val contact = Contact(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                )
                contactList.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return contactList
    }

    fun getIdContact(idToFind: Long): Contact? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS WHERE $COLUMN_ID = ?"
        if (idToFind == -1L) return null
        val cursor = db.rawQuery(selectQuery, arrayOf(idToFind.toString()))
        var contact: Contact? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val surname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
            val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            contact = Contact(idToFind, name, surname, email, phone, address, imageUri)
        }
        cursor.close()
        db.close()
        return contact
    }

    fun getIdList(idToFind: Long): MutableList<Message>{
        val messageList = mutableListOf<Message>()
        val selectQuery = "SELECT * FROM $TABLE_MESSAGES WHERE $COLUMN_MESSAGE_CONTACT_ID = ?"
        val selectionArgs = arrayOf(idToFind.toString())
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, selectionArgs)

        if(cursor.moveToFirst()){
            do{
                val directionInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_DIRECTION))
                val direction = MsgDir.entries[directionInt]
                val message = Message(
                    direction,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_CONTENT)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_CONTACT_ID)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TIMESTAMP))
                   )
                messageList.add(message)
            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return messageList
    }

}