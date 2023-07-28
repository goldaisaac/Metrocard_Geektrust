package com.example.geektrust.constants;

public final class AppConstants {
	
    public static final long ADULT = 200;                     // Fare for adult passengers.
    public static final long SENIOR_CITIZEN = 100;            // Fare for senior citizen passengers.
    public static final long KID = 50;                        // Fare for kid passengers.
    public static final long R_ADULT = 100;                   // Return fare for adult passengers.
    public static final long R_SENIOR_CITIZEN = 50;           // Return fare for senior citizen passengers.
    public static final long R_KID = 25;                      // Return fare for kid passengers.
    public static final double SERVICE_CHARGES = 0.02;        // Service charges for balance recharge.
    public static final String ONE_WAY = "ONE_WAY";           // One-way travel type.
    public static final String RETURN = "RETURN";             // Return travel type.
    public static final String AIRPORT = "AIRPORT";           // Airport destination.
    public static final String CENTRAL = "CENTRAL";           // Central destination.
    
    private AppConstants() {
        // Private constructor to prevent instantiation of the class.
    }
}
