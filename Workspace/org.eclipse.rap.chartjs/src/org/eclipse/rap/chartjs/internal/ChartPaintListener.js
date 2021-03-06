/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
//@ sourceURL=ChartPaintListener.js

function handleEvent( event ) {
  var widget = event.widget;
  if( event.gc.measureText ) { // exclude old IE
    var type = widget.getData( "chartType" );
    var data = widget.getData( "chartData" );
    var options = widget.getData( "chartOptions" );
    var chart = widget.getData( "chart" );
    if( chart ) {
      chart.stop();
      chart.destroy();
      chart = null;
    }
    if( type ) {
      event.gc.canvas.style.zIndex = 10000; // small hack to make sure chart gets the mouse events
      event.gc.canvas.style.position = "relative";
      chart = new Chart( event.gc )[ type ]( data, options );
      options.animation = false; // no animation on refresh
    }
    widget.setData( "chart", chart );
  }
}
