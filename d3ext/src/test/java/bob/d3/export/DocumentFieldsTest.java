package bob.d3.export;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3ExException.DatabaseException;
import bob.d3.export.DocumentFields;

public class DocumentFieldsTest {

	@Test
	public void testGetDefault() throws DatabaseException {
		DocumentFields x = DocumentFields.getDefault();
		Assert.assertNotNull(x);
		String f = x.lookFor("RECH", 5);
		System.out.println("RECH (5) = " + f);
		Assert.assertNotNull(f);
	}

}
