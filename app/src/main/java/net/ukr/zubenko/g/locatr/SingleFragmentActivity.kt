package net.ukr.zubenko.g.locatr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class SingleFragmentActivity: AppCompatActivity() {
    abstract fun createFragment(): Fragment
    protected open fun getLayoutResId() = R.layout.activity_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null)
            supportFragmentManager.
                beginTransaction().
                add(R.id.fragment_container, createFragment()).
                commit()
    }
}