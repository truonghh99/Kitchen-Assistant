package com.example.kitchen_assistant.fragments.nutrition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentDailyReportBinding;
import com.example.kitchen_assistant.databinding.FragmentHistoryReportBinding;
import com.example.kitchen_assistant.databinding.FragmentRecipeNutritionBinding;
import com.example.kitchen_assistant.helpers.ChartHelper;
import com.example.kitchen_assistant.models.HistoryEntry;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.User;
import com.example.kitchen_assistant.storage.CurrentHistoryEntries;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;

public class HistoryReportFragment extends Fragment {

    private static final String TITLE = "History Nutrition Report";
    private static final String TAG = "HistoryReportFragment";

    private FragmentHistoryReportBinding fragmentHistoryReportBinding;
    private LineChart lcNutrition;
    private LineChart lcCalories;

    public HistoryReportFragment() {
    }

    public static DailyReportFragment newInstance() {
        DailyReportFragment fragment = new DailyReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = User.fetchFromUserId(ParseUser.getCurrentUser().getObjectId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHistoryReportBinding = FragmentHistoryReportBinding.inflate(getLayoutInflater());
        lcCalories = fragmentHistoryReportBinding.lcCalories;
        lcNutrition = fragmentHistoryReportBinding.lcNutrition;

        ((MainActivity) getContext()).getSupportActionBar().setTitle(TITLE);

        return fragmentHistoryReportBinding.getRoot();
    }

    private void getNutritionInfo(Date startDate, Date endDate) {
        HistoryEntry firstEntry = CurrentHistoryEntries.getFirstWithLowerBound(startDate);
        HistoryEntry lastEntry = CurrentHistoryEntries.getLastWithUpperBound(endDate);

        calories = lastEntry.getCumulativeCalories() - firstEntry.getCumulativeCalories();
        protein = lastEntry.getCumulativeProtein() - firstEntry.getCumulativeProtein();
        carbs = lastEntry.getCumulativeCarbs() - firstEntry.getCumulativeCarbs();
        fat = lastEntry.getCumulativeFat() - firstEntry.getCumulativeFat();
    }
}