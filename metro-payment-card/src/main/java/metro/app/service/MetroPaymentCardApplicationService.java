package metro.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import metro.app.constants.Category;
import metro.app.constants.Destination;
import metro.app.constants.InputCommands;
import metro.app.dto.MetroCardDetails;
import metro.app.response.PassengerSummary;

@Slf4j
@Service
public class MetroPaymentCardApplicationService implements CommandLineRunner {

	@Autowired
	CheckInService checkInService;

	@Autowired
	BalanceService balanceService;

	@Autowired
	SummaryService summaryService;
	
	private final List<MetroCardDetails> metroCardDetails;
	private final List<PassengerSummary> passengerSummaries;
	
	@Autowired
    public MetroPaymentCardApplicationService(List<MetroCardDetails> metroCardDetails, List<PassengerSummary> passengerSummaries) {
        this.metroCardDetails = metroCardDetails;
        this.passengerSummaries = passengerSummaries;
    }

	@Override
	public void run(String... args) throws Exception {
		
		log.info("MetroPaymentCardApplicationService - START");
		
		for (Destination destination : Destination.values()) {
		    PassengerSummary passengerSummary = new PassengerSummary();
		    passengerSummary.setDestination(destination);
		    EnumMap<Category, Integer> countMap = new EnumMap<>(Category.class);
		    passengerSummary.setPassengerCount(countMap);
		    passengerSummaries.add(passengerSummary);
		}
		
		try (Scanner scanner = new Scanner(System.in)) {
			
			System.out.println("Enter file name");
			String fileName = scanner.next();
			log.info("Processing File : {}", fileName);

			try {
				List<String> items = Files.readAllLines(Paths.get(fileName));
				items.stream()
						.map(line -> line.split(" "))
						.filter(words -> words.length > 0)
						.forEach(words -> {
							String firstWord = words[0];
							InputCommands command = InputCommands.valueOf(firstWord.toUpperCase());
							switch (command) {
								case BALANCE -> 
									balanceService.processBalance(metroCardDetails, String.join(" ", words));
								case CHECK_IN ->
									checkInService.checkIn(metroCardDetails, passengerSummaries, String.join(" ", words));
								case PRINT_SUMMARY ->
							        summaryService.processSummary(passengerSummaries, summary -> System.out.println(summary));
								default -> 
									throw new IllegalArgumentException("Unexpected value: ");
							};
						});
			} catch (IOException e) {
				System.out.println("File not found / empty");
			}
		}
		
		log.info("MetroPaymentCardApplicationService - END");
	}
}