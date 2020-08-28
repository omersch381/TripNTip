package com.example.TripNTip.WeatherAPI;

public class TravelGuideRecommendation {

    private String extendedRecommendation;
    private int recommendationGrade;

    public TravelGuideRecommendation(String extendedRecommendation, int recommendationGrade) {
        this.extendedRecommendation = extendedRecommendation;
        this.recommendationGrade = recommendationGrade;
    }

    public int getRecommendationGrade() {
        return recommendationGrade;
    }

        public String getExtendedRecommendation() {
        return extendedRecommendation;
    }
}
