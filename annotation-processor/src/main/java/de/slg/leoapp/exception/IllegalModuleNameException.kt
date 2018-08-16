package de.slg.leoapp.exception

class IllegalModuleNameException : RuntimeException {
    constructor(): super()
    constructor(desc: String): super(desc)
}