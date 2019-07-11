package cenergy.central.com.pwb_store.extensions

fun Int.isOdd():Boolean {
    return this % 2 != 0
}

fun Int.isEvent():Boolean {
    return this % 2 == 0
}