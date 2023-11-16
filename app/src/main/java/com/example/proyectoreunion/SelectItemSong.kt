package com.example.proyectoreunion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.proyectoreunion.databinding.ActivitySelectItemSongBinding

class SelectItemSong : AppCompatActivity() {
    private lateinit var binding:ActivitySelectItemSongBinding
    private var isFirstSelection = true
    private lateinit var key:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySelectItemSongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        key= intent.getStringExtra("key").toString()
        if (intent.hasExtra("songs")) {
            val canciones = intent.getStringArrayExtra("songs")


            if (canciones != null) {
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, canciones)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = adapter
                binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                        if (!isFirstSelection) {
                            val itemSeleccionado = parentView.getItemAtPosition(position) as String
                            Toast.makeText(applicationContext, "Item seleccionado: $itemSeleccionado", Toast.LENGTH_SHORT).show()
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

}