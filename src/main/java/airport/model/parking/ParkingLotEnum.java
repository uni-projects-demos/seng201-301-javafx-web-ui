package airport.model.parking;

/**
 * Constants for parking lot class
 *
 * @author Adam Ross
 */
public enum ParkingLotEnum {

	DAYS("days"), HRS("hours"), MINS("minutes"), SECS("seconds"), DEFAULT_CHARGE("NZD 0.00");

	private final String parkingLot;

	/**
	 * Converts a string to a parking lot name constant enum
	 *
	 * @param parkingLot
	 *            the string being converted to a parking lot name constant enum
	 */
	ParkingLotEnum(final String parkingLot) {
		this.parkingLot = parkingLot;
	}

	/**
	 * Converts a parking lot name constant to a string
	 *
	 * @return the parking lot name constant as a string
	 */
	public String getValue() {
		return parkingLot;
	}
}
