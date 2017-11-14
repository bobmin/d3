package bob.d3.d3ext;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.d3ext.D3ExException.DatabaseException;

public class D3ExDocFieldsTest {

	@Test
	public void testGetDefault() throws DatabaseException {
		D3ExDocFields x = D3ExDocFields.getDefault();
		Assert.assertNotNull(x);
		String f = x.lookFor("RECH", 5);
		System.out.println("RECH (5) = " + f);
		Assert.assertNotNull(f);
	}

}
