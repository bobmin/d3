package bob.d3.finder;

import org.junit.Assert;
import org.junit.Test;

public class SqlGeneratorTest {

	// @formatter:off
	private static final String[][] DATA = {
			{
				"",                    
				"#START #TAIL"
			}, {
				"id = P0005287",       
				"#START WHERE doc_id IN ('P0005287') #TAIL"
			}, {
				"id = P0005287 id = P000005287",     
				"#START WHERE doc_id IN ('P0005287', 'P000005287') #TAIL"
			}, {
				"ext = PDF",           
				"#START WHERE doc_ext IN ('PDF') #TAIL"
			}, {
				"ext = PDF ext = TIF", 
				"#START WHERE doc_ext IN ('PDF', 'TIF') #TAIL"
			}, {
				"knr = 11016 ext = PDF", 
				"#START WHERE doc_ext IN ('PDF') AND prop_longtext = 'Kunden-Nr.' AND prop_value = '11016' #TAIL"
			}, {
				"ext = PDF lnr = 5000", 
				"#START WHERE doc_ext IN ('PDF') AND prop_longtext = 'Lieferanten-Nr.' AND prop_value = '5000' #TAIL"
			}
	};
	// @formatter:on

	@Test
	public void testGetCommand() {
		for (String[] x : DATA) {
			final String value = x[0];
			final String expected = x[1].replaceFirst("#START", SqlGenerator.START).replaceFirst("#TAIL",
					SqlGenerator.TAIL);
			System.out.println("value: \"" + value + "\"\n\t\"" + expected + "\"");
			Assert.assertTrue(expected.contains("SELECT"));
			Assert.assertTrue(expected.contains("FROM"));
			SqlGenerator generator = new SqlGenerator(value);
			final String command = generator.getCommand();
			System.out.println("\t\"" + command + "\"");
			System.out.println("\t" + generator);
			Assert.assertEquals(expected, command);
		}
	}

}
