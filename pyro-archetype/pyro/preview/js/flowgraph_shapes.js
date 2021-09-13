joint.shapes.flowgraph = {};

joint.shapes.flowgraph.End = joint.shapes.pyro.ToolElement.extend({
	
	    markup: '<g class="rotatable"><g class="scalable"><ellipse class="pyrox0tag" /><ellipse class="pyro0x0tag" /></g></g>',
	    defaults: _.defaultsDeep({
	
	        type: 'flowgraph.End',
	        size: {
	            width: 36,
	            height: 36
	        },
	        attrs: {
	            '.': {
	                magnet: 'passive'
	            },
	            hasInformation:true,
	            editLabel:false,
	            disableResize:false,
	            disableRemove:false,
	            disableSelect:false,
	            disableEdge:true,
	            'ellipse.pyrox0tag':{
	            	rx:18,
	            	ry:18,
	            	height: 36,
	            	width: 36,
	            	fill: 'rgb(255,255,255)'
	            	,
	            	stroke: 'rgb(164,29,29)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':2
	            	,
	            }
	            ,
	            'ellipse.pyro0x0tag':{
	            	'ref':'ellipse.pyrox0tag',
	            	rx:12,
	            	ry:12,
	            	refHeight: '48%',
	            	refWidth: '48%',
	            	fill: 'rgb(255,255,255)'
	            	,
	            	stroke: 'rgb(164,29,29)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':2
	            	,
	            	'text-anchor': 'middle',
	            	'x-alignment': 'middle',
	            	dy:'-0.5em',
	            	'ref-x': "50.0%",
	            	'ref-y': "50.0%"
	            }
	        }
	    }, joint.shapes.pyro.ToolElement.prototype.defaults)
	});

joint.shapes.flowgraph.Swimlane = joint.shapes.pyro.ToolElement.extend({
	
	    markup: '<g class="rotatable"><g class="scalable"><rect class="pyrox0tag" /></g><text class="pyro0x0tag"/></g>',
	    defaults: _.defaultsDeep({
	
	        type: 'flowgraph.Swimlane',
	        size: {
	            width: 400,
	            height: 100
	        },
	        attrs: {
	            '.': {
	                magnet: 'passive'
	            },
	            hasInformation:true,
	            editLabel:false,
	            disableResize:false,
	            disableRemove:false,
	            disableSelect:false,
	            disableEdge:true,
	            'rect.pyrox0tag':{
	            	height: 100,
	            	width: 400,
	            	fill: 'rgb(255,236,202)'
	            	,
	            	stroke: 'rgb(0,0,0)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':1
	            	,
	            }
	            ,
	            'text.pyro0x0tag':{
	            	'ref':'rect.pyrox0tag',
	            	'text':'',
	            	height: 40,
	            	width: 40,
	            	fill: 'rgb(0,0,0)'
	            	,
	            	stroke: 'none',
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':1
	            	,
	            	'x':10,
	            	'y':10
	            }
	        }
	    }, joint.shapes.pyro.ToolElement.prototype.defaults)
	});

joint.shapes.flowgraph.SubFlowGraph = joint.shapes.pyro.ToolElement.extend({
	
	    markup: '<g class="rotatable"><g class="scalable"><rect class="pyrox0tag" /></g><text class="pyro0x0tag"/></g>',
	    defaults: _.defaultsDeep({
	
	        type: 'flowgraph.SubFlowGraph',
	        size: {
	            width: 96,
	            height: 32
	        },
	        attrs: {
	            '.': {
	                magnet: 'passive'
	            },
	            hasInformation:true,
	            editLabel:false,
	            disableResize:false,
	            disableRemove:false,
	            disableSelect:false,
	            disableEdge:false,
	            'rect.pyrox0tag':{
	            	rx:8,
	            	ry:8,
	            	height: 32,
	            	width: 96,
	            	fill: 'rgb(101,175,95)'
	            	,
	            	stroke: 'rgb(0,0,0)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':2
	            	,
	            	'x':0,
	            	'y':0
	            }
	            ,
	            'text.pyro0x0tag':{
	            	'ref':'rect.pyrox0tag',
	            	'text':'',
	            	height: 40,
	            	width: 40,
	            	fill: 'rgb(0,0,0)'
	            	,
	            	stroke: 'none',
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':1
	            	,
	            	'text-anchor': 'middle',
	            	'x-alignment': 'middle',
	            	'y-alignment': 'middle',
	            	'ref-x': "50.0%",
	            	'ref-y': "50.0%"
	            }
	        }
	    }, joint.shapes.pyro.ToolElement.prototype.defaults)
	});

joint.shapes.flowgraph.Start = joint.shapes.pyro.ToolElement.extend({
	
	    markup: '<g class="rotatable"><g class="scalable"><ellipse class="pyrox0tag" /></g></g>',
	    defaults: _.defaultsDeep({
	
	        type: 'flowgraph.Start',
	        size: {
	            width: 36,
	            height: 36
	        },
	        attrs: {
	            '.': {
	                magnet: 'passive'
	            },
	            hasInformation:true,
	            editLabel:false,
	            disableResize:false,
	            disableRemove:false,
	            disableSelect:false,
	            disableEdge:false,
	            'ellipse.pyrox0tag':{
	            	rx:18,
	            	ry:18,
	            	height: 36,
	            	width: 36,
	            	fill: 'rgb(255,255,255)'
	            	,
	            	stroke: 'rgb(81,156,88)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':2
	            	,
	            }
	        }
	    }, joint.shapes.pyro.ToolElement.prototype.defaults)
	});

joint.shapes.flowgraph.Activity = joint.shapes.pyro.ToolElement.extend({
	
	    markup: '<g class="rotatable"><g class="scalable"><rect class="pyrox0tag" /></g><text class="pyro0x0tag"/></g>',
	    defaults: _.defaultsDeep({
	
	        type: 'flowgraph.Activity',
	        size: {
	            width: 96,
	            height: 32
	        },
	        attrs: {
	            '.': {
	                magnet: 'passive'
	            },
	            hasInformation:true,
	            editLabel:false,
	            disableResize:false,
	            disableRemove:false,
	            disableSelect:false,
	            disableEdge:false,
	            'rect.pyrox0tag':{
	            	rx:8,
	            	ry:8,
	            	height: 32,
	            	width: 96,
	            	fill: 'rgb(144,207,238)'
	            	,
	            	stroke: 'rgb(0,0,0)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':2
	            	,
	            	'x':0,
	            	'y':0
	            }
	            ,
	            'text.pyro0x0tag':{
	            	'ref':'rect.pyrox0tag',
	            	'text':'',
	            	height: 40,
	            	width: 40,
	            	fill: 'rgb(0,0,0)'
	            	,
	            	stroke: 'none',
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':1
	            	,
	            	'text-anchor': 'middle',
	            	'x-alignment': 'middle',
	            	'y-alignment': 'middle',
	            	'ref-x': "50.0%",
	            	'ref-y': "50.0%"
	            }
	        }
	    }, joint.shapes.pyro.ToolElement.prototype.defaults)
	});

joint.shapes.flowgraph.ExternalActivity = joint.shapes.pyro.ToolElement.extend({
	
	    markup: '<g class="rotatable"><g class="scalable"><rect class="pyrox0tag" /></g><text class="pyro0x0tag"/></g>',
	    defaults: _.defaultsDeep({
	
	        type: 'flowgraph.ExternalActivity',
	        size: {
	            width: 96,
	            height: 32
	        },
	        attrs: {
	            '.': {
	                magnet: 'passive'
	            },
	            hasInformation:true,
	            editLabel:false,
	            disableResize:false,
	            disableRemove:false,
	            disableSelect:false,
	            disableEdge:false,
	            'rect.pyrox0tag':{
	            	rx:8,
	            	ry:8,
	            	height: 32,
	            	width: 96,
	            	fill: 'rgb(101,175,95)'
	            	,
	            	stroke: 'rgb(0,0,0)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':2
	            	,
	            	'x':0,
	            	'y':0
	            }
	            ,
	            'text.pyro0x0tag':{
	            	'ref':'rect.pyrox0tag',
	            	'text':'',
	            	height: 40,
	            	width: 40,
	            	fill: 'rgb(0,0,0)'
	            	,
	            	stroke: 'none',
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':1
	            	,
	            	'text-anchor': 'middle',
	            	'x-alignment': 'middle',
	            	'y-alignment': 'middle',
	            	'ref-x': "50.0%",
	            	'ref-y': "50.0%"
	            }
	        }
	    }, joint.shapes.pyro.ToolElement.prototype.defaults)
	});

joint.shapes.flowgraph.Transition = joint.dia.Link.extend({
		markup: '<path class="connection"/>'+
		'<path class="marker-source"/>'+
		'<path class="marker-target"/>'+
		'<path class="connection-wrap"/>'+
		'<g class="labels"></g>'+
		'<g class="marker-vertices"/>'+
		'<g class="marker-arrowheads"/>'+
		'<g class="link-tools" />',
	    defaults: {
	        type: 'flowgraph.Transition',
	        attrs: {
	        	hasInformation:true,
	        	disableResize:false,
	        	disableRemove:false,
	        	disableSelect:false,
	        	disableEdge:false,
	            '.connection': {
	                fill: 'rgb(255,255,255)'
	                ,
	                stroke: 'rgb(0,0,0)'
	                ,
	                'font-family':'Helvetica',
	                'font-size':'12px',
	                'font-weight':'normal',
	                'font-style':'normal'
	                ,
	                'stroke-width':1
	            },
	            '.marker-target': { 
	            	d: 'M 0,0 L 5,-5 M 0,0 L 5,5',
	            	fill: 'rgb(144,207,238)'
	            	,
	            	stroke: 'rgb(0,0,0)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':2
	            	,
	            	markerWidth:4,
	            	markerHeight:4
	            },
	            '.marker-source': { 
	            	fill: '#000', stroke: '#000'
	            }
	        },
	        labels:[
	        ]
	    }
	});

joint.shapes.flowgraph.LabeledTransition = joint.dia.Link.extend({
		markup: '<path class="connection"/>'+
		'<path class="marker-source"/>'+
		'<path class="marker-target"/>'+
		'<path class="connection-wrap"/>'+
		'<g class="labels"></g>'+
		'<g class="marker-vertices"/>'+
		'<g class="marker-arrowheads"/>'+
		'<g class="link-tools" />',
	    defaults: {
	        type: 'flowgraph.LabeledTransition',
	        attrs: {
	        	hasInformation:true,
	        	disableResize:false,
	        	disableRemove:false,
	        	disableSelect:false,
	        	disableEdge:false,
	            '.connection': {
	                fill: 'rgb(144,207,238)'
	                ,
	                stroke: 'rgb(0,0,0)'
	                ,
	                'font-family':'Helvetica',
	                'font-size':'12px',
	                'font-weight':'normal',
	                'font-style':'normal'
	                ,
	                'stroke-width':2
	            },
	            '.marker-target': { 
	            	d: 'M 0,0 L 5,-5 M 0,0 L 5,5',
	            	fill: 'rgb(144,207,238)'
	            	,
	            	stroke: 'rgb(0,0,0)'
	            	,
	            	'font-family':'Helvetica',
	            	'font-size':'12px',
	            	'font-weight':'normal',
	            	'font-style':'normal'
	            	,
	            	'stroke-width':2
	            	,
	            	markerWidth:4,
	            	markerHeight:4
	            },
	            '.marker-source': { 
	            	fill: '#000', stroke: '#000'
	            }
	        },
	        labels:[
	        	{
	        		position: 0.3,
	        		markup:'<text class="pyro0link"/>',
	        	    attrs: {
	        			'text.pyro0link': {
	        				text:'%s',
	        				height: 40,
	        				width: 40,
	        				fill: 'rgb(0,0,0)'
	        				,
	        				stroke: 'none',
	        				'font-family':'Helvetica',
	        				'font-size':'12px',
	        				'font-weight':'normal',
	        				'font-style':'normal'
	        				,
	        				'stroke-width':1
	        				,
	        			}
	        	     }
	        	}
	        ]
	    }
	});

