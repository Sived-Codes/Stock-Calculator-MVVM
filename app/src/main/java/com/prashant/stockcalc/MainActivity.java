package com.prashant.stockcalc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prashant.averageprice.R;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText editTextInitialQuantity, editTextDesiredProfit, editTextInitialPrice, editTextCurrentMarketPrice, editTextAdditionalQuantity, editTextCurrentMarketPriceProfitLoss;
    private Button buttonCalculateAveragePrice, buttonCalculateProfitLoss, buttonCalculateMarketPrice, clearData;
    private TextView textViewAveragePrice, textViewProfitLoss, textViewMarketPrice;

    private SharedPreferences sharedPreferences;
    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        editTextInitialQuantity = findViewById(R.id.editTextInitialQuantity);
        editTextInitialPrice = findViewById(R.id.editTextInitialPrice);
        editTextCurrentMarketPrice = findViewById(R.id.editTextCurrentMarketPrice);
        editTextAdditionalQuantity = findViewById(R.id.editTextAdditionalQuantity);
        editTextCurrentMarketPriceProfitLoss = findViewById(R.id.editTextCurrentMarketPriceProfitLoss);

        buttonCalculateAveragePrice = findViewById(R.id.buttonCalculateAveragePrice);
        buttonCalculateProfitLoss = findViewById(R.id.buttonCalculateProfitLossCurrentPrice);
        clearData = findViewById(R.id.clearData);

        textViewAveragePrice = findViewById(R.id.textViewAveragePrice);
        textViewProfitLoss = findViewById(R.id.textViewProfitLossCurrentPrice);

        editTextDesiredProfit = findViewById(R.id.editTextDesiredProfit);
        buttonCalculateMarketPrice = findViewById(R.id.desiredProfitCalculateButton);
        textViewMarketPrice = findViewById(R.id.textViewMarketPrice);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Load saved values from SharedPreferences
        getSavedValues();

        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData(v);
            }
        });

        buttonCalculateAveragePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAveragePrice();
            }
        });

        buttonCalculateProfitLoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateProfitLoss();
            }
        });

        buttonCalculateMarketPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateDesiredPrice();
            }
        });

        observeLiveData();
    }

    private void observeLiveData() {
        mainViewModel.getAveragePriceLiveData().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double averagePrice) {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                textViewAveragePrice.setText("Average Price: " + decimalFormat.format(averagePrice));
            }
        });

        mainViewModel.getProfitLossLiveData().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double profitLoss) {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                textViewProfitLoss.setTextColor(profitLoss >= 0 ? getResources().getColor(R.color.profitColor) : getResources().getColor(R.color.lossColor));
                textViewProfitLoss.setText(profitLoss >= 0 ? "Profit: Rs " + decimalFormat.format(profitLoss) : "Loss: Rs " + decimalFormat.format(profitLoss));
            }
        });

        mainViewModel.getDesiredPriceLiveData().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double desiredPrice) {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                textViewMarketPrice.setText("Price: Rs " + decimalFormat.format(desiredPrice));
            }
        });
    }

    private void getSavedValues() {
        editTextInitialQuantity.setText(sharedPreferences.getString("initialQuantity", ""));
        editTextInitialPrice.setText(sharedPreferences.getString("initialPrice", ""));
        editTextCurrentMarketPrice.setText(sharedPreferences.getString("currentMarketPrice", ""));
        editTextAdditionalQuantity.setText(sharedPreferences.getString("additionalQuantity", ""));
        editTextCurrentMarketPriceProfitLoss.setText(sharedPreferences.getString("currentMarketPriceProfitLoss", ""));
        // Set EditText fields to empty strings
        editTextInitialQuantity.setText("");
        editTextInitialPrice.setText("");
        editTextCurrentMarketPrice.setText("");
        editTextAdditionalQuantity.setText("");
        editTextCurrentMarketPriceProfitLoss.setText("");
        editTextDesiredProfit.setText("");

        // Set TextView fields to default values
        textViewAveragePrice.setText("Average Price: N/A");
        textViewProfitLoss.setText("Profit/Loss: N/A");
        textViewProfitLoss.setTextColor(Color.parseColor("#3F51B5"));
        textViewMarketPrice.setText("Desired Price: N/A");

        Toast.makeText(this, "Data cleared successfully", Toast.LENGTH_SHORT).show();
    }

    private double[] calculateAveragePrice() {
        String initialQuantityStr = editTextInitialQuantity.getText().toString().trim();
        String initialPriceStr = editTextInitialPrice.getText().toString().trim();
        String additionalQuantityStr = editTextAdditionalQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(initialQuantityStr) || TextUtils.isEmpty(initialPriceStr) || TextUtils.isEmpty(additionalQuantityStr)) {
            return new double[0];
        }

        int initialQuantity = Integer.parseInt(initialQuantityStr);
        double initialPrice = Double.parseDouble(initialPriceStr);
        int additionalQuantity = Integer.parseInt(additionalQuantityStr);

        double currentMarketPrice = Double.parseDouble(editTextCurrentMarketPrice.getText().toString().trim());

        mainViewModel.calculateAveragePrice(initialQuantity, initialPrice, additionalQuantity, currentMarketPrice);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("initialQuantity", initialQuantityStr);
        editor.putString("initialPrice", initialPriceStr);
        editor.putString("currentMarketPrice", editTextCurrentMarketPrice.getText().toString().trim());
        editor.putString("additionalQuantity", additionalQuantityStr);
        editor.putString("currentMarketPriceProfitLoss", "");
        editor.apply();
        return new double[0];
    }

    private void calculateProfitLoss() {
        String currentMarketPriceStr = editTextCurrentMarketPriceProfitLoss.getText().toString().trim();

        if (TextUtils.isEmpty(currentMarketPriceStr)) {
            Toast.makeText(this, "Please enter current market price", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double currentMarketPrice = Double.parseDouble(currentMarketPriceStr);

            // Retrieve average price and total quantity from calculateAveragePrice() method
            double[] averagePriceAndQuantity = calculateAveragePrice();

            if (averagePriceAndQuantity != null && averagePriceAndQuantity.length >= 1) {
                double averagePrice = averagePriceAndQuantity[0];
                int totalQuantity = (int) averagePriceAndQuantity[1];

                double profitLoss = (currentMarketPrice - averagePrice) * totalQuantity;

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                textViewProfitLoss.setTextColor(profitLoss >= 0 ? getResources().getColor(R.color.profitColor) : getResources().getColor(R.color.lossColor));
                textViewProfitLoss.setText(profitLoss >= 0 ? "Profit: Rs " + decimalFormat.format(profitLoss) : "Loss: Rs " + decimalFormat.format(profitLoss));
            } else {
                // Handle the case where calculateAveragePrice() returns null or an array with insufficient length
                String initialQuantityStr = editTextInitialQuantity.getText().toString().trim();
                String initialPriceStr = editTextInitialPrice.getText().toString().trim();

                if (TextUtils.isEmpty(initialQuantityStr) || TextUtils.isEmpty(initialPriceStr)) {
                    Toast.makeText(this, "Unable to calculate profit/loss. Please check input values.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int initialQuantity = Integer.parseInt(initialQuantityStr);
                double initialPrice = Double.parseDouble(initialPriceStr);

                double profitLoss = (currentMarketPrice - initialPrice) * initialQuantity;

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                textViewProfitLoss.setTextColor(profitLoss >= 0 ? getResources().getColor(R.color.profitColor) : getResources().getColor(R.color.lossColor));
                textViewProfitLoss.setText(profitLoss >= 0 ? "Profit: Rs " + decimalFormat.format(profitLoss) : "Loss: Rs " + decimalFormat.format(profitLoss));
            }
        } catch (NumberFormatException e) {
            // Handle the case where the current market price is not a valid number
            e.printStackTrace();
            Toast.makeText(this, "Please enter a valid current market price", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateDesiredPrice() {
        String desiredProfitStr = editTextDesiredProfit.getText().toString().trim();

        if (TextUtils.isEmpty(desiredProfitStr)) {
            Toast.makeText(this, "Please enter desired profit", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double desiredProfit = Double.parseDouble(desiredProfitStr);

            // Retrieve average price and total quantity from calculateAveragePrice() method
            double[] averagePriceAndQuantity = calculateAveragePrice();

            if (averagePriceAndQuantity == null || averagePriceAndQuantity.length < 2) {
                // Handle the case where calculateAveragePrice() returns null or an array with insufficient length
                String initialQuantityStr = editTextInitialQuantity.getText().toString().trim();
                String initialPriceStr = editTextInitialPrice.getText().toString().trim();

                if (TextUtils.isEmpty(initialQuantityStr) || TextUtils.isEmpty(initialPriceStr)) {
                    Toast.makeText(this, "Unable to calculate desired price. Please check input values.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int initialQuantity = Integer.parseInt(initialQuantityStr);
                double initialPrice = Double.parseDouble(initialPriceStr);

                double desiredPrice = (desiredProfit / initialQuantity) + initialPrice;

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                textViewMarketPrice.setText("Desired Price: Rs " + decimalFormat.format(desiredPrice));
            } else {
                double averagePrice = averagePriceAndQuantity[0];
                int totalQuantity = (int) averagePriceAndQuantity[1];

                double desiredPrice = (desiredProfit / totalQuantity) + averagePrice;

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                textViewMarketPrice.setText("Desired Price: Rs " + decimalFormat.format(desiredPrice));
            }
        } catch (NumberFormatException e) {
            // Handle the case where the desired profit is not a valid number
            e.printStackTrace();
            Toast.makeText(this, "Please enter a valid desired profit", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearData(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        getSavedValues();
        Toast.makeText(this, "Data cleared successfully", Toast.LENGTH_SHORT).show();
    }
}
