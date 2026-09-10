package airport.model.parking;

import org.joda.money.Money;

import java.util.HashMap;

/**
 * Econo park parking
 *
 * @author Adam Ross
 */
public class EconoParkParkingLot extends AbstractParkingLot implements ParkingLot {

	/**
	 * Constructor for class.
	 *
	 * @param name
	 *            Name of the parking lot.
	 * @param initialCapacity
	 *            Capacity of the parking lot.
	 */
	public EconoParkParkingLot(String name, int initialCapacity) {
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
		final long HOUR_CHARGE = 5; // constant for the hourly price of parking
		final long DAY_CHARGE = 15; // constant for the daily price of parking
		long charge = 0; // default for incrementing parking charges

		if ((duration.get(ParkingLotEnum.HRS.getValue()) % DAY_HOURS) != 23) { // checks that the hour is not 23
			if ((duration.get(ParkingLotEnum.MINS.getValue()) % HOUR_MINUTES) > 0) { // checks minutes is above zero
				duration.put(ParkingLotEnum.HRS.getValue(), duration.get(ParkingLotEnum.HRS.getValue()) + 1);
			}
		}

		// checks if the amount of days parked is equal to zero while also checking that
		// the clock has started
		if (duration.get(ParkingLotEnum.DAYS.getValue()) < 1 && duration.get(ParkingLotEnum.SECS.getValue()) > 0) {
			charge += DAY_CHARGE; // increments to charge the value charged for a period of 0-1 days of parking
		}
		long modHours = duration.get(ParkingLotEnum.HRS.getValue()) % DAY_HOURS; // variable for the hours parked

		if (duration.get(ParkingLotEnum.DAYS.getValue()) >= 1) { // checks if days of parking is above or equal to a day
			if (modHours > 2) { // checks if the hours modulo of 24 hours is above two hours
				duration.put(ParkingLotEnum.DAYS.getValue(), duration.get(ParkingLotEnum.DAYS.getValue()) + 1);
			}

			if (modHours <= 2) { // checks if the hours modulo of 24 hours is below or equal to two hours
				charge += modHours * HOUR_CHARGE; // increments to charge the hours multiplied by the hourly price
			}
			charge += DAY_CHARGE * duration.get(ParkingLotEnum.DAYS.getValue()); // increments to charge days * charge
		}

		Money chargeMoney = Money.parse(DEFAULT_CHARGE); // default charge is set to zero in NZD currency
		chargeMoney = chargeMoney.plus(charge); // the final charge value is converted to money in NZD
		return chargeMoney; // returns the appropriate charge for the specified time that a vehicle has been
							// parked
	}
}
