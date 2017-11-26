package bob.d3.finder;

import org.junit.Assert;
import org.junit.Test;

public class MemoryPropLongtextTest {

	@Test
	public void testGetDefault() {
		MemoryPropLongtext x = MemoryPropLongtext.getDefault();
		Assert.assertNotNull(x);
	}

	@Test
	public void testGetSize() {
		MemoryPropLongtext x = MemoryPropLongtext.getDefault();
		Assert.assertEquals(24, x.getSize());
	}

}
