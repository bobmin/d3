package bob.d3.d3ext;

import org.junit.Test;

import bob.d3.D3ExException.SourceException;

public class DocumentFolderTest {

	@Test
	public void testCreate() throws SourceException {
		DocumentFolder s = DocumentFolder.create();
		System.out.println(s);
	}

}
