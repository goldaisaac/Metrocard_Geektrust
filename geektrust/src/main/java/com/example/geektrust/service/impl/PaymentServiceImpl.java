package com.example.geektrust.service.impl;

import org.springframework.stereotype.Service;

import com.example.geektrust.constants.AppConstants;
import com.example.geektrust.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	/**
	 * Calculates the recharge amount based on the current balance, return type, and
	 * base amount.
	 *
	 * @param balance    The current balance.
	 * @param isReturn   A boolean indicating if it is a return journey.
	 * @param baseAmount The base amount for the journey.
	 * @return The recharge amount considering the service charges of 2%.
	 */
	@Override
	public long rechargeAmount(long balance, boolean isReturn, Long baseAmount) {

		log.info("Calculating recharge amount - balance: {}, isReturn: {}, baseAmount: {}", balance, isReturn,
				baseAmount);
		long rechargeAmount = baseAmount + (long) (((isReturn ? AppConstants.R_ADULT : AppConstants.ADULT) - balance)
				* AppConstants.SERVICE_CHARGES);

		log.info("Recharge amount calculated: {}", rechargeAmount);
		return rechargeAmount;
	}

	/**
	 * Calculates the amount collected based on the current balance, fare, and
	 * return type. If the balance is sufficient, it returns the fare amount.
	 * Otherwise, it calculates the recharge amount based on the fare and return
	 * type.
	 *
	 * @param balance  The current balance.
	 * @param fare     The fare amount for the journey.
	 * @param isReturn A boolean indicating if it is a return journey.
	 * @return The amount collected considering the balance and fare.
	 */
	@Override
	public long calculateAmountCollected(long balance, long fare, boolean isReturn) {
		log.info("Calculating amount collected - balance: {}, fare: {}, isReturn: {}", balance, fare, isReturn);
		return balance >= fare ? fare : this.rechargeAmount(balance, isReturn, fare);
	}

	/**
	 * Calculates the return amount collected based on the fare and return type. If
	 * it is a return journey, it returns the fare amount. Otherwise, it returns 0.
	 *
	 * @param fare     The fare amount for the journey.
	 * @param isReturn A boolean indicating if it is a return journey.
	 * @return The return amount collected.
	 */
	@Override
	public long calculateReturnCollected(long fare, boolean isReturn) {
		log.info("Calculating return collected - fare: {}, isReturn: {}", fare, isReturn);
		return isReturn ? fare : 0;
	}

	/**
	 * Updates the balance based on the current balance and the amount collected. It
	 * subtracts the amount collected from the balance. If the resulting balance is
	 * negative, it sets it to 0.
	 *
	 * @param balance         The current balance.
	 * @param amountCollected The amount collected.
	 * @return The updated balance.
	 */
	@Override
	public long updateBalance(long balance, long amountCollected) {
		log.info("Updating balance - balance: {}, amountCollected: {}", balance, amountCollected);
		balance -= amountCollected;
		return Math.max(0, balance);
	}

}
