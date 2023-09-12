package com.example.googlemapsandplaces;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTime {
    private Context context;

    public DateTime(Context context) {
        this.context = context;
    }

    public void showDateTimePicker(final DateTimeCallback callback) {
        Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog for date selection
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Handle the selected date here
                    showTimePicker(selectedYear, selectedMonth, selectedDayOfMonth, callback);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void showTimePicker(int year, int month, int dayOfMonth, final DateTimeCallback callback) {
        Calendar calendar = Calendar.getInstance();

        // Create a TimePickerDialog for time selection
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, selectedHourOfDay, selectedMinute) -> {
                    // Handle the selected date and time here
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth, selectedHourOfDay, selectedMinute);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                    String selectedDate = dateFormat.format(selectedCalendar.getTime());
                    String selectedTime = timeFormat.format(selectedCalendar.getTime());

                    callback.onDateTimeSelected(selectedDate, selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true); // true for 24-hour format

        // Show the TimePickerDialog
        timePickerDialog.show();
    }

    public interface DateTimeCallback {
        void onDateTimeSelected(String selectedDate, String selectedTime);
    }
}
