package com.example.alarmemotivacional.ui.alarms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmemotivacional.R
import com.google.android.material.materialswitch.MaterialSwitch

class AlarmAdapter(
    private val lista: MutableList<AlarmData>,
    private val onToggle: (AlarmData) -> Unit,
    private val onRemove: (AlarmData) -> Unit
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

        holder.textHora.text = alarme.formattedTime
        holder.textLabel.text = if (alarme.isActive) {
            holder.itemView.context.getString(R.string.exemplo_label)
        } else {
            holder.itemView.context.getString(R.string.alarm_disabled)
        }

        holder.switchAtivo.setOnCheckedChangeListener(null)
        holder.switchAtivo.isChecked = alarme.isActive
        holder.switchAtivo.setOnCheckedChangeListener { _, isChecked ->
            onToggle(alarme.copy(isActive = isChecked))
        }

        holder.itemView.setOnLongClickListener {
            onRemove(alarme)
            true
        }
    }

    override fun getItemCount(): Int = lista.size
}
