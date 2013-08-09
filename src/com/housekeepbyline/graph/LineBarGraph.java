package com.housekeepbyline.graph;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.housekeepbyline.main.GraphData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

public class LineBarGraph extends Activity{
	
	ArrayList<GraphData> GRAPH_DATA = new ArrayList<GraphData>();
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        //インテント
	        Intent intent = getIntent();
	        GRAPH_DATA = (ArrayList<GraphData>) intent.getSerializableExtra("data");
	        
	        // レイアウト作成
	        LinearLayout layout=new LinearLayout(this);
	        setContentView(layout);
	        
	        // グラフ作成
	        GraphicalView graphView =TimeChartView();
	        
	        // レイアウトにグラフを配置
	        layout.addView(graphView);
	    }

	    /*
	     * 横軸が時刻のタイムチャートを作成する
	     */
	    private GraphicalView TimeChartView() 
	    {
	        // (1)グラフデータの準備

	        Date[] xDateValue = new Date[GRAPH_DATA.size()];
	        int[] yDoubleValue1 = new int[GRAPH_DATA.size()];;
	        int i=0;
	        int maxCost = 0;
	        Date dateMin = new Date(Long.MAX_VALUE);
			Date dateMax = new Date(Long.MIN_VALUE);
	        for (GraphData g : GRAPH_DATA) 
	        {
	            xDateValue[i] = g.getX();
	            yDoubleValue1[i] = g.getY();
	            if(maxCost<=yDoubleValue1[i])maxCost=yDoubleValue1[i];
	            if(dateMin.after(xDateValue[i]))dateMin = xDateValue[i];
	            if(dateMax.before(xDateValue[i]))dateMax = xDateValue[i];
	            i++;
	        }
	        
	        // (2) グラフのタイトル、X軸Y軸ラベル、色等の設定
	        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	        renderer.setChartTitle("グラフ");     // グラフタイトル
	        renderer.setXTitle("日付");                     // X軸タイトル
	        renderer.setYTitle("金額");                 // Y軸タイトル
	        renderer.setAxisTitleTextSize(50);              // 
	        renderer.setChartTitleTextSize(50);             //
	        renderer.setLabelsTextSize(50);
	        renderer.setAxisTitleTextSize(50);
	        renderer.setLegendTextSize(50);                 // 凡例　テキストサイズ
	        renderer.setPointSize(10f);                      // ポイントマーカーサイズ
	        renderer.setXAxisMin(dateMin.getTime());  // X軸最小値
	        renderer.setXAxisMax(dateMax.getTime());    // X軸最大値
	        renderer.setYAxisMin(0.0);                     // Y軸最小値
	        renderer.setYAxisMax(maxCost);                    // Y軸最大値
	        renderer.setXLabels(20);
			renderer.setYLabels(20);
	        renderer.setXLabelsAlign(Align.CENTER);         // X軸ラベル配置位置
	        renderer.setYLabelsAlign(Align.RIGHT);          // Y軸ラベル配置位置
	        renderer.setAxesColor(Color.LTGRAY);            // X軸、Y軸カラー
	        renderer.setLabelsColor(Color.parseColor("#191970"));          // ラベルカラー
	        
	        // X軸のラベルをカスタムラベルにする
	        renderer.setXLabels(0);                         // X軸ラベルのおおよその数を0にする
	        makeCustomXLabel(renderer,dateMin,dateMax,1); // 3時間毎にラベル表示
	        // カスタムXラベルに対応するグリッド線を引くかどうか
	        renderer.setShowCustomTextGrid(true);

	        renderer.setYLabels(5);                         // Y軸ラベルのおおよその数
	        renderer.setLabelsTextSize(15);                 // ラベルサイズ
	        // グリッド表示
	        renderer.setShowGrid(true);
	        // グリッド色
	        renderer.setGridColor(Color.BLACK); // グリッドカラーaqua
	        // グラフ描画領域マージン top, left, bottom, right
	        renderer.setMargins(new int[] { 50, 80, 15, 50 });  // 
	        renderer.setMarginsColor(Color.parseColor("#F0F8FF"));

	        // (3) データ系列の色、マーク等の設定
	        XYSeriesRenderer r1 = new XYSeriesRenderer();
	        r1.setColor(Color.BLUE);
	        r1.setPointStyle(PointStyle.CIRCLE);
	        r1.setFillPoints(true);
	        renderer.addSeriesRenderer(r1);
	        //XYSeriesRenderer r2 = new XYSeriesRenderer();
	        //r2.setColor(Color.MAGENTA);
	        //r2.setPointStyle(PointStyle.DIAMOND);
	        //r2.setFillPoints(true);
	        //renderer.addSeriesRenderer(r2);

	        // (4) データ系列　データの設定
	        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	        TimeSeries series1 = new TimeSeries("金額");        // データ系列凡例
	        //TimeSeries series2 = new TimeSeries("鹿角市の気温");        // データ系列凡例
	        for (int j = 0; j < GRAPH_DATA.size(); j++) 
	        {
	            series1.add(xDateValue[j], yDoubleValue1[j]);
	            //series2.add(xDateValue[i], yDoubleValue2[i]);
	        }
	        dataset.addSeries(series1);
	        //dataset.addSeries(series2);
	        
	        // (5)タイムチャートグラフの作成(折れ線と棒グラフの複合)
	        renderer.setBarSpacing(0.2);                        // バー間の間隔
	        //String[] types = new String[] { BarChart.TYPE , LineChart.TYPE };
	        //GraphicalView mChartView = ChartFactory.getCombinedXYChartView(this, dataset, renderer, types);
	        GraphicalView mChartView = ChartFactory.getBarChartView(this,dataset,renderer,BarChart.Type.DEFAULT);
	        //GraphicalView mChartView=ChartFactory.getTimeChartView(this, dataset,  renderer, "dd");
	        
	        return mChartView;
	    }
	    
	    /*
	     * X軸のカスタムラベル作成
	     */
	    private void makeCustomXLabel(XYMultipleSeriesRenderer renderer,Date xMin,Date xMax,int xInterval)
	    {
	        SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd");
	        SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
	        Date CurrDate;

	        Calendar cal = Calendar.getInstance();
	        cal.setTime(xMin);
	        int month=cal.get(Calendar.MONTH);
	        
	        while(true) 
	        {
	            CurrDate=cal.getTime();
	            
	            if(CurrDate.after(xMax))
	            {
	                break;
	            }
	            String Xlabel="";

	            
	            if(month != cal.get(Calendar.MONTH))
	            {
	                Xlabel=sdf1.format(CurrDate) ;
	                renderer.addXTextLabel(CurrDate.getTime(),Xlabel);
	                month = cal.get(Calendar.MONTH);
	            }
	            else
	            {
	                Xlabel=sdf2.format(CurrDate) ;
	                renderer.addXTextLabel(CurrDate.getTime(),Xlabel);
	            }
	            
	            cal.add(Calendar.DAY_OF_MONTH, 1);


	        }
	        
	    }
}
