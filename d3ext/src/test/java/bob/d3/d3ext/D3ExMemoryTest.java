package bob.d3.d3ext;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.d3ext.D3ExException.LocalDatabaseException;

public class D3ExMemoryTest {

	@Test
	public void testGetPath() throws LocalDatabaseException {
		D3ExMemory loc = D3ExMemory.getPath(System.getProperty("java.io.tmpdir"));
		Assert.assertNotNull(loc);
	}

	@Test
			public void testPullFile() throws LocalDatabaseException {
				D3ExMemory loc = D3ExMemory.getPath(System.getProperty("java.io.tmpdir"));
				loc.saveFile("P5730553", "VRGP", 5730553, "P573", "P5730553.PDF", "PDF", 9018027l);
			}

	@Test
			public void testPullProp_without_name() throws LocalDatabaseException {
				D3ExMemory loc = D3ExMemory.getPath(System.getProperty("java.io.tmpdir"));
				loc.saveProp("P5730553", "dok_dat_feld_21", null, "919");
			}

	@Test
			public void testPullProp_with_name() throws LocalDatabaseException {
				D3ExMemory loc = D3ExMemory.getPath(System.getProperty("java.io.tmpdir"));
				loc.saveProp("P5730553", "dok_dat_feld_21", "Gruppen-Nr.", "919");
			}

}
