package com.example.smartmobilevehicle;

public class DashBaodImages {

    private String name;
    private int imageResourceId;


    public static final DashBaodImages[] dashimages={

            new DashBaodImages("Emergency",R.drawable.emergency),
            new DashBaodImages("Machanic map view",R.drawable.hire_machanic),
            new DashBaodImages("Inform Insuerance",R.drawable.insuerance),
            new DashBaodImages("Hire your machanic",R.drawable.map_pin),
            new DashBaodImages("Petrol and Tires",R.drawable.petrol_shed),

    };

    private DashBaodImages(String name,int imageResourceId){
        this.name=name;
        this.imageResourceId=imageResourceId;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
