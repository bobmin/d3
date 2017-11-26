package bob.d3;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3Config;

public class D3ConfigTest {

	@Test
	public void testGetProperty() {
		D3Config cfg = D3Config.getDefault();
		String usr = cfg.getProperty(D3Config.DATABASE_USERNAME);
		System.out.println("cfg: database username = " + usr);
		Assert.assertNotNull(usr);
		String pwd = cfg.getProperty(D3Config.DATABASE_PASSWORD);
		Assert.assertNotNull(pwd);
		System.out.println("cfg: database password = " + pwd.replaceAll(".", "\\*"));
	}

	@Test
	public void testIsDevelopmentActiv() {
		System.setProperty(D3Config.DEVELOPMENT_PROPERTY, "true");
		D3Config cfg = D3Config.getDefault();
		boolean x = cfg.isDevelomentActiv();
		System.out.println("development: " + x);
		Assert.assertTrue(x);
	}

}
