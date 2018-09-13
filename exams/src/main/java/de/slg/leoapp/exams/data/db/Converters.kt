package de.slg.leoapp.exams.data.db

import androidx.room.TypeConverter
import de.slg.leoapp.exams.R
import java.util.*

/**
 * @author Moritz
 * Erstelldatum: 07.09.2018
 */
class Converters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    @TypeConverter
    fun toSubject(s: String): Subject {
        return Subject(
                when (s.toUpperCase().replace(Regex("\\d"), "")) {
                    "M" -> "Mathe"
                    "D" -> "Deutsch"
                    "E" -> "Englisch"
                    "F" -> "Französisch"
                    "BI", "B" -> "Biologie"
                    "CH" -> "Chemie"
                    "PH" -> "Physik"
                    "IF" -> "Informatik"
                    "PK", "SW" -> "Politik"
                    "KR" -> "Katholische Religion"
                    "ER" -> "Evangelische Religion"
                    "P" -> "Philosophie"
                    "S" -> "Spanisch"
                    "SP" -> "Sport"
                    "N" -> "Niederländisch"
                    "L" -> "Latein"
                    "EK" -> "Erdkunde"
                    "GE" -> "Geschichte"
                    "GEF" -> "Geschichte bilingual"
                    "PA" -> "Pädagogik"
                    else -> s
                },
                when (s.toUpperCase().replace(Regex("\\d"), "")) {
                    "M" -> R.color.colorMathe
                    "D" -> R.color.colorDeutsch
                    "E" -> R.color.colorEnglisch
                    "F" -> R.color.colorFranze
                    "BI" -> R.color.colorBiologie
                    "CH" -> R.color.colorChemie
                    "PH" -> R.color.colorPhysik
                    "IF" -> R.color.colorInformatik
                    "PK", "SW" -> R.color.colorPolitik
                    "KR", "ER", "P" -> R.color.colorReligion
                    "S" -> R.color.colorSpanisch
                    "SP" -> R.color.colorSport
                    "N" -> R.color.colorNiederlaendisch
                    "L" -> R.color.colorLatein
                    "EK" -> R.color.colorErdkunde
                    "GE", "GEF" -> R.color.colorGeschichte
                    "PA" -> R.color.colorPaedagogik
                    else -> R.color.colorOther
                }
        )
    }

    @TypeConverter
    fun toString(s: Subject): String {
        return when (s.name) {
            "Mathe" -> "M"
            "Deutsch" -> "D"
            "Englisch" -> "E"
            "Französisch" -> "F"
            "Biologie" -> "BI"
            "Chemie" -> "CH"
            "Physik" -> "PH"
            "Informatik" -> "IF"
            "Politik" -> "PK"
            "Katholische Religion" -> "KR"
            "Evangelische Religion" -> "ER"
            "Philosophie" -> "P"
            "Spanisch" -> "S"
            "Sport" -> "SP"
            "Niederländisch" -> "N"
            "Latein" -> "L"
            "Erdkunde" -> "EK"
            "Geschichte" -> "GE"
            "Geschichte bilingual" -> "GEF"
            "Pädagogik" -> "PA"
            else -> s.name
        }
    }
}