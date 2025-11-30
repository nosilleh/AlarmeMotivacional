package com.example.alarmemotivacional.ui.alarms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmemotivacional.R
import com.google.android.material.materialswitch.MaterialSwitch

class AlarmAdapter(
    private val lista: List<AlarmData>,
    private val onToggle: (AlarmData, Boolean) -> Unit,
    private val onEdit: (AlarmData) -> Unit,
    private val onDelete: (AlarmData) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHora: TextView = itemView.findViewById(R.id.textHora)
        val textLabel: TextView = itemView.findViewById(R.id.textLabel)
        val switchAtivo: MaterialSwitch = itemView.findViewById(R.id.switchAtivo)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarme, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = lista[position]
        val horaCompleta = String.format("%02d:%02d", alarm.hour, alarm.minute)
        holder.textHora.text = horaCompleta
        val status =
            if (alarm.active) holder.itemView.context.getString(R.string.alarm_active)
            else holder.itemView.context.getString(R.string.alarm_inactive)
        val soundText = alarm.soundUri?.let {
            holder.itemView.context.getString(R.string.sound_selected, it)
        } ?: holder.itemView.context.getString(R.string.no_sound)
        holder.textLabel.text = "$status • $soundText"

        holder.switchAtivo.setOnCheckedChangeListener(null)
        holder.switchAtivo.isChecked = alarm.active

        holder.switchAtivo.setOnCheckedChangeListener { _, isChecked ->
            onToggle(alarm, isChecked)
        }

        holder.btnEdit.setOnClickListener {
            onEdit(alarm)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(alarm)
        }
    }

    override fun getItemCount(): Int = lista.size
}
