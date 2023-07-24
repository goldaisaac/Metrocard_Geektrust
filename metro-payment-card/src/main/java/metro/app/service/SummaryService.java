package metro.app.service;

import java.util.List;
import java.util.function.Consumer;

import metro.app.response.PassengerSummary;

public interface SummaryService {
    void processSummary(List<PassengerSummary> passengerSummaries, Consumer<String> printFunction);
}