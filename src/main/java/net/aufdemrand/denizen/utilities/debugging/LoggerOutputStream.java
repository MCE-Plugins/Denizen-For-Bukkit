package net.aufdemrand.denizen.utilities.debugging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LoggerOutputStream extends ByteArrayOutputStream
{
	private final String separator = System.getProperty("line.separator");
	private final Logger logger;
	private final Level level;

	public LoggerOutputStream(Logger logger, Level level) {
		this.logger = logger;
		this.level = level;
	}

	public void flush() throws IOException {
		synchronized (this) {
			super.flush();
			String record = toString();
			super.reset();

			if ((record.length() > 0) && (!(record.equals(this.separator))))
				this.logger.log(this.level, record);
		}
	}
}
