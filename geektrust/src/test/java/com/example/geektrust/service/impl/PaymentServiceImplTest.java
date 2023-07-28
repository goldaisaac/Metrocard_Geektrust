package com.example.geektrust.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.geektrust.constants.AppConstants;

public class PaymentServiceImplTest {

	private PaymentServiceImpl paymentService = new PaymentServiceImpl();

	// Test case to check the rechargeAmount method for Return Journey
	@Test
	void testRechargeAmount_ReturnJourney_ShouldCalculateCorrectAmount() {
		// Prepare test data
		long balance = 500;
		boolean isReturn = true;
		long baseAmount = 1000;

		// Calculate the recharge amount using the rechargeAmount method
		long rechargeAmount = paymentService.rechargeAmount(balance, isReturn, baseAmount);

		// Calculate the expected recharge amount
		long expectedRechargeAmount = baseAmount
				+ (long) (((AppConstants.R_ADULT - balance) * AppConstants.SERVICE_CHARGES));

		// Assert that the calculated recharge amount matches the expected recharge
		// amount
		assertEquals(expectedRechargeAmount, rechargeAmount);
	}

	// Test case to check the rechargeAmount method for One-Way Journey
	@Test
	public void testRechargeAmount_OneWayJourney_ShouldCalculateCorrectAmount() {
		// Prepare test data
		long balance = 800;
		boolean isReturn = false;
		long baseAmount = 600;

		// Calculate the recharge amount using the rechargeAmount method
		long rechargeAmount = paymentService.rechargeAmount(balance, isReturn, baseAmount);

		// Calculate the expected recharge amount
		long expectedRechargeAmount = baseAmount
				+ (long) (((AppConstants.ADULT - balance) * AppConstants.SERVICE_CHARGES));

		// Assert that the calculated recharge amount matches the expected recharge
		// amount
		assertEquals(expectedRechargeAmount, rechargeAmount);
	}

	// Test case to check the calculateAmountCollected method for sufficient balance
	@Test
	public void testCalculateAmountCollected_SufficientBalance_ShouldReturnFareAmount() {
		// Prepare test data
		long balance = 1000;
		long fare = 800;
		boolean isReturn = true;

		// Calculate the amount collected using the calculateAmountCollected method
		long amountCollected = paymentService.calculateAmountCollected(balance, fare, isReturn);

		// Assert that the amount collected matches the fare amount
		assertEquals(fare, amountCollected);
	}

	// Test case to check the calculateAmountCollected method for insufficient
	// balance
	@Test
	public void testCalculateAmountCollected_InsufficientBalance_ShouldCalculateRechargeAmount() {
		// Prepare test data
		long balance = 400;
		long fare = 800;
		boolean isReturn = false;

		// Calculate the expected recharge amount using the rechargeAmount method
		long expectedRechargeAmount = paymentService.rechargeAmount(balance, isReturn, fare);

		// Calculate the amount collected using the calculateAmountCollected method
		long amountCollected = paymentService.calculateAmountCollected(balance, fare, isReturn);

		// Assert that the amount collected matches the expected recharge amount
		assertEquals(expectedRechargeAmount, amountCollected);
	}

	// Test case to check the calculateReturnCollected method for Return Journey
	@Test
	public void testCalculateReturnCollected_ReturnJourney_ShouldReturnFareAmount() {
		// Prepare test data
		long fare = 800;
		boolean isReturn = true;

		// Calculate the return amount collected using the calculateReturnCollected
		// method
		long returnCollected = paymentService.calculateReturnCollected(fare, isReturn);

		// Assert that the return amount collected matches the fare amount
		assertEquals(fare, returnCollected);
	}

	// Test case to check the calculateReturnCollected method for One-Way Journey
	@Test
	public void testCalculateReturnCollected_OneWayJourney_ShouldReturnZero() {
		// Prepare test data
		long fare = 800;
		boolean isReturn = false;

		// Calculate the return amount collected using the calculateReturnCollected
		// method
		long returnCollected = paymentService.calculateReturnCollected(fare, isReturn);

		// Assert that the return amount collected is zero for One-Way Journey
		assertEquals(0, returnCollected);
	}

	// Test case to check the updateBalance method for positive amount collected
	@Test
	public void testUpdateBalance_PositiveAmountCollected_ShouldUpdateBalanceCorrectly() {
		// Prepare test data
		long balance = 1000;
		long amountCollected = 600;

		// Update the balance using the updateBalance method
		long updatedBalance = paymentService.updateBalance(balance, amountCollected);

		// Assert that the balance is correctly updated after a positive amount is
		// collected
		assertEquals(balance - amountCollected, updatedBalance);
	}

	// Test case to check the updateBalance method for negative amount collected
	@Test
	public void testUpdateBalance_NegativeAmountCollected_ShouldSetBalanceToZero() {
		// Prepare test data
		long balance = 400;
		long amountCollected = 800;

		// Update the balance using the updateBalance method
		long updatedBalance = paymentService.updateBalance(balance, amountCollected);

		// Assert that the balance is set to zero after a negative amount is collected
		assertEquals(0, updatedBalance);
	}
}
