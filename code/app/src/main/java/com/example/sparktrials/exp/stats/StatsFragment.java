package com.example.sparktrials.exp.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sparktrials.R;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.GeoLocation;
import com.example.sparktrials.models.Profile;
import com.example.sparktrials.models.Trial;
import com.example.sparktrials.models.TrialBinomial;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.common.primitives.Doubles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * The class containing the UI associated with showing experiment statistics
 */
public class StatsFragment extends Fragment {
    View view;
    Experiment experiment;
    public StatsFragment(Experiment experiment){
        this.experiment = experiment;
    }
    BarChart barChart;
    LineChart lineChart;
    TextView mean_tv, numTrials_tv, std_tv, median_tv, q1_tv, q3_tv, histoHeading_tv, plotHeading_tv;

    String label = "Mean";
    /**
     * Creates the stat fragment view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * The stat fragment view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats, container, false);
        return view;
    }

    /**
     * Displays all necessary data in appropriate locations
     * @param view
     * @param savedInstanceState
     */
    public void onViewCreated(View view, Bundle savedInstanceState){

        /**
         * Initialize descriptive statistics UI and set them appropriately calling from experiment class methods
         */
        mean_tv = getView().findViewById(R.id.meanID);
        mean_tv.setText(experiment.getMean());

        numTrials_tv = getView().findViewById(R.id.trialsID);
        numTrials_tv.setText(experiment.getNumTrials());


        std_tv = getView().findViewById(R.id.stdID);
        std_tv.setText(experiment.getStd());


        median_tv= getView().findViewById(R.id.medianID);
        median_tv.setText((experiment.getMedian()));


        q1_tv = getView().findViewById(R.id.q1ID);
        q1_tv.setText(experiment.getQ1());

        q3_tv = getView().findViewById(R.id.q3ID);
        q3_tv.setText((experiment.getQ3()));

        histoHeading_tv = getView().findViewById(R.id.HistogramID);
        histoHeading_tv.setText(experiment.getHistogramHeader());

        plotHeading_tv = getView().findViewById(R.id.PlotID);
        plotHeading_tv.setText(experiment.getPlotHeader());

        /**
         * Initialize histogram and set X and Y data calling from experiment class
         */

        barChart = (BarChart) getView().findViewById(R.id.barchartID);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int[] frequencies = experiment.frequencies();
        int xAxisLength = experiment.getXaxis().length;
        if (!experiment.getValidTrials().isEmpty()) {
            for (int i = 0; i < xAxisLength; i++) {
                barEntries.add(new BarEntry((float) i, (float) (frequencies[i])));
            }
            if (experiment.getType().equals("counts")) {
                label = "Counts";
            } else if (experiment.getType().equals("binomial trials")) {
                label = "Proportion of Success";
            }
            BarDataSet barDataSet = new BarDataSet(barEntries, "Frequency");
            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelCount(experiment.removeDupes().length);

            xAxis.setDrawGridLines(false);
            xAxis.setEnabled(true);
            /**
             * Format X-Axis for histogram
             */
            xAxis.setValueFormatter(new ValueFormatter() {
                /**
                 * This method casts the X axis values necessary for the used graphing Library
                 *
                 * @param value
                 * @return Integer values for x-axis of histogram
                 */
                @Override
                public String getFormattedValue(float value) {
                    String[] vals = experiment.getXaxis();
                    return vals[(int) value];
                }
            });
            // Set data to histogram
            BarData theData = new BarData(barDataSet);
            barDataSet.setValueTextSize(10f);
            barChart.setData(theData);
            barChart.getDescription().setEnabled(false);
        }

        /**
         * Implements Line Plots
         */
        lineChart = (LineChart) getView().findViewById(R.id.linechartID);
        if(!experiment.getValidTrials().isEmpty()) {

            // Stores Plot data
            ArrayList<Entry> lineEntries = new ArrayList<>();
            for (int i = 0; i < experiment.daysOfTrials().size(); i++) {
                lineEntries.add(new Entry(((float) i), (float) (experiment.daysFrequencies()[i])));
            }
            // Customize Lines For Data
            ArrayList<ILineDataSet> lines = new ArrayList<>();
            LineDataSet mainDataSet = new LineDataSet(lineEntries, label);
            mainDataSet.setCircleColor(Color.RED);
            mainDataSet.setColor(Color.RED);
            mainDataSet.setValueTextSize(10f);
            lineChart.getDescription().setEnabled(false);
            lines.add(mainDataSet);
            //LineData data = new LineData(lines);
            //lineChart.setData(data);
            XAxis plotXAxis = lineChart.getXAxis();
            plotXAxis.setLabelCount(experiment.daysOfTrials().size(), true);
            plotXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            YAxis plotYAxis = lineChart.getAxisLeft();
            double min = Doubles.min(experiment.daysFrequencies());
            plotYAxis.setAxisMinimum((float) min);
            plotXAxis.setValueFormatter(new ValueFormatter() {
                /**
                 * This method casts the X axis values necessary for the used graphing Library
                 *
                 * @param value
                 * @return String values for x-axis of line plot
                 */
                final ArrayList<String> vals = experiment.daysOfTrials();
                @Override
                public String getAxisLabel(float value, AxisBase axisBase) {
                    int pos = Math.round(value);
                    for (int i = 0; i < vals.size(); i++) {
                        if (value > i - 2 && value < i + 1) {
                            pos = i;
                        }
                        if (pos < vals.size()) {
                            return vals.get(pos);
                        }
                    }
                    return "";
                }
            });


            // Check if experiment is measurement or non-negative for quanitle data
            if (experiment.getType().equals("measurement trials") || experiment.getType().equals("non-negative integer counts")) {
                ArrayList<Entry> q1Entries = new ArrayList<>();
                for (int i = 0; i < experiment.daysOfTrials().size(); i++) {
                    q1Entries.add(new Entry((float) i, (float) (experiment.q1Plots()[i])));

                }
                LineDataSet q1DataSet = new LineDataSet(q1Entries, "Quartile 1");
                q1DataSet.setCircleColor(Color.BLUE);
                q1DataSet.setColor(Color.BLUE);
                q1DataSet.setValueTextSize(10f);
                lines.add(q1DataSet);
                ArrayList<Entry> q3Entries = new ArrayList<>();
                for (int i = 0; i < experiment.daysOfTrials().size(); i++) {
                    q3Entries.add(new Entry((float) i, (float) (experiment.q3Plots()[i])));

                }
                LineDataSet q3DataSet = new LineDataSet(q3Entries, "Quartile 3");
                q3DataSet.setCircleColor(Color.GREEN);
                q3DataSet.setColor(Color.GREEN);
                q3DataSet.setValueTextSize(10f);
                lines.add(q3DataSet);
                double q1Min = Doubles.min(experiment.q1Plots());
                double q3Min = Doubles.min(experiment.q3Plots());
                double meanMin = Doubles.min(experiment.daysFrequencies());
                if (q1Min < meanMin || q3Min < meanMin) {
                    double lowerB = Doubles.min(q1Min, q3Min);
                    plotYAxis.setAxisMinimum((float) lowerB);
                }


            }
            LineData data = new LineData(lines);
            lineChart.setData(data);

        }
    }
}