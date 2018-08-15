package de.leoappslg.exception

class AuthenticationModuleNotFoundException : RuntimeException {
    constructor(): super()
    constructor(description: String): super(description)
}