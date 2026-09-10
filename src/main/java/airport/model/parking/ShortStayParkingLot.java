package airport.model.parking;

import org.joda.money.Money;

import java.util.HashMap;

/**
 * Short stay parking
 *
 * @author Adam Ross
 */
public class ShortStayParkingLot extends AbstractParkingLot implements ParkingLot {

	/**
	 * Constructor for class.
	 *
	 * @param name
	 *            Name of the parking lot.
	 * @param initialCapacity
	 *            Capacity of the parking lot.
	 */
	public ShortStayParkingLot(String name, int initialCapacity) {
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
		final long MINIMUM_PRICE = 12; // constant price charged for parking between initial period of 0-2 hours
		final long HOUR_PRICE = 8; // constant price charged for each additional hour parked after initial period
		final long DAY_ONE_THREE_HOURS_CHARGE = 20; // constant price charged for first 3 hours of parking on first day
		final long DAY_CHARGE = 25; // constant of maximum price charged for every 24 hour period of continuous
									// parking
		long charge = 0; // default for incrementing parking charges

		if ((duration.get(ParkingLotEnum.MINS.getValue()) % HOUR_MINUTES) > 0) { // checks if minutes is above zero
			duration.put(ParkingLotEnum.HRS.getValue(), duration.get(ParkingLotEnum.HRS.getValue()) + 1); // hours+1
		}
		long daysCharge = DAY_CHARGE * duration.get(ParkingLotEnum.DAYS.getValue()); // price charged for 24 hours

		// checks if the amount of days parked is equal to zero while also checking that
		// seconds is above zero
		if (duration.get(ParkingLotEnum.DAYS.getValue()) == 0 && duration.get(ParkingLotEnum.SECS.getValue()) > 0) {
			if (duration.get(ParkingLotEnum.HRS.getValue()) <= 3) { // checks if hours parked is below 3 hours
				if (duration.get(ParkingLotEnum.HRS.getValue()) <= 2) { // checks if hours parked is below two hours
					charge += MINIMUM_PRICE; // increments to charge the min value of 12 charged for parking to charge
				} else {
					charge += DAY_ONE_THREE_HOURS_CHARGE; // increments the value for parking for the first 3 hours
				}
			} else {
				charge += DAY_CHARGE; // increments to charge the value for anywhere between 4-24 hours of parking
			}
		} else {
			long modHours = duration.get(ParkingLotEnum.HRS.getValue()) % DAY_HOURS; // variable for the hours parked

			if (modHours > 3) { // checks if the hours modulo of 24 hours is above three hours
				duration.put(ParkingLotEnum.DAYS.getValue(), duration.get(ParkingLotEnum.DAYS.getValue()) + 1); // day+1
			}
			charge += daysCharge; // increments to charge the days parked multiplied by the daily charge value of
									// 25

			if (modHours <= 3) { // checks if the hours modulo of 24 hours is below or equal to three hours
				charge += HOUR_PRICE * modHours; // increments to charge the value of hours multiplied by hours parked
			} else {
				charge += DAY_CHARGE; // increments the value charged for anywhere between 4-24 hours of parking
			}
		}

		Money chargeMoney = Money.parse(DEFAULT_CHARGE); // default charge is set to zero in NZD currency
		chargeMoney = chargeMoney.plus(charge); // the final charge value is converted to money in NZD
		return chargeMoney; // returns the appropriate charge for the specified time that a vehicle has been
							// parked
	}
}
