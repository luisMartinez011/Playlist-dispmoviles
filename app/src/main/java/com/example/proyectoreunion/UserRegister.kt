package com.example.proyectoreunion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.proyectoreunion.databinding.ActivityUserRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRegister : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityUserRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        binding.btnAction.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val confirmpassword = binding.confirmPass.text.toString().trim()
            val name = binding.name.text.toString().trim()
            val lastName = binding.lastName.text.toString().trim()
            if (password.equals(confirmpassword)) {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                Toast.makeText(
                                    this,
                                    "Registro exitoso. UID: ${user?.uid}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                savedataUserFirestore(name, lastName, email, password)

                                val goToHome = Intent(this, WelcomeHome::class.java)
                                startActivity(goToHome)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error al registrar. ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.confirmPass.error = "Contraseñas no iguales"

            }
        }
    }

    private fun savedataUserFirestore(
        name: String,
        lastName: String,
        email: String,
        password: String
    ) {
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val usuario = auth.currentUser

        if (usuario != null) {
            val uid = usuario.uid
            val documentRef = db.collection("Users").document(uid)

            val datos = hashMapOf(
                "name" to name,
                "lastName" to lastName,
                "email" to email,
                "password" to password,
            )

            documentRef.set(datos)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Datos subidos exitosamente a Firestore.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al subir datos a Firestore..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

}