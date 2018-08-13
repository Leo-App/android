package de.leoappslg.processor

class AuthenticationModuleNotFoundException : RuntimeException {
    constructor(): super()
    constructor(desc: String): super(desc)
}