package com.example.kitchen_assistant.fragments.nutrition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitchen_assistant.R;
import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.databinding.FragmentDailyReportBinding;
import com.example.kitchen_assistant.databinding.FragmentRecipeNutritionBinding;
import com.example.kitchen_assistant.fragments.profile.ProfileFragment;
import com.example.kitchen_assistant.helpers.ChartHelper;
import com.example.kitchen_assistant.helpers.NutritionHelper;
import com.example.kitchen_assistant.models.HistoryEntry;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.User;
import com.example.kitchen_assistant.storage.CurrentHistoryEntries;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;
import java.util.HashMap;

public class DailyReportFragment extends Fragment {

    private static final String KEY_START_DATE = "startDate";
    private static final String KEY_END_DATE = "endDate";
    private static final String TITLE = "Today Nutrition Report";
    private static final String TAG = "DailyReportFragment";

    private FragmentDailyReportBinding fragmentDailyReportBinding;
    private BarChart bcNutrition;
    private PieChart pcCalories;
    private Date startDate;
    private Date endDate;

    private float calories;
    private float protein;
    private float carbs;
    private float fat;
    private float goal;

    public DailyReportFragment() {
    }

    public static DailyReportFragment newInstance(Date startDate, Date endDate) {
        DailyReportFragment fragment = new DailyReportFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_START_DATE, startDate);
        args.putSerializable(KEY_END_DATE, endDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startDate = (Date) getArguments().getSerializable(KEY_START_DATE);
            endDate = (Date) getArguments().getSerializable(KEY_END_DATE);
        }
        User user = User.fetchFromUserId(ParseUser.getCurrentUser().getObjectId());
        goal = user.getCaloriesGoal();
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_report_toolbar, menu);
        ((MainActivity) getContext()).getSupportActionBar().setTitle(TITLE);

        MenuItem miProfile = menu.findItem(R.id.miProfile);
        MenuItem miHistory = menu.findItem(R.id.miHistory);

        miProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                goToProfile();
                return true;
            }
        });

        miHistory.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                goToHistory();
                return true;
            }
        });
    }

    private void goToHistory() {
        HistoryReportFragment fragment = HistoryReportFragment.newInstance();
        MainActivity.switchFragment(fragment);
    }

    private void goToProfile() {
        ProfileFragment fragment = ProfileFragment.newInstance();
        MainActivity.switchFragment(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentDailyReportBinding = FragmentDailyReportBinding.inflate(getLayoutInflater());
        bcNutrition = fragmentDailyReportBinding.bcNutrition;
        pcCalories = fragmentDailyReportBinding.pcCalories;

        HashMap<String, Float> nutrition = NutritionHelper.getNutritionInfoInDuration(startDate, endDate);

        ChartHelper.drawCaloriesByNutritionChart(nutrition.get("calories"), nutrition.get("carbs"), nutrition.get("protein"), nutrition.get("fat"), goal, pcCalories, getContext());
        ChartHelper.drawNutritionBarChart(nutrition.get("carbs"), nutrition.get("protein"), nutrition.get("fat"), bcNutrition, getContext());

        return fragmentDailyReportBinding.getRoot();
    }
}