package com.example.alarmemotivacional.ui.alarms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmemotivacional.R
import com.google.android.material.materialswitch.MaterialSwitch

class AlarmAdapter(
    private val lista: List<AlarmStorage.AlarmData>,
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHora: TextView = itemView.findViewById(R.id.textHora)
        val textLabel: TextView = itemView.findViewById(R.id.textLabel)
        val switchAtivo: MaterialSwitch = itemView.findViewById(R.id.switchAtivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarme, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarme = lista[position]
        holder.textHora.text = alarme.hora // Ex: “07:30”
        holder.textLabel.text = if (alarme.ativo) "Ativo" else "Inativo"

        holder.switchAtivo.apply {
            isChecked = alarme.ativo
            isEnabled = false
        }
    }

    override fun getItemCount(): Int = lista.size
}
