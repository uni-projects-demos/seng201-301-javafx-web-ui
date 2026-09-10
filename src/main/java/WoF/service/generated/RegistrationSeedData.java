package WoF.service.generated;

import WoF.service.BrowserDatabaseStorage;

/**
 * Build-time seed generated from AssignmentOneDB.sqlite.
 */
public final class RegistrationSeedData {

	private RegistrationSeedData() {
	}

	public static void populate(BrowserDatabaseStorage store) {
		store.seedOwner("EXTRA", "TEST", "four@test.com", "four", "", "", "");
		store.seedOwner("TEST", "DUMMY", "one@test.com", "one", "", "", "");
		store.seedOwner("TEST", "DUMBER", "two@test.com", "two", "", "", "");
		store.seedVehicle("000000", "RELIANT", "ROBIN", "1973-12-31", "MERRIE", "ENGLAND", "O", "OTHER");
		store.seedVehicle("111111", "DUHR123", "VAZ-2101", "1970-12-31", "SOVETSKIY", "SOYUZ", "MA", "PETROL");
		store.seedVehicle("ABC123", "FORD", "MODEL T", "1970-01-01", "MICHIGAN", "USA", "MA", "OTHER");
		store.seedVehicle("HIJ345", "BMW", "MINI", "2000-01-01", "MÜNCHEN", "DEUTSCHLAND", "MA", "PETROL");
		store.seedVehicle("XYZ987", "VW", "BEETLE", "1970-01-01", "WOLFSBURG", "DEUTSCHLAND", "MA", "DIESEL");
		store.seedLink("one@test.com", "111111", "11111111111111111");
		store.seedLink("one@test.com", "ABC123", "12345678901234567");
		store.seedLink("one@test.com", "HIJ345", "");
		store.seedLink("two@test.com", "000000", "00000000000000000");
		store.seedLink("two@test.com", "XYZ987", "89012345678901234");
		store.seedHistory("00000000000000000", "000001", "1991-12-31", "2019-07-07", "PASSED");
		store.seedHistory("11111111111111111", "111111", "1971-01-01", "1972-01-01", "PASSED");
		store.seedHistory("12345678901234567", "999999", "1970-01-01", "1970-01-11", "FAILED");
		store.seedHistory("89012345678901234", "999999", "1970-01-01", "1970-01-11", "EXPIRED");
	}
}
