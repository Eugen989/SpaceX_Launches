package com.example.spacexlaunches.domain.usecase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.spacexlaunches.R;
import com.example.spacexlaunches.data.models.LaunchEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LaunchDetailActivity extends AppCompatActivity {

    public static final String EXTRA_LAUNCH = "extra_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_detail);

        LaunchEntity launch = (LaunchEntity) getIntent().getSerializableExtra(EXTRA_LAUNCH);

        if (launch != null) {
            setupViews(launch);
            setupClickListeners(launch);
        } else {
            finish();
        }
    }

    private void setupViews(LaunchEntity launch) {
        TextView detailTitle = findViewById(R.id.detailTitle);
        detailTitle.setText(launch.getName());

        ImageView detailRocketImage = findViewById(R.id.detailRocketImage);
        if (launch.getRocketImages() != null && !launch.getRocketImages().isEmpty()) {
            coil.Coil.imageLoader(this).enqueue(
                    new coil.request.ImageRequest.Builder(this)
                            .data(launch.getRocketImages())
                            .target(detailRocketImage)
                            .placeholder(R.drawable.rocket_placeholder)
                            .error(R.drawable.rocket_placeholder)
                            .build()
            );
        } else {
            detailRocketImage.setImageResource(R.drawable.rocket_placeholder);
        }

        TextView detailFlightNumber = findViewById(R.id.detailFlightNumber);
        detailFlightNumber.setText("Flight #" + launch.getFlightNumber());

        TextView detailDate = findViewById(R.id.detailDate);
        detailDate.setText(formatDate(launch.getDateUtc()));

        TextView detailRocketType = findViewById(R.id.detailRocketType);
        detailRocketType.setText(launch.getRocketType() != null ? launch.getRocketType() : "Unknown Rocket");

        TextView detailStatus = findViewById(R.id.detailStatus);
        if (launch.getUpcoming()) {
            detailStatus.setText("Upcoming");
            detailStatus.setTextColor(ContextCompat.getColor(this, R.color.blue));
        } else if (launch.getSuccess() != null) {
            if (launch.getSuccess()) {
                detailStatus.setText("Success");
                detailStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
            } else {
                detailStatus.setText("Failed");
                detailStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
            }
        } else {
            detailStatus.setText("Unknown");
            detailStatus.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }

        TextView detailRocketName = findViewById(R.id.detailRocketName);
        detailRocketName.setText(launch.getRocketName() != null ? launch.getRocketName() : "Unknown Rocket");

        TextView detailRocketCompany = findViewById(R.id.detailRocketCompany);
        detailRocketCompany.setText(launch.getRocketCompany() != null ? launch.getRocketCompany() : "Unknown Company");

        TextView detailRocketCountry = findViewById(R.id.detailRocketCountry);
        detailRocketCountry.setText(launch.getRocketCountry() != null ? launch.getRocketCountry() : "Unknown Country");

        TextView detailDescription = findViewById(R.id.detailDescription);
        detailDescription.setText(launch.getDetails() != null ? launch.getDetails() : "No details available");

        TextView detailRocketDescription = findViewById(R.id.detailRocketDescription);
        detailRocketDescription.setText(launch.getRocketDescription() != null ? launch.getRocketDescription() : "No description available");

        TextView detailRocketStages = findViewById(R.id.detailRocketStages);
        String stagesText = launch.getRocketStages() != null ? launch.getRocketStages() + " stages" : "Unknown stages";
        detailRocketStages.setText(stagesText);

        TextView detailRocketCost = findViewById(R.id.detailRocketCost);
        String costText = launch.getRocketCostPerLaunch() != null ? "$" + launch.getRocketCostPerLaunch() : "Unknown cost";
        detailRocketCost.setText(costText);

        TextView detailRocketSuccessRate = findViewById(R.id.detailRocketSuccessRate);
        String successRateText = launch.getRocketSuccessRate() != null ? launch.getRocketSuccessRate() + "%" : "Unknown success rate";
        detailRocketSuccessRate.setText(successRateText);

        TextView detailRocketActive = findViewById(R.id.detailRocketActive);
        String activeStatus = launch.getRocketActive() != null && launch.getRocketActive() ? "Active" : "Retired";
        detailRocketActive.setText(activeStatus);

        Button wikipediaButton = findViewById(R.id.detailWikipediaButton);
        if (launch.getRocketWikipedia() != null) {
            wikipediaButton.setVisibility(android.view.View.VISIBLE);
        } else {
            wikipediaButton.setVisibility(android.view.View.GONE);
        }
    }

    private void setupClickListeners(LaunchEntity launch) {
        Button wikipediaButton = findViewById(R.id.detailWikipediaButton);
        wikipediaButton.setOnClickListener(v -> {
            if (launch.getRocketWikipedia() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(launch.getRocketWikipedia()));
                startActivity(intent);
            }
        });

        Toolbar detailToolbar = findViewById(R.id.detailToolbar);
        detailToolbar.setNavigationOnClickListener(v -> finish());
    }

    private String formatDate(String dateUtc) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateUtc);

            return outputFormat.format(date != null ? date : new Date());
        } catch (Exception e) {
            return dateUtc;
        }
    }
}