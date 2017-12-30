package bob.d3;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ConsoleUtil {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(ConsoleUtil.class.getName());

	/** der Zeitpunkt vom Programmstart */
	private static final long startTimeMillis = System.currentTimeMillis();

	public static void log(final String msg) {
		long interval = System.currentTimeMillis() - startTimeMillis;
		final long hr = TimeUnit.MILLISECONDS.toHours(interval);
		final long min = TimeUnit.MILLISECONDS.toMinutes(interval) % 60;
		final long sec = TimeUnit.MILLISECONDS.toSeconds(interval) % 60;
		LOG.info(String.format("%02d:%02d:%02d %s", hr, min, sec, msg));
	}

	public static void log(final String msg, Object... args) {
		log(String.format(msg, args));
	}

}
