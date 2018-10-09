package de.slg.leoapp.timetable.data.db

import androidx.room.TypeConverter
import de.slg.leoapp.timetable.R
import de.slg.leoapp.timetable.data.Subject

class Converters {
    @TypeConverter
    fun toSubject(s: String): Subject = Subject(s.toUpperCase().replace(Regex("\\d"), ""),
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

    @TypeConverter
    fun fromSubject(s: Subject): String = s.name
}
