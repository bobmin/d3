package bob.d3.d3ext;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.d3ext.D3ExException.ResourceException;

public class D3ExResourceTest {

	@Test(expected = D3ExException.ResourceException.class)
	public void testPathNull() throws ResourceException {
		new D3ExResource(null);
	}

	@Test
	public void testGetText() throws ResourceException {
		D3ExResource res = new D3ExResource("/d3exresource_demo.txt");
		String txt = res.getText();
		System.out.println(txt);
		Assert.assertNotNull(txt);
		Assert.assertTrue(0 < txt.indexOf("öäüÖÄÜ"));
	}

}
