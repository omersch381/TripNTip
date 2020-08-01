package com.example.backup;


import com.example.TripNTip.SquaredImageView;


public class Picture {
    private String name;
    private SquaredImageView pictureView;

    public Picture(String name, SquaredImageView pictureView) {
        this.name = name;
        this.pictureView = pictureView;
    }

    public String getName() {
        return name;
    }

    public SquaredImageView getPictureView() {
        return pictureView;
    }

    public void setPictureView(SquaredImageView pictureView) {
        this.pictureView = pictureView;
    }

}
