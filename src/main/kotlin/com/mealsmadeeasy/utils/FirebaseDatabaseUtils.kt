package com.mealsmadeeasy.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.tasks.Task
import java.util.concurrent.Semaphore

inline fun <reified T> DatabaseReference.subscribe(noinline onNext: (List<T>) -> Unit) {
    addValueEventListener(TableListener.of(onNext))
}

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

fun <T> Task<T>.block(): T {
    val semaphore = Semaphore(0)
    addOnCompleteListener {
        semaphore.release()
    }

    semaphore.acquire()
    return result
}

class TableListener<T>(
        private val clazz: Class<T>,
        private val onNext: (List<T>) -> Unit
) : ValueEventListener {

    companion object {
        inline fun <reified T> of(noinline onNext: (List<T>) -> Unit): TableListener<T> {
            return TableListener(T::class.java, onNext)
        }
    }

    override fun onCancelled(error: DatabaseError) {
        throw error.toException()
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        onNext(snapshot.children.map { it.getValue(clazz) })
    }

}