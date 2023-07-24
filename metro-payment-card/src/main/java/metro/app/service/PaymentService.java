package metro.app.service;

public interface PaymentService {

	long rechargeAmount(long balance, boolean isReturn, Long baseAmount);

	long calculateAmountCollected(long balance, long fare, boolean isReturn);

	long calculateReturnCollected(long fare, boolean isReturn);

	long updateBalance(long balance, long amountCollected);

}
