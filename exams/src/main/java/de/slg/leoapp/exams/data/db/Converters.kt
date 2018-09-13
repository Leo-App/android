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
                when (s.toUpperCase().replace(Regex("\\d"), "")) { //TODO STRINGS XML !!!
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
                    "M" -> R.color.colorMath
                    "D" -> R.color.colorGerman
                    "E" -> R.color.colorEnglish
                    "F" -> R.color.colorFrench
                    "BI" -> R.color.colorBiology
                    "CH" -> R.color.colorChemistry
                    "PH" -> R.color.colorPhysics
                    "IF" -> R.color.colorCS
                    "PK", "SW" -> R.color.colorPolitics
                    "KR", "ER", "P" -> R.color.colorReligion
                    "S" -> R.color.colorSpanish
                    "SP" -> R.color.colorSport
                    "N" -> R.color.colorDutch
                    "L" -> R.color.colorLatin
                    "EK" -> R.color.colorGeography
                    "GE", "GEF" -> R.color.colorHistory
                    "PA" -> R.color.colorEducation
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