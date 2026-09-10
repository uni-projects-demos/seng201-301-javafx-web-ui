package airport.model.parking;

import org.joda.money.Money;

import java.util.HashMap;

/**
 * Long stay parking
 *
 * @author Adam Ross
 */
public class LongStayParkingLot extends AbstractParkingLot implements ParkingLot {

	/**
	 * Constructor for class.
	 *
	 * @param name
	 *            Name of the parking lot.
	 * @param initialCapacity
	 *            Capacity of the parking lot.
	 */
	public LongStayParkingLot(String name, int initialCapacity) {
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
		final long DAY_PRICE = 25; // constant of maximum price charged for each 24 hour period of continuous
									// parking
		final long HOUR_PRICE = 8; // constant price charged for each hour after initial 24 hour period
		final long WEEK_PRICE = 125; // constant of maximum price charged for each 7 day period of continuous parking
		long charge = 0; // default for incrementing parking charges

		if ((duration.get(ParkingLotEnum.MINS.getValue()) % HOUR_MINUTES) > 0) { // checks if minutes is above zero
			duration.put(ParkingLotEnum.HRS.getValue(), duration.get(ParkingLotEnum.HRS.getValue()) + 1); // hours+1
		}

		// checks if the amount of days parked is below one while also checking that
		// seconds is above zero
		if (duration.get(ParkingLotEnum.DAYS.getValue()) < 1 && duration.get(ParkingLotEnum.SECS.getValue()) > 0) {
			charge += DAY_PRICE; // increments to charge the price of a single day of continuous parking
		}

		if (duration.get(ParkingLotEnum.DAYS.getValue()) >= 1) { // checks if days is above or equal to 1 day
			long modHours = duration.get(ParkingLotEnum.HRS.getValue()) % DAY_HOURS; // calculates the hours parked

			if (modHours > 3) { // checks if the hours modulo is above 3 hours
				duration.put(ParkingLotEnum.DAYS.getValue(), duration.get(ParkingLotEnum.DAYS.getValue()) + 1); // day+1
			}
			long weeks = (duration.get(ParkingLotEnum.DAYS.getValue()) / WEEK_DAYS); // calculates weeks parked
			long daysMod = (duration.get(ParkingLotEnum.DAYS.getValue()) % WEEK_DAYS); // calculates days parked

			if (daysMod <= 4) { // checks if the days modulo is below or equal to 4 days
				if (modHours <= 3) { // checks if the hours modulo is below or equal to 3 hours
					charge += modHours * HOUR_PRICE; // increments to charge the hourly price multiplied by hours parked
				}
				charge += DAY_PRICE * daysMod; // increments to charge the daily max price multiplied by days parked
			} else {
				++weeks; // increments weeks by 1 week
			}
			charge += weeks * WEEK_PRICE; // increments to charge the weekly max price multiplied by weeks parked
		}

		Money chargeMoney = Money.parse(DEFAULT_CHARGE); // default charge is set to zero in NZD currency
		chargeMoney = chargeMoney.plus(charge); // the final charge value is converted to money in NZD
		return chargeMoney; // returns the appropriate charge for the specified time that a vehicle has been
							// parked
	}
}
