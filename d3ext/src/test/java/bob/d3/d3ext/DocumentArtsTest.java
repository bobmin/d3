package bob.d3.d3ext;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3ExException.DatabaseException;

public class DocumentArtsTest {

	@Test
	public void testGetDefault() throws DatabaseException {
		DocumentArts x = DocumentArts.getDefault();
		Assert.assertNotNull(x);
	}

	@Test
	public void testLookFor() throws DatabaseException {
		DocumentArts x = DocumentArts.getDefault();
		Assert.assertEquals("Auftragsakte", x.lookFor("AUAK"));
		Assert.assertEquals("Empfangsschein", x.lookFor("EMSC"));
	}

}
