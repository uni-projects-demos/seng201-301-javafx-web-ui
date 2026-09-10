package airport.model.nosying;

/**
 * Constants for NosyParker class
 *
 * @author Adam Ross
 */
enum NosyParkerEnum {

	CRADDICKS_INDOOR("Craddocks Indoor Parking Lot"), CRADDOCKS_OUTDOOR("Craddocks Outdoor Parking Lot"), ECONO_PARK(
			"Econo Park Parking Lot"), EXPRESS_PARK("Express Parking Lot"), LONG_STAY(
					"Long Stay Parking Lot"), SHORT_STAY("Short Stay Parking Lot"), REG(
							"^[\\w]+$"), COST("cost"), DAYS("days"), HRS("hours"), MINS("minutes");

	private final String nosyParker;

	/**
	 * Converts a string to a NosyParker class constant
	 *
	 * @param nosyParker
	 *            the string being converted to a NosyParker class constant
	 */
	NosyParkerEnum(final String nosyParker) {
		this.nosyParker = nosyParker;
	}

	/**
	 * Converts a NosyParker class constant to a string
	 *
	 * @return the NosyParker class constant as a string
	 */
	String getValue() {
		return nosyParker;
	}
}
