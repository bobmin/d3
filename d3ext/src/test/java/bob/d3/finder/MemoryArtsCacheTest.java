package bob.d3.finder;

import org.junit.Assert;
import org.junit.Test;

public class MemoryArtsCacheTest {

	@Test
	public void testGetDefault() {
		MemoryArtsCache x = MemoryArtsCache.getDefault();
		Assert.assertNotNull(x);
	}

	@Test
	public void testGetSize() {
		MemoryArtsCache x = MemoryArtsCache.getDefault();
		Assert.assertEquals(32, x.getSize());
	}

	@Test
	public void testLookFor() {
		MemoryArtsCache x = MemoryArtsCache.getDefault();
		Assert.assertEquals("Verhandlungsprotokoll Gruppe", x.lookFor("VEGP"));
		Assert.assertEquals("Verträge Gruppe", x.lookFor("VRGP"));
		Assert.assertEquals("Rechnung", x.lookFor("RECH"));
	}

}
