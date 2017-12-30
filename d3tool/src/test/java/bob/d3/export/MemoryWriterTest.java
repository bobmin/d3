package bob.d3.export;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import bob.d3.D3ExException.LocalDatabaseException;

public class MemoryWriterTest {

	private static final String ROOT = System.getProperty("java.io.tmpdir");

	private static final File PATH = new File(ROOT, "d3exDemo");

	private MemoryWriter writer = null;

	private Date einbring = null;

	private Date sterbe = null;

	@Before
	public void before() throws ParseException, ClassNotFoundException, SQLException {
		if (PATH.exists()) {
			delete(PATH, true);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		einbring = sdf.parse("01/01/2001");
		sterbe = sdf.parse("01/01/2051");
		writer = new MemoryWriter(PATH);
	}

	private void delete(File f, boolean recursiv) {
		if (recursiv && f.isDirectory()) {
			for (File x : f.listFiles()) {
				delete(x, true);
			}
			delete(f, false);
		} else {
			Assert.assertTrue(0 == f.getAbsolutePath().indexOf(ROOT));
			System.out.println("delete " + f.getAbsolutePath() + " [" + f.delete() + "]");
		}
	}

	@Test
	public void testPullFile() throws LocalDatabaseException, ClassNotFoundException, SQLException {
		writer.saveFile("P5730553", einbring, "VRGP", 5730553, "P573", "P5730553.PDF", "PDF", 9018027l, sterbe);
	}

	@Test
	public void testPullProp_with_name() throws LocalDatabaseException, ClassNotFoundException, SQLException {
		testPullFile();
		writer.saveProp("P5730553", "dok_dat_feld_21", "Gruppen-Nr.", "919");
	}

	@Test
	public void testPullProp_without_name() throws LocalDatabaseException, ClassNotFoundException, SQLException {
		testPullFile();
		writer.saveProp("P5730553", "dok_dat_feld_21", null, "919");
	}

	@After
	public void after() {
		writer.close();
	}

}
