package bob.d3.finder;

import org.junit.Assert;
import org.junit.Test;

public class IndexQueryTest {

	// @formatter:off
	private static final String[][] DATA = {
			{
				"#direkt Kunden-Nr.:11016",                    
				"Kunden-Nr.:11016"
			}, {
				"",                    
				"P*"
			}, {
				"id = P0005287",       
				"ID:(P0005287)"
			}, {
				"id = P0005287 id = P000005287",     
				"ID:(P0005287+P000005287)"
			}, {
				"knr = 11016", 
				"Kunden-Nr.:(11016)"
			}, {
				"knr = 5000 knr = 11016", 
				"Kunden-Nr.:(5000+11016)"
			}, {
				"knr = 5000 ext = TIF knr = 11016", 
				"ERW:(tif) AND Kunden-Nr.:(5000+11016)"
			}, {
				"datum > 01.01.2017", 
				"EINBRING:[20170101 TO 20991231]"
			}, {
				"datum < 11.11.2017", 
				"EINBRING:[19990101 TO 20171111]"
			}, {
				"datum > 10.10.2017 datum < 11.11.2017", 
				"EINBRING:[20171010 TO 20171111]"
			}, {
				"datum > 10.10.2017 datum < 11.11.2017 knr = 11016", 
				"Kunden-Nr.:(11016) AND EINBRING:[20171010 TO 20171111]"
			}, {
				"#direkt EINBRING:[20171010 TO 20171111]", 
				"EINBRING:[20171010 TO 20171111]"
			}
	};
	// @formatter:on

	@Test
	public void testGetCommand() {
		for (String[] x : DATA) {
			final String value = x[0];
			final String expected = x[1];
			System.out.println("value: \"" + value + "\"\n\t\"" + expected + "\"");
			IndexQuery query = new IndexQuery(value);
			final String command = query.getCommand();
			System.out.println("\t\"" + command + "\"");
			System.out.println("\t" + query);
			Assert.assertEquals(expected, command);
		}
	}

}
