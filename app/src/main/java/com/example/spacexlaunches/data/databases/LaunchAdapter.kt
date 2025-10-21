package com.example.spacexlaunches.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexlaunches.R
import com.example.spacexlaunches.data.databases.LaunchEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LaunchAdapter : ListAdapter<LaunchEntity, LaunchAdapter.LaunchViewHolder>(DiffCallback) {

    class LaunchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.launchName)
        private val dateTextView: TextView = itemView.findViewById(R.id.launchDate)
        private val statusTextView: TextView = itemView.findViewById(R.id.launchStatus)
        private val rocketTypeTextView: TextView = itemView.findViewById(R.id.rocketType)
        private val flightNumberTextView: TextView = itemView.findViewById(R.id.flightNumber)

        fun bind(launch: LaunchEntity) {
            nameTextView.text = launch.name
            dateTextView.text = formatDate(launch.dateUtc)
            flightNumberTextView.text = "Flight #${launch.flightNumber}"
            rocketTypeTextView.text = launch.rocketType ?: "Unknown Rocket"

            when {
                launch.upcoming -> {
                    statusTextView.text = "Upcoming"
                    statusTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue))
                }
                launch.success == true -> {
                    statusTextView.text = "Success"
                    statusTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                }
                launch.success == false -> {
                    statusTextView.text = "Failed"
                    statusTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                }
                else -> {
                    statusTextView.text = "Unknown"
                    statusTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray))
                }
            }
        }

        private fun formatDate(dateUtc: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateUtc)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateUtc
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_launch, parent, false)
        return LaunchViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<LaunchEntity>() {
        override fun areItemsTheSame(oldItem: LaunchEntity, newItem: LaunchEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LaunchEntity, newItem: LaunchEntity): Boolean {
            return oldItem == newItem
        }
    }
}