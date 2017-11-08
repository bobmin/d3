package bob.d3.d3ext;

import org.junit.Assert;
import org.junit.Test;

public class D3ExConfigTest {

	@Test
	public void testGetProperty() {
		D3ExConfig cfg = D3ExConfig.getDefault();
		String usr = cfg.getProperty(D3ExConfig.DATABASE_USERNAME);
		System.out.println("cfg: database username = " + usr);
		Assert.assertNotNull(usr);
		String pwd = cfg.getProperty(D3ExConfig.DATABASE_PASSWORD);
		Assert.assertNotNull(pwd);
		System.out.println("cfg: database password = " + pwd.replaceAll(".", "\\*"));
	}

}
