package fgori.ft_hanguots


import java.lang.IllegalArgumentException
import androidx.preference.contains
import androidx.preference.isNotEmpty


class Contact(private val id : Long) {
    var name: String = ""

    var email: String = "ciao"
        set(value) {
            if (value.isEmpty() && !value.contains('@')) {
                throw IllegalArgumentException("Formato email non valido.")
            }
            field = value
        }
    var phone: String = ""
    var address: String = ""
    var img: String = ""
}