package metro.app.dto;

import java.util.List;

import lombok.Data;

@Data
public class MetroCardDetails {

	String metroCardId;
	
	BalanceDto balance;
	
	List<CheckInDto> checkInDetails;
}
