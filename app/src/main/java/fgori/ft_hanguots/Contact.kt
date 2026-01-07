package fgori.ft_hanguots


import java.lang.IllegalArgumentException



class Contact(public val id : Long) {
    private var name: String = ""
    private var surname: String = ""
    private var email: String = "" 
    private var phone: String = ""
    private var address: String = ""
    private var img: String = ""
    var messages: MutableMap<Long, Message> = mutableMapOf()

    
    constructor(id: Long, name: String, surname: String ,email: String, phone: String, address: String, img: String) : this(id)
    {
        this.name = name
        this.surname = surname
        this.email = email
        this.phone = phone
        this.address = address
        this.img = img
    }
    
    fun getValue(value: String): String
    {
        return when(value)
        {
            "id" -> id.toString()
            "name" -> name
            "surname" -> surname
            "email" -> email
            "phone" -> phone
            "address" -> address
            "img" -> img
            else -> "Error"   
        }
    }
    
    fun setValue(value: String, newValue: String)
    {
        if (newValue.trim().isEmpty() || value.trim().isEmpty())
            throw IllegalArgumentException("Value cannot be empty")
        if (value != "name" && value != "surname" && value != "email" && value != "phone" && value != "address" && value != "img")
            throw IllegalArgumentException("Value cannot be empty")
        if (value == "email" && !newValue.contains("@"))
            throw IllegalArgumentException("Email is not valid")
        when(value)
        {
             "name" -> name = newValue
             "surname" -> surname = newValue
             "email" -> email = newValue
             "phone" -> phone = newValue
             "address" -> address = newValue
             "img" -> img = newValue
        }
    }
    
    fun addMessage(message: Message, contact: Contact)
    {

    }
}