package de.slg.leoapp.exams

import de.slg.leoapp.exams.data.db.Subject
import java.util.*

/**
 * @author Moritz
 * Erstelldatum: 03.09.2018
 */
class Klausur(
        val id: Int?,
        val subject: Subject,
        val datum: Date
)