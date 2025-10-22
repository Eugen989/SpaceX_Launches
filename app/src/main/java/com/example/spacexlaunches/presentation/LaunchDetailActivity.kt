package com.example.spacexlaunches.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.example.spacexlaunches.R
import com.example.spacexlaunches.data.models.LaunchEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LaunchDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LAUNCH = "extra_launch"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_detail)

        val launch = intent.getSerializableExtra(EXTRA_LAUNCH) as? LaunchEntity

        if (launch != null) {
            setupViews(launch)
            setupClickListeners(launch)
        } else {
            finish()
        }
    }

    private fun setupViews(launch: LaunchEntity) {
        findViewById<TextView>(R.id.detailTitle).text = launch.name

        findViewById<ImageView>(R.id.detailRocketImage).load(launch.rocketImages) {
            placeholder(R.drawable.rocket_placeholder)
            error(R.drawable.rocket_placeholder)
            crossfade(true)
        }

        findViewById<TextView>(R.id.detailFlightNumber).text = "Flight #${launch.flightNumber}"
        findViewById<TextView>(R.id.detailDate).text = formatDate(launch.dateUtc)
        findViewById<TextView>(R.id.detailRocketType).text = launch.rocketType ?: "Unknown Rocket"

        val statusTextView = findViewById<TextView>(R.id.detailStatus)
        when {
            launch.upcoming -> {
                statusTextView.text = "Upcoming"
                statusTextView.setTextColor(ContextCompat.getColor(this, R.color.blue))
            }
            launch.success == true -> {
                statusTextView.text = "Success"
                statusTextView.setTextColor(ContextCompat.getColor(this, R.color.green))
            }
            launch.success == false -> {
                statusTextView.text = "Failed"
                statusTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
            else -> {
                statusTextView.text = "Unknown"
                statusTextView.setTextColor(ContextCompat.getColor(this, R.color.gray))
            }
        }

        findViewById<TextView>(R.id.detailRocketName).text = launch.rocketName ?: "Unknown Rocket"
        findViewById<TextView>(R.id.detailRocketCompany).text = launch.rocketCompany ?: "Unknown Company"
        findViewById<TextView>(R.id.detailRocketCountry).text = launch.rocketCountry ?: "Unknown Country"

        val detailsTextView = findViewById<TextView>(R.id.detailDescription)
        detailsTextView.text = launch.details ?: "No details available"

        findViewById<TextView>(R.id.detailRocketDescription).text =
            launch.rocketDescription ?: "No description available"

        val stagesText = if (launch.rocketStages != null) "${launch.rocketStages} stages" else "Unknown stages"
        findViewById<TextView>(R.id.detailRocketStages).text = stagesText

        val costText = if (launch.rocketCostPerLaunch != null) "$${launch.rocketCostPerLaunch}" else "Unknown cost"
        findViewById<TextView>(R.id.detailRocketCost).text = costText

        val successRateText = if (launch.rocketSuccessRate != null) "${launch.rocketSuccessRate}%" else "Unknown success rate"
        findViewById<TextView>(R.id.detailRocketSuccessRate).text = successRateText

        val activeStatus = if (launch.rocketActive == true) "Active" else "Retired"
        findViewById<TextView>(R.id.detailRocketActive).text = activeStatus

        val wikipediaButton = findViewById<Button>(R.id.detailWikipediaButton)
        if (launch.rocketWikipedia != null) {
            wikipediaButton.visibility = android.view.View.VISIBLE
        } else {
            wikipediaButton.visibility = android.view.View.GONE
        }
    }

    private fun setupClickListeners(launch: LaunchEntity) {
        findViewById<Button>(R.id.detailWikipediaButton).setOnClickListener {
            launch.rocketWikipedia?.let { wikipediaUrl ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
                startActivity(intent)
            }
        }

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.detailToolbar).setNavigationOnClickListener {
            finish()
        }
    }

    private fun formatDate(dateUtc: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateUtc)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateUtc
        }
    }
}