package kr.co.deotis.util;

import java.util.Calendar;
import java.util.TimerTask;

public class JobTimer extends TimerTask {

	@Override
	public void run() {
		Calendar date = Calendar.getInstance();
		String stamp = date.get(Calendar.HOUR_OF_DAY)+":"
				+ date.get(Calendar.MINUTE)+":"
				+ date.get(Calendar.SECOND)+":"
				+ date.get(Calendar.MILLISECOND);
		
	}

}
