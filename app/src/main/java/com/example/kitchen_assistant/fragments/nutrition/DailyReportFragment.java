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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import org.parceler.Parcels;

import java.util.Date;

public class DailyReportFragment extends Fragment {

    private static final String KEY_DATE = "date";

    private Recipe recipe;

    private FragmentDailyReportBinding fragmentDailyReportBinding;
    private BarChart bcNutrition;
    private PieChart pcCalories;
    private TextView tvCalories;
    private Date date;

    public DailyReportFragment() {
    }

    public static DailyReportFragment newInstance(Parcelable date) {
        DailyReportFragment fragment = new DailyReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = Parcels.unwrap(getArguments().getParcelable(KEY_DATE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentDailyReportBinding = FragmentDailyReportBinding.inflate(getLayoutInflater());
        bcNutrition = fragmentDailyReportBinding.bcNutrition;
        pcCalories = fragmentDailyReportBinding.pcCalories;

        ((MainActivity) getContext()).getSupportActionBar().setTitle(recipe.getName());

        tvCalories.setText(recipe.getNutrition().getCalories() + " kcal");

        ChartHelper.drawNutritionBarChart(recipe.getNutrition().getCarbs(), recipe.getNutrition().getProtein(), recipe.getNutrition().getFat(), bcNutrition, getContext());
        ChartHelper.drawCaloriesPercentageChart(recipe.getNutrition().getCalories(), 1200, pcCalories, getContext()); // TODO: Change total to user's customized goal

        return fragmentDailyReportBinding.getRoot();
    }
}