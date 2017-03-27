/*
 * Copyright (c) 2017, Intelidata S.A.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package cl.intelidata.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author Dev-DFeliu
 */
public class Utils {

    public static final Locale LOCAL_ES = new Locale("es", "ES");
    public static final TimeZone TIMEZONE_ES = TimeZone.getTimeZone("America/Santiago");
    public static final String FORMAT_PATTERN = "MMMMM d, yyyy";

    public static String dateToString(Date date) {
        return (new SimpleDateFormat(FORMAT_PATTERN, LOCAL_ES)).format(date);
    }

    public static String dateToString(Date date, String pattern) {
        return (new SimpleDateFormat(pattern, LOCAL_ES)).format(date);
    }

    public static String calendarToString(Calendar calendar) {
        return (new SimpleDateFormat(FORMAT_PATTERN, LOCAL_ES)).format(calendar.getTime());
    }

    public static String calendarToString(Calendar calendar, String pattern) {
        return (new SimpleDateFormat(pattern, LOCAL_ES)).format(calendar.getTime());
    }
}
