package bob.d3.export;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

public class DocumentArtsTest {

	@Test
	public void testGetDefault() throws ClassNotFoundException, SQLException {
		DocumentArts x = DocumentArts.getDefault();
		Assert.assertNotNull(x);
	}

	@Test
	public void testLookFor() throws ClassNotFoundException, SQLException {
		DocumentArts x = DocumentArts.getDefault();
		Assert.assertEquals("Auftragsakte", x.lookFor("AUAK"));
		Assert.assertEquals("Empfangsschein", x.lookFor("EMSC"));
	}

}
