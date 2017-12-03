package bob.d3.finder;

import org.junit.Assert;
import org.junit.Test;

public class MemoryArtsTest {

	@Test
	public void testGetDefault() {
		MemoryArts x = MemoryArts.getDefault();
		Assert.assertNotNull(x);
	}

	@Test
	public void testGetSize() {
		MemoryArts x = MemoryArts.getDefault();
		Assert.assertEquals(32, x.getSize());
	}

	@Test
	public void testLookFor() {
		MemoryArts x = MemoryArts.getDefault();
		Assert.assertEquals("Verhandlungsprotokoll Gruppe", x.lookFor("VEGP"));
		Assert.assertEquals("Verträge Gruppe", x.lookFor("VRGP"));
		Assert.assertEquals("Rechnung", x.lookFor("RECH"));
	}

}
