package com.fuad.ramadancalendar.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset;
import static com.fuad.ramadancalendar.constants.EnumData.*;

import com.fuad.ramadancalendar.R;
import com.fuad.ramadancalendar.constants.EnumData;
import com.fuad.ramadancalendar.viewmodels.Ramadan;
import com.fuad.ramadancalendar.adapters.RamadanAdapter;

public class RamadanCalendarFragment extends Fragment {

    public RecyclerView recyclerView;
    public RamadanAdapter adapter;
    public List<Ramadan> ramadanList;
    public TextView tvDate, tvDivision;
    public static boolean flag = true;
    public int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ramadan_calendar, container, false);

        String[] DAYS = {getContext().getResources().getString(R.string.sunday), getContext().getResources().getString(R.string.monday), getContext().getResources().getString(R.string.tuesday), getContext().getResources().getString(R.string.wednesday), getContext().getResources().getString(R.string.thursday), getContext().getResources().getString(R.string.friday), getContext().getResources().getString(R.string.saturday)};

        tvDate = view.findViewById(R.id.date);
        tvDivision = view.findViewById(R.id.division_name);

        ramadanList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.month_calendar_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RamadanAdapter(ramadanList, getContext());
        recyclerView.setAdapter(adapter);

        String division = getFromSharedPref("division_index", getContext());
        if (!division.isEmpty()) {
            tvDivision.setText(setLatitudeLongitude(getContext(),Integer.parseInt(division)));
        }
        tvDate.setText(monthDateFormat.format(new Date()));

        Calendar calendar = Calendar.getInstance();
        try {
            // For Language Change
            String tempDate = dateFormat.format(usDateFormat.parse(firstRamadanDate));

            Date date = dateFormat.parse(tempDate);
            for (int i = 0; i < 30; i++) {
                calendar.setTime(date);
                Calendar[] sunriseSunset = getSunriseSunset(calendar, DIVISION_LATITUDE, DIVISION_LONGITUDE);

                Date sunrise = sunriseSunset[0].getTime();
                sunrise.setMinutes(sunrise.getMinutes() - 72 + SUNRISE_BUFFER);
                Date sunset = sunriseSunset[1].getTime();
                sunset.setMinutes(sunset.getMinutes() + 72 + SUNSET_BUFFER);

                Ramadan ramadan = new Ramadan();
                ramadan.setDay(DAYS[date.getDay()]);
                ramadan.setDate(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(date));
                ramadan.setSahr(timeFormat.format(sunrise));
                ramadan.setItmam(timeFormat.format(sunset));
                ramadanList.add(ramadan);

                if(ramadan.getDate().equals(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date())))
                    position = i;

                date.setDate(date.getDate() + 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(position);

        return view;
    }
}