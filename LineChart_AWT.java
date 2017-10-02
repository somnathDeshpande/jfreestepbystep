package com.org;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChart_AWT extends ApplicationFrame {

   public LineChart_AWT( String applicationTitle , String chartTitle, DefaultCategoryDataset dataset ) {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         "Days","Number of HD",dataset,
         //createDataset(),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      try {
		ChartUtilities.saveChartAsJPEG(new File("E:\\BTCH\\pie_Chart.jpeg"), lineChart, 900 , 750);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      /*ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 900 , 750 ) );
      setContentPane( chartPanel );*/
   }

   public DefaultCategoryDataset createDataset( ) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      dataset.addValue( 15 , " " , "1970" );
      dataset.addValue( 30 , " " , "1980" );
      dataset.addValue( 60 , " " ,  "1990" );
      dataset.addValue( 120 , " " , "2000" );
      dataset.addValue( 240 , " " , "2010" );
      dataset.addValue( 300 , " " , "2014" );
      return dataset;
   }
   
   public static void main( DefaultCategoryDataset dataset ) {
      LineChart_AWT chart = new LineChart_AWT(
         "Graph" ,
         "Numer of HD",dataset);

      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
   }
}
