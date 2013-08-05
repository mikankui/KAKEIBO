package com.housekeepbyline.graph;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

public class LineBarGraph extends Activity{
	   @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	        // x軸 時刻
	        String [] xStrValue={"2012/04/01 09:00","2012/04/01 10:00","2012/04/01 11:00",
	                          "2012/04/01 12:00","2012/04/01 13:00","2012/04/01 14:00",
	                          "2012/04/01 15:00","2012/04/01 16:00","2012/04/01 17:00",
	                          "2012/04/01 18:00","2012/04/01 19:00","2012/04/01 20:00",
	                          "2012/04/01 21:00","2012/04/01 22:00","2012/04/01 23:00", 
	                          "2012/04/02 00:00","2012/04/02 01:00","2012/04/02 02:00",
	                          "2012/04/02 03:00","2012/04/02 04:00","2012/04/02 05:00" 
	                         };
	        // y軸 気温 仙台市
	        double[] yDoubleValue1={-1.0,1.0,3.0,
	                                8.0,12.0,10.0,
	                                9.0,5.0,2.0,
	                                2.0,3.0,1.0,
	                                1.0,0.0,1.0,
	                                -1.0,-2.0,-2.0,
	                                -1.0,1.0,2.0
	                               };
	        // y軸 気温 鹿角市
	        double[] yDoubleValue2={-2.0,2.0,4.0,
	                                12.0,10.0,08.0,
	                                7.0,4.0,3.0,
	                                1.0,-2.0,-1.0,
	                                -3.0,-4.0,-6.0,
	                                -5.0,-4.0,-3.0,
	                                -3.0,-2.0,1.0
	                               };
	        
	        // 日付を文字形式から Date型へ変換
	        int DataCount=xStrValue.length;
	        Date[] xDateValue = new Date[DataCount];
	        for (int i = 0; i < DataCount; i++) 
	        {
	            xDateValue[i] =String2date(xStrValue[i]);
	        }
	        
	        // (2) グラフのタイトル、X軸Y軸ラベル、色等の設定
	        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	        renderer.setChartTitle("4月1日の気温変化");     // グラフタイトル
	        renderer.setChartTitleTextSize(20);             //
	        renderer.setXTitle("時間");                     // X軸タイトル
	        renderer.setYTitle("気温[℃]");                 // Y軸タイトル
	        renderer.setAxisTitleTextSize(16);              // 
	        renderer.setLegendTextSize(15);                 // 凡例　テキストサイズ
	        renderer.setPointSize(5f);                      // ポイントマーカーサイズ
	        renderer.setXAxisMin(xDateValue[0].getTime());  // X軸最小値
	        renderer.setXAxisMax(xDateValue[xDateValue.length-1].getTime());    // X軸最大値
	        renderer.setYAxisMin(-5.0);                     // Y軸最小値
	        renderer.setYAxisMax(+15.0);                    // Y軸最大値
	        renderer.setXLabelsAlign(Align.CENTER);         // X軸ラベル配置位置
	        renderer.setYLabelsAlign(Align.RIGHT);          // Y軸ラベル配置位置
	        renderer.setAxesColor(Color.LTGRAY);            // X軸、Y軸カラー
	        renderer.setLabelsColor(Color.YELLOW);          // ラベルカラー
	        
	        // X軸のラベルをカスタムラベルにする
	        renderer.setXLabels(0);                         // X軸ラベルのおおよその数を0にする
	        makeCustomXLabel(renderer,xDateValue[0],xDateValue[DataCount-1],3); // 3時間毎にラベル表示
	        // カスタムXラベルに対応するグリッド線を引くかどうか
	        renderer.setShowCustomTextGrid(true);

	        renderer.setYLabels(5);                         // Y軸ラベルのおおよその数
	        renderer.setLabelsTextSize(15);                 // ラベルサイズ
	        // グリッド表示
	        renderer.setShowGrid(true);
	        // グリッド色
	        renderer.setGridColor(Color.parseColor("#00FFFF")); // グリッドカラーaqua
	        // グラフ描画領域マージン top, left, bottom, right
	        renderer.setMargins(new int[] { 30, 30, 15, 40 });  // 

	        // (3) データ系列の色、マーク等の設定
	        XYSeriesRenderer r1 = new XYSeriesRenderer();
	        r1.setColor(Color.GREEN);
	        r1.setPointStyle(PointStyle.CIRCLE);
	        r1.setFillPoints(true);
	        renderer.addSeriesRenderer(r1);
	        XYSeriesRenderer r2 = new XYSeriesRenderer();
	        r2.setColor(Color.MAGENTA);
	        r2.setPointStyle(PointStyle.DIAMOND);
	        r2.setFillPoints(true);
	        renderer.addSeriesRenderer(r2);

	        // (4) データ系列　データの設定
	        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	        TimeSeries series1 = new TimeSeries("仙台市の気温");        // データ系列凡例
	        TimeSeries series2 = new TimeSeries("鹿角市の気温");        // データ系列凡例
	        for (int i = 0; i < DataCount; i++) 
	        {
	            series1.add(xDateValue[i], yDoubleValue1[i]);
	            series2.add(xDateValue[i], yDoubleValue2[i]);
	        }
	        dataset.addSeries(series1);
	        dataset.addSeries(series2);
	        
	        // (5)タイムチャートグラフの作成(折れ線と棒グラフの複合)
	        renderer.setBarSpacing(0.5);                        // バー間の間隔
	        String[] types = new String[] { BarChart.TYPE , LineChart.TYPE };
	        GraphicalView mChartView = ChartFactory.getCombinedXYChartView(this, dataset, renderer, types);

	        //GraphicalView mChartView=ChartFactory.getTimeChartView(this, dataset,  renderer, "HH:mm");
	        
	        return mChartView;
	    }
	    
	    /*
	     * 日付時刻文字列を Date型に変換
	     */
	    private Date String2date(String strDate) 
	    {
	        Date dateDate=null;
	        // 日付文字列→date型変換フォーマットを指定して
	        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	    
	        try 
	        {
	            dateDate = sdf1.parse(strDate);
	        } 
	        catch (ParseException e) 
	        {
	            Log.e("日付変換エラー", "String2dateメソッドに引き渡された引数:" + strDate  );
	        } catch (java.text.ParseException e) {
	            Log.e("日付変換エラー", "String2dateメソッドに引き渡された引数:" + strDate  );
	        }
	        return dateDate;    
	    }
	    
	    /*
	     * X軸のカスタムラベル作成
	     */
	    private void makeCustomXLabel(XYMultipleSeriesRenderer renderer,Date xMin,Date xMax,int xInterval)
	    {
	        SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd HH:mm");
	        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
	        Date CurrDate;

	        Calendar cal = Calendar.getInstance();
	        cal.setTime(xMin);

	        while(true) 
	        {
	            CurrDate=cal.getTime();
	            if(CurrDate.after(xMax))
	            {
	                break;
	            }
	            String Xlabel="";

	            int hour =cal.get(Calendar.HOUR_OF_DAY); // 現在の時間を取得
	            if(hour == 0)
	            {
	                Xlabel=sdf1.format(CurrDate) ;
	                renderer.addXTextLabel(CurrDate.getTime(),Xlabel);
	            }
	            else if(hour % xInterval ==0)
	            {
	                Xlabel=sdf2.format(CurrDate) ;
	                renderer.addXTextLabel(CurrDate.getTime(),Xlabel);
	            }
	            
	            cal.add(Calendar.HOUR, 1);


	        }
	        
	    }
}
