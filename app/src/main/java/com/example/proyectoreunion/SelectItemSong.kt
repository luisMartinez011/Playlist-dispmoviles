package com.example.proyectoreunion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.proyectoreunion.databinding.ActivitySelectItemSongBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SelectItemSong : AppCompatActivity() {
    private lateinit var binding:ActivitySelectItemSongBinding
    private var isFirstSelection = true
    private lateinit var key:String
    private lateinit var db: FirebaseFirestore

    private lateinit var cancionesReproduccion: List<String>
    private lateinit var  idUsuario: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySelectItemSongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        key= intent.getStringExtra("key").toString()

        if (intent.hasExtra("songs")) {
            val canciones = intent.getStringArrayExtra("songs")

            val arrayList = ArrayList<String>()
            arrayList.add("Selecciona una opción")
            for (cancion in canciones!!) {

                arrayList.add(cancion)
            }

//            val nuevaLista = arrayList + canciones

            if (canciones != null) {
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = adapter
                binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {

                        if (!isFirstSelection) {
                            val itemSeleccionado = parentView.getItemAtPosition(position) as String
                            Toast.makeText(applicationContext, "Cancion seleccionada: $itemSeleccionado", Toast.LENGTH_SHORT).show()
                            setearArrayEnFirestore(itemSeleccionado);
                            val intent = Intent(applicationContext, PartyMeeting::class.java)
                            intent.putExtra("item", itemSeleccionado)
                            intent.putExtra("songs",canciones)
                            intent.putExtra("key",key)
                            startActivity(intent)
                        } else {
                            isFirstSelection = false
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>) {
                        // Manejar el caso en que no se selecciona nada
                    }
                }
            }
        }
    }


    fun setearArrayEnFirestore(cancionAgregada: String) {
        // Obtén una instancia de Firestore
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val usuario = auth.currentUser

        if (usuario != null) {
            val uid = usuario.uid

            obtenerArrayDesdeFirebase { datosExistente ->
                if (datosExistente != null) {

                    val listadoCanciones = datosExistente.plusElement(cancionAgregada);

                    val documentRef = db.collection("partyMeeting").document(idUsuario);
                    // Actualiza el documento en Firestore con los nuevos datos
                    documentRef.set(mapOf("songOrderList" to listadoCanciones),SetOptions.merge())
                        .addOnSuccessListener {
                            // Operación exitosa
                            println("Datos modificados y seteados en Firestore")
                        }
                        .addOnFailureListener {
                            // Manejar errores
                            println("Error al modificar y setear datos en Firestore: ${it.message}")
                        }
                    print(listadoCanciones);
                } else {
                    // No hay datos existentes para modificar
                    println("No hay datos existentes para modificar en Firebase")
                }
            }
        }

        // Realiza la operación de modificación y seteo en Firestore

    }


    fun obtenerArrayDesdeFirebase(callback: (List<String>?) -> Unit) {
        // Obtén una referencia a la instancia de Firebase Realtime Database
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val usuario = auth.currentUser

        if (usuario != null) {
            val uid = usuario.uid
            val documentRef = db.collection("partyMeeting").whereEqualTo("key", key);

            // Realiza una consulta para obtener los datos
            documentRef.get().addOnSuccessListener { document  ->
                if (document  != null ) {
                    idUsuario = document.documents[0].id
                    val arrayDatos = document.documents[0].get("songOrderList") as List<String>;
                    cancionesReproduccion = arrayDatos;
                    print(arrayDatos);

                    callback(arrayDatos)
                }
            }
        }

    }


}