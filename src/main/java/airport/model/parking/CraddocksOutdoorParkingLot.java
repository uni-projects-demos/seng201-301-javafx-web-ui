package airport.model.parking;

import org.joda.money.Money;

import java.util.HashMap;

/**
 * Craddocks outdoor parking
 *
 * @author Adam Ross
 */
public class CraddocksOutdoorParkingLot extends AbstractParkingLot implements ParkingLot {

	/**
	 * Constructor for class.
	 *
	 * @param name
	 *            Name of the parking lot.
	 * @param initialCapacity
	 *            Capacity of the parking lot.
	 */
	public CraddocksOutdoorParkingLot(String name, int initialCapacity) {
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
		final long DAY_MINUTES = 1440; // constant for the amount of minutes in a 24 hour day
		final long FIRST_THREE_DAYS_DAILY_PRICE = 22; // constant for daily price for the first three days of parking
		final long AFTER_THREE_DAYS_DAILY_PRICE = 8; // constant for daily price for days parked after first three days
		long charge = 0; // default for incrementing parking charges

		if ((duration.get(ParkingLotEnum.MINS.getValue()) % DAY_MINUTES) > 0) { // checks if minutes is above zero
			duration.put(ParkingLotEnum.DAYS.getValue(), duration.get(ParkingLotEnum.DAYS.getValue()) + 1); // days + 1
		}

		// calculates the price charged during the first three days of parking by the
		// amount of days parked
		long firstThreeDaysCharge = FIRST_THREE_DAYS_DAILY_PRICE * duration.get(ParkingLotEnum.DAYS.getValue());
		// calculates price charged for all days after first three days and adds to
		// first three days total charge
		long afterThreeDaysCharge = AFTER_THREE_DAYS_DAILY_PRICE * (duration.get(ParkingLotEnum.DAYS.getValue()) - 3)
				+ (FIRST_THREE_DAYS_DAILY_PRICE * 3);

		if (duration.get(ParkingLotEnum.DAYS.getValue()) < 4) { // checks if days parked is below four
			charge += firstThreeDaysCharge; // increments a charge appropriate for parking in three days or less
		} else {
			charge += afterThreeDaysCharge; // increments a charge appropriate for parking in four days or more
		}

		Money chargeMoney = Money.parse(DEFAULT_CHARGE); // default charge is set to zero in NZD currency
		chargeMoney = chargeMoney.plus(charge); // the final charge value is converted to money in NZD
		return chargeMoney; // returns the appropriate charge for the specified time that a vehicle has been
							// parked
	}
}
