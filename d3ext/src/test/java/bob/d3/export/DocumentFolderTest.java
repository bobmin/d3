package bob.d3.export;

import org.junit.Test;

import bob.d3.D3ExException.SourceException;
import bob.d3.export.DocumentFolder;

public class DocumentFolderTest {

	@Test
	public void testCreate() throws SourceException {
		DocumentFolder s = DocumentFolder.create();
		System.out.println(s);
	}

}
