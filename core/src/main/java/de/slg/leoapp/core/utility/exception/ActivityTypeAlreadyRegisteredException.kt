package de.slg.leoapp.core.utility.exception

class ActivityTypeAlreadyRegisteredException : RuntimeException {
    constructor() : super()
    constructor(desc: String) : super(desc)
}