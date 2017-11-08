package bob.d3.d3ext;

import org.junit.Test;

import bob.d3.d3ext.D3ExException.SourceException;

public class D3ExSourceFolderTest {

	@Test
	public void testCreate() throws SourceException {
		D3ExSourceFolder s = D3ExSourceFolder.create();
		System.out.println(s);
	}

}
