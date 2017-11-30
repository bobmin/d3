package bob.d3.finder;

import org.junit.Assert;
import org.junit.Test;

public class MemoryPropsCacheTest {

	@Test
	public void testGetDefault() {
		MemoryPropsCache x = MemoryPropsCache.getDefault();
		Assert.assertNotNull(x);
	}

	@Test
	public void testGetSize() {
		MemoryPropsCache x = MemoryPropsCache.getDefault();
		Assert.assertEquals(24, x.getSize());
	}

}
