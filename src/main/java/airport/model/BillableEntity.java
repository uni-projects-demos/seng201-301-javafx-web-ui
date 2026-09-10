package airport.model;

/**
 * Class providing contact info for billing.
 *
 * @author Adam Ross
 */
class BillableEntity {

	private final String name; // attribute for the identity of the driver of the vehicle
	private final String address; // attribute for the location of the owner of the vehicle
	private final int phone; // attribute for the phone of the owner of the vehicle

	/**
	 * Constructor for class.
	 *
	 * @param name
	 *            Name of person or business.
	 * @param address
	 *            Postal address.
	 * @param phone
	 *            Phone number.
	 */
	BillableEntity(String name, String address, int phone) {
		this.name = name; // variable for the owner/driver of the vehicle
		this.address = address; // variable for the address of the owner/driver of the vehicle
		this.phone = phone; // variable for the phone of the owner/driver of the vehicle
	}
}
