package com.example.geektrust.service;

/**
 * The PaymentService interface provides methods for calculating recharge amount,
 * amount collected, return amount collected, and updating the balance for MetroCard transactions.
 */
public interface PaymentService {

    /**
     * Calculates the recharge amount based on the current balance, return type, and base amount.
     *
     * @param balance    The current balance.
     * @param isReturn   A boolean indicating if it is a return journey.
     * @param baseAmount The base amount for the journey.
     * @return The recharge amount considering the service charges of 2%.
     */
    long rechargeAmount(long balance, boolean isReturn, Long baseAmount);

    /**
     * Calculates the amount collected based on the current balance, fare, and return type.
     * If the balance is sufficient, it returns the fare amount. Otherwise, it calculates
     * the recharge amount based on the fare and return type.
     *
     * @param balance  The current balance.
     * @param fare     The fare amount for the journey.
     * @param isReturn A boolean indicating if it is a return journey.
     * @return The amount collected considering the balance and fare.
     */
    long calculateAmountCollected(long balance, long fare, boolean isReturn);

    /**
     * Calculates the return amount collected based on the fare and return type.
     * If it is a return journey, it returns the fare amount. Otherwise, it returns 0.
     *
     * @param fare     The fare amount for the journey.
     * @param isReturn A boolean indicating if it is a return journey.
     * @return The return amount collected.
     */
    long calculateReturnCollected(long fare, boolean isReturn);

    /**
     * Updates the balance based on the current balance and the amount collected.
     * It subtracts the amount collected from the balance. If the resulting balance
     * is negative, it sets it to 0.
     *
     * @param balance         The current balance.
     * @param amountCollected The amount collected.
     * @return The updated balance.
     */
    long updateBalance(long balance, long amountCollected);

}
