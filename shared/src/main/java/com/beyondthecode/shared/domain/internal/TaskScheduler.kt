package com.beyondthecode.shared.domain.internal

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val NUMBER_OF_THREADS = 4 // TODO: make this depend on device's hw

interface Scheduler{
    fun execute(task: () -> Unit)

    fun postToMainThread(task: () -> Unit)

    fun postDelayedToMainThread(delay: Long, task: () -> Unit)
}

/**
 * A shim [Scheduler] that by default handles operations in the [AsyncScheduler]. *
 * */
object DefaultScheduler: Scheduler{
    private var delegate: Scheduler = AsyncScheduler

    /**
     * Sets the new delegate scheduler, null to revert to the default async one.
     * */
    fun setDelegate(newDelegate: Scheduler?){
        delegate = newDelegate ?: AsyncScheduler
    }

    override fun execute(task: () -> Unit) {
        delegate.execute(task)
    }

    override fun postToMainThread(task: () -> Unit) {
        delegate.postToMainThread(task)
    }

    override fun postDelayedToMainThread(delay: Long, task: () -> Unit) {
        delegate.postDelayedToMainThread(delay, task)
    }
}

/**
 * Runs tasks in a [ExecutorService] with a fixed thread of pools
 * */

internal object AsyncScheduler : Scheduler{
    private val executorService: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

    override fun execute(task: () -> Unit) {
        executorService.execute(task)
    }

    override fun postToMainThread(task: () -> Unit) {
        if (isMainThread()){
            task()
        }
    }

    private fun isMainThread(): Boolean{
        return Looper.getMainLooper().thread === Thread.currentThread()
    }

    override fun postDelayedToMainThread(delay: Long, task: () -> Unit) {
        val mainThreadHandler = Handler(Looper.getMainLooper())
        mainThreadHandler.postDelayed(task, delay)
    }
}

/**
 * Runs tasks synchronously
 * */
object SyncScheduler : Scheduler{
    private val postDelayedTasks = mutableListOf<() -> Unit>()

    override fun execute(task: () -> Unit) {
        task()
    }

    override fun postToMainThread(task: () -> Unit) {
        task()
    }

    override fun postDelayedToMainThread(delay: Long, task: () -> Unit) {
        postDelayedTasks.add(task)
    }

    fun runAllScheduledPostDelayedTasks(){
        val tasks = postDelayedTasks.toList()
        clearScheduledPostDelayedTasks()
        for(task in tasks){
            task()
        }
    }

    fun clearScheduledPostDelayedTasks(){
        postDelayedTasks.clear()
    }
}