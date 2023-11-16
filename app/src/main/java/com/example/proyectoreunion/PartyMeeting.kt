package com.example.proyectoreunion

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import com.example.proyectoreunion.databinding.ActivityPartyMeetingBinding
import com.makeramen.roundedimageview.RoundedImageView

class PartyMeeting : AppCompatActivity() {
    private var canciones: Array<String>? = null
    private lateinit var binding: ActivityPartyMeetingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPartyMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val key = intent.getStringExtra("key")
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
        val selectdItemSpinnerSong = mutableListOf<String>()

        val itemSpinner=intent.getStringExtra("item")
        if (itemSpinner != null) {
            selectdItemSpinnerSong.add(itemSpinner)
        }
        for (cancion in selectdItemSpinnerSong!!) {
            binding.TextSongs.append("$cancion \n")
        }
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
        binding.textKey.text=key
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
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Texto copiado", text)
        Toast.makeText(this,"Clave copiada $text",Toast.LENGTH_SHORT).show()
        clipboardManager.setPrimaryClip(clipData)
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