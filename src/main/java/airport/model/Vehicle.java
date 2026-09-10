package airport.model;

import airport.model.parking.ParkingLot;
import airport.model.parking.ParkingLotEnum;
import org.joda.money.Money;

import java.time.LocalDateTime;

/**
 * Vehicle class
 *
 * @author Adam Ross
 */
public class Vehicle {

	private final String vehicleRegistration; // attribute for the registration number of the vehicle
	private BillableEntity ownerIdentity; // attribute for the owner/driver of the vehicle
	private LocalDateTime arrival; // attribute for the arrival time of an admitted vehicle
	private LocalDateTime departure; // attribute for the departure time of a released vehicle
	private ParkingLot parkingLot; // attribute for the parking lot vehicle is parked in
	private Money totalBill; // attribute for the total amount owing by the vehicle owner

	/**
	 * Constructor for class
	 *
	 * @param vehicleRegistration
	 *            The registration identification of the vehicle
	 */
	public Vehicle(String vehicleRegistration) {
		this.vehicleRegistration = vehicleRegistration; // the registration number of the vehicle
	}

	/**
	 * Constructor for class
	 *
	 * @param vehicleRegistration
	 *            The registration identification of the vehicle
	 * @param ownerIdentity
	 *            The identity of the driver/owner of the vehicle
	 */
	public Vehicle(String vehicleRegistration, BillableEntity ownerIdentity) {
		this.vehicleRegistration = vehicleRegistration; // variable for the registration number of the vehicle
		this.ownerIdentity = ownerIdentity; // variable for the owner/driver of the vehicle
		this.totalBill = Money.parse(ParkingLotEnum.DEFAULT_CHARGE.getValue()); // variable for the total bill
	}

	/**
	 * Setter for the arrival time of a vehicle to a parking lot
	 *
	 * @param arrival
	 *            the arrival time of a vehicle to a parking lot
	 */
	public void setArrival(LocalDateTime arrival) {
		this.arrival = arrival;
	}

	/**
	 * Setter for the departure time of a vehicle from a parking lot
	 *
	 * @param departure
	 *            The departure time of a vehicle from a parking lot
	 */
	public void setDeparture(LocalDateTime departure) {
		this.departure = departure;
	}

	/**
	 * Returns the arrival time of a vehicle from a parking lot
	 *
	 * @return the arrival time that a vehicle is admitted to a parking lot
	 */
	public LocalDateTime getArrival() {
		return this.arrival;
	}

	/**
	 * Returns the departure time of a vehicle from a parking lot
	 *
	 * @return the departure time that a vehicle is released from a parking lot
	 */
	public LocalDateTime getDeparture() {
		return this.departure;
	}

	/**
	 * Retrieves info about whom to send charges to, etc.
	 *
	 * @return The identity of the driver/owner of the vehicle
	 */
	BillableEntity getOwner() {
		return this.ownerIdentity;
	}

	/**
	 * Provides info about whom to send charges to, etc.
	 *
	 * @param owner
	 *            The identity of the driver/owner of the vehicle
	 */
	void setOwner(BillableEntity owner) {
		this.ownerIdentity = owner; // the owner/driver of the vehicle
	}

	/**
	 * Sets the parkingLot that a vehicle is parked in
	 *
	 * @param parkingLot
	 *            the parking lot
	 */
	public void setParkingLot(ParkingLot parkingLot) {
		this.parkingLot = parkingLot;
	}

	/**
	 * Gets the parking lot that a vehicle is parked in
	 *
	 * @return the parking lot the vehicle is parked in
	 */
	public ParkingLot getParkingLot() {
		return this.parkingLot;
	}

	/**
	 * Reports the registration number as it appears on the license plate
	 *
	 * @return The registration identity of the vehicle
	 */
	public String regNo() {
		return this.vehicleRegistration; // returns the registration number of the vehicle
	}

	/**
	 * Prints the registration identification of the vehicle as a string
	 *
	 * @return The registration identification of the vehicle
	 */
	public String toString() {
		return this.vehicleRegistration; // returns the registration number of the vehicle for printing
	}

	/**
	 * Checks if a new vehicle is already in the parking lot
	 *
	 * @param newVehicle
	 *            the new vehicle being admitted to a parking lot
	 * @return True if there is a match of new vehicle registration with a vehicle
	 *         in the parking lot, otherwise false
	 */
	boolean identityCrisis(String newVehicle) {
		return this.vehicleRegistration.equalsIgnoreCase(newVehicle);
	}
}
