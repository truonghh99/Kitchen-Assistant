package com.example.kitchen_assistant.fragments.nutrition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentDailyReportBinding;
import com.example.kitchen_assistant.databinding.FragmentRecipeNutritionBinding;
import com.example.kitchen_assistant.helpers.ChartHelper;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.storage.CurrentHistoryEntries;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import org.parceler.Parcels;

import java.util.Date;

public class DailyReportFragment extends Fragment {

    private static final String KEY_START_DATE = "startDate";
    private static final String KEY_END_DATE = "endDate";

    private FragmentDailyReportBinding fragmentDailyReportBinding;
    private BarChart bcNutrition;
    private PieChart pcCalories;
    private Date startDate;
    private Date endDate;

    private float calories;
    private float protein;
    private float carbs;
    private float fat;

    public DailyReportFragment() {
    }

    public static DailyReportFragment newInstance(Parcelable startDate, Parcelable endDate) {
        DailyReportFragment fragment = new DailyReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_START_DATE, startDate);
        args.putParcelable(KEY_END_DATE, endDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startDate = Parcels.unwrap(getArguments().getParcelable(KEY_START_DATE));
            endDate = Parcels.unwrap(getArguments().getParcelable(KEY_END_DATE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentDailyReportBinding = FragmentDailyReportBinding.inflate(getLayoutInflater());
        bcNutrition = fragmentDailyReportBinding.bcNutrition;
        pcCalories = fragmentDailyReportBinding.pcCalories;

        ((MainActivity) getContext()).getSupportActionBar().setTitle(recipe.getName());

        getNutritionInfo(startDate, endDate);

        ChartHelper.drawNutritionBarChart(recipe.getNutrition().getCarbs(), recipe.getNutrition().getProtein(), recipe.getNutrition().getFat(), bcNutrition, getContext());
        ChartHelper.drawCaloriesPercentageChart(recipe.getNutrition().getCalories(), 1200, pcCalories, getContext()); // TODO: Change total to user's customized goal

        return fragmentDailyReportBinding.getRoot();
    }

    private void getNutritionInfo(Date startDate, Date endDate) {
        startDate = CurrentHistoryEntries.getFirstWithLowerBound(startDate);
        endDate = CurrentHistoryEntries.getLastWithUpperBound(endDate);
    }
}