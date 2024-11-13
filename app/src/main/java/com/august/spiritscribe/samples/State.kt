package com.august.spiritscribe.samples

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshots.Snapshot

fun main() {
    var z = mutableIntStateOf(10)
    val a = Snapshot.takeMutableSnapshot()
    a.enter {
        z.intValue = 30
        println(z)
    }
    println(z)
    val b = Snapshot.takeSnapshot()
    b.enter {
        assert(z.intValue == 10)
    }
}