package airport.model.parking;

import org.joda.money.Money;

import java.util.HashMap;

/**
 * Express parking
 *
 * @author Adam Ross
 */
public class ExpressParkingLot extends AbstractParkingLot implements ParkingLot {

	/**
	 * Constructor for class.
	 *
	 * @param name
	 *            Name of the parking lot.
	 * @param initialCapacity
	 *            Capacity of the parking lot.
	 */
	public ExpressParkingLot(String name, int initialCapacity) {
		super(name, initialCapacity);
	}

	/**
	 * Compute the cost of parking in this lot between specified duration as of 2015
	 *
	 * @param duration
	 *            the duration of the time that a vehicle has been parked in a lot
	 * @return The cost of parking here.
	 */
	public Money computeCharge(HashMap<String, Long> duration) {
		final long ZERO_CHARGE = 0; // constant of the zero charge for continuous parking below 16 minutes
		final long SIXTEEN_TO_FORTY_MINUTES_PRICE = 4; // constant price charged for parking between 16 and 40 minutes
		final long HOUR_PRICE = 8; // constant price charged for each hour of parking until the daily/weekly max
									// price
		final long DAY_PRICE = 32; // constant of maximum price charged for each 24-hour period of continuous
									// parking
		final long WEEK_PRICE = 160; // constant of maximum price charged for each 7-day period of continuous parking
		long charge = 0; // default for incrementing parking charges

		if ((duration.get(ParkingLotEnum.MINS.getValue()) % HOUR_MINUTES) > 0) { // checks minutes is above zero
			duration.put(ParkingLotEnum.HRS.getValue(), duration.get(ParkingLotEnum.HRS.getValue()) + 1); // hour+1
		}
		long hoursCharge = HOUR_PRICE * duration.get(ParkingLotEnum.HRS.getValue()); // max price * hours parked

		if (duration.get(ParkingLotEnum.DAYS.getValue()) == 0) { // checks days is equal to zero and minutes above zero
			if (duration.get(ParkingLotEnum.HRS.getValue()) < 4) { // checks if hours is below 4 hours
				if (duration.get(ParkingLotEnum.HRS.getValue()) <= 1) { // checks if hours is below or equal to 1 hour
					if (duration.get(ParkingLotEnum.MINS.getValue()) < 41) { // checks if minutes is below 41 minutes
						if (duration.get(ParkingLotEnum.MINS.getValue()) < 16) { // checks if minutes below 16 minutes
							charge += ZERO_CHARGE; // increments to charge the zero price charge, which is zero
						} else {
							charge += SIXTEEN_TO_FORTY_MINUTES_PRICE; // increments 16 to 40 minute charge, which is 4
						}
					} else {
						charge += HOUR_PRICE; // increments to charge the hourly charge, which is 8
					}
				} else {
					charge += hoursCharge; // increments to charge the hourly price multiplied by the hours parked
				}
			} else {
				charge += DAY_PRICE; // increments to charge the daily maximum charge, which is 32
			}
		}

		if (duration.get(ParkingLotEnum.DAYS.getValue()) != 0) { // checks if days is not equal to zero
			long modHours = duration.get(ParkingLotEnum.HRS.getValue()) % DAY_HOURS; // calculates the hours parked

			if (modHours > 3) { // checks is the hours modulo is above 3 hours
				duration.put(ParkingLotEnum.DAYS.getValue(), duration.get(ParkingLotEnum.DAYS.getValue()) + 1); // day+1
			}

			if (modHours <= 3) { // checks if the hours modulo is below or equal to 3 hours
				charge += modHours * HOUR_PRICE; // increments to charge the hourly charge multiplied by hours parked
			}
			long weeks = (duration.get(ParkingLotEnum.DAYS.getValue()) / WEEK_DAYS); // calculates weeks parked
			long daysMod = (duration.get(ParkingLotEnum.DAYS.getValue()) % WEEK_DAYS); // calculates days parked

			if (daysMod > 5) { // checks if the days modulo is above 5 days
				++weeks; // increments weeks by 1 week
			}

			if (weeks > 0) { // checks if weeks is above zero weeks
				charge += weeks * WEEK_PRICE; // increments to charge the weekly maximum charge multiplied by weeks
			}

			if (daysMod != 0 && daysMod <= 5) { // checks if the days is below or equal to 5 and above zero days
				charge += DAY_PRICE * daysMod; // increments to charge the daily max charge multiplied by days parked
			}
		}

		Money chargeMoney = Money.parse(DEFAULT_CHARGE); // default charge is set to zero in NZD currency
		chargeMoney = chargeMoney.plus(charge); // the final charge value is converted to money in NZD
		return chargeMoney; // returns the appropriate charge for the specified time that a vehicle has been
							// parked
	}
}
