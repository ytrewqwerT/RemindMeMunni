/* Source (adapted to Kotlin):
 * https://github.com/android/architecture-samples/blob/dev-todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/SingleLiveEvent.java
 */

package com.example.remindmemunni.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {

    private var mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) {
            if (mPending.compareAndSet(true, false)) observer.onChanged(it)
        }
    }

    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
}