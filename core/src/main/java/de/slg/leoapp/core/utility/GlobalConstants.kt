@file:Suppress("unused")

package de.slg.leoapp.core.utility

//Permissions
const val PERMISSION_NOT_VERIFIED = 0
const val PERMISSION_STUDENT = 1
const val PERMISSION_TEACHER = 2
const val PERMISSION_ADMIN = 3

//URLs
/**
 * Domain zum Erreichen des Dev-Servers.
 */
const val DOMAIN_DEV = "http://moritz.liegmanns.de/leoapp_php"

/**
 * Basisdomain zum Erreichen des LeoApp-Servers.
 */
const val BASE_DOMAIN = "https://ucloud4schools.de"

/**
 * Basisdomain zum Erreichen des LeoApp-Userservers.
 */
const val BASE_DOMAIN_SCHOOL = "https://secureaccess.itac-school.de"

/**
 * Pfad zu den PHP-Skripts auf dem Leo-Server.
 */
const val BASE_URL_PHP = "$BASE_DOMAIN/ext/slg/leoapp_php"

/**
 * Pfad zum WebDAV-Verzeichnis
 */
const val URL_WEBDAV = "$BASE_DOMAIN_SCHOOL/slg/hcwebdav"

/**
 * Pfad zum PHP-Ordner auf dem Schulserver
 */
const val URL_PHP_SCHOOL = "$BASE_DOMAIN_SCHOOL/slgweb/leoapp_php"

/**
 * Pfad zu den PHP Skripts der Essensbestellung
 */
const val URL_LUNCH_LEO = "http://lunch.leo-ac.de/include"


class Permissions { //For java interoperability
    companion object {
        const val PERMISSION_NOT_VERIFIED = 0
        const val PERMISSION_STUDENT = 1
        const val PERMISSION_TEACHER = 2
        const val PERMISSION_ADMIN = 3
    }
}

class Network { //For java interoperability
    companion object {
        /**
         * Domain zum Erreichen des Dev-Servers.
         */
        const val DOMAIN_DEV = "http://moritz.liegmanns.de/leoapp_php"

        /**
         * Basisdomain zum Erreichen des LeoApp-Servers.
         */
        const val BASE_DOMAIN = "https://ucloud4schools.de"

        /**
         * Basisdomain zum Erreichen des LeoApp-Userservers.
         */
        const val BASE_DOMAIN_SCHOOL = "https://secureaccess.itac-school.de"

        /**
         * Pfad zu den PHP-Skripts auf dem Leo-Server.
         */
        const val BASE_URL_PHP = "$BASE_DOMAIN/ext/slg/leoapp_php"

        /**
         * Pfad zum WebDAV-Verzeichnis
         */
        const val URL_WEBDAV = "$BASE_DOMAIN_SCHOOL/slg/hcwebdav"

        /**
         * Pfad zum PHP-Ordner auf dem Schulserver
         */
        const val URL_PHP_SCHOOL = "$BASE_DOMAIN_SCHOOL/slgweb/leoapp_php"

        /**
         * Pfad zu den PHP Skripts der Essensbestellung
         */
        const val URL_LUNCH_LEO = "http://lunch.leo-ac.de/include"
    }
}