package com.example.proyectoreunion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoreunion.databinding.ActivityWelcomeHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import kotlin.random.Random

class WelcomeHome : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private val listaCanciones = mutableListOf<String>()
    private lateinit var binding: ActivityWelcomeHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.joinTheParty.setOnClickListener {
            binding.relativeParent.visibility = View.GONE
            binding.relativeGoToParty.visibility = View.VISIBLE
        }
        binding.createParty.setOnClickListener {
            binding.relativeParent.visibility = View.GONE
            binding.relativeSounds.visibility = View.VISIBLE

        }
        binding.btnAddSongs.setOnClickListener {
            val songs = binding.song1.text.toString().trim()
            if (songs.isNotEmpty()) {
                listaCanciones.add(songs)
                Toast.makeText(this,"Cancion agregada",Toast.LENGTH_SHORT).show()
                binding.song1.text!!.clear()
            } else {
                binding.song1.error = "Campo obligatorio"
            }
            val generateKey = generateKey()
            binding.btnAction.setOnClickListener {
                savedataUserFirestore(listaCanciones, generateKey)
            }

        }
        enterMeetingWithKey()
    }

    private fun generateKey(): String {
        val letrasMayusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numero1 = Random.nextInt(0, 10)
        val numero2 = Random.nextInt(0, 10)
        val numero3 = Random.nextInt(0, 10)
        val numero4 = Random.nextInt(0, 10)
        val numero5 = Random.nextInt(0, 10)
        val letraMayuscula = letrasMayusculas[Random.nextInt(letrasMayusculas.length)]
        return "$numero1$numero5$letraMayuscula$numero2$numero4$numero3"
    }

    private fun enterMeetingWithKey() {
        db = FirebaseFirestore.getInstance()
        binding.goToMeetParty.setOnClickListener {
            val textKeyMeet = binding.goToPartyKey.text.toString().trim()
            if (textKeyMeet.isNotEmpty()) {
                db.collection("partyMeeting").whereEqualTo("key", textKeyMeet).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val querySnapshot: QuerySnapshot = task.result
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                val key = querySnapshot.documents[0].getString("key")
                                val canciones = mutableListOf<String>()
                                for (i in 1..40) {
                                    val cancion = querySnapshot.documents[0].getString("cancion$i")
                                    if (cancion != null) {
                                        canciones.add(cancion)
                                    } else {
                                        break
                                    }
                                }

                                for (cancion in canciones) {
                                    println("Canción: $cancion")
                                }

                                val intent = Intent(this@WelcomeHome, PartyMeeting::class.java)
                                intent.putExtra("key", key)
                                intent.putExtra("songs", canciones.toTypedArray())
                                startActivity(intent)

                                Toast.makeText(
                                    this@WelcomeHome, "Reunion encontrada", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                showAlert()
                                Toast.makeText(
                                    this@WelcomeHome,
                                    "key de reunion no encontrado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@WelcomeHome, "Error al consultar Firestore", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this@WelcomeHome, "Ingrese el codigo de la reunion", Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No existe reunion con esa clave!!!")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun savedataUserFirestore(
        canciones: List<String>,
        key: String
    ) {
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val usuario = auth.currentUser
        val cancionesMutable = mutableListOf<String>()
        if (usuario != null) {
            val uid = usuario.uid
            val documentRef = db.collection("partyMeeting").document(uid)


            val datosUsuario = mutableMapOf<String, Any>()
            datosUsuario["key"] = key

            for ((index, cancion) in canciones.withIndex()) {
                datosUsuario["cancion${index + 1}"] = cancion
                cancionesMutable.add(cancion)

            }

            documentRef.set(datosUsuario, SetOptions.merge()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this, "Datos subidos exitosamente a Firestore.", Toast.LENGTH_SHORT
                    ).show()
                    val goToPartyMeeting = Intent(this, PartyMeeting::class.java)
                    goToPartyMeeting.putExtra("key", key)
                    goToPartyMeeting.putExtra("songs", canciones.toTypedArray())
                    startActivity(goToPartyMeeting)
                } else {
                    Toast.makeText(
                        this, "Error al subir datos a Firestore..", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

}