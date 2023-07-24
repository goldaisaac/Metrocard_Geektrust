package metro.app.response;

import java.util.EnumMap;

import lombok.Data;
import metro.app.constants.Category;
import metro.app.constants.Destination;

@Data
public class PassengerSummary {

	Destination destination;
	
	long totalAmountCollected;
	
	long returnAmountCollected;

	private EnumMap<Category, Integer> passengerCount;
}
