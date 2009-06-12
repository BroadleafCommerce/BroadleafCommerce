/*
Copyright � 2006 Adobe Systems Incorporated

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.


THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/


function $() {
  var elements = new Array();

  for (var i = 0; i < arguments.length; i++) {
    var element = arguments[i];
    if (typeof element == 'string')
      element = document.getElementById(element);

    if (arguments.length == 1)
      return element;

    elements.push(element);
  }

  return elements;
}

var codes = {
	'get_slider': 'trace( flexApp.getSlider().getValue() )', 
	'set_check': '\n\
var currentCheckValue = flexApp.getCheck().getSelected();\n\
flexApp.getCheck().setSelected( ! currentCheckValue )\n\
	', 
	'invoke_as': 'flexApp.testFunc( "Hello, Actionscript World! Love, Javascript..." );', 
	'button_event_handler': '\n\
var callback = function() {\n\
	alert("Hello, Javascript! Love, Actionscript...");\n\
}\n\
flexApp.getButton().addEventListener("click", callback);\n\
	', 
	'slider_event_handler': '\n\
var callback = function(event) {\n\
	trace(event.getValue());\n\
}\n\
flexApp.getSlider().addEventListener("change", callback);\n\
	', 
	'create_datagrid': '\n\
var grid = FABridge.example.create("mx.controls.DataGrid");\n\
var col1 = FABridge.example.create("mx.controls.dataGridClasses.DataGridColumn");\n\
col1.setDataField("apples");\n\
var col2 = FABridge.example.create("mx.controls.dataGridClasses.DataGridColumn");\n\
col2.setDataField("oranges");\n\
grid.setColumns( [col1, col2] );\n\
grid.setWidth(300);\n\
grid.setDataProvider( [ { apples: 12, oranges: 32 }, { apples: 7, oranges: 47 }, { apples: 14, oranges:21 } ] );\n\
flexApp.getPanel().addChild(grid);\n\
grid.addEventListener("change", function(event) { trace("apples: " + event.getTarget().getSelectedItem().apples); } );\n\
	', 
	'make_rect': '\n\
var spr = FABridge.example.create("flash.display.Sprite");\n\
trace(spr);\n\
var g = spr.getGraphics();\n\
g.beginFill(0xFF0000);\n\
g.lineStyle(2,2);\n\
g.drawRect(-100,-100,200,200);\n\
g.endFill();\n\
flexApp.addChild(spr);\n\
spr.setX(300);\n\
spr.setY(300);\n\
	', 
	'make_chart': '\n\
var chart = FABridge.example.create("mx.charts.ColumnChart");\n\
\n\
chart.setName("chart");\n\
\n\
var s1 = FABridge.example.create("mx.charts.series.ColumnSeries");\n\
s1.setYField("apples");\n\
\n\
var s2 = FABridge.example.create("mx.charts.series.ColumnSeries");\n\
s2.setYField("oranges");\n\
\n\
chart.setSeries( [s1, s2] );\n\
chart.setWidth(300);\n\
chart.setHeight(200);\n\
\n\
flexApp.getPanel().addChild(chart);\n\
	', 
	'make_interpolate': '\n\
// make sure to \'create a chart\' first\n\
var chart = flexApp.getPanel().getChildByName("chart");\n\
var series = chart.getSeries();\n\
var effect = FABridge.example.create("mx.charts.effects.SeriesInterpolate");\n\
effect.setMinimumElementDuration(300);\n\
series[0].setStyle("hideDataEffect", null);\n\
series[0].setStyle("showDataEffect", effect);\n\
series[1].setStyle("hideDataEffect", null);\n\
series[1].setStyle("showDataEffect", effect);\n\
// now generate new data...\n\
	', 
	'make_zoom': '\n\
// make sure to \'create a chart\' first\n\
var chart = flexApp.getPanel().getChildByName("chart");\n\
var series = chart.getSeries();\n\
var effect = FABridge.example.create("mx.charts.effects.SeriesZoom");\n\
effect.setMinimumElementDuration(300);\n\
series[0].setStyle("hideDataEffect",effect);\n\
series[0].setStyle("showDataEffect",effect);\n\
series[1].setStyle("hideDataEffect",effect);\n\
series[1].setStyle("showDataEffect",effect);\n\
// now generate new data...			\n\
	', 
	'make_data': '\n\
var chart = flexApp.getPanel().getChildByName("chart");\n\
\n\
var dp = [];\n\
for(var i=0; i < 30; i++) {\n\
	dp.push( {apples: Math.random()*100, oranges: Math.random()*100} );\n\
}\n\
chart.setDataProvider(dp);\n\
	', 
	'make_spinner': '\n\
var spr = FABridge.example.create("flash.display.Sprite");\n\
trace(spr);\n\
var g = spr.getGraphics();\n\
g.beginFill(0xFF0000);\n\
g.lineStyle(2,2);\n\
g.drawRect(-100,-100,200,200);\n\
g.endFill();\n\
flexApp.addChild(spr);\n\
spr.setX(300);\n\
spr.setY(300);\n\
\n\
var speed = Math.random() * 13;\n\
flexApp.getStage().addEventListener("enterFrame", function(e) {\n\
	spr.setRotation(spr.getRotation() + speed);\n\
});\n\
	' 
};

function updateCode(event)
{
	var code;
	
	if(typeof(event) == "string") {
		code = event;
	}
	else
	{
		var elt = getEventTarget(event);
		code = elt['id'];
	}

	if (typeof codes[code] != "undefined") {
		newCode = codes[code];
	}
	$("expr").value = "var flexApp = FABridge.example.root();\n\n" + newCode;
}

function testEval() {
	var funcExpr = $("expr").value;
	eval(funcExpr);
}

function trace(msg) {
	$("output").value = msg.toString() + "\n" + $("output").value;	
}

function getEventTarget(e) {
	if (/Explorer/.test(navigator.appName))
		return e.srcElement;
	else
		return e.target;
}

function dumpit(e) {
	var out = "";
	for (var aProp in e)
		out += ("obj[" + aProp + "] = " + e[aProp]) + "\n";
	trace(out);
}
