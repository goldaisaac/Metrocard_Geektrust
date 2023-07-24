package metro.app.service;

import java.util.List;

import metro.app.dto.MetroCardDetails;
import metro.app.response.PassengerSummary;

public interface CheckInService {

	List<MetroCardDetails> checkIn(List<MetroCardDetails> metroCardDetails, List<PassengerSummary> passengerSummaries, String data);

}
