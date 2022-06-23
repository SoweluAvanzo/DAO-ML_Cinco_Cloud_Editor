package de.jabc.cinco.meta.plugin.pyro.canvas

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import java.util.regex.Matcher
import java.util.regex.Pattern
import mgl.ContainingElement
import mgl.Edge
import mgl.GraphModel
import mgl.IncomingEdgeElementConnection
import mgl.MGLModel
import mgl.Node
import mgl.NodeContainer
import mgl.OutgoingEdgeElementConnection
import org.eclipse.emf.ecore.EObject
import style.EdgeStyle
import style.MultiText
import style.NodeStyle
import style.Styles
import style.Text
import mgl.GraphicalModelElement

class Controller extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNameController() '''controller.js'''

	def contentController(GraphModel g,Styles styles) {
		val nodes = g.nodes.filter[!isIsAbstract]
		val edges = g.edges.filter[!isIsAbstract]
		

	'''
	var $graph_«g.jsCall» = null;
	var $paper_«g.jsCall» = null;
	var $map_paper_«g.jsCall» = null;
	var $router_«g.jsCall» = 'manhattan';
	var $connector_«g.jsCall» = 'rounded';
	var $graphmodel_id_«g.jsCall» = -1;
	var $_disable_events_«g.jsCall» = true;
	var $checkResults_«g.jsCall» = [];
	var $cursor_manager_«g.jsCall» = {};
	var $«g.jsCall»_lib = null;
	
	
	var $cb_functions_«g.jsCall» = {};

	
	function load_«g.jsCall»(
	    w,
	    h,
	    scale,
	    graphmodelid,
	    router,
	    connector,
	    //callback afert initialization
	    initialized,
	    //message callbacks
	    cb_element_selected,
	    cb_graphmodel_selected,
	    cb_update_bendpoint,
	    cb_can_move_node,
	    cb_can_reconnect_edge,
	    cb_get_valid_targets,
	    cb_is_valid_connection,
	    cb_get_valid_containers,
	    cb_is_valid_container,
	    cb_get_custom_actions,
	    cb_fire_dbc_actions,
	    cb_delete_selected,
	    cb_cursor_moved,
	    cb_property_update,
		«FOR elem:g.nodesAndEdges.filter[!isIsAbstract]  SEPARATOR ","»
			«IF elem instanceof Node»
			    cb_create_node_«elem.jsCall(g)»,
			    cb_remove_node_«elem.jsCall(g)»,
			    cb_move_node_«elem.jsCall(g)»,
			    cb_resize_node_«elem.jsCall(g)»,
			    cb_rotate_node_«elem.jsCall(g)»
			«ENDIF»
			«IF elem instanceof Edge»
				cb_create_edge_«elem.jsCall(g)»,
				cb_remove_edge_«elem.jsCall(g)»,
				cb_reconnect_edge_«elem.jsCall(g)»
			«ENDIF»
		«ENDFOR»
	    
	) {
	    $router_«g.jsCall» = router;
	    $graphmodel_id_«g.jsCall» = graphmodelid;
	    $connector_«g.jsCall» = connector;
	    $checkResults_«g.jsCall» = [];
	    $property_persist_fun = cb_property_update;
	
	    $cb_functions_«g.jsCall» = {
	    	cb_element_selected:cb_element_selected,
		    cb_update_bendpoint:cb_update_bendpoint,
		    cb_can_move_node:cb_can_move_node,
		    cb_can_reconnect_edge:cb_can_reconnect_edge,
		    cb_get_valid_targets:cb_get_valid_targets,
		    cb_is_valid_connection:cb_is_valid_connection,
		    cb_get_valid_containers:cb_get_valid_containers,
		    cb_is_valid_container:cb_is_valid_container,
		    cb_cursor_moved:cb_cursor_moved,
			«FOR elem:g.nodesAndEdges.filter[!isIsAbstract] SEPARATOR ","»
				«IF elem instanceof Node»
					cb_create_node_«elem.jsCall(g)»:cb_create_node_«elem.jsCall(g)»,
					cb_remove_node_«elem.jsCall(g)»:cb_remove_node_«elem.jsCall(g)»,
					cb_move_node_«elem.jsCall(g)»:cb_move_node_«elem.jsCall(g)»,
					cb_resize_node_«elem.jsCall(g)»:cb_resize_node_«elem.jsCall(g)»,
					cb_rotate_node_«elem.jsCall(g)»:cb_rotate_node_«elem.jsCall(g)»
				«ENDIF»
				«IF elem instanceof Edge»
					cb_create_edge_«elem.jsCall(g)»:cb_create_edge_«elem.jsCall(g)»,
					cb_remove_edge_«elem.jsCall(g)»:cb_remove_edge_«elem.jsCall(g)»,
					cb_reconnect_edge_«elem.jsCall(g)»:cb_reconnect_edge_«elem.jsCall(g)»
				«ENDIF»
			«ENDFOR»
	    };
	
		
	    $graph_«g.jsCall» = new joint.dia.Graph;
	    $paper_«g.jsCall» = new joint.dia.Paper({
	
	        el: document.getElementById('paper_«g.jsCall»'),
	        width: w,
	        height: h,
	        gridSize: 5,
	        drawGrid: true,
	        model: $graph_«g.jsCall»,
	        snapLinks: false,
	        linkPinning: false,
	        elementView: constraint_element_view($graph_«g.jsCall»,highlight_valid_targets_«g.jsCall»,highlight_valid_containers_«g.jsCall»),
	        embeddingMode: true,
	
	        highlighting: {
	            'default': {
	                name: 'stroke',
	                options: {
	                    padding: 10
	                }
	            },
	            'elementAvailability': {
	                name: 'stroke',
	                options: {
	                    'color':'green',
	                    padding: 10,
	                    attrs: {
	                        'stroke':'green',
	                        'fill-opacity':0.1,
	                        'fill':'green',
	                    }
	                }
	            },
	            'embedding': {
	                name: 'stroke',
	                options: {
	                    'color':'green',
	                    padding: 10,
	                    attrs: {
	                        'stroke':'green',
	                        'fill-opacity':0.1,
	                        'fill':'green',
	                    }
	                }
	            }
	        },
	        validateEmbedding: function(childView, parentView) {
	        	if(childView == null) {
	        		return false;
	        	}
	        	var nodeId = childView.model.attributes.attrs.id;
	        	if(parentView == null) {
	        		return $cb_functions_«g.jsCall».cb_is_valid_container(nodeId,graphmodelid);
	        	}
	        	var parentId = parentView.model.attributes.attrs.id;
	            return $cb_functions_«g.jsCall».cb_is_valid_container(nodeId,parentId);
	        },
	
	        validateConnection: function(cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
	        	if(cellViewS == null || cellViewT == null || linkView == null) {
	        		return false;
	        	}
	            var sourceId = cellViewS.model.attributes.attrs.id;
	            var targetId = cellViewT.model.attributes.attrs.id;
	            var edgeId = linkView.model.attributes.attrs.id;
	            return $cb_functions_«g.jsCall».cb_is_valid_connection(edgeId,sourceId,targetId);
	        }
	    });
	    $paper_«g.jsCall».options.multiLinks = false;
	    $paper_«g.jsCall».options.markAvailable = true;
	    $paper_«g.jsCall».options.restrictTranslate=false;
	    $paper_«g.jsCall».options.drawGrid= { name: 'mesh', args: { color: 'black' }};
	    adjustDimensions($paper_«g.jsCall»);
		$paper_«g.jsCall».scale(scale);
		$paper_«g.jsCall».options.defaultConnectionPoint = {
		    name: 'boundary',
		    args: {
		        sticky: true
		    }
		};
		
	    /*
	    Register event listener triggering the callbacks to the NG-App
	     */
		 
		 $«g.jsCall»_lib = pyroGlueLines(joint,$graph_«g.jsCall», $paper_«g.jsCall», {
		   shapeType: 'pyro.GlueLine',
		   verticalLineColor: 'red',
		   horizontalLineColor: 'blue',
		   middleLineColor: 'green',
		   offset: 10
		 });
		 
		 /**
		   * Element has been added
		   * change selection
		   * show properties
		   */
		$graph_«g.jsCall».on('change:position', _.debounce($«g.jsCall»_lib.handleChangePosition, 10));
	
	    /**
	     * Graphmodel (Canvas) actions
	     */
		$paper_«g.jsCall».on('blank:pointerclick',function () {
			removeMenus();
		});
		$paper_«g.jsCall».on('blank:pointerdblclick', function(evt,x,y){
			removeMenus();
			
			//fetch ca for element
			cb_fire_dbc_actions($graphmodel_id_«g.jsCall»);
			console.log("graphmodel double clicked");
		});
	    $paper_«g.jsCall».on('blank:pointerup', function(evt, x, y) {
	    	removeMenus();
	        deselect_all_elements(null,$paper_«g.jsCall»,$graph_«g.jsCall»);
	        cb_graphmodel_selected();
	        console.log("graphmodel clicked");
	    });
	    $paper_«g.jsCall».on('blank:pointerdown', function(evt, x, y) {
	        evt.data = { action: 'paper_drag', paper_drag_x: x, paper_drag_y: y };
	    });
	    $paper_«g.jsCall».on('blank:pointermove', function(evt, x, y) {
	        var trans = $paper_«g.jsCall».translate();
	        if(x - evt.data.x != 0 || y - evt.data.y != 0) {
	        	var sc = $paper_«g.jsCall».scale();
	            $paper_«g.jsCall».translate(
	               (trans.tx) + Math.round((x - evt.data.paper_drag_x) * sc.sx),
	               (trans.ty) + Math.round((y - evt.data.paper_drag_y) * sc.sy)
	            );
	        }
	    });
	    $paper_«g.jsCall».on('blank:mousewheel', function(evt, x, y,delta) {
			zoom_paper($paper_«g.jsCall»,evt,x,y,delta);
		});
		$paper_«g.jsCall».on('cell:mousewheel', function(cv,evt, x, y,delta) {
			zoom_paper($paper_«g.jsCall»,evt,x,y,delta);
		});
		$paper_«g.jsCall».on('cell:pointerclick',function () {
			removeMenus();
		});
	    $paper_«g.jsCall».on('cell:pointerdblclick', function(cellView,evt,x,y){
	    	removeMenus();
	    	
	    	//fetch ca for element
	    	cb_fire_dbc_actions(cellView.model.attributes.attrs.id);
	    });
	    /**
	     * Element has been selected
	     * change selection
	     * show properties
	     */
	    $paper_«g.jsCall».on('cell:pointerup', function(cellView,evt) {
			if(cellView.model.attributes.attrs.isDeleted!==true) {
				
				//check for select disabled and try for container
				while(cellView.model.attributes.attrs.disableSelect===true) {
					// get container
					if(cellView.model.attributes.parent == null){
						cb_graphmodel_selected();
						return;
					}
					cellView = $paper_«g.jsCall».findViewByModel($graph_«g.jsCall».getCell(cellView.model.attributes.parent));
				}
				
			    update_selection(cellView,$paper_«g.jsCall»,$graph_«g.jsCall»);
			    cb_element_selected(cellView.model.attributes.attrs.id);
				«FOR node:nodes»
					if(cellView.model.attributes.type=='«node.typeName»'){
						//check if container has changed
						move_node_«node.jsCall(g)»_hook(cellView);
						if(!cellView.model.attributes.attrs.disableResize) {
					    	$cb_functions_«g.jsCall».cb_resize_node_«node.jsCall(g)»(Math.round(cellView.model.attributes.size.width),Math.round(cellView.model.attributes.size.height),$node_resize_last_direction,cellView.model.attributes.attrs.id);	 	        		
						}
					}
				«ENDFOR»
				«FOR edge:edges»
					if(cellView.model.attributes.type=='«edge.typeName»'){
						var source = $graph_«g.jsCall».getCell(cellView.model.attributes.source.id);
						var target = $graph_«g.jsCall».getCell(cellView.model.attributes.target.id);
						reconnect_edge_«edge.jsCall(g)»_hook(cellView);
						$cb_functions_«g.jsCall».cb_update_bendpoint(
							cellView.model.attributes.vertices,
							cellView.model.attributes.attrs.id
						);
					}
				«ENDFOR»
			     console.log(cellView);
			     console.log("element clicked");
			}
	     });
	     
		/**
		* Link has been selected
		* change selection
		* show properties
		*/
		$paper_«g.jsCall».on('link:options', function(cellView,evt, x, y) {
			removeMenus();
			update_selection(cellView,$paper_«g.jsCall»,$graph_«g.jsCall»);
			cb_element_selected(cellView.model.attributes.attrs.id);
			console.log("link clicked");
		});
		/**
		 * Canvas has been right clicked
		 * Show context menu for the graphmodel
		 * including registered custome actions
		 */
		$paper_«g.jsCall».on('blank:contextmenu', function(evt,x,y){
			removeMenus();
			//fetch ca for graphmodel
			var pos = getPaperToScreenPosition(x,y,$paper_«g.jsCall»);
			cb_get_custom_actions($graphmodel_id_«g.jsCall»,Math.round(pos.x),Math.round(pos.y+$(document).scrollTop()),x,y);
			console.log("graphmodel context menu clicked");
		});
		/**
		 * Element has been right clicked
		 * Show context menu for the element
		 * including registered custome actions
		 */
		$paper_«g.jsCall».on('cell:contextmenu', function(cellView,evt,x,y){
			removeMenus();
			//fetch ca for element
			var pos = getPaperToScreenPosition(x,y,$paper_«g.jsCall»);
			cb_get_custom_actions(cellView.model.attributes.attrs.id,Math.round(pos.x),Math.round(pos.y+$(document).scrollTop()),x,y);
		});

		(function() {
			/**
			 * emit the cursor position of the user on the paper
			 * fire mousemove event every n seconds
			 */
		
		    var UPDATE_DELAY = 2000; // ms
		
			function handleMousemove(evt) {
				if ($paper_«g.jsCall» == null || $cursor_manager_resizestart) return; 
				var pointOnPaper = $paper_«g.jsCall».clientToLocalPoint(evt.clientX, evt.clientY);
				$cb_functions_«g.jsCall».cb_cursor_moved(Math.round(pointOnPaper.x),Math.round(pointOnPaper.y));
			}
			
			var handleMousemoveThrottled = _.throttle(handleMousemove, UPDATE_DELAY);
		
			$(document.getElementById('paper_«g.jsCall»')).on('mousemove', handleMousemoveThrottled);
		}());
		
		$cursor_manager_«g.jsCall» = (function(){
		
		  // after how many seconds of inactivity the cursor is removed
		  var REMOVE_TIMEOUT = 4000;
		  
		  // how many chars of the username should be displayed
		  var MAX_USERNAME_LENGTH = 8;
		  
		  var ANIMTATION_TRANSITION = {
		  	delay: 0,
		  	duration: 400,
		  	valueFunction: joint.util.interpolate.number,
		    timingFunction: joint.util.timing.linear
		  };
		  
		  // userId -> color
		  var color_cache = {};
		  
		  // userId -> (cell, timer)
		  var cursors = {};
		  
		  function handle_cursor_timeout(userId) {
		    cursors[userId].timer = window.setTimeout(function() {
			  cursors[userId].cell.remove();
			  delete cursors[userId];
			}, REMOVE_TIMEOUT);
		  }
		  
		  function add_cursor(userId, username, x, y) {      
		      var color = generate_cursor_color(userId);
		      var cursor = new joint.shapes.pyro.PyroCursor(); 
		      cursor.attr('.pyro-cursor/fill', color);
		      cursor.attr('.pyro-cursor-tooltip/fill', color);
		      $graph_«g.jsCall».addCell(cursor);
		      
		      // update label to username
		      // update width of tooltip accordingly
		      // querySelectorAll because of the same id is also used in the mini map
		      var cursorNodes = document.querySelectorAll('g[model-id="' + cursor.id + '"]');
		      cursorNodes.forEach(function(cursorNode){
		        var tooltip = cursorNode.querySelector('.pyro-cursor-tooltip'); 
		        var text = cursorNode.querySelector('text');
		        text.textContent = username.length > MAX_USERNAME_LENGTH ? username.substring(0, 8) + '...' : username;
		        tooltip.setAttribute('width', text.getBBox().width + 8);
		      });
		          
		      cursors[userId] = {
		        cell: cursor,
		        timer: null
		      };
		      
		      handle_cursor_timeout(userId);
		    }
					  
		  function generate_random_number(min, max) {
		    return Math.random() * (+max - +min) + +min
		  }
		  
		  function generate_cursor_color(userId) {
		    if (color_cache[userId] != null) {
		      return color_cache[userId];
		    }
		  
		    var h = generate_random_number(0, 359);
		    var s = generate_random_number(35, 100);	// not so super grayish colors
		    var l = generate_random_number(15, 25);		// low lighness so that white text color is readable
		  
		    var color = 'hsl(' + h + ', ' + s + '%, ' + l + '%)';
		    color_cache[userId] = color;
		    return color;
		  }
		  
		  return {
			update_cursor: function(userId, username, x, y) {
		  	  if (cursors[userId] == null) {
		  	    add_cursor(userId, username, x, y);
		      } else {
		        window.clearTimeout(cursors[userId].timer);
		        
		  	    var cursor = cursors[userId].cell;
		  	    cursor.transition('position/x', x, ANIMTATION_TRANSITION);
		  	    cursor.transition('position/y', y, ANIMTATION_TRANSITION);
		  	    
		  	    handle_cursor_timeout(userId);
		      }
		    }
		  }
		}());
	
	    /**
	     * Element has been added
	     * change selection
	     * show properties
	     */
	    $graph_«g.jsCall».on('add', function(cellView) {
	    	if(!$_disable_events_«g.jsCall» && cellView.attributes.type!=='pyro.PyroLink' && cellView.attributes.type!=='pyro.GlueLine' ){
	    		update_selection($paper_«g.jsCall».findViewByModel(cellView),$paper_«g.jsCall»,$graph_«g.jsCall»);
		        //for each edge
				«FOR edge:edges»
					if(cellView.attributes.type==='«edge.typeName»') {
					    var link = $graph_«g.jsCall».getCell(cellView.attributes.id);
					    var source = link.getSourceElement();
					    var target = link.getTargetElement();
					    if(source.id===target.id){
					    	var p1 = {
					    		x:source.attributes.position.x,
					    		y:source.attributes.position.y-source.attributes.size.height
					    	};
					        var p2 = {
					            x:source.attributes.position.x-source.attributes.size.width,
					            y:source.attributes.position.y
					        };
					        link.set('vertices', [p1,p2]);
					    }
					    refresh_routing_«g.jsCall»();
					    $cb_functions_«g.jsCall».cb_create_edge_«edge.jsCall(g)»(
					      source.attributes.attrs.id,
					      target.attributes.attrs.id,
					      cellView.attributes.id,
					      cellView.attributes.vertices
					    );
					}
				«ENDFOR»
		        console.log(cellView);
		        console.log("element added");
	        }
	    });
	
		function remove_cascade_node_«g.jsCall»(cellView) {
			if(!$_disable_events_«g.jsCall»){
				 deselect_all_elements(null,$paper_«g.jsCall»,$graph_«g.jsCall»);
				 //trigger callback
				 cb_graphmodel_selected();
				 //foreach node
				 «FOR node:nodes»
				        if(cellView.attributes.type==='«node.typeName»'){
				            $cb_functions_«g.jsCall».cb_remove_node_«node.jsCall(g)»(cellView.attributes.attrs.id);
				        }
				 «ENDFOR»
				 fitContent($paper_«g.jsCall»,$map_paper_«g.jsCall»);
			}
		
		}
	
	    /**
	     * Element has been removed
	     * change selection to the graphmodel
	     * show properties
	     */
	    $graph_«g.jsCall».on('remove', function(cellView) {
	    	if(cellView.attributes.type == 'pyro.GlueLine') {
	    		return;
	    	}
	    	if(!$_disable_events_«g.jsCall»){
		        deselect_all_elements(null,$paper_«g.jsCall»,$graph_«g.jsCall»);
		        //trigger callback
		        if(cellView.attributes.type.substring(0, 5)!=='pyro.'){
		        	cb_graphmodel_selected();
		        }
		        //foreach edge
				«FOR edge:edges»
				    if(cellView.attributes.type==='«edge.typeName»'){
				    	cellView.attributes.attrs.isDeleted = true;
				        $cb_functions_«g.jsCall».cb_remove_edge_«edge.jsCall(g)»(cellView.attributes.attrs.id);
				    }
				«ENDFOR»
		        console.log(cellView);
		        console.log("element removed");
	        }
	    });
	    
		$("html").off('dragend');
		$("html").on("dragend", function(event) {
		    event.preventDefault();  
		    event.stopPropagation();
		    unhighlight_all_element_valid_target($paper_«g.jsCall»,$graph_«g.jsCall»);
		});
	    $(document).off('mouseup');
	    $(document).mouseup(function (evt) {
	        $mouse_clicked_menu=false;
	        if($temp_link!==null && !$edge_menu_shown)
	        {
	           unhighlight_all_element_valid_target($paper_«g.jsCall»,$graph_«g.jsCall»);
	           var rp = getRelativeScreenPosition(evt.clientX,evt.clientY,$paper_«g.jsCall»);
	           var views = $paper_«g.jsCall».findViewsFromPoint(rp);
	           if(views.length > 0)
	           {
	             «edgecreation(g)»
	           }
	           var pyroLink = $graph_«g.jsCall».getCell($temp_link.id);
	           $graph_«g.jsCall».removeCells([pyroLink]);
	           $temp_link=null;
	        }
	    });
	    
	    var disableRemove = [
			«FOR e:g.nodesAndEdges.filter[!isAbstract].filter[!removable] SEPARATOR ","»
				'«e.typeName»'
			«ENDFOR»
	    ];
		var disableResize = [
			«FOR e:g.nodesAndEdges.filter[!isAbstract].filter(GraphicalModelElement).filter[!resizable] SEPARATOR ","»
				'«e.typeName»'
			«ENDFOR»
		];
		var disableEdge = [
			«FOR e:g.nodes.filter[!isAbstract].filter[!connectable] SEPARATOR ","»
				'«e.typeName»'
		    «ENDFOR»
		];
		
	    init_event_system($paper_«g.jsCall»,$graph_«g.jsCall»,remove_cascade_node_«g.jsCall»,disableRemove,disableResize,disableEdge,highlight_valid_containers_«g.jsCall»);
	    
	    create_«g.jsCall»_map();
	    
	    //key bindings
	    $(window).off('keyup');
	    $(window).keyup(function(evt){
	    	// remove key
	    	if(evt.which == 46) {
	    		evt.preventDefault();
	    		//delete selected element
	    		cb_delete_selected();
	    		deselect_all_elements(null,$paper_«g.jsCall»,$graph_«g.jsCall»);
	    	}
	    });
	
	    //callback after initialization
	    initialized();
	    
	}
	
	
	function start_propagation_«g.jsCall»() {
		if($paper_«g.jsCall» != null) {
	    	block_user_interaction($paper_«g.jsCall»);
	    }
	    $_disable_events_«g.jsCall» = true;
	}
	function end_propagation_«g.jsCall»() {
		if($paper_«g.jsCall» != null) {
			unblock_user_interaction($paper_«g.jsCall»);
		}
	    $_disable_events_«g.jsCall» = false;
	}
	
	function destroy_«g.jsCall»() {
	    block_user_interaction($paper_«g.jsCall»);
	    deselect_all_elements(null,$paper_«g.jsCall»,$graph_«g.jsCall»);
	    $paper_«g.jsCall» = null;
	    $map_paper_«g.jsCall» = null;
	    $graph_«g.jsCall» = null;
	    $('#paper_«g.jsCall»').empty();
	    $('#paper_map').empty();
	}
	
	function highlight_valid_targets_«g.jsCall»(cell) {
		var validTargets = $cb_functions_«g.jsCall».cb_get_valid_targets(cell.model.attributes.attrs.id);
		validTargets.forEach(function(vt){
			var elem = findElementById(vt,$graph_«g.jsCall»);
			if(elem == null) {
				return;
			}
			var cellView = $paper_«g.jsCall».findViewByModel(elem);
			highlight_cell_valid_target(cellView);
		});
	}
	
	function highlight_valid_containers_«g.jsCall»(id, type, isReference) {
		var validContainers = $cb_functions_«g.jsCall».cb_get_valid_containers(id, type, isReference);
		validContainers.forEach(function(vt){
			var elem = findElementById(vt,$graph_«g.jsCall»);
			if(elem == null) {
				return;
			}
			var cellView = $paper_«g.jsCall».findViewByModel(elem);
			highlight_cell_valid_target(cellView);
		});
	}
	
	function refresh_checks_«g.jsCall»(checkResults) {
		$checkResults_«g.jsCall» = checkResults;
		unhighlight_all_elements_check($paper_«g.jsCall»,$graph_«g.jsCall»);
		checkResults.forEach(function(e) {
	        var elem = findElementById(e['id'],$graph_«g.jsCall»);
	        if(elem == null) {
	        	return;
	        }
	        var cell = $paper_«g.jsCall».findViewByModel(elem);
			highlight_cell_check(cell,e['level'],e['errors'],$graph_«g.jsCall»);
		});
	}
	
	function refresh_gluelines_«g.jsCall»(status) {
		if(status) {
			$«g.jsCall»_lib.enable();
		} else {
			$«g.jsCall»_lib.disable();
		}
	}
	
	
	function create_«g.jsCall»_map() {
		var map = $('#paper_map');
		if(map.length && $graph_«g.jsCall») {
			//create map
			$map_paper_«g.jsCall» = new joint.dia.Paper({
			    el: map,
			    width: 100,
			    height: 100,
			    model: $graph_«g.jsCall»,
			    gridSize: 1,
			    interactive:false
			});
			$graph_«g.jsCall».resetCells($graph_«g.jsCall».getCells());
			adjustMapDimensions($map_paper_«g.jsCall»);
			$rebuild_map_fun = function rebuild_«g.jsCall»_map_rect() {
			};
		}
	}
	
	/*
	 Settings handling methods to be called from the NG-App
	 */
	
	function update_routing_«g.jsCall»(routing,connector) {
		$router_«g.jsCall» = routing;
		$connector_«g.jsCall» = connector;
	    refresh_routing_«g.jsCall»();
	}
	
	function «g.jsCall»_jump(id) {
		jump_to_element(id,$graph_«g.jsCall»,$paper_«g.jsCall»,$cb_functions_«g.jsCall»);
	}
	
	function refresh_routing_«g.jsCall»() {
		update_edeg_routing($router_«g.jsCall»,$connector_«g.jsCall»,$graph_«g.jsCall»);
	}
	
	function update_scale_«g.jsCall»(scale) {
	    $paper_«g.jsCall».scale(scale);
	    fitContent($paper_«g.jsCall»,$map_paper_«g.jsCall»);
	}
	
	/*
	Element handling methods to be called from the NG-App
	 */
	
	/**
	 *
	 * @param cellId
	 * @param id
	 * @param styleArgs
	 */
	function update_element_«g.jsCall»(cellId,id,styleArgs,information,label,graph,paper) {
		graph = (typeof graph !== 'undefined') ?  graph : $graph_«g.jsCall»;
		if(styleArgs!==null) {
			var elem = findElementById(id,graph);

			if(cellId!=null && elem!=null){
				if(elem.id!=cellId) {
					// remove dublicate
					var dublicate = graph.getCell(cellId);
					remove_element(dublicate, paper)
				}
			}
			else if(cellId!=null && elem==null){
				elem =  graph.getCell(cellId);
			}
		    if(elem == null) {
				return;
		    }

		    paper = (typeof paper !== 'undefined') ?  paper : $paper_«g.jsCall»;
			var cell = paper.findViewByModel(elem);
			«FOR node:nodes»
			«{
				val styleForNode = node.styling(styles) as NodeStyle
				'''
				if(cell.model.attributes.type==='«node.typeName»') {				
					«styleForNode.updateStyleArgs(g.modelPackage as MGLModel)»
				}
				'''
			}»
			«ENDFOR»
			«FOR edge:edges»
			«{
				val styleForEdge = edge.styling(styles) as EdgeStyle
				'''
				if(cell.model.attributes.type==='«edge.typeName»') {
					«styleForEdge.updateStyleArgs»
				}
				'''
			}»
			«ENDFOR»
		}
	    update_element_internal(cellId,id,styleArgs,information,label,graph);
	}
	
	function update_element_highlight_«g.jsCall»(id,
			background_r,background_g,background_b,
			foreground_r,foreground_g,foreground_b
	) {
		var elem = findElementById(id,$graph_«g.jsCall»);
		if(elem == null) {
			return;
		}
		var cell = $paper_«g.jsCall».findViewByModel(elem);
		«FOR node:nodes.filter[styling(styles)!==null]»
			«{
				val styleForNode = node.styling(styles) as NodeStyle
				'''
					if(cell.model.attributes.type==='«node.typeName»') {
						«styleForNode.updateHighlight(g)»
					}
				'''
			}»
		«ENDFOR»
		«FOR edge:edges.filter[styling(styles)!==null]»
			«{
				'''
					if(cell.model.attributes.type==='«edge.typeName»') {
							return update_node_highlight_internal(
						    	cell,'.connection',
								background_r,background_g,background_b,
								foreground_r,foreground_g,foreground_b
							);
					}
				'''
			}»
		«ENDFOR»
	}

	function update_element_appearance_«g.jsCall»(id,shapeId,
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
		var elem = findElementById(id,$graph_«g.jsCall»);
		if(elem == null) {
			return;
		}
		var cell = $paper_«g.jsCall».findViewByModel(elem);
		«FOR node:nodes.filter[!styling(styles).appearanceProvider.nullOrEmpty]»
			«{
				val styleForNode = node.styling(styles) as NodeStyle
				'''
					if(cell.model.attributes.type==='«node.typeName»') {
						«styleForNode.updateAppearance(g)»
					}
				'''
			}»
		«ENDFOR»
		«FOR edge:edges.filter[!styling(styles).appearanceProvider.nullOrEmpty]»
			«{
				val styleForEdge = edge.styling(styles) as EdgeStyle
				'''
					if(cell.model.attributes.type==='«edge.typeName»') {
						«styleForEdge.updateAppearance»
					}
				'''
			}»
		«ENDFOR»
		
	}
	
	«FOR node:nodes»
		/**
		 * Build the WYSIWYG Palette for node type «node.name.escapeDart»
		 */
		function build_palette_«node.jsCall(g)»() {
			var graphP = new joint.dia.Graph();
			
			var paperP = new joint.dia.Paper({
			    el: $('#wysiwig«node.typeName.toString.replaceAll('\\.','_')»'),
			    width: '100%',
			    height: 50,
			    model: graphP,
			    gridSize: 1,
			    interactive:false,
			    elementView:constraint_element_view_palette()
			});
			
			var elem = new «node.shapeFQN»({
				position: {
			 	   x: 0,
			 	   y: 0
				},
			});
			graphP.addCell(elem);
			update_element_«g.jsCall»(elem.attributes.id,-1,[«node.styleDefaults.map['''"«it»"'''].join(",")»],"","",graphP,paperP);
			paperP.scaleContentToFit({padding:15});
			
			enable_wysiwyg_palette(
				paperP,
				graphP,
				$paper_«g.jsCall»,
				$graph_«g.jsCall»,
				'«node.typeName»',
				create_node_«g.jsCall»_after_drop
			);
		}
		
		
		/**
		 * creates a «node.name.escapeDart» node in position
		 * this method is called by th NG-App
		 * @param x
		 * @param y 
		 * @param id
		 * @param containerId
		 * @param styleArgs
		 * @returns {*}
		 */
		function create_node_«node.jsCall(g)»(x,y,width,height,id,containerId,styleArgs,information,label«IF node.isPrime»,primeId«ENDIF») {
		    var elem = findElementById(id,$graph_«g.jsCall»);
		    
			if(elem == null) {
				if(width != null && height != null) {
					elem = new «node.shapeFQN»({
			    		position: {
			    		    x: x,
			    		    y: y
			    		},
			    		size: {
			    		  	width:width,
			    		   	height:height
			    		},
			    		attrs:{
			    		    id:id,
			    		    disableMove:«IF node.movable»false«ELSE»true«ENDIF»,
			    		    disableResize:«IF node.resizable»false«ELSE»true«ENDIF»
			    		}
			    	});
				} else {
					elem = new «node.shapeFQN»({
				        position: {
				            x: x,
				            y: y
				        },
				        attrs:{
				            id:id,
				            disableMove:«IF node.movable»false«ELSE»true«ENDIF»,
				            disableResize:«IF node.resizable»false«ELSE»true«ENDIF»
				        }
				    });
				}
				add_node_internal(elem,$graph_«g.jsCall»,$paper_«g.jsCall»,$map_paper_«g.jsCall»);
			}
		    var pos = {x:x,y:y};
		    if(containerId>-1&&containerId!=$graphmodel_id_«g.jsCall»){
		    	var parent = findElementById(containerId,$graph_«g.jsCall»);
		    	if(parent == null) {
					return;
				}
		    	parent.embed(elem);
		    	pos.x -= parent.position().x;
		    	pos.y -= parent.position().y;
			}
			update_element_«g.jsCall»(elem.attributes.id,id,styleArgs,information,label);
		    if(!$_disable_events_«g.jsCall»){
		    	$cb_functions_«g.jsCall».cb_create_node_«node.jsCall(g)»(Math.round(pos.x), Math.round(pos.y),Math.round(elem.attributes.size.width),Math.round(elem.attributes.size.height), elem.attributes.id,containerId«IF node.isPrime»,parseInt(primeId)«ENDIF»);
		    }
		    return 'ready';
		}
		
		function move_node_«node.jsCall(g)»_hook(elem) {
			if(!$_disable_events_«g.jsCall»){
				var parentId = $graphmodel_id_«g.jsCall»;
				var pos = {x:elem.model.attributes.position.x,y:elem.model.attributes.position.y};
			    if(elem.model.attributes.parent != null){
			    	var parent = $graph_«g.jsCall».getCell(elem.model.attributes.parent);
			         parentId = parent.attributes.attrs.id;
			         pos.x -= parent.position().x;
			         pos.y -= parent.position().y;
			    }
			    //check if the container change was allowed
			    var valid = $cb_functions_«g.jsCall».cb_can_move_node(elem.model.attributes.attrs.id,parentId);
			    if(valid===true) {
			    	//movement has been valid
				    $cb_functions_«g.jsCall».cb_move_node_«node.jsCall(g)»(Math.round(pos.x),Math.round(pos.y),elem.model.attributes.attrs.id,parentId);
				    fitContent($paper_«g.jsCall»,$map_paper_«g.jsCall»);
			    } else {
			    	//movement is not valid and has to be reseted
			    	var preX = valid['x'];
			    	var preY = valid['y'];
			    	var diffX = preX - elem.model.attributes.position.x;
			    	var diffY = preY - elem.model.attributes.position.y;
			    	var preContainerId = valid['containerId'];
			    	//remove the containement
			    	if(elem.model.attributes.parent != null) {
				    	$graph_«g.jsCall».getCell(elem.model.attributes.parent).unembed($graph_«g.jsCall».getCell(elem.model.id));
				    }
			    	//check if the pre container was not the graphmodel
			    	if(preContainerId!==$graphmodel_id_«g.jsCall»)
			    	{
			    		//embed the node in the precontainer
				    	var parentCell = findElementById(preContainerId,$graph_«g.jsCall»);
				    	if(parentCell == null) {
				    		return;
				    	}
				    	parentCell.embed(elem.model);
				    	//move back
				    	elem.model.position(preX,preY,{ parentRealtive: true });
			    	}
			    	else {
				    	//move back
			    		elem.model.position(preX,preY);
			    	}
					«IF node instanceof NodeContainer»
						//move all children
						
						elem.model.getEmbeddedCells({deep:true}).forEach(function(child){
							var childCell = $graph_«g.jsCall».getCell(child);
							if(!childCell.isLink()) {
								var childPos = childCell.position();
								childCell.position(childPos.x+diffX,childPos.y+diffY);
							}
						
						});
					«ENDIF»
			    	
			    }
		        console.log("node «g.jsCall» change position");
		    }
		}
		
		
		/**
		 * moves the «node.name.escapeDart» node to another position, relative to its parent container
		 * if the container id is provided (containerId != -1). the node is
		 * embedded in the given container
		 * this method is called by th NG-App
		 * 
		 * @param x
		 * @param y
		 * @param id
		 * @param containerId
		 */
		function move_node_«node.jsCall(g)»(x,y,id,containerId) {
		    if(containerId==$graphmodel_id_«g.jsCall»){
		        move_node_internal(x,y,id,-1,$graph_«g.jsCall»);
		    } else {
		        move_node_internal(x,y,id,containerId,$graph_«g.jsCall»);
		    }
		    fitContent($paper_«g.jsCall»,$map_paper_«g.jsCall»);
		    return 'ready';
		}
		
		/**
		 * removes the «node.name.escapeDart» node by id
		 * this method is called by th NG-App
		 * @param id
		 */
		function remove_node_«node.jsCall(g)»(id) {
		    remove_node_internal(id,$graph_«g.jsCall»,$paper_«g.jsCall»);
		    fitContent($paper_«g.jsCall»,$map_paper_«g.jsCall»);
		    return 'ready';
		}
		
		/**
		 * resizes the «node.name.escapeDart» node by id depended on the 
		 * given absolute width and height
		 * this method is called by th NG-App
		 * @param width
		 * @param height
		 * @param id
		 */
		function resize_node_«node.jsCall(g)»(width,height,direction,id) {
		    resize_node_internal(width,height,direction,id,$graph_«g.jsCall»,$paper_«g.jsCall»);
		    return 'ready';
		}
		
		/**
		 * rotates the «node.name.escapeDart» node by id depended on the 
		 * given on the absolute angle
		 * this method is called by th NG-App
		 * @param angle
		 * @param id
		 */
		function rotate_node_«node.jsCall(g)»(angle,id) {
		    rotate_node_internal(angle,id,$graph_«g.jsCall»);
		    return 'ready';
		}
	«ENDFOR»
	
	/**
	* removes a edge with the given id from the canvas
	* this method is called by th NG-App
	* @param id
	*/
	function remove_edge__«g.jsCall(g)»(id) {
		remove_edge_internal(id,$graph_«g.jsCall»);
		return 'ready';
	}
	«FOR edge:edges»
		
		/**
		 * creates a «edge.name.escapeDart» edge connecting
		 * the nodes specified by the source and target id
		 * registers the listener for reconnnection and bendpoints
		 * this method is called by th NG-App
		 * @param sourceId
		 * @param targetId
		 * @param id
		 * @param styleArgs
		 */
		function create_edge_«edge.jsCall(g)»(sourceId,targetId,id,positions,styleArgs,information,label) {
		    var sourceN = findElementById(sourceId,$graph_«g.jsCall»);
		    var targetN = findElementById(targetId,$graph_«g.jsCall»);
			if(sourceN == null || targetN == null) {
				return;
			}
			
		    var link = new «edge.shapeFQN»({
		        attrs:{
		            id:id,
		            disableMove:«IF edge.movable»false«ELSE»true«ENDIF»,
		            disableResize:«IF edge.resizable»false«ELSE»true«ENDIF»,
		            styleArgs:styleArgs,
		            information:information,
		            label:label
		        },
		        source: { id: sourceN.id },
		        target: { id: targetN.id }
		    });
		    if(positions!==null){
			    link.set('vertices', positions.map(function (n) {
			    	return {x:n.x,y:n.y};
			    }));
		    }
		    add_edge_internal(link,$graph_«g.jsCall»,$router_«g.jsCall»,$connector_«g.jsCall»);
		    update_element_«g.jsCall»(link.attributes.id,id,styleArgs,information,label);
		    return 'ready';
		}
		
		/**
		 * removes the «edge.name.escapeDart» edge with the given id from the canvas
		 * this method is called by th NG-App
		 * @param id
		 */
		function remove_edge_«edge.jsCall(g)»(id) {
		    remove_edge_internal(id,$graph_«g.jsCall»);
		    return 'ready';
		}
		
		/**
		 * reconnets the «edge.name.escapeDart» edge to a new target and source node
		 * specified by their id
		 * this method is called by th NG-App
		 * @param sourceId
		 * @param targetId
		 * @param id
		 */
		function reconnect_edge_«edge.jsCall(g)»(sourceId,targetId,id) {
		    reconnect_edge_internal(sourceId,targetId,id,$graph_«g.jsCall»);
		    return 'ready';
		}
		
		function reconnect_edge_«edge.jsCall(g)»_hook(elem) {
			if(!$_disable_events_«g.jsCall»){
				var edgeId = elem.model.attributes.attrs.id;
				var source = elem.model.attributes.source.id;
				var sourceId = $graph_«g.jsCall».getCell(source).attributes.attrs.id;
				var target = elem.model.attributes.target.id;
				var targetId = $graph_«g.jsCall».getCell(target).attributes.attrs.id;
			    //check if the container change was allowed
			    var valid = $cb_functions_«g.jsCall».cb_can_reconnect_edge(edgeId,sourceId,targetId);
			    if(valid===true) {
			    	//reconnection has been valid
				    $cb_functions_«g.jsCall».cb_reconnect_edge_«edge.jsCall(g)»(sourceId,targetId,edgeId);
			    } else {
			    	//movement is not valid and has to be reseted
			    	var preSource = valid.source;
			    	var preTarget = valid.target;
			    	reconnect_edge_internal(preSource,preTarget,edgeId,$graph_«g.jsCall»);
			    	
			    }
		    }
		}
	«ENDFOR»
	
	/**
	 * updates the edge verticles
	 * specified by the edge id and all verticle positions
	 * this method is called by th NG-App
	 * @param point {x,y}
	 * @param id
	 */
	function update_bendpoint_«g.jsCall»(points,id) {
		removeMenus();
	    update_bendpoint_internal(points, id,$graph_«g.jsCall»);
	    return 'ready';
	}
	
	function create_prime_node_menu_«g.jsCall»(possibleNodes, x, y, absX, absY, containerId, elementId) {
		create_options_menu(
			absX, absY,
			possibleNodes,
			(e) => e,
			(e) => {
				switch(e){
					«FOR node:nodes.filter[isPrime]»
					case '«node.typeName»':{
						create_node_«node.jsCall(g)»(x,y,null,null,-1,containerId,"undefined",null,null,elementId);
					    break;
					}
					«ENDFOR»
				}
			}
		);
	}
	
	/**
	 *
	 * @param ev
	 */
	function drop_on_canvas_«g.jsCall»(ev) {
		removeMenus();
		unhighlight_all_element_valid_target($paper_«g.jsCall»,$graph_«g.jsCall»);
		ev.preventDefault();
		var rp = getRelativeScreenPosition(ev.clientX,ev.clientY,$paper_«g.jsCall»);
		var x = rp.x;
		var y = rp.y;
		var containerId = get_container_id_«g.jsCall»(rp);
		var content = JSON.parse(ev.dataTransfer.getData("text"));
		var typeName = content.typename;
		var elementId = content.elementid;
		//check prime node
		if(typeof elementId !== 'undefined' && typeName != ''){
			var possibleNodes = [];
			//for all prime nodes
			//check prime referenced type and super types
			«FOR pr:nodes.filter[prime].filter[primeCreatabel]»
				«{
					val subTypes = pr.primeReference.type.resolveSubTypesAndType
					'''
						if(
							«FOR sub:subTypes SEPARATOR "||"»
								typeName == '«sub.typeName»'
							«ENDFOR»
						)
					'''
				}»
				{
					if(is_containement_allowed_«g.jsCall»(rp,'«pr.typeName»')) {
						possibleNodes[possibleNodes.length] = '«pr.typeName»'; 
					}
				}
			«ENDFOR»
			if(possibleNodes.length==1){
				//one node possible
				switch (possibleNodes[0]) {
					//foreach node
					«FOR node:nodes.filter[isPrime]» 
						case '«node.typeName»':{
							create_node_«node.jsCall(g)»(x,y,null,null,-1,containerId,null,null,null,elementId);
						    break;
						}
					«ENDFOR»
				}
			}
			else{
				//multiple nodes possible
				//show selection
				create_prime_node_menu_«g.jsCall»(possibleNodes,x,y,ev.clientX,ev.clientY,containerId,elementId);
			}
			return;
		}
		if(typeName != ''){
		    // create node
		    create_node_«g.jsCall»_after_drop(x,y,typeName);
		}
		fitContent($paper_«g.jsCall»,$map_paper_«g.jsCall»);
	}
	
	function create_node_«g.jsCall»_after_drop(x,y,typeName) {
		if(is_containement_allowed_«g.jsCall»({x:x,y:y},typeName)) {
			var containerId = get_container_id_«g.jsCall»({x:x,y:y});
			switch (typeName) {
				//foreach node
				«FOR node:nodes» 
				case '«node.typeName»': {
					create_node_«node.jsCall(g)»(x,y,null,null,-1,containerId,null,-1);
				    break;
				}
				«ENDFOR»
			}
		}
	}
	
	function get_container_id_«g.jsCall»(rp) {
		var views = $paper_«g.jsCall».findViewsFromPoint(rp);
		if(views.length<=0){
			return $graphmodel_id_«g.jsCall»;
		}
		return views[views.length-1].model.attributes.attrs.id;
	}
	
	function is_containement_allowed_«g.jsCall»(rp,creatableTypeName) {
	    var views = $paper_«g.jsCall».findViewsFromPoint(rp);
	    if(views.length<=0){
	    	var targetNode = null;
	        //target is graphmodel
	        «g.containmentCheck(g)»
	    }
		«IF !nodes.filter(NodeContainer).empty»
			else {
			    var targetNode = views[views.length-1];
			    var targetType = targetNode.model.attributes.type;
				//foreach container
				«FOR container:nodes.filter(NodeContainer)»
					if(targetType==='«container.typeName»')
					{
						«container.containmentCheck(g)»
					}
			    «ENDFOR»
			}
		«ENDIF»
	    return false;
	}
	
	function confirm_drop_«g.jsCall»(ev) {
		ev.preventDefault();
	    ev.stopPropagation();
	    var rp = getRelativeScreenPosition(ev.clientX,ev.clientY,$paper_«g.jsCall»);
		var content;
		try {
			content = JSON.parse(ev.dataTransfer.getData("text"));
		} catch(e) {}
		var typeName = content? content['typename'] : "";
	    if(typeName != ''){
	    	if(!is_containement_allowed_«g.jsCall»(rp,typeName)) {
	       	   		ev.dataTransfer.effectAllowed= 'none';
	       	        ev.dataTransfer.dropEffect= 'none';
	       	 }
	    }
	}
	
	function reaAdjustDimensions_«g.jsCall»() {
	    reAdjustDimensions($paper_«g.jsCall», $map_paper_«g.jsCall»);
	}
	
	$(window).resize(function(e) {
		if(e != null && e.target != null && e.target.getElementsByTagName) { // widgets are resized
			if(e.target.getElementsByTagName('map').length>0) {
				adjustMapDimensions($map_paper_«g.jsCall»);
			} else if(e.target.getElementsByTagName('pyro-canvas').length>0) {
				adjustDimensions($paper_«g.jsCall»);
			}
		} else { // window is resized
			reaAdjustDimensions_«g.jsCall»();
		}
	});
	'''
	}
	
	def getStyleDefaults(Node node) {
		val ann = node.annotations.findFirst[name.equals("styleDefaults")]
		if(ann !== null) {
			return ann.value
		}
		#[]
	}
	
	def containmentCheck(ContainingElement ce, GraphModel g) {
		val containableElements = ce.resolvePossibleContainingTypes.filter(mgl.BoundedConstraint).toSet
		containmentCheckTemplate(
			containableElements,
			[t| ''' creatableTypeName === '«t.typeName»' '''],
			'''var groupSize;''',
			[concreteTypes, upperBound| 
				'''
					«IF upperBound>-1»
						groupSize = 0;
						«FOR t:concreteTypes»
							groupSize += getContainedByType(targetNode,'«t.typeName»',$graph_«g.jsCall»).length
						«ENDFOR»
						// check bounding constraint
						if(groupSize>=«upperBound») {
							// node can not be placed
							return false;
						}
					«ENDIF»
				'''
			],
			
			'''return true;'''
		)
	}
	
	def edgecreation(GraphModel g)
	{
		val nodes = g.nodes
		
		'''
			var sourceNode = $graph_«g.jsCall».getCell($temp_link.attributes.source.id);
			var sourceType = sourceNode.attributes.type;
			var targetNode = $graph_«g.jsCall».getCell(views[views.length-1].model.id);
			var targetType = targetNode.attributes.type;
			var outgoing = getOutgoing(sourceNode,$graph_«g.jsCall»);
			var incoming = getIncoming(targetNode,$graph_«g.jsCall»);
			//create the correct link
			var possibleEdges = {};
			var hypotheticalEdges = [];
			var markedEdges = [];
			
			«FOR source:nodes.filter[!isAbstract] SEPARATOR " else "
			»if(sourceType == '«source.typeName»')
			{
				«{
					val constraintsOutgoing =  new java.util.HashSet<mgl.BoundedConstraint>();
					val possibleOutgoing = source.possibleOutgoing.filter[!isAbstract]
					constraintsOutgoing += source.possibleOutgoingConstraints
					connectionCheckTemplate(
						constraintsOutgoing,
						null,
						null,
						[concreteTypesEdgeOutgoing, upperBoundEdgeOutgoing| 
							'''
								«IF upperBoundEdgeOutgoing > -1»
									var groupSizeOutgoing = 0;
									«FOR t:concreteTypesEdgeOutgoing»
										groupSizeOutgoing += filterEdgesByType(outgoing,'«t.typeName»').length;
									«ENDFOR»
								«ENDIF»
								«IF upperBoundEdgeOutgoing > -1»if(groupSizeOutgoing<«upperBoundEdgeOutgoing») {«ENDIF»«{
									val possibleTargets = concreteTypesEdgeOutgoing.filter(Edge).filter[!isAbstract].map[possibleTargets].flatten.filter[!isAbstract].toSet
									'''
										«FOR target:possibleTargets SEPARATOR " else "
 										»if(targetType == '«target.typeName»') {
											«{
												val possibleEdges = target.possibleIncoming.filter[!isAbstract].filter[possibleOutgoing.contains(it)]
												'''
													«{
														val constraintsIncoming = source.possibleIncomingConstraints
														connectionCheckTemplate(
															constraintsIncoming,
															null,
															null,
															[concreteTypesEdgeIncoming, upperBoundEdgeIncoming| 
																'''
																	«IF upperBoundEdgeIncoming > -1»
																		var groupSizeIncoming = 0;
																		«FOR t:concreteTypesEdgeIncoming»
																			groupSizeIncoming += filterEdgesByType(incoming,'«t.typeName»').length;
																		«ENDFOR»
																		// check bounding constraint
																		if(groupSizeIncoming<«upperBoundEdgeIncoming») {
																			// edges can not be applied
																			«FOR e: concreteTypesEdgeIncoming»
																				markedEdges.push('«e.typeName»');
																			«ENDFOR»
																		}
																	«ELSE»
																		// => unbounded constraint
																	«ENDIF»
																'''
															],
															'''
																// identify the hypothetical edges
																«FOR e:possibleEdges»
																	hypotheticalEdges.push('«e.typeName»');
																«ENDFOR»
															'''
														)
													}»
												'''
											}»
										}«
										ENDFOR»
									'''
								}»«IF upperBoundEdgeOutgoing > -1»}«ENDIF»
							'''
						],
						null
					)
				}»
			}«
			ENDFOR»
			
			// add non-marked edges to possibleEdges
			«{
																		
				val createableEdge = g.edges.filter[creatabel]
				'''
					«FOR e : createableEdge»
						if(hypotheticalEdges.indexOf('«e.typeName»')>-1 && markedEdges.indexOf('«e.typeName»')<=-1) {
							// Edge '«e.typeName»'
							var link = new «e.shapeFQN»({
							    source: { id: sourceNode.attributes.id }, target: { id: targetNode.attributes.id }
							});
							possibleEdges['«e.typeName»'] = {
								name: '«e.typeName»',
								type: link
							};
						}
					«ENDFOR»
				'''
			}»
			
			var possibleEdgeSize = hypotheticalEdges.length;
			if(possibleEdgeSize==1) {
				//only one edge can be created
				//so, create it
				$temp_link_multi = $temp_link;
				create_edge(targetNode,possibleEdges[Object.keys(possibleEdges)[0]].type,$paper_«g.jsCall»,$graph_«g.jsCall»,$map_paper_«g.jsCall»);
			}
			else if(possibleEdgeSize>1) {
				//multiple edge types possible
				//show menu
				create_edge_menu(targetNode,possibleEdges,evt.clientX,evt.clientY+$(document).scrollTop(),$paper_«g.jsCall»,$graph_«g.jsCall»);
			}
		'''
	}
	
	def addLinkListeners(String link,Edge edge,GraphModel g)
	'''
		«link».on('change:source', function() {
			if(!$_disable_events_«g.jsCall»){
				var source = «link».getSourceElement();
				var target = «link».getTargetElement();
				$cb_functions_«g.jsCall».cb_reconnect_edge_«edge.jsCall(g)»(source.attrs.id,target.attrs.id,«link».attrs.id);
				console.log("change link source");
			}
		});
		«link».on('change:target', function() {
			if(!$_disable_events_«g.jsCall»){
		        var source = «link».getSourceElement();
		        var target = «link».getTargetElement();
		        $cb_functions_«g.jsCall».cb_reconnect_edge_«edge.jsCall(g)»(source.attrs.id,target.attrs.id,«link».attrs.id);
		        console.log("change link target");
		    }
		});
		«link».on('change:vertices', function() {
			if(!$_disable_events_«g.jsCall»){
		        $cb_functions_«g.jsCall».cb_update_bendpoint_«edge.jsCall(g)»(«link».get('verticles'),«link».attrs.id);
			        console.log("change link target");
		    }
		});
	'''
	
	def Map<Integer,Edge> indexed(List<Edge> edges) {
		val result = new HashMap
		edges.forEach[e,i|result.put(i,e)]
		result
	}
	
	def Iterable<Edge> possibleEdges(GraphModel g,OutgoingEdgeElementConnection outgoing, IncomingEdgeElementConnection incoming) {
		if(outgoing.connectingEdges.empty && incoming.connectingEdges.empty) {
			return g.edges
		}
		if(outgoing.connectingEdges.empty && !incoming.connectingEdges.empty) {
			return incoming.connectingEdges.map[name.subTypesAndType(g).filter(Edge)].flatten
		}
		if(!outgoing.connectingEdges.empty && incoming.connectingEdges.empty) {
			return outgoing.connectingEdges.map[name.subTypesAndType(g).filter(Edge)].flatten
		}
		return incoming.connectingEdges.map[name.subTypesAndType(g).filter(Edge)].flatten.filter[e|outgoing.connectingEdges.map[name.subTypesAndType(g).filter(Edge)].flatten.toSet.contains(e)]
	}
	
	def updateStyleArgs(NodeStyle ns,MGLModel g)
	'''
		«FOR textShape:new Shapes(gc).collectSelectorTags(ns.mainShape,"x",0).entrySet.filter[n|new Shapes(gc).getIsTextual(n.key)]»
			cell.model.attr('«textShape.value»/text',  vsprintf("«textShape.key.value»", styleArgs) );
	    «ENDFOR»
	'''
	
	def updateStyleArgs(EdgeStyle es)
	'''
		cell.model.attributes.labels.forEach(function (label,idx) {
		«FOR decorator:es.decorator.filter[n|n.decoratorShape instanceof Text ||n.decoratorShape instanceof MultiText].indexed»
			if(label.attrs.hasOwnProperty('text.pyro«decorator.key»link')){
				cell.model.prop(['labels',idx,'attrs','text.pyro«decorator.key»link','text'], vsprintf("«decorator.value.decoratorShape.value»", styleArgs) );
«««				label.attrs['text.pyro«decorator.key»link'].text = "«decorator.value.decoratorShape.value.parsePlaceholder»";
			}
	    «ENDFOR»
		});
«««		cell.renderLabels();
	'''
	
	def updateAppearance(NodeStyle ns,GraphModel g)
	'''
		«FOR shape:new Shapes(gc).collectSelectorTags(ns.mainShape,"x",0).entrySet»
		if('«shape.value»'.endsWith(shapeId)) {
			update_node_apperance_internal(cell,'«shape.value»',
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
			);
		}
	    «ENDFOR»
	'''
	
	def updateHighlight(NodeStyle ns,GraphModel g) {
		val s = new Shapes(gc)
		val shape = '''«s.selector(ns.mainShape,"x",0)»'''
		'''
		return update_node_highlight_internal(cell,'«shape»',
			background_r,background_g,background_b,
			foreground_r,foreground_g,foreground_b
		);
		'''
	}
	
	
	
	def updateAppearance(EdgeStyle es)
	{
		val l = new LinkedHashMap
		val target = es.decorator.filter[it.location==1.0]
		val source = es.decorator.filter[it.location==0.0]
		es.decorator.forEach[n,idx|l.put(n,'''pyrox«idx»tag''')]	
	'''
		//update textual edge decorators
		cell.model.attributes.labels.forEach(function (label,idx) {
			«FOR decorator:es.decorator.filter[n|n.decoratorShape instanceof Text ||n.decoratorShape instanceof MultiText].indexed»
				if(shapeId == '«l.get(decorator)»' && label.attrs.hasOwnProperty('text.pyro«decorator.key»link')) {
					update_edge_text_apperance_internal(
						idx,cell,'text.pyro«decorator.key»link',
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
						);
				}
			«ENDFOR»
		});
		//update edge
		if(shapeId == 'root') {
		    update_node_apperance_internal(
		    	cell,'.connection',
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
		    );
		}
		«IF !target.isEmpty»
			//update target marker
			if(shapeId == '«l.get(target.get(0))»') {
			    update_node_apperance_internal(
			    	cell,'.marker-target',
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
			    );
			}
		«ENDIF»
		«IF !source.isEmpty»
			//update source marker
			if(shapeId == '«l.get(source.get(0))»') {
			    update_node_apperance_internal(
			    	cell,'.marker-source',
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
			    );
			}
		«ENDIF»
	'''
	}
	
	def getValue(EObject shape){
		if(shape instanceof Text){
			return shape.value
		}
		if(shape instanceof MultiText){
			return shape.value
		}
		return ""
	}
	
	
	def parsePlaceholder(String s){
		s.parseIterativePlaceholder.parseIndexedPlaceholder
	}
	
	def parseIterativePlaceholder(String s) {
		var result = ""
		var m = Pattern.compile("%s").matcher(s);
		var parameterIdx = 0;
		var preIdx = 0;
		//var postIdx = 0;
		while (m.find()) {
		    //add in between
    		result += s.substring(preIdx,m.start)
    		//set post index
    		preIdx = m.end
    		//replace
    		result += '''"+styleArgs[«parameterIdx»]+"'''
    		parameterIdx++
		}
		//suffix
		result += s.substring(preIdx)
	}
	
	def parseIndexedPlaceholder(String s) {
		//String::format(s, ) 
		var result = ""
		//%1$s
		var m = Pattern.compile("%\\d+\\$s").matcher(s);
		var parameterIdx = 0;
		var preIdx = 0;
		//var postIdx = 0;
		while (m.find()) {
		    var repString = m.group();
		    var start = m.start
		    //add in between
    		result += s.substring(preIdx,start)
    		//set post index
    		preIdx = m.end
    		//replace
    		result += '''"+styleArgs[«(repString.number-1)»]+"'''
    		parameterIdx++
		}
		//suffix
		result += s.substring(preIdx)
	}
	
	def getNumber(String input) {
		var Pattern lastIntPattern = Pattern.compile("\\d");
		var Matcher matcher = lastIntPattern.matcher(input);
		if (matcher.find()) {
		    var String someNumberStr = matcher.group();
		    return Integer.parseInt(someNumberStr);
		}
		return 0
	}

	
}
