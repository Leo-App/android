package de.leoappslg.exception

class IllegalModuleNameException : RuntimeException {
    constructor(): super()
    constructor(desc: String): super(desc)
}