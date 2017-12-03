package bob.d3.finder;

import org.junit.Assert;
import org.junit.Test;

public class MemoryPropsTest {

	@Test
	public void testGetDefault() {
		MemoryProps x = MemoryProps.getDefault();
		Assert.assertNotNull(x);
	}

	@Test
	public void testGetSize() {
		MemoryProps x = MemoryProps.getDefault();
		Assert.assertEquals(24, x.getSize());
	}

}
