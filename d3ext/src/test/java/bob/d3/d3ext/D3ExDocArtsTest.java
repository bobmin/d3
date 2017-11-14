package bob.d3.d3ext;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.d3ext.D3ExException.DatabaseException;

public class D3ExDocArtsTest {

	@Test
	public void testGetDefault() throws DatabaseException {
		D3ExDocArts x = D3ExDocArts.getDefault();
		Assert.assertNotNull(x);
	}

}
