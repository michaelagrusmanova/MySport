package com.example.mysport

import java.util.*

/**
 * Modelová třída pro ukládání do databáze.
 * Symbolizuje jednu aktivitu.
 */
data class ActivityModel(
    var id: Int = getAutoId(),
    var name: String,
    var distance: String,
    var duration: String,
    var notes: String,
    var image: ByteArray?
) {
    companion object {
        /**
         * Vygenerování id pro novou aktivitu.
         */
        fun getAutoId(): Int {
            val random = Random()
            return random.nextInt(1000)
        }
    }
}