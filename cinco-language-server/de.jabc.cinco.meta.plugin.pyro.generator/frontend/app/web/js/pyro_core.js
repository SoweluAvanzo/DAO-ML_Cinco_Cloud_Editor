/*
 -----
 Global Scope Variables
 -----
 */
// stores the link which is used for creation
var $temp_link = null;
// stores the link if multiple links can be created
var $temp_link_multi = null;
// stores if the possible edge menu is displayed
var $edge_menu_shown = false;
// stores if the context menu is displayed
var $context_menu_shown = false;
// stores the context-menu for options to pick
var $option_picker;
// stores if the possible edge menu is displayed
var $edge_to_create = null;
// function referece to remove_node_cascade method
var $delete_node_fun = null;

var $node_resize_last_direction = null;

var $highlight_valid_containers_fun = null;

var $rebuild_map_fun = null;

var $property_persist_fun = null;

var $cursor_manager_resizestart = false;

joint.shapes.pyro = {};
joint.shapes.pyro.PyroLink = joint.dia.Link.extend({

    markup: '<path class="connection"/>'+
    '<path class="marker-source"/>'+
    '<path class="marker-target"/>'+
    '<path class="connection-wrap"/>'+
    '<g class="labels" />'+
    '<g class="marker-vertices"/>'+
    '<g class="marker-arrowheads"/>'+
    '<g class="link-tools" />',

    defaults: {
        type: 'pyro.PyroLink',
        attrs: {
            '.connection': {
                'stroke-width': 2,
                'stroke': '#000'
            }
        }
    }
});


joint.shapes.pyro.PyroCursor = joint.shapes.basic.Generic.extend({
	markup: ''
	    + '<g class="rotatable">'
		+ '  <g class="scalable">'
		+ '    <circle class="pyro-cursor"/>'
		+ '  </g>'
		+ '  <g class="scalable">'
		+ '    <rect class="pyro-cursor-tooltip" />'
		+ '    <text>username</text>'
		+ '  </g>'
		+ '</g>',
	defaults: joint.util.deepSupplement({
		type: 'pyro.PyroCursor',
	    attrs: {
	        '.pyro-cursor': { 
	        	strokeWidth: 0, 
        		r: 14,
        		fill: 'black' 
        	},
        	'.pyro-cursor-tooltip': {
        	   width: 68,
        	   height: 18,
        	   fill: 'black',
        	   strokeWidth: 0,
        	   x: 10,
	           y: -24
        	},
	        text: {
	          fill: 'white',
	          dx: 14,
	          dy: -10
	        }
	    },
	    size: { width: 14, height: 14 }
	}, joint.shapes.basic.Generic.prototype.defaults)
});	


joint.shapes.pyro.ToolElement = joint.shapes.basic.Generic.extend({

    toolMarkup: ['<g class="element-tools">',
    '<rect class="border" stroke="#000" />',
    '<rect style="cursor:nw-resize" class="resizer t l" />',
     '<rect style="cursor:ns-resize" class="resizer t m" />',
    '<rect style="cursor:ne-resize" class="resizer t r" />',
     '<rect style="cursor:ew-resize" class="resizer c l" />',
     '<rect style="cursor:ew-resize" class="resizer c r" />',
    '<rect style="cursor:sw-resize" class="resizer b l" />',
    '<rect style="cursor:ns-resize" class="resizer b m" />',
    '<rect style="cursor:se-resize" class="resizer b r" />',
        '<g class="element-tool-remove" style="cursor:pointer" ><circle class="remove-circle" fill="red" r="11"/>',
        '<path transform="scale(.8) translate(-16, -16)" d="M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z"/>',
        '<title>Remove this element from the model</title>',
        '</g>',
        '<g class="element-tool-edge" style="cursor:pointer"><circle class="edge-circle" fill="#000" r="11"/>',
        '<path transform="scale(1.5) translate(-3, -3)" d="M0,0 L0,6 L9,3z"/>',
        '<title>Draw an edge to another node</title>',
        '</g>',
        '<g class="element-tool-information" style="cursor:pointer"><circle class="information-circle" fill="#007bff" r="11"/>',
        '<path transform="scale(0.3) translate(-27,-27)" d="M 24.3320 13.2461 C 24.3320 15.1211 25.8320 16.6211 27.7070 16.6211 C 29.6055 16.6211 31.0820 15.1211 31.0586 13.2461 C 31.0586 11.3477 29.6055 9.8477 27.7070 9.8477 C 25.8320 9.8477 24.3320 11.3477 24.3320 13.2461 Z M 18.5195 44.2305 C 18.5195 45.3789 19.3399 46.1523 20.5820 46.1523 L 35.4179 46.1523 C 36.6601 46.1523 37.4805 45.3789 37.4805 44.2305 C 37.4805 43.1055 36.6601 42.3320 35.4179 42.3320 L 30.7070 42.3320 L 30.7070 24.4492 C 30.7070 23.1836 29.8867 22.3399 28.6680 22.3399 L 21.2383 22.3399 C 20.0195 22.3399 19.1992 23.0899 19.1992 24.2148 C 19.1992 25.3867 20.0195 26.1602 21.2383 26.1602 L 26.3711 26.1602 L 26.3711 42.3320 L 20.5820 42.3320 C 19.3399 42.3320 18.5195 43.1055 18.5195 44.2305 Z"/>',
        '</g>',
        '</g>'].join(''),

    checkMarkup: ['<g class="element-checks">',
    '<rect class="check-border" stroke="#000" />',
    '</g>'].join(''),
    
    validMarkup: ['<g class="element-valid">',
    '<rect class="valid-border" stroke="#000" />',
    '</g>'].join(''),
    
    labelMarkup: [
	  	'<foreignObject class="html-element" >',
	    '<body class="body-direct-input" style="margin:0;width:100%;height:100%;display: flex;align-items: center" xmlns="http://www.w3.org/1999/xhtml">',
	    '<textarea class="label-input" rows="1" style="border: none; outline: none; margin: 2px;"></textarea>',
	    '</body>',
	    '</foreignObject> '
	  ].join(''),
	  
	popupMarkup: `<foreignObject class="html-element">
        <body style="margin:0;width:100%;height:100%;display:flex;box-sizing:border-box" xmlns="http://www.w3.org/1999/xhtml">
          <div 
            style="background: #5e6366;
                   margin: 10px;
                   border-radius: 4px;
                   box-shadow: 0 2px 5px rgba(0,0,0,.5);
                   width: 100%;                
                   font-family: sans-serif;
                   font-size: 13px;
                   color: #fff;
                   z-index:999999;
                   position: relative;"
          >
            <div 
              class="info-popup-arrow"
              style="position:absolute;
              left: -10px;
              width: 0; 
              height: 0; 
              border-top: 10px solid transparent;
              border-bottom: 10px solid transparent; 
              border-right:10px solid #5e6366;"
            >  
            </div>
            <div 
              class="info-popup-text" 
              style="padding: 10px; overflow: auto"
            >
            </div>
          </div>
        </body>
      </foreignObject>
    `,
	



    defaults: joint.util.deepSupplement({
        attrs: {
        	'.html-element': {
		        'text-anchor': 'middle',
		       	'ref-x': 0,
		      	'refX2': 0,
		        'refY2': 0,
		        'ref-y': 0.5,
		        'y-alignment': 'middle',
		        'refWidth': 1,
		        'refWidth2': '100%',
		        'refHeight2': '100%',
		        'refHeight': 1,
		        'stroke': '#000'
		    },
        	'.resizer': { 
            	'height':6,
                'width':6,
                'text-anchor': 'middle',
                'y-alignment': 'middle',
                'fill':'#000',
                'stroke':'#000',
                'z':999
            },
            '.resizer.t': { 
            	 'refY2':-10, 'ref-y': 0
            },
            '.resizer.c': { 
            	 'refY2':0, 'ref-y': '50%'
            },
            '.resizer.b': { 
            	 'refY2':10, 'ref-y': '100%'
            },
            '.resizer.l': { 
            	 'refX2':-13, 'ref-x': 0
            },
            '.resizer.m': { 
            	 'refX2':0, 'ref-x': '50%'
            },
            '.resizer.r': { 
            	 'refX2':7, 'ref-x': '100%'
            },
            
            
            '.border': { 
                  'text-anchor': 'middle',
                  'ref-x': 0,
                  'refX2':-10,
                  'refY2':0,
                  'ref-y': 0.5,
                  'y-alignment': 'middle',
                  'refWidth':'100%',
                  'refWidth2':'20',
                  'refHeight2':'100%',
                  'refHeight':20,
                  'stroke':'#000',
                  'fill-opacity':0,
                  'stroke-dasharray':"5,5",
                  'z':999
            },
            '.check-border': { 
                 'text-anchor': 'middle',
                  'ref-x': 0,
                  'refX2':-1,
                  'refY2':0,
                  'ref-y': 0.5,
                  'y-alignment': 'middle',
                  'refWidth':'100%',
                  'refWidth2':'1',
                  'refHeight2':'100%',
                  'refHeight':1,
                  'stroke':'red',
                  'fill-opacity':0.1,
                  'fill':'red',
                  'z':999
            },
            '.valid-border': { 
                  'text-anchor': 'middle',
                  'ref-x': 0,
                  'refX2':-10,
                  'refY2':0,
                  'ref-y': 0.5,
                  'y-alignment': 'middle',
                  'refWidth':'100%',
                  'refWidth2':'20',
                  'refHeight2':'100%',
                  'refHeight':20,
                  'stroke':'green',
                  'fill-opacity':0.1,
                  'fill':'green',
                  'z':999
            },
            '.element-tool-edge': { 
                'text-anchor': 'middle',
                'ref-x': '100%',
                'refX2':30,
                'refY2':20,
                'ref-y': 0,
                'y-alignment': 'middle',
                'z':999
            },
            '.element-tool-remove': { 
                'text-anchor': 'middle',
                'ref-x': '100%',
                'refX2':30,
                'refY2':50,
                'ref-y': 0,
                'y-alignment': 'middle',
                'z':999
            },
            
            '.element-tool-information': { 
                'text-anchor': 'middle',
                'ref-x': '100%',
                'refX2':30,
                'refY2':-10,
                'ref-y': 0,
                'y-alignment': 'middle',
                'z':999
            },

        },
    }, joint.shapes.basic.Generic.prototype.defaults)

});

/*
 -----
 Utils
 -----
 */

/**
 * Downloads the svg with the given name, present in the tag found by id
 * @param doc_element_id
 * @param filename
 */
function download_svg(filename){
	downloadPyroCanvas(filename,"svg");
}

function download_png(filename){
	downloadPyroCanvas(filename,"png");
}

/**
 * Resizes the paper to fit the content.
 * @param paper
 */
function fitContent(paper,map) {
    //paper.fitToContent(5,5,{ top: 200, right: 200, bottom: 200, left: 200 },{minWidth:1000,minHeight:1000});
    if (map != null) {
      map.scaleContentToFit();
      if($rebuild_map_fun !== null) {
    	$rebuild_map_fun();
      }
    }
}

/**
 * Returns a number which is always greater or equals 1
 * @param i
 * @returns {number}
 */
function geq1(i) {
    return i<1?1:i
}

function getOutgoing(node,graph) {
    return graph.getCells()
        .filter(function (element) {
            return element.isLink();
        })
        .filter(function (link) {
            return link.attributes.source.id==node.attributes.id&&link.attributes.type!=='pyro.PyroLink';
        });
}

function getIncoming(node,graph) {
    return graph.getCells()
        .filter(function (element) {
            return element.isLink();
        })
        .filter(function (link) {
            return link.attributes.target.id==node.attributes.id&&link.attributes.type!=='pyro.PyroLink';
        });
}

function filterEdgesByType(edges,type) {
    return edges
        .filter(function (element) {
            return element.attributes.type == type;
        });
}

function getContainedByType(target,type,graph) {
    if(target==null) {
        return graph.getCells().filter(function (element) {
            return element.attributes.parent==null && element.attributes.type===type;
        });
    }
    return graph.getCells().filter(function (element) {
        return element.attributes.parent===target.model.attributes.id && element.attributes.type===type;
    });
}

/**
 *
 * @param x
 * @param y
 * @param paper
 * @returns {*}
 */
function getRelativeScreenPosition(x,y,paper) {
    return paper.clientToLocalPoint({x:x,y:y});
}

/**
 *
 * @param x
 * @param y
 * @param paper
 * @param paper
 * @returns {*}
 */
function getPaperToScreenPosition(x,y,paper) {
    return paper.localToClientPoint({x:x,y:y});
}

/**
 *
 * @param graph
 * @param id
 * @returns {*}
 */
function findElementById(id,graph) {
    var results = graph.getCells().filter(function (element) {
        return element.attributes.attrs.id===id;
    });
    if(results.length<1){
        return null;
    }
    return results[0];
}

function zoom_paper(paper,evt,x,y,delta) {
  var scaleFactor = paper.scale().sx + (0.01*delta);
  if(scaleFactor > 0.25 && scaleFactor < 1.5) {
  	  evt.preventDefault();
      var pre = getRelativeScreenPosition(evt.clientX,evt.clientY,paper);
      paper.scale(scaleFactor,scaleFactor);
      var post = getRelativeScreenPosition(evt.clientX,evt.clientY,paper);
      var tx = Math.round(post.x - pre.x);
      var ty = Math.round(post.y - pre.y);
      paper.translate(
          paper.translate().tx + Math.round(tx*scaleFactor),
          paper.translate().ty + Math.round(ty*scaleFactor)
        );
    }
}

function jump_to_element(id,graph,paper,functs) {
	var elem = findElementById(id,graph);
	var cell = paper.findViewByModel(elem);
	//translate to position
	var parent = paper.$el.parent().parent().parent().parent();
	var cX = $(parent).offset().left + ($(parent).width()/2);
	var cY = $(parent).offset().top + ($(parent).height()/2);
	var target = getRelativeScreenPosition(cX,cY,paper);
	paper.translate(
		paper.translate().tx + Math.round(target.x -cell.model.attributes.position.x * paper.scale().sx),
		paper.translate().ty + Math.round(target.y -cell.model.attributes.position.y * paper.scale().sy)
	);
	update_selection(cell,paper,graph);
	functs.cb_element_selected(cell.model.attributes.attrs.id);
}

function enable_wysiwyg_palette(paperP,graphP,paper,graph,typename,cb_create) {
	paperP.on('cell:pointerdown', function(cellView, e, x, y) {
	  $highlight_valid_containers_fun(-1,typename, false);
      $('body').append('<div id="flyPaper" style="position:fixed;z-index:9999;opacity:1;background-color:rgba(255, 255, 255, 0);pointer-event:none;"></div>');
      var sc = paper.scale();
      var flyGraph = new joint.dia.Graph(),
        flyPaper = new joint.dia.Paper({
          el: $('#flyPaper'),
          model: flyGraph,
          interactive: false,
          width: Math.round((cellView.model.attributes.size.width)*sc.sx)+6,
    			height: Math.round((cellView.model.attributes.size.height)*sc.sy)+6,
        }),
        flyShape = cellView.model.clone(),
       
        pos = cellView.model.position(),
        offset = {
          x:Math.round(cellView.model.attributes.size.width*sc.sx/2)+3,
          y:Math.round(cellView.model.attributes.size.height*sc.sy/2)+3
        };
			
      	flyShape.position(0, 0);
        
        flyPaper.scale(sc.sx,sc.sy);
      	flyGraph.addCell(flyShape);
        if(cellView.isElliptic()) {
          flyPaper.translate((offset.x),(offset.y));
        }
        
	      $("#flyPaper").offset({
	        left: e.pageX - offset.x,
	        top: e.pageY - offset.y
	      });
	      $('body').on('mousemove.fly', function(e) {
	        $("#flyPaper").offset({
	          left: e.pageX - offset.x,
	          top: e.pageY - offset.y
	        });
	      });
      $('body').on('mouseup.fly', function(e) {
      unhighlight_all_element_valid_target(paper,graph);
        var x = e.pageX,
          y = e.pageY,
          target = paper.$el.offset(),
          constraintTarget = paper.$el.parent().offset();

        // Dropped over paper ?
        if (x > constraintTarget.left && x < constraintTarget.left + paper.$el.parent().width() && y > constraintTarget.top && y < constraintTarget.top + paper.$el.parent().height()) {
          //var s = flyShape.clone();
          var rp = getRelativeScreenPosition(e.clientX,e.clientY,paper);
          var xC = x - target.left - offset.x;
          var yC = y - target.top - offset.y;
          //s.position(xC,yC);
          if(cellView.isElliptic()) {
            cb_create(rp.x,rp.y,typename);
          } else {
            cb_create(rp.x - offset.x,rp.y - offset.y,typename);
          }
          
          //graph.addCell(s);
          //graphP.addCell(cellView.model.clone());
        } else {
        	//move the node in initial position
          var m = graphP.getCell(cellView.model.id);
          m.position(0,0);
        }
        $('body').off('mousemove.fly').off('mouseup.fly');
        flyShape.remove();
        $('#flyPaper').remove();
        
      });
    });
}

/*
 -----
 Highlighter
 -----
 */

function select_element(cellView,graph) {
    highlight_cell(cellView,graph);
}

/**
 * Deselects all elements
 * @param paper
 * @param graph
 */
function deselect_all_elements(cellView,paper,graph) {
    graph.getCells().forEach(function(e){
        if(cellView===null||cellView.model.id !== e.id) {
            unhighlight_cell(paper.findViewByModel(e),graph);
        }
        
    });

}

/**
 * Sets a new selected elemenet and enables
 * the highlighting
 * @param cellView
 * @param paper
 * @param graph
 */
function update_selection(cellView,paper,graph) {
    deselect_all_elements(cellView,paper,graph);
    select_element(cellView,graph);
}

/**
 * Highlights a give node or edge
 * @param cellView
 * @param graph
 */
function highlight_cell(cellView,graph) {
    var model = graph.getCell(cellView.model.id);
    if (typeof model !== "undefined") {
        if(model.isLink()){
            cellView.highlight(null, {
                highlighter: {
                    name: 'addClass',
                    options: {
                        className: 'pyro_edge_highlight'
                    }
                }
            });
        } else {
            cellView.renderTools();
        }
    }
}

function highlight_cell_valid_target(cellView) {
    cellView.renderValid();
}

function unhighlight_all_element_valid_target(paper,graph) {
    graph.getCells().forEach(function(e){
        if(!e.isLink()) {
            var cellView = paper.findViewByModel(e);
            cellView.hideValid();
        }
    });
}

/**
 * Highlights a give node or edge based of a check level
 * @param cellView
 * @param graph
 */
function highlight_cell_check(cellView,level,results,graph) {
	var sC = 'rgba(255, 0, 0, 0.5)';
	if(level == 'warning') {
		sC = 'rgba(255, 153, 0, 0.5)';
	}
	if(level == 'info') {
		sC = 'rgba(0, 149, 255, 0.5)';
	}
    var model = graph.getCell(cellView.model.id);
    if(model.isLink()) {
    	cellView.highlight(null, {
    	    highlighter: {
    	        name: 'stroke',
    	        options: {
    	            padding: 10,
    	            rx: 5,
    	            ry: 5,
    	            attrs: {
    	                'stroke-width': 3,
    	                stroke: sC
    	            }
    	        }
    	    }
    	});
    } else {
        cellView.renderChecks(sC,results);
    }
    
}

function unhighlight_all_elements_check(paper,graph) {
    graph.getCells().forEach(function(e){
        var cellView = paper.findViewByModel(e);
        if(e.isLink()) {
            cellView.unhighlight(null, {
                highlighter: {
                    name: 'stroke',
                    options: {
                        padding: 10,
                        rx: 5,
                        ry: 5,
                        attrs: {
                            'stroke-width': 3,
                        }
                    }
                }
            });
        } else {
            cellView.hideChecks();
        }
        
    });

}

/**
 * Unhighlights a given node or edge
 * @param cellView
 * @param graph
 */
function unhighlight_cell(cellView,graph) {
    var model = graph.getCell(cellView.model.id);
    if(model.isLink()){
        cellView.unhighlight(null, {
            highlighter: {
                name: 'addClass',
                options: {
                    className: 'pyro_edge_highlight'
                }
            }
        });
    } else {
        cellView.hideTools();
        cellView.hideInput();
    }
}

/*
 -----
 Context Menu Control
 -----
 */

function showContextMenu(entryMap,x,y,id,cb_menu_clicked) {
    var btn_group = $('<div id="pyro_context_menu" class="btn-group-vertical btn-group-sm" style="position: absolute;z-index: 99999;top: '+y+'px;left: '+x+'px;"></div>');
    $('body').append(btn_group);
    $context_menu_shown = true;
    for(var menuEntry in entryMap) {
        var button = $('<button type="button" class="btn btn-secondary" data-menu-key="'+menuEntry+'">'+entryMap[menuEntry]+'</button>');

        btn_group.append(button);

        $(button).on('click',function () {
            var e = $(this).data('menu-key');
            remove_context_menu();
            cb_menu_clicked(e,id);
        });

    }
}

function init_menu_eventsystem(paper,graph,remove_node,disableRemove,disableResize,disableEdge) {
    $delete_node_fun = remove_node;
}



function update_edeg_routing(router,connector,graph) {
    graph.getLinks().forEach(function (link) {
        update_single_edge_routing(link,router,connector);
    });
}

function update_single_edge_routing(link,router,connector) {
    if (router) {
        link.set('router', { name: router });
    } else {
        link.unset('router');
    }
    link.set('connector', { name: connector });
}

function removeMenus() {
	remove_context_menu();
	remove_option_picker();
}

function remove_context_menu() {
    if($context_menu_shown) {
        $('#pyro_context_menu').remove();
        $context_menu_shown = false;
    }
}

function remove_option_picker() {
	if($option_picker != null) {
		$option_picker.remove();
		$option_picker = null;
        $edge_menu_shown = false;
	}
}

function createOptionPicker(absX, absY) {
	removeMenus();
	$option_picker = $('<div id="pyro_option_menu" class="btn-group-vertical btn-group-sm" style="position: absolute;z-index: 99999;top: '+absY+'px;left: '+absX+'px;"></div>');
	$('body').append($option_picker);
}

function addOptionPickerEntry(label, entry, action) {
    var button = $('<button type="button" class="btn">'+label+'</button>');
    $option_picker.append(button);
    $(button).on(
		'click',
		() => {
			action(entry);
	        removeMenus();
		}
	);
}

/**
 * absX, absY	The position of the optionsMenu
 * options		Contains entries which represent the pickable-options 
 * labelMapper	Receives an entry of 'options' and maps it to a string, representing the label of the pickable-option
 * action		The action, triggered when clicking the pickable-option. Receives an entry of options
 */
function create_options_menu(absX, absY, options, labelMapper, action) {
	$temp_link_multi = $temp_link;
    createOptionPicker(absX, absY);
    for(var key in options) {
        if (options.hasOwnProperty(key)) {
            var entry = options[key];
            var label = labelMapper(entry);
			addOptionPickerEntry(
                label,
				entry,
                action
            );
        }
    }
}

function create_edge_menu(target_view, possibleEdges, absX, absY, paper, graph) {
    $edge_menu_shown = true;
    $temp_link_multi = $temp_link;
	create_options_menu(
		absX, absY,
		possibleEdges,
		(e) => e.name,
		(e) => {
	        create_edge(target_view,e.type,paper,graph);
	    }
	);
}

function create_edge(target_view,possibleEdge,paper,graph,map) {
    var sourceId = $temp_link_multi.attributes.source.id;
    if(sourceId===target_view.attributes.id) {
        graph.addCell(possibleEdge);
        fitContent(paper,map);
    } else {
        graph.addCell(possibleEdge);
    }
    paper.findViewByModel(possibleEdge.getSourceElement()).render();
    paper.findViewByModel(possibleEdge).render();
    $temp_link_multi = null;
}

function init_edge_eventsystem(paper) {
    $(document).off('mousemove');
    $(document).mousemove(function (evt) {
        if($temp_link!==null){
            var rp = getRelativeScreenPosition(evt.clientX,evt.clientY,paper);
            var target = $temp_link.attributes.target;
            target['x']=rp.x;
            target['y']=rp.y;
            var temp_link_view = paper.findViewByModel($temp_link);
            temp_link_view.render();
            temp_link_view.pointermove(evt,rp.x,rp.y);
        }
    });

}

/*
 -----
 Node Control
 -----
 */

/**
 * Creates the constraint view for all nodes.
 * It is used to realize the resizing feature
 * @returns {Object|void|*}
 */
function constraint_element_view_palette() {
    return joint.dia.ElementView.extend({
        isElliptic: function() {
                var centerAnchorPoint = false;
                Object.getOwnPropertyNames(this.model.attributes.attrs).forEach(function (n) {
                    if(n.indexOf('.pyrox0tag') !== -1){
                        propName = n;
                        if(n.indexOf('ellipse') !== -1){
                            centerAnchorPoint = true;
                        }
                    }
                });
                return centerAnchorPoint;
        }
    });
}

/**
 * Creates the constraint view for all nodes.
 * It is used to realize the resizing feature
 * @returns {Object|void|*}
 */
function constraint_element_view(g,highlight_valid_targets,highlight_valid_containers) {
    var graph = g;
    return joint.dia.ElementView.extend({

        selectedBorder: false,
        toolsVisible: false,
        removeClicked: false,
        checksVisible: false,
        checkResults: [],
        validVisible: false,
        inputVisible: false,
        infoVisible: false,

        pointerup: function(evt, x, y) {
        	this.removeClicked = false;
            this.selectedBorder = false;
            unhighlight_all_element_valid_target(this.paper,g);
            if(this.model.attributes.attrs.disableSelect===true&&this.model.attributes.parent!=null) {
                var p = this.paper.getModelById(this.model.attributes.parent);
                joint.dia.ElementView.prototype.pointerup.apply(this.paper.findViewByModel(p), [evt, x, y]);
                return;
            }
            if(typeof evt.target.parentNode.getAttribute != 'undefined') {
	            var className = evt.target.parentNode.getAttribute('class');
	            if(className == 'element-tool-remove') {
	                $delete_node_fun(this.model);
	                return;
	            }
			}

            joint.dia.ElementView.prototype.pointerup.apply(this, [evt, x, y]);
        },
        
        autoResizeTextArea: function(defaultValue, textarea) {
		    const resizeTextArea = () => {
		      window.setTimeout(() => {
		        textarea.style.height = 'auto';
		        textarea.style.padding = '0';
		        textarea.style.height = `${textarea.scrollHeight}px`;
		      }, 0);
		    }
		
		    textarea.style.overflow = 'hidden';
		    textarea.style.resize = 'none';
		    textAreaKeydownHandler = textarea.addEventListener('keydown', resizeTextArea);
		    resizeTextArea(textarea);
		},
		
		closeInfoDialog: function() {
			if(this.infoVisible) {
				this.$el.find('foreignObject').remove();
				this.infoVisible = false;
			}
		},
		
		openInfoDialog: function(buttonWidth, buttonHeight, dialogHeight, dialogWidth) {
			if (this.infoVisible) {
				return;
			}
			this.infoVisible = true;
		
		    const el = this.el;
		
		    let foreignObject = el.querySelector('foreignObject');
		    if (foreignObject != null) { // do not append the dialog more than once
		      return ;
		    } else {
		      const markup = this.model.popupMarkup || this.model.get('popupMarkup');
		      const nodes = V(markup);
		      V(el).append(nodes);
		      foreignObject = el.querySelector('foreignObject');
		    }
		
		    // place dialog right of the info button
		    // and center dialog with info button
		    const translateY = -1 * (Math.floor((dialogHeight - buttonHeight) / 2)) - 20;
		    const translateX = buttonHeight + this.model.attributes.size.width +22;
		    foreignObject.setAttributeNS(null, 'width', dialogWidth);
		    foreignObject.setAttributeNS(null, 'height', dialogHeight);
		    foreignObject.setAttributeNS(null, 'transform', `translate(${translateX},${translateY})`);
		
		    // place arrow so that it points to the info button
		    const arrow = foreignObject.querySelector('.info-popup-arrow');
		    arrow.style.top = `${(dialogHeight - buttonHeight - 20) / 2}px`; // 20 because of the 10px margin of the dialog
		
		    // fill dialog with text
		    foreignObject.querySelector('.info-popup-text').innerText = this.model.attributes.attrs.information;
		  },
		
        
        
        renderInput: function() {
        
        	if (this.inputVisible && this.model.attributes.attrs.label) {
        	  const el = this.el;
			  const text = this.model.attributes.attrs.label;
				
			  var markup = this.model.labelMarkup || this.model.get('labelMarkup');
			  const nodes = V(markup);
			  V(el).append(nodes);
			
			  const foreignObject = el.querySelector('foreignObject')
			  foreignObject.setAttributeNS(null, 'width', this.model.attributes.size.width);
			  foreignObject.setAttributeNS(null, 'height', this.model.attributes.size.height);
			
			  const textarea = foreignObject.querySelector('textarea');
			  textarea.style.disply = 'flex';
			  textarea.style.maxWidth = '100%';
			  textarea.style.maxHeight = '100%';
			  textarea.style.textAlign = 'center';
			  textarea.value = text;
			
			  this.autoResizeTextArea(text, textarea);
			
			  // select text in textarea
			  textarea.focus();
			  textarea.select();
			 }

		  },

        hideTools: function () {
            //this.render();
            if(!this.toolsVisible) {
                return;
            }
            V(this.el).find('.element-tools').forEach(function(e) {
                V(e).remove();
            });
            this.update();
            this.toolsVisible = false;
        },

        hideChecks: function () {
            //this.render();
            if(!this.checksVisible) {
                return;
            }
            V(this.el).find('.element-checks').forEach(function(e) {
                V(e).remove();
            });
            this.update();
            this.checksVisible = false;
            this.checkResults = [];
        },
        
        hideInput: function () {
            //this.render();
            if(!this.inputVisible) {
                return;
            }
            var input = $(this.el).find('textarea');
            $property_persist_fun(this.model.attributes.attrs.id,input.val());
            V(this.el).find('.html-element').forEach(function(e) {
                V(e).remove();
            });
            this.update();
            this.inputVisible = false;
        },
        
        hideValid: function () {
            if(!this.validVisible) {
                return;
            }
            V(this.el).find('.element-valid').forEach(function(e) {
                V(e).remove();
            });
            this.update();
            this.validVisible = false;
        },

        isElliptic: function() {
                var centerAnchorPoint = false;
                Object.getOwnPropertyNames(this.model.attributes.attrs).forEach(function (n) {
                    if(n.indexOf('.pyrox0tag') !== -1){
                        propName = n;
                        if(n.indexOf('ellipse') !== -1){
                            centerAnchorPoint = true;
                        }
                    }
                });
                return centerAnchorPoint;
        },

        mouseenter : function(evt) {
            if(evt.target.classList.contains("remove-circle") || evt.target.classList.contains("edge-circle") ) {
                joint.dia.ElementView.prototype.mouseenter.apply(this, arguments);
                return;
            }
            if (evt.target.classList.contains("information-circle")) {
		      //if (!this.el.contains(evt.target)) { // if we move within the cell we do nothing
			     this.openInfoDialog(20, 20, 200, 150);
		      //}
		    }

            if(this.checkResults.length > 0) {
                $('#check'+this.model.attributes.attrs.id).remove();
                var markup = ['<div id="check'+this.model.attributes.attrs.id+'" class="pyro_check_result_menu" style="top:'+(evt.clientY+10)+'px;left:'+(evt.clientX+10)+'px;">'];
                this.checkResults.forEach(function(er){
                    var color = '#f00'
                    if(er['type'] == 'warning') {
                        color = 'rgb(255, 153, 0)';
                    } else if(er['type'] == 'info') {
                        color = 'rgb(0, 149, 255)';
                    }
                    markup.push('<p style="margin:0;color:'+color+'" >'+er['message']+'</p>');
                });
                markup.push('</div>');
                $('body').append(markup.join(''));

            }
            joint.dia.ElementView.prototype.mouseenter.apply(this, arguments);
        },

        removeCheckMessages : function () {
            $('#check'+this.model.attributes.attrs.id).remove();
        },
        
        removeInformationMessages : function () {
            $('#information'+this.model.attributes.attrs.id).remove();
        },

        mouseout : function(evt) {
            this.removeCheckMessages();
            joint.dia.ElementView.prototype.mouseout.apply(this, arguments);
        },
        
        mouseleave : function(evt) {
        	this.closeInfoDialog();
        	joint.dia.ElementView.prototype.mouseleave.apply(this, arguments);
        },
        
        
        renderTools: function () {

            if(this.toolsVisible) {
                return;
            }

            this.toolsVisible = true;

	        var toolMarkup = this.model.toolMarkup || this.model.get('toolMarkup');
	
	        if (toolMarkup) {
	
	            

                var centerAnchorPoint = this.isElliptic();

                if(centerAnchorPoint) {
                    this.model.attr('.border/ref-x',"-50%");
                    this.model.attr('.border/ref-y',"-0.5");
                    this.model.attr('.resizer.t/ref-y',"-50%");
                    this.model.attr('.resizer.c/ref-y',"0%");
                    this.model.attr('.resizer.b/ref-y',"50%");
                    this.model.attr('.resizer.l/ref-x',"-50%");
                    this.model.attr('.resizer.m/ref-x',"0%");
                    this.model.attr('.resizer.r/ref-x',"50%");
                    
                    this.model.attr('.element-tool-remove/ref-y',"-50%");
                    this.model.attr('.element-tool-remove/ref-x',"50%");
                    
                    this.model.attr('.element-tool-edge/ref-y',"-50%");
                    this.model.attr('.element-tool-edge/ref-x',"50%");
                }

                var nodes = V(toolMarkup);
                V(this.el).append(nodes);

                if(this.model.attributes.attrs.disableResize===true){
                    V(this.el).find('.resizer').forEach(function(e){
                        e.remove();
                    });
                }
                if(!this.model.attributes.attrs.information || this.model.attributes.attrs.information == "null") {
                    V(this.el).find('.element-tool-information').forEach(function(e){
                        e.remove();
                    });
                }
                if(this.model.attributes.attrs.disableRemove===true){
                    V(this.el).find('.element-tool-remove').forEach(function(e){
                        e.remove();
                    });
                }
                if(this.model.attributes.attrs.disableEdge===true) {
                    //check runtime upper bound
                    V(this.el).find('.element-tool-edge').forEach(function(e){
                        e.remove();
                    });
                }
                
	
	        }
            this.update();
	        return this;
	    },

        renderChecks: function (color,errors) {

            if(this.checksVisible) {
                return;
            }

            this.checksVisible = true;
            this.checkResults = errors;

            var checkMarkup = this.model.checkMarkup || this.model.get('checkMarkup');
    
            if (checkMarkup) {
    
                var centerAnchorPoint = this.isElliptic();

                if(centerAnchorPoint) {
                    this.model.attr('.check-border/ref-x',"-50%");
                    this.model.attr('.check-border/ref-y',"-0.5");
                }
                
                this.model.attr('.check-border/stroke',color);
                this.model.attr('.check-border/fill',color);

                var nodes = V(checkMarkup);
                V(this.el).append(nodes);
            }

            this.update();
            return this;
        },
        
        renderValid: function () {

            if(this.validVisible) {
                return;
            }

            this.validVisible = true;

            var validMarkup = this.model.validMarkup || this.model.get('validMarkup');
    
            if (validMarkup) {
    
                var centerAnchorPoint = this.isElliptic();

                if(centerAnchorPoint) {
                    this.model.attr('.valid-border/ref-x',"-50%");
                    this.model.attr('.valid-border/ref-y',"-0.5");
                    this.model.attr('.valid-border/stroke','green');
                    this.model.attr('.valid-border/fill','green');
                }

                var nodes = V(validMarkup);
                V(this.el).append(nodes);
            }

            this.update();
            return this;
        },

        pointerdown: function(evt, x, y) {
            if (this.model.attributes.type === 'pyro.PyroCursor') {
              evt.preventDefault();
              return false;
            }
        
        	$('#check'+this.model.attributes.attrs.id).remove();
        	$('#information'+this.model.attributes.attrs.id).remove();
            var className = evt.target.parentNode.getAttribute('class');
            if(className == 'element-tool-remove') {
            	this.removeClicked = true;
            }
            if(className == 'element-tool-edge') {
                var cell = new joint.shapes.pyro.PyroLink({
                    creation_mode : true,
                    source: { id: this.model.id },
                    target: { x: x, y:y}
                });
                this.hideTools();
                this.hideInput();
                graph.addCell(cell);
                $temp_link = cell;
				highlight_valid_targets(this);
                return;
            }
            className = evt.target.getAttribute('class');
            if(className!=null&&className.includes('resizer')) {
                this.selectedBorder = className;
                return;
            }
            if(this.model.attributes.attrs.disableSelect===true&&this.model.attributes.parent!=null) {
                var p = this.paper.getModelById(this.model.attributes.parent);
                joint.dia.ElementView.prototype.pointerdown.apply(this.paper.findViewByModel(p), [evt, x, y]);
                return;
            }
            highlight_valid_containers(this.model.attributes.attrs.id,this.model.attributes.type, false);
            joint.dia.ElementView.prototype.pointerdown.apply(this, arguments);
        },
        
        pointerdblclick: function(evt, x, y) {
        	console.log("DB");
        	console.log(this.model.attributes.attrs);
			if(this.model.attributes.attrs.editLabel){
                this.inputVisible = true;
		    	this.renderInput();
            }				
		  	joint.dia.CellView.prototype.pointerdblclick.apply(this, arguments);
		  },


        pointermove: function(evt, x, y) {
            if (this.model.attributes.type === 'pyro.PyroCursor') {
              evt.preventDefault();
              return false;
            }
            
            this.hideTools();
            $('#check'+this.model.attributes.attrs.id).remove();
            $('#information'+this.model.attributes.attrs.id).remove();
            if(this.model.attributes.attrs.disableSelect===true&&this.model.attributes.parent!=null) {
                var p = this.paper.getModelById(this.model.attributes.parent);
                
                joint.dia.ElementView.prototype.pointermove.apply(this.paper.findViewByModel(p), [evt, x, y]);
                return;
            }
            if(this.model.attributes.attrs.disableMove===true){
                return;
            }

			
            if(this.removeClicked) {
	            return;
            }
            if( this.selectedBorder===false ||this.model.attributes.attrs.disableResize===true){
                joint.dia.ElementView.prototype.pointermove.apply(this, [evt, x, y]);
            } else {
                var pos_x = this.model.attributes.position.x;
                var pos_y = this.model.attributes.position.y;
                if(this.isElliptic()) {
                    pos_x = pos_x - this.model.attributes.size.width/2;
                    pos_y = pos_y - this.model.attributes.size.height/2;
                }

                if(this.selectedBorder==='resizer t l'){

                    var yDif = pos_y-y;
                    var xDif = pos_x-x;
                    if(
                        this.model.attributes.size.width+xDif>=10 &&
                        this.model.attributes.size.height+yDif>=10
                        ) {
                            this.model.resize(
                            geq1(this.model.attributes.size.width+xDif),
                            geq1(this.model.attributes.size.height+yDif),
                            {'direction':'top-left'});
                        }
                    
                }
                else if(this.selectedBorder==='resizer t r'){
                    var yDif = pos_y-y;
                    var xDif = x-(pos_x+this.model.attributes.size.width);
                    if(
                        this.model.attributes.size.width+xDif>=10 &&
                        this.model.attributes.size.height+yDif>=10
                        ) {
                        this.model.resize(
                            geq1(this.model.attributes.size.width+xDif),
                            geq1(this.model.attributes.size.height+yDif),
                            {'direction':'top-right'});
                    }
                }
                else if(this.selectedBorder==='resizer b l'){
                    var yDif = y-(pos_y+this.model.attributes.size.height);
                    var xDif = pos_x-x;
                    if(
                        this.model.attributes.size.width+xDif>=10 &&
                        this.model.attributes.size.height+yDif>=10
                        ) {
                        this.model.resize(
                            geq1(this.model.attributes.size.width+xDif),
                            geq1(this.model.attributes.size.height+yDif),
                            {'direction':'bottom-left'});
                    }
                }
                else if(this.selectedBorder==='resizer b r'){
                    var yDif = y-(pos_y+this.model.attributes.size.height);
                    var xDif = x-(pos_x+this.model.attributes.size.width);
                    if(
                        this.model.attributes.size.width+xDif>=10 &&
                        this.model.attributes.size.height+yDif>=10
                        ) {
                    this.model.resize(
                        geq1(this.model.attributes.size.width+xDif),
                        geq1(this.model.attributes.size.height+yDif),
                        {'direction':'bottom-right'});
                    }
                }
                else if(this.selectedBorder==='resizer t m'){
                    var yDif = pos_y-y;
                    if(
                        this.model.attributes.size.height+yDif>=10
                        ) {
                        this.model.resize(
                            geq1(this.model.attributes.size.width),
                            geq1(this.model.attributes.size.height+yDif),
                            {'direction':'top'});
                    }
                }
                else if(this.selectedBorder==='resizer b m'){
                    var yDif = y-(pos_y+this.model.attributes.size.height);
                    if(
                        this.model.attributes.size.height+yDif>=10
                        ) {
                        this.model.resize(
                            geq1(this.model.attributes.size.width),
                            geq1(this.model.attributes.size.height+yDif),
                            {'direction':'bottom'});
                    }
                }
                else if(this.selectedBorder==='resizer c l'){
                    var xDif = pos_x-x;
                    if(
                        this.model.attributes.size.width+xDif>=10
                        ) {
                        this.model.resize(
                            geq1(this.model.attributes.size.width+xDif),
                            geq1(this.model.attributes.size.height),
                            {'direction':'left'});
                    }
                }
                else if(this.selectedBorder==='resizer c r'){
                    var xDif = x-(pos_x+this.model.attributes.size.width);
                    if(
                        this.model.attributes.size.width+xDif>=10
                        ) {
                        this.model.resize(
                            geq1(this.model.attributes.size.width+xDif),
                            geq1(this.model.attributes.size.height),
                            {'direction':'right'});
                    }
                }
            }

        }
    });
}

/*
 Element Control
 Internal actions
 */


function update_element_internal(cellId,id,styleArgs,information,label,graph){
    var element;
    if(id<0){ // id over cellId
        element = graph.getCell(cellId);
    } else {
        element = findElementById(id,graph);
    }
    if(element != null) {
        _update_element_internal(element, id, styleArgs, information, label);
    }
}

function _update_element_internal(element, id, styleArgs, information, label) {
    element.attributes.attrs.id = id;
    element.attributes.attrs.styleArgs = styleArgs;
    element.attributes.attrs.information = information;
    element.attributes.attrs.label = label;
}

function update_node_highlight_internal(cell,shape,
                                        background_r,background_g,background_b,
                                        foreground_r,foreground_g,foreground_b
) {
    var result = {};
    result['background'] = cell.model.attr(shape+'/fill');
    cell.model.attr(shape+'/fill',"rgb("+background_r+","+background_g+","+background_b+")");
    result['foreground'] = cell.model.attr(shape+'/stroke');
    cell.model.attr(shape+'/stroke',"rgb("+foreground_r+","+foreground_g+","+foreground_b+")");
    return result;
}

function update_node_apperance_internal(cell,shape,
                                        background_r,background_g,background_b,
                                        foreground_r,foreground_g,foreground_b,
                                        lineInVisible,
                                        lineStyle,
                                        transparency,
                                        lineWidth,
                                        filled,
                                        angle,
                                        fontName,
                                        fontSize,
                                        fontBold,
                                        fontItalic,
                                        imagePath
) {

    if(shape.startsWith("text.")){
        if(filled=='TRUE'||filled=='UNDEF'){
            cell.model.attr(shape+'/fill',"rgb("+foreground_r+","+foreground_g+","+foreground_b+")");
        }
        cell.model.attr(shape+'/stroke',"none");
    } else {
        if(filled=='TRUE'||filled=='UNDEF'){
            cell.model.attr(shape+'/fill',"rgb("+background_r+","+background_g+","+background_b+")");
        }
        cell.model.attr(shape+'/stroke',"rgb("+foreground_r+","+foreground_g+","+foreground_b+")");
    }
    cell.model.attr(shape+'/transform',"rotate("+angle+")");
    if(lineInVisible) {
        cell.model.attr(shape+'/stroke-opacity',0.0);
    } else {
        cell.model.attr(shape+'/stroke-opacity',1.0);
    }
    if(lineStyle=='DASH') {
        cell.model.attr(shape+'/stroke-dasharray','10, 5');
    }
    if(lineStyle=='DASHDOT') {
        cell.model.attr(shape+'/stroke-dasharray','5, 5, 1, 5');
    }
    if(lineStyle=='DOT') {
        cell.model.attr(shape+'/stroke-dasharray','1, 5');
    }
    if(lineStyle=='DASHDOTDOT') {
        cell.model.attr(shape+'/stroke-dasharray','5, 5, 1, 5, 1, 5');
    }
    if(lineStyle=='SOLID') {
        cell.model.attr(shape+'/stroke-dasharray','');
    }
    cell.model.attr(shape+'/fill-opacity',1.0-transparency);
    cell.model.attr(shape+'/opacity',1.0-transparency);
    if(!lineInVisible) {
        cell.model.attr(shape+'/stroke-opacity',1.0-transparency);
    }
    cell.model.attr(shape+'/stroke-width',lineWidth);
    if(fontName!=null) {
        cell.model.attr(shape+'/font-family',fontName);
    }
    cell.model.attr(shape+'/font-size',fontSize+"px");
    if(fontBold) {
        cell.model.attr(shape+'/font-weight',"bold");
    } else {
        cell.model.attr(shape+'/font-weight',"normal");
    }
    if(fontItalic) {
        cell.model.attr(shape+'/font-style',"italic");
    } else {
        cell.model.attr(shape+'/font-style',"normal");
    }
    if(imagePath!=null) {
        cell.model.attr(shape+'/xlink:href', imagePath);
        cell.model.attr(shape+'/refHeight', '100%');
        cell.model.attr(shape+'/refWidth', '100%');
        if(
            !shape.startsWith("ellipse.")
        ) {
            cell.model.attr(shape+'/ref-x', '50.0%');
            cell.model.attr(shape+'/ref-y', '50.0%');
        }
    }
}

function update_edge_text_apperance_internal(idx,cell,shape,
                                             background_r,background_g,background_b,
                                             foreground_r,foreground_g,foreground_b,
                                             lineInVisible,
                                             lineStyle,
                                             transparency,
                                             lineWidth,
                                             filled,
                                             angle,
                                             fontName,
                                             fontSize,
                                             fontBold,
                                             fontItalic,
                                             imagePath
) {
    if(filled=='TRUE'||filled=='UNDEF') {
        cell.model.prop(['labels',idx,'attrs',shape,'fill'],"rgb("+background_r+","+background_g+","+background_b+")");
    }
    cell.model.prop(['labels',idx,'attrs',shape,'stroke'],"rgb("+foreground_r+","+foreground_g+","+foreground_b+")");
    cell.model.prop(['labels',idx,'attrs',shape,'transform'],"rotate("+angle+")");
    if(lineInVisible) {
        cell.model.prop(['labels',idx,'attrs',shape,'stroke-opacity'],0.0);
    } else {
        cell.model.prop(['labels',idx,'attrs',shape,'stroke-opacity'],1.0);
    }
    if(lineStyle=='DASH') {
        cell.model.prop(['labels',idx,'attrs',shape,'stroke-dasharray'],'10, 5');
    }
    if(lineStyle=='DASHDOT') {
        cell.model.prop(['labels',idx,'attrs',shape,'stroke-dasharray'],'5, 5, 1, 5');
    }
    if(lineStyle=='DOT') {
        cell.model.prop(['labels',idx,'attrs',shape,'stroke-dasharray'],'1, 5');
    }
    if(lineStyle=='DASHDOTDOT') {
        cell.model.prop(['labels',idx,'attrs',shape,'stroke-dasharray'],'5, 5, 1, 5, 1, 5');
    }
    if(lineStyle=='SOLID') {
        cell.model.prop(['labels',idx,'attrs',shape,'stroke-dasharray'],'');
    }
    cell.model.prop(['labels',idx,'attrs',shape,'fill-opacity'],1.0-transparency);
    cell.model.prop(['labels',idx,'attrs',shape,'opacity'],1.0-transparency);
    if(!lineInVisible) {
        cell.model.prop(['labels',idx,'attrs',shape,'stroke-opacity'],1.0-transparency);
    }
    cell.model.prop(['labels',idx,'attrs',shape,'stroke-width'],lineWidth);
    if(fontName!=null) {
        cell.model.prop(['labels',idx,'attrs',shape,'font-family'],fontName);
    }
    cell.model.prop(['labels',idx,'attrs',shape,'font-size'],fontSize+"px");
    if(fontBold) {
        cell.model.prop(['labels',idx,'attrs',shape,'font-weight'],"bold");
    } else {
        cell.model.prop(['labels',idx,'attrs',shape,'font-weight'],"normal");
    }
    if(fontItalic) {
        cell.model.prop(['labels',idx,'attrs',shape,'font-style'],"italic");
    } else {
        cell.model.prop(['labels',idx,'attrs',shape,'font-style'],"normal");
    }
    if(imagePath!=null) {
        cell.model.prop(['labels',idx,'attrs',shape,'xlink:href'],"img/"+imagePath);
    }
}

function add_node_internal(element,graph,paper,map){
    graph.addCells([ element ]);
    fitContent(paper,map);
}

function add_edge_internal(element,graph,router,connector){
    update_single_edge_routing(element,router,connector);
    element.addTo(graph).reparent();
}

function move_node_internal(x,y,id,containerId,graph)
{
    var node = findElementById(id,graph);
    var parentId = node.attributes.parent;
    if(parentId){
        var parent = graph.getCell(parentId, graph);
        parent.unembed(node);
    }
    if(containerId>0) {
        var container = findElementById(containerId, graph);
        container.embed(node);
    }
    node.position(x, y,{ parentRealtive: true });
}

function remove_node_internal(id,graph,paper)
{
    var node = findElementById(id,graph);
	if(node != null) {
	    remove_element(node, paper);
	}
}

function remove_element(elem, paper) {
    try{
        paper.findViewByModel(elem).removeCheckMessages();
    } catch(e) {}
    try{
        paper.findViewByModel(elem).removeInformationMessages();
    } catch(e) {}
    try{
        elem.remove({disconnectLinks:true});
    } catch(e) {
        try{
            edge.remove();
        } catch(e) {}
    }
}

function resize_node_internal(width,height,direction,id,graph,paper)
{
    var node = findElementById(id,graph);
    node.resize(width,height,{direction:direction});
}

function rotate_node_internal(angle,id,graph)
{
    var node = findElementById(id,graph);
    node.rotate(angle,true);
}

function remove_edge_internal(id,graph)
{
    var edge = findElementById(id,graph);
	if(edge != null) {
    	edge.remove();
	}
}

function reconnect_edge_internal(sourceId,targetId,id,graph)
{
    var edge = findElementById(id,graph);
    edge.set('source',findElementById(sourceId, graph));
    edge.set('target',findElementById(targetId,graph));
    edge.reparent();
}

function update_bendpoint_internal(positions,id,graph)
{
    var link = findElementById(id,graph);
    if(positions !== null) {
        var vertices = [];
        for(var j=0; j < positions.length; j++){
            var position = positions[j];
            var vertex = { 
                x: position.x,
                y: position.y
            };
            vertices.push(vertex);
        }
    	link.set('vertices', vertices);
    }
}

/*
 General Event Mechanism
 */

/**
 * Disbales user interaction on the given paper
 * @param paper
 */
function block_user_interaction(paper) {
    paper.setInteractivity(false);
}

/**
 * Enables the user interaction for the given paper
 * @param paper
 */
function unblock_user_interaction(paper) {
    paper.setInteractivity(true);
}

/**
 * Resets all listeners and register new ones
 */
function init_event_system(paper,graph,remove_cascade,disableRemove,disableResize,disableEdge,highlight_valid_containers)
{
    $temp_link = null;
    $edge_menu_shown = false;
    $highlight_valid_containers_fun = highlight_valid_containers;
    init_edge_eventsystem(paper);
    init_menu_eventsystem(paper,graph,remove_cascade,disableRemove,disableResize,disableEdge);
}


function confirm_drop(ev) {
    ev.preventDefault();
}

/**
 *
 * @param ev
 */
function start_drag_element(ev) {
    if(!ev || !ev.target || !ev.target.dataset)
        return;
    $edge_to_create = ev.target.dataset.typename;
	var isReference = ev.target.dataset.reference ? true : false;
    var content = JSON.stringify({
        'typename':ev.target.dataset.typename,
        "elementid": ev.target.dataset.elementid,
		'isReference': isReference
    });
    $highlight_valid_containers_fun(-1, ev.target.dataset.typename, isReference);
    ev.dataTransfer.setData("text", content);
}

/**
 * Download the pyro canvas.
 * Requires that SvgSaver is globally available.
 *
 * @param name {string} The name of the file without file extension.
 * @parem type {string} The type of the download file (svg|png).
 */
function downloadPyroCanvas(name, type) {
    if (name == null) throw 'Missing argument: name';
    if (type == null) throw 'Missing argument: type';

    type = type.toLowerCase();    
    if (!['svg', 'png'].includes(type)) {
      throw 'Unsupported download type';
    }

    const svgSaver = new SvgSaver();

    const svg = document.querySelector('pyro-canvas svg')
    const copy = svg.cloneNode(true);

    // replace &nbsp; from text nodes so that the svg is valid
    const regex = new RegExp(String.fromCharCode(160), "g");
    copy.querySelectorAll('text tspan').forEach(n => {
      n.textContent = n.textContent.replace(regex, ' ');
    });

    // append download and remove the copy so that transitions are exported correctly
    document.body.appendChild(copy);
    console.log(copy);

    // scale copy to viewport so that the whole viewport is exported and not just the visible part of it
    const g = copy.querySelector('.joint-viewport');
    g.removeAttribute('transform');
    const scale = g.getScreenCTM().inverse().multiply(copy.getScreenCTM()).a;
    console.log(scale);
    const dimension = copy.getBBox();
    console.log(dimension);
    
	copy.setAttribute('viewBox', dimension.x+' '+dimension.y+' '+(dimension.width+20)+' '+(dimension.height+20));
    copy.setAttribute('width', Math.abs(dimension.x)+dimension.width+20);
    copy.setAttribute('height', Math.abs(dimension.y)+dimension.height+20);
    copy.querySelector('.joint-viewport').setAttribute('transform', 'translate(10,10)')

    if (type === 'svg') {
      svgSaver.asSvg(copy, name + ".svg");
    } else if (type === 'png') {
      svgSaver.asPng(copy, name + ".png");
    }
       
    copy.parentNode.removeChild(copy);
}

function downloadContent(filename, text) {
    var pom = document.createElement('a');
    pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    pom.setAttribute('download', filename);

    if (document.createEvent) {
        var event = document.createEvent('MouseEvents');
        event.initEvent('click', true, true);
        pom.dispatchEvent(event);
    }
    else {
        pom.click();
    }
    pom.remove();
}

function reAdjustDimensions(paper, paperMap) {
    adjustMapDimensions(paperMap);
}

function adjustMapDimensions(paperMap) {
    if(paperMap == null)
        return;
	var paper_map_obj = document.getElementById("paper_map");
	if(paper_map_obj == null)
        return;
	var container = paper_map_obj.closest("bs-tab-content"); // 'classic' layout-container
	var mapWidth;
	var mapHeight;
	if(container == null) {
		container = paper_map_obj.parentElement; // 'micro' layout-container
		mapWidth = container.clientWidth;
		mapHeight = container.clientHeight;
	} else {
        container.style.overflow = 'hidden';
        var deltaH = 5;
        var placeholder = document.getElementsByClassName("grid-stack-placeholder")[0];
        if(placeholder != null) {
            container = placeholder.children[0];
            
            var parent = paper_map_obj.closest(".grid-stack-item-content");
            var header = parent.children[0];
            var tabZone = parent.children[1].children[0];
            deltaH += header.clientHeight + tabZone.clientHeight;
        }
		mapWidth = container.clientWidth;
		mapHeight = (container.clientHeight - deltaH);
	}
	paperMap.setDimensions(mapWidth, mapHeight);
    paperMap.scaleContentToFit();
}

function viewportToPixels(value) {
    var parts = value.match(/([0-9\.]+)(vh|vw)/)
    var q = Number(parts[1])
    var side = window[['innerHeight', 'innerWidth'][['vh', 'vw'].indexOf(parts[2])]]
    return side * (q/100)
}