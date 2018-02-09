package com.mealsmadeeasy.utils

import com.google.firebase.database.*
import com.google.firebase.tasks.Task
import java.util.concurrent.Semaphore

operator fun FirebaseDatabase.get(path: String): DatabaseReference = getReference(path)

operator fun DatabaseReference.get(child: String): DatabaseReference = child(child)

inline fun <reified T> DatabaseReference.first(crossinline onNext: (T) -> Unit) {
    addValueEventListener(object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            throw error.toException()
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            onNext(snapshot.getValue(T::class.java))
            removeEventListener(this)
        }
    })
}

inline fun <reified T> DatabaseReference.firstList(crossinline onNext: (List<T>) -> Unit) {
    addValueEventListener(object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            throw error.toException()
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            onNext(snapshot.children.map { it.getValue(T::class.java) })
            removeEventListener(this)
        }
    })
}

inline fun <reified T> DatabaseReference.firstBlocking(): T? {
    val semaphore = Semaphore(0)
    var result: T? = null

    first<T?> {
        result = it
        semaphore.release()
    }

    semaphore.acquire()
    return result
}

inline fun <reified T> DatabaseReference.firstBlockingList(): List<T>? {
    val semaphore = Semaphore(0)
    var result: List<T>? = null

    firstList<T> {
        result = it
        semaphore.release()
    }

    semaphore.acquire()
    return result
}

fun <T> Task<T>.block(): T {
    val semaphore = Semaphore(0)
    addOnCompleteListener {
        semaphore.release()
    }

    semaphore.acquire()
    return result
}
