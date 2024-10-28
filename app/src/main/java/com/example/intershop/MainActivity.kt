package com.example.intershop

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button
    private lateinit var progressBar: ProgressBar
    private var databaseHelper: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar la base de datos
        databaseHelper = DatabaseHelper(this)

        // Insertar un usuario de prueba si la base de datos está vacía
        insertTestUser()

        // Inicializar las vistas
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

    private fun insertTestUser() {
        // Insertar un usuario de prueba
        val testEmail = "test@test.com"
        val testPassword = "123456"

        if (!databaseHelper!!.checkUser(testEmail, testPassword)) {
            databaseHelper!!.addUser("Usuario de prueba", testEmail, testPassword)
            Toast.makeText(this, "Usuario de prueba creado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString().trim { it <= ' ' }
        val password = editTextPassword.text.toString().trim { it <= ' ' }

        if (validateInput(email, password)) {
            showProgress()

            // Simular un pequeño retraso para mostrar el progreso
            Handler().postDelayed(
                { performLogin(email, password) }, 1000
            )
        }
    }

    private fun performLogin(email: String, password: String) {
        if (databaseHelper!!.checkUser(email, password)) {
            // Login exitoso
            Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity, ProductListActivity::class.java))
            finish()
        } else {
            // Login fallido
            Toast.makeText(this@MainActivity, "Email o contraseña incorrectos", Toast.LENGTH_LONG)
                .show()
        }
        hideProgress()
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.error = "Por favor ingrese su email"
            editTextEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Por favor ingrese un email válido"
            editTextEmail.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.error = "Por favor ingrese su contraseña"
            editTextPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            editTextPassword.error = "La contraseña debe tener al menos 6 caracteres"
            editTextPassword.requestFocus()
            return false
        }

        return true
    }
    override fun onStart() {
        super.onStart()
        databaseHelper = DatabaseHelper(this)
        databaseHelper!!.open() // Abrir base de datos
        insertTestUser()
    }

    override fun onStop() {
        super.onStop()
        databaseHelper!!.close() // Cerrar base de datos
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
}
