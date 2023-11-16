package com.example.proyectoreunion

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import com.example.proyectoreunion.databinding.ActivityPartyMeetingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.makeramen.roundedimageview.RoundedImageView

class PartyMeeting : AppCompatActivity() {
    private var canciones: Array<String>? = null
    private lateinit var binding: ActivityPartyMeetingBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var cancionesReproduccion: List<String>
    var  key: String? = null
    private lateinit var  idUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPartyMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        key = intent.getStringExtra("key")
        Toast.makeText(this, " $key", Toast.LENGTH_SHORT).show()
        /*  // Inicializar Firebase
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // Obtener el UID del usuario actual
        val currentUserUid = auth.currentUser?.uid

        // Verificar si el UID es null (usuario no autenticado)
        if (currentUserUid != null) {
            // Construir la referencia al documento en la colección "Users" con el UID del usuario
            val userDocumentRef = db.collection("Users").document(currentUserUid)

            // Obtener datos del documento
            userDocumentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // El documento existe, puedes acceder a los campos
                        val userName = documentSnapshot.getString("name")
                        // Hacer algo con el nombre (por ejemplo, mostrarlo en un TextView)
                    } else {
                        // El documento no existe
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar errores al obtener datos
                }
        } else {
            // El usuario no está autenticado
        }
    }*/

        val intent = intent
//        val selectdItemSpinnerSong = mutableListOf<String>()

        //agregar
//        val itemSpinner=intent.getStringExtra("item")
//        if (itemSpinner != null) {
//            selectdItemSpinnerSong.add(itemSpinner)
//        }
//        for (cancion in cancionesReproduccion!!) {
//            binding.TextSongs.append("$cancion \n")
//        }
        if (intent.hasExtra("songs")) {
            canciones = intent.getStringArrayExtra("songs")
            if (canciones != null) {
                /*for (cancion in canciones!!) {
                    binding.TextSongs.append("$cancion \n")
                }*/
            }
        }
        val albumArt = findViewById<RoundedImageView>(R.id.albumArt)

        val rotationAnimator = ObjectAnimator.ofFloat(albumArt, "rotation", 0f, 360f)
        rotationAnimator.duration = 5000
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.interpolator = LinearInterpolator()

        rotationAnimator.start()
        //binding.TextSongs.text = "$song1 \n $song3 \n $song4 \n $song5 \n $song6 \n $song7 \n $key"
        binding.textKey.text="Codigo: " + key

        getListadoCanciones()
        binding.textKey.setOnClickListener {
            val keyName=binding.textKey.text.toString()
            copyToClipboard(keyName)
        }
        binding.btnAddSong.setOnClickListener {
            val intent = Intent(this, SelectItemSong::class.java)
            intent.putExtra("key", key)
            intent.putExtra("songs", canciones)
            startActivity(intent)
        }

        binding.textKey.setOnClickListener {
            val keyName=binding.textKey.text.toString()
            copyToClipboard(keyName)
        }

        binding.albumArt.setOnClickListener{

            val nuevaLista = cancionesReproduccion.subList(1, cancionesReproduccion.size)

           setListadoCanciones(nuevaLista)
            getListadoCanciones()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Texto copiado", text)
        Toast.makeText(this,"Clave copiada $text",Toast.LENGTH_SHORT).show()
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun getListadoCanciones(){
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val usuario = auth.currentUser

        if (usuario != null) {
            val uid = usuario.uid
            val documentRef = db.collection("partyMeeting").whereEqualTo("key", key);

            // Realiza una consulta para obtener los datos
            documentRef.addSnapshotListener   { snapshots, e  ->
                Log.w(TAG,"firebaseSnapShot Se ejecuto el get ");

                if (snapshots != null && !snapshots.isEmpty()) {
                    val arrayDatos = snapshots.documents[0].get("songOrderList") as List<String>
                    val obtenerIdUsuario = snapshots.documents[0].id
                    idUsuario = obtenerIdUsuario
                    cancionesReproduccion = arrayDatos

                    if(arrayDatos.size == 0 ){
                        binding.TextSongs.text = ""
                    }
                    binding.TextSongs.text = ""
                    for (cancion in arrayDatos!!) {

                        binding.TextSongs.append("$cancion \n")
                        print(cancion)
                    }

                    Log.w(TAG,"Se ejecuto el get");
                }

//                if (e != null) {
//                    Log.w(TAG, "firebaseSnapShot Error en el get ", e)
//                    return@addSnapshotListener
//                }
//
//                if (value  != null ) {
//                    val arrayDatos = value.get("songOrderList") as List<String>;
//                    cancionesReproduccion = arrayDatos
//
//                    if(arrayDatos.size == 0 ){
//                        binding.TextSongs.text = ""
//                    }
//                    binding.TextSongs.text = ""
//                    for (cancion in arrayDatos!!) {
//
//                        binding.TextSongs.append("$cancion \n")
//                        print(cancion)
//                    }
//
//                    Log.w(TAG,"Se ejecuto el get");
//
//                }
            }
        }
    }



    private fun setListadoCanciones(nuevaLista: List<String>) {
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val usuario = auth.currentUser

        if (usuario != null) {
            val uid = usuario.uid
            val documentRef = db.collection("partyMeeting").document(idUsuario);
            documentRef.set(mapOf("songOrderList" to nuevaLista), SetOptions.merge())
                .addOnSuccessListener {
                    // Operación exitosa

                    Log.w(TAG,"Datos modificados y seteados en Firestore")
                }
                .addOnFailureListener {
                    // Manejar errores
                    Log.w(TAG,"Error al modificar y setear datos en Firestore: ${it.message}")
                }
        }
    }
    /*fun checkBoxSongs(){
        // Obtén la referencia al contenedor de CheckBox en tu diseño
        val checkBoxContainer = findViewById<LinearLayout>(R.id.checkBoxContainer)

        // Tu array de canciones
        val canciones = arrayOf("Canción 1", "Canción 2", "Canción 3", "Canción 4")

        // Crea dinámicamente CheckBox para cada canción en el array
        for ((index, cancion) in canciones.withIndex()) {
            val checkBox = CheckBox(this)
            checkBox.text = cancion
            checkBoxContainer.addView(checkBox)

            // Configura el comportamiento del CheckBox
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // La canción está seleccionada
                    Toast.makeText(this, "$cancion seleccionada", Toast.LENGTH_SHORT).show()
                } else {
                    // La canción no está seleccionada
                    Toast.makeText(this, "$cancion deseleccionada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/
}