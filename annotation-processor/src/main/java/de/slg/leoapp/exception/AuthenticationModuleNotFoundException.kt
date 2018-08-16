package de.slg.leoapp.exception

class AuthenticationModuleNotFoundException : RuntimeException {
    constructor(): super()
    constructor(description: String): super(description)
}