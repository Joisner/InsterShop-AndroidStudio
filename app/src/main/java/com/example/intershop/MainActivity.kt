package com.example.intershop

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonRegister = findViewById(R.id.buttonRegister)
        progressBar = findViewById(R.id.progressBar)

        buttonLogin.setOnClickListener { loginUser() }
        buttonRegister.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_product_management -> {
                startActivity(Intent(this, ProductManagementActivity::class.java))
                true
            }
            R.id.action_shopping_cart -> {
                startActivity(Intent(this, ShoppingCartActivity::class.java))
                true
            }
            R.id.action_payment -> {
                startActivity(Intent(this, MyPaymentActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (validateInput(email, password)) {
            showProgress()
            performLogin(email, password)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                editTextEmail.error = "Por favor ingrese su email"
                editTextEmail.requestFocus()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                editTextEmail.error = "Por favor ingrese un email válido"
                editTextEmail.requestFocus()
                return false
            }
            password.isEmpty() -> {
                editTextPassword.error = "Por favor ingrese su contraseña"
                editTextPassword.requestFocus()
                return false
            }
            password.length < 6 -> {
                editTextPassword.error = "La contraseña debe tener al menos 6 caracteres"
                editTextPassword.requestFocus()
                return false
            }
        }
        return true
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
        buttonLogin.isEnabled = false
        buttonRegister.isEnabled = false
    }

    private fun hideProgress() {
        progressBar.visibility = View.GONE
        buttonLogin.isEnabled = true
        buttonRegister.isEnabled = true
    }

    private fun performLogin(email: String, password: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val success = withContext(Dispatchers.IO) {
                databaseHelper.checkUser(email, password)
            }
            hideProgress()
            if (success) {
                Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, ProductListActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@MainActivity, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show()
            }
        }
    }
}