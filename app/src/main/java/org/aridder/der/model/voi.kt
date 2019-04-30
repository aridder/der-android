package org.aridder.der.model

import java.util.*

data class Voi(
    val id: String,
    val short: String,
    val name: String,
    val zone: Int,
    val type: String,
    val status: String,
    val bounty: Int,
    val location: List<Double>,
    val battery: Int,
    val locked: Boolean,
    val updated: String,
    val milage: Int
    )