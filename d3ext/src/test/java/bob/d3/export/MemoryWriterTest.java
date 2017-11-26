package bob.d3.export;

import java.io.File;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3ExException.LocalDatabaseException;
import bob.d3.export.MemoryWriter;

public class MemoryWriterTest {

	@Test
	public void testGetPath() throws LocalDatabaseException, ClassNotFoundException, SQLException {
		MemoryWriter loc = new MemoryWriter(new File(System.getProperty("java.io.tmpdir"), "d3exDemo"));
		Assert.assertNotNull(loc);
	}

	@Test
	public void testPullFile() throws LocalDatabaseException, ClassNotFoundException, SQLException {
		MemoryWriter loc = new MemoryWriter(new File(System.getProperty("java.io.tmpdir"), "d3exDemo"));
		loc.saveFile("P5730553", "VRGP", 5730553, "P573", "P5730553.PDF", "PDF", 9018027l);
	}

	@Test
	public void testPullProp_without_name() throws LocalDatabaseException, ClassNotFoundException, SQLException {
		MemoryWriter loc = new MemoryWriter(new File(System.getProperty("java.io.tmpdir"), "d3exDemo"));
		loc.saveProp("P5730553", "dok_dat_feld_21", null, "919");
	}

	@Test
	public void testPullProp_with_name() throws LocalDatabaseException, ClassNotFoundException, SQLException {
		MemoryWriter loc = new MemoryWriter(new File(System.getProperty("java.io.tmpdir"), "d3exDemo"));
		loc.saveProp("P5730553", "dok_dat_feld_21", "Gruppen-Nr.", "919");
	}

}
