package bob.d3.export;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

public class DocumentFieldsTest {

	@Test
	public void testGetDefault() throws ClassNotFoundException, SQLException {
		DocumentFields x = DocumentFields.getDefault();
		Assert.assertNotNull(x);
		String f = x.lookFor("RECH", 5);
		System.out.println("RECH (5) = " + f);
		Assert.assertNotNull(f);
	}

}
