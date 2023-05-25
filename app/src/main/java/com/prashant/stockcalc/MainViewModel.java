package com.prashant.stockcalc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Double> averagePriceLiveData = new MutableLiveData<>();
    private MutableLiveData<Double> profitLossLiveData = new MutableLiveData<>();
    private MutableLiveData<Double> desiredPriceLiveData = new MutableLiveData<>();

    public LiveData<Double> getAveragePriceLiveData() {
        return averagePriceLiveData;
    }

    public LiveData<Double> getProfitLossLiveData() {
        return profitLossLiveData;
    }

    public LiveData<Double> getDesiredPriceLiveData() {
        return desiredPriceLiveData;
    }

    public void calculateAveragePrice(int initialQuantity, double initialPrice, int additionalQuantity, double currentMarketPrice) {
        int totalQuantity = initialQuantity + additionalQuantity;
        double totalCost = (initialQuantity * initialPrice) + (additionalQuantity * currentMarketPrice);
        double averagePrice = totalCost / totalQuantity;
        averagePriceLiveData.setValue(averagePrice);
    }

    public void calculateProfitLoss(double currentMarketPrice, double averagePrice, int totalQuantity) {
        double profitLoss = (currentMarketPrice - averagePrice) * totalQuantity;
        profitLossLiveData.setValue(profitLoss);
    }

    public void calculateDesiredPrice(double desiredProfit, double averagePrice, int totalQuantity) {
        double desiredPrice = (desiredProfit / totalQuantity) + averagePrice;
        desiredPriceLiveData.setValue(desiredPrice);
    }
}
