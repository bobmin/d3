package bob.d3;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3ExException.ResourceException;

public class ResourceFileTest {

	@Test(expected = NullPointerException.class)
	public void testPathNull() throws ResourceException {
		new ResourceFile(null);
	}

	@Test
	public void testGetText() throws ResourceException {
		ResourceFile res = new ResourceFile("/d3exresource_demo.txt");
		String txt = res.getText();
		System.out.println(txt);
		Assert.assertNotNull(txt);
		Assert.assertTrue(0 < txt.indexOf("öäüÖÄÜ"));
	}

}
