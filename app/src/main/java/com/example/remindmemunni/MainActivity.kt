package com.example.remindmemunni

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.remindmemunni.utils.InjectorUtils
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels {
        InjectorUtils.provideMainActivityViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navMenu = navView.menu
        navMenu.add(MainActivityViewModel.CATEGORY_ALL).also {
            it.isCheckable = true
            navView.setCheckedItem(it)
        }
        navMenu.add(MainActivityViewModel.CATEGORY_NONE).isCheckable = true

        val categoryMenu = navMenu.addSubMenu("Categories")
        viewModel.categories.observe(this) { categories ->
            categoryMenu.clear()
            for (category in categories) categoryMenu.add(category).isCheckable = true
        }

        navView.setNavigationItemSelectedListener {
            val success = viewModel.setCategoryFilter("${it.title}")
            if (success) drawerLayout.close()
            success
        }
    }
}