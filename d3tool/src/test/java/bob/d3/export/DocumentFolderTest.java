package bob.d3.export;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3ExException.SourceException;

public class DocumentFolderTest {

	@Test
	public void testCreate() throws SourceException, FileNotFoundException {
		File root = new File(System.getProperty("java.io.tmpdir"));
		DocumentFolder s = DocumentFolder.create(root);
		System.out.println(s);
		Assert.assertNotNull(s);
	}

}
