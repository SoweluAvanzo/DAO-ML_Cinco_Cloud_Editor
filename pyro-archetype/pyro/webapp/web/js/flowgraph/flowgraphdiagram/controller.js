var $graph_flowgraphdiagram = null;
var $paper_flowgraphdiagram = null;
var $map_paper_flowgraphdiagram = null;
var $router_flowgraphdiagram = 'manhattan';
var $connector_flowgraphdiagram = 'rounded';
var $graphmodel_id_flowgraphdiagram = -1;
var $_disable_events_flowgraphdiagram = true;
var $checkResults_flowgraphdiagram = [];
var $cursor_manager_flowgraphdiagram = {};
var $flowgraphdiagram_lib = null;


var $cb_functions_flowgraphdiagram = {};


function load_flowgraphdiagram(
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
	cb_create_node_end_flowgraphdiagram,
	cb_remove_node_end_flowgraphdiagram,
	cb_move_node_end_flowgraphdiagram,
	cb_resize_node_end_flowgraphdiagram,
	cb_rotate_node_end_flowgraphdiagram,
	cb_create_node_swimlane_flowgraphdiagram,
	cb_remove_node_swimlane_flowgraphdiagram,
	cb_move_node_swimlane_flowgraphdiagram,
	cb_resize_node_swimlane_flowgraphdiagram,
	cb_rotate_node_swimlane_flowgraphdiagram,
	cb_create_node_subflowgraph_flowgraphdiagram,
	cb_remove_node_subflowgraph_flowgraphdiagram,
	cb_move_node_subflowgraph_flowgraphdiagram,
	cb_resize_node_subflowgraph_flowgraphdiagram,
	cb_rotate_node_subflowgraph_flowgraphdiagram,
	cb_create_edge_transition_flowgraphdiagram,
	cb_remove_edge_transition_flowgraphdiagram,
	cb_reconnect_edge_transition_flowgraphdiagram,
	cb_create_node_start_flowgraphdiagram,
	cb_remove_node_start_flowgraphdiagram,
	cb_move_node_start_flowgraphdiagram,
	cb_resize_node_start_flowgraphdiagram,
	cb_rotate_node_start_flowgraphdiagram,
	cb_create_node_activity_flowgraphdiagram,
	cb_remove_node_activity_flowgraphdiagram,
	cb_move_node_activity_flowgraphdiagram,
	cb_resize_node_activity_flowgraphdiagram,
	cb_rotate_node_activity_flowgraphdiagram,
	cb_create_edge_labeledtransition_flowgraphdiagram,
	cb_remove_edge_labeledtransition_flowgraphdiagram,
	cb_reconnect_edge_labeledtransition_flowgraphdiagram
    
) {
    $router_flowgraphdiagram = router;
    $graphmodel_id_flowgraphdiagram = graphmodelid;
    $connector_flowgraphdiagram = connector;
    $checkResults_flowgraphdiagram = [];
    $property_persist_fun = cb_property_update;

    $cb_functions_flowgraphdiagram = {
    	cb_element_selected:cb_element_selected,
	    cb_update_bendpoint:cb_update_bendpoint,
	    cb_can_move_node:cb_can_move_node,
	    cb_can_reconnect_edge:cb_can_reconnect_edge,
	    cb_get_valid_targets:cb_get_valid_targets,
	    cb_is_valid_connection:cb_is_valid_connection,
	    cb_get_valid_containers:cb_get_valid_containers,
	    cb_is_valid_container:cb_is_valid_container,
	    cb_cursor_moved:cb_cursor_moved,
		cb_create_node_end_flowgraphdiagram:cb_create_node_end_flowgraphdiagram,
		cb_remove_node_end_flowgraphdiagram:cb_remove_node_end_flowgraphdiagram,
		cb_move_node_end_flowgraphdiagram:cb_move_node_end_flowgraphdiagram,
		cb_resize_node_end_flowgraphdiagram:cb_resize_node_end_flowgraphdiagram,
		cb_rotate_node_end_flowgraphdiagram:cb_rotate_node_end_flowgraphdiagram,
		cb_create_node_swimlane_flowgraphdiagram:cb_create_node_swimlane_flowgraphdiagram,
		cb_remove_node_swimlane_flowgraphdiagram:cb_remove_node_swimlane_flowgraphdiagram,
		cb_move_node_swimlane_flowgraphdiagram:cb_move_node_swimlane_flowgraphdiagram,
		cb_resize_node_swimlane_flowgraphdiagram:cb_resize_node_swimlane_flowgraphdiagram,
		cb_rotate_node_swimlane_flowgraphdiagram:cb_rotate_node_swimlane_flowgraphdiagram,
		cb_create_node_subflowgraph_flowgraphdiagram:cb_create_node_subflowgraph_flowgraphdiagram,
		cb_remove_node_subflowgraph_flowgraphdiagram:cb_remove_node_subflowgraph_flowgraphdiagram,
		cb_move_node_subflowgraph_flowgraphdiagram:cb_move_node_subflowgraph_flowgraphdiagram,
		cb_resize_node_subflowgraph_flowgraphdiagram:cb_resize_node_subflowgraph_flowgraphdiagram,
		cb_rotate_node_subflowgraph_flowgraphdiagram:cb_rotate_node_subflowgraph_flowgraphdiagram,
		cb_create_edge_transition_flowgraphdiagram:cb_create_edge_transition_flowgraphdiagram,
		cb_remove_edge_transition_flowgraphdiagram:cb_remove_edge_transition_flowgraphdiagram,
		cb_reconnect_edge_transition_flowgraphdiagram:cb_reconnect_edge_transition_flowgraphdiagram,
		cb_create_node_start_flowgraphdiagram:cb_create_node_start_flowgraphdiagram,
		cb_remove_node_start_flowgraphdiagram:cb_remove_node_start_flowgraphdiagram,
		cb_move_node_start_flowgraphdiagram:cb_move_node_start_flowgraphdiagram,
		cb_resize_node_start_flowgraphdiagram:cb_resize_node_start_flowgraphdiagram,
		cb_rotate_node_start_flowgraphdiagram:cb_rotate_node_start_flowgraphdiagram,
		cb_create_node_activity_flowgraphdiagram:cb_create_node_activity_flowgraphdiagram,
		cb_remove_node_activity_flowgraphdiagram:cb_remove_node_activity_flowgraphdiagram,
		cb_move_node_activity_flowgraphdiagram:cb_move_node_activity_flowgraphdiagram,
		cb_resize_node_activity_flowgraphdiagram:cb_resize_node_activity_flowgraphdiagram,
		cb_rotate_node_activity_flowgraphdiagram:cb_rotate_node_activity_flowgraphdiagram,
		cb_create_edge_labeledtransition_flowgraphdiagram:cb_create_edge_labeledtransition_flowgraphdiagram,
		cb_remove_edge_labeledtransition_flowgraphdiagram:cb_remove_edge_labeledtransition_flowgraphdiagram,
		cb_reconnect_edge_labeledtransition_flowgraphdiagram:cb_reconnect_edge_labeledtransition_flowgraphdiagram
    };

	
    $graph_flowgraphdiagram = new joint.dia.Graph;
    $paper_flowgraphdiagram = new joint.dia.Paper({

        el: document.getElementById('paper_flowgraphdiagram'),
        width: w,
        height: h,
        gridSize: 5,
        drawGrid: true,
        model: $graph_flowgraphdiagram,
        snapLinks: false,
        linkPinning: false,
        elementView: constraint_element_view($graph_flowgraphdiagram,highlight_valid_targets_flowgraphdiagram,highlight_valid_containers_flowgraphdiagram),
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
        		return $cb_functions_flowgraphdiagram.cb_is_valid_container(nodeId,graphmodelid);
        	}
        	var parentId = parentView.model.attributes.attrs.id;
            return $cb_functions_flowgraphdiagram.cb_is_valid_container(nodeId,parentId);
        },

        validateConnection: function(cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
        	if(cellViewS == null || cellViewT == null || linkView == null) {
        		return false;
        	}
            var sourceId = cellViewS.model.attributes.attrs.id;
            var targetId = cellViewT.model.attributes.attrs.id;
            var edgeId = linkView.model.attributes.attrs.id;
            return $cb_functions_flowgraphdiagram.cb_is_valid_connection(edgeId,sourceId,targetId);
        }
    });
    $paper_flowgraphdiagram.options.multiLinks = false;
    $paper_flowgraphdiagram.options.markAvailable = true;
    $paper_flowgraphdiagram.options.restrictTranslate=false;
    $paper_flowgraphdiagram.options.drawGrid= { name: 'mesh', args: { color: 'black' }};
	$paper_flowgraphdiagram.scale(scale);
	$paper_flowgraphdiagram.options.defaultConnectionPoint = {
	    name: 'boundary',
	    args: {
	        sticky: true
	    }
	};
	
    /*
    Register event listener triggering the callbacks to the NG-App
     */
	 
	 $flowgraphdiagram_lib = pyroGlueLines(joint,$graph_flowgraphdiagram, $paper_flowgraphdiagram, {
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
	$graph_flowgraphdiagram.on('change:position', _.debounce($flowgraphdiagram_lib.handleChangePosition, 10));

    /**
     * Graphmodel (Canvas) has been clicked
     */
    $paper_flowgraphdiagram.on('blank:pointerup', function(evt, x, y) {
    	remove_edge_creation_menu();
    	remove_context_menu();
        deselect_all_elements(null,$paper_flowgraphdiagram,$graph_flowgraphdiagram);
        cb_graphmodel_selected();
        console.log("graphmodel clicked");
    });
    $paper_flowgraphdiagram.on('blank:pointerdown', function(evt, x, y) {
        evt.data = { action: 'paper_drag', paper_drag_x: x, paper_drag_y: y };
    });
    
    $paper_flowgraphdiagram.on('blank:pointermove', function(evt, x, y) {
        var trans = $paper_flowgraphdiagram.translate();
        if(x - evt.data.x != 0 || y - evt.data.y != 0) {
        	var sc = $paper_flowgraphdiagram.scale();
            $paper_flowgraphdiagram.translate(
               (trans.tx) + Math.round((x - evt.data.paper_drag_x) * sc.sx),
               (trans.ty) + Math.round((y - evt.data.paper_drag_y) * sc.sy)
            );
        }
    });
    
    $paper_flowgraphdiagram.on('blank:mousewheel', function(evt, x, y,delta) {
		zoom_paper($paper_flowgraphdiagram,evt,x,y,delta);
	});
	$paper_flowgraphdiagram.on('cell:mousewheel', function(cv,evt, x, y,delta) {
		zoom_paper($paper_flowgraphdiagram,evt,x,y,delta);
	});
	
	(function() {
		/**
		 * emit the cursor position of the user on the paper
		 * fire mousemove event every n seconds
		 */
	
	    var UPDATE_DELAY = 2000; // ms
	
		function handleMousemove(evt) {
			if ($paper_flowgraphdiagram == null || $cursor_manager_resizestart) return; 
			var pointOnPaper = $paper_flowgraphdiagram.clientToLocalPoint(evt.clientX, evt.clientY);
			$cb_functions_flowgraphdiagram.cb_cursor_moved(Math.round(pointOnPaper.x),Math.round(pointOnPaper.y));
		}
		
		var handleMousemoveThrottled = _.throttle(handleMousemove, UPDATE_DELAY);
	
		$(document.getElementById('paper_flowgraphdiagram')).on('mousemove', handleMousemoveThrottled);
	}());
	
	$cursor_manager_flowgraphdiagram = (function(){
	
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
	      $graph_flowgraphdiagram.addCell(cursor);
	      
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
	* Link has been selected
	* change selection
	* show properties
	*/
	$paper_flowgraphdiagram.on('link:options', function(cellView,evt, x, y) {
		remove_edge_creation_menu();
		remove_context_menu();
		update_selection(cellView,$paper_flowgraphdiagram,$graph_flowgraphdiagram);
		cb_element_selected(cellView.model.attributes.attrs.id);
		console.log("link clicked");
	});
    
    /**
	 * Canvas has been right clicked
	 * Show context menu for the graphmodel
	 * including registered custome actions
	 */
	$paper_flowgraphdiagram.on('blank:contextmenu', function(evt,x,y){
		//fetch ca for graphmodel
		var pos = getPaperToScreenPosition(x,y,$paper_flowgraphdiagram);
		cb_get_custom_actions($graphmodel_id_flowgraphdiagram,Math.round(pos.x),Math.round(pos.y+$(document).scrollTop()),x,y);
		console.log("graphmodel context menu clicked");
	});
	
	/**
	* Graphmodel has been double clicked
	* Activate double click action
	*/
	$paper_flowgraphdiagram.on('blank:pointerdblclick', function(evt,x,y){
		//fetch ca for element
		cb_fire_dbc_actions($graphmodel_id_flowgraphdiagram);
		console.log("graphmodel double clicked");
	});
    
    /**
     * Element has been right clicked
     * Show context menu for the element
     * including registered custome actions
     */
    $paper_flowgraphdiagram.on('cell:contextmenu', function(cellView,evt,x,y){
    	//fetch ca for element
    	var pos = getPaperToScreenPosition(x,y,$paper_flowgraphdiagram);

    	cb_get_custom_actions(cellView.model.attributes.attrs.id,Math.round(pos.x),Math.round(pos.y+$(document).scrollTop()),x,y);
    });
    
    /**
     * Element has been double clicked
     * Activate double click action
     */
    $paper_flowgraphdiagram.on('cell:pointerdblclick', function(cellView,evt,x,y){
    	//fetch ca for element
    	cb_fire_dbc_actions(cellView.model.attributes.attrs.id);
    });
    
    
    /**
     * Element has been selected
     * change selection
     * show properties
     */
    $paper_flowgraphdiagram.on('cell:pointerup', function(cellView,evt) {
		if(cellView.model.attributes.attrs.isDeleted!==true) {
			
			//check for select disabled and try for container
			while(cellView.model.attributes.attrs.disableSelect===true) {
				// get container
				if(cellView.model.attributes.parent == null){
					cb_graphmodel_selected();
					return;
				}
				cellView = $paper_flowgraphdiagram.findViewByModel($graph_flowgraphdiagram.getCell(cellView.model.attributes.parent));
			}
			
		    update_selection(cellView,$paper_flowgraphdiagram,$graph_flowgraphdiagram);
		    cb_element_selected(cellView.model.attributes.attrs.id);
		    if(cellView.model.attributes.type=='flowgraph.End'){
		    	//check if container has changed
		    	move_node_end_flowgraphdiagram_hook(cellView);
		    	if(!cellView.model.attributes.attrs.disableResize) {
		        	$cb_functions_flowgraphdiagram.cb_resize_node_end_flowgraphdiagram(Math.round(cellView.model.attributes.size.width),Math.round(cellView.model.attributes.size.height),$node_resize_last_direction,cellView.model.attributes.attrs.id);	 	        		
		    	}
		    }
		    if(cellView.model.attributes.type=='flowgraph.Swimlane'){
		    	//check if container has changed
		    	move_node_swimlane_flowgraphdiagram_hook(cellView);
		    	if(!cellView.model.attributes.attrs.disableResize) {
		        	$cb_functions_flowgraphdiagram.cb_resize_node_swimlane_flowgraphdiagram(Math.round(cellView.model.attributes.size.width),Math.round(cellView.model.attributes.size.height),$node_resize_last_direction,cellView.model.attributes.attrs.id);	 	        		
		    	}
		    }
		    if(cellView.model.attributes.type=='flowgraph.SubFlowGraph'){
		    	//check if container has changed
		    	move_node_subflowgraph_flowgraphdiagram_hook(cellView);
		    	if(!cellView.model.attributes.attrs.disableResize) {
		        	$cb_functions_flowgraphdiagram.cb_resize_node_subflowgraph_flowgraphdiagram(Math.round(cellView.model.attributes.size.width),Math.round(cellView.model.attributes.size.height),$node_resize_last_direction,cellView.model.attributes.attrs.id);	 	        		
		    	}
		    }
		    if(cellView.model.attributes.type=='flowgraph.Start'){
		    	//check if container has changed
		    	move_node_start_flowgraphdiagram_hook(cellView);
		    	if(!cellView.model.attributes.attrs.disableResize) {
		        	$cb_functions_flowgraphdiagram.cb_resize_node_start_flowgraphdiagram(Math.round(cellView.model.attributes.size.width),Math.round(cellView.model.attributes.size.height),$node_resize_last_direction,cellView.model.attributes.attrs.id);	 	        		
		    	}
		    }
		    if(cellView.model.attributes.type=='flowgraph.Activity'){
		    	//check if container has changed
		    	move_node_activity_flowgraphdiagram_hook(cellView);
		    	if(!cellView.model.attributes.attrs.disableResize) {
		        	$cb_functions_flowgraphdiagram.cb_resize_node_activity_flowgraphdiagram(Math.round(cellView.model.attributes.size.width),Math.round(cellView.model.attributes.size.height),$node_resize_last_direction,cellView.model.attributes.attrs.id);	 	        		
		    	}
		    }
		    if(cellView.model.attributes.type=='flowgraph.Transition'){
		    	var source = $graph_flowgraphdiagram.getCell(cellView.model.attributes.source.id);
		    	var target = $graph_flowgraphdiagram.getCell(cellView.model.attributes.target.id);
		    	reconnect_edge_transition_flowgraphdiagram_hook(cellView);
		    	$cb_functions_flowgraphdiagram.cb_update_bendpoint(
		    		cellView.model.attributes.vertices,
		    		cellView.model.attributes.attrs.id
		    	);
		    }
		    if(cellView.model.attributes.type=='flowgraph.LabeledTransition'){
		    	var source = $graph_flowgraphdiagram.getCell(cellView.model.attributes.source.id);
		    	var target = $graph_flowgraphdiagram.getCell(cellView.model.attributes.target.id);
		    	reconnect_edge_labeledtransition_flowgraphdiagram_hook(cellView);
		    	$cb_functions_flowgraphdiagram.cb_update_bendpoint(
		    		cellView.model.attributes.vertices,
		    		cellView.model.attributes.attrs.id
		    	);
		    }
		     console.log(cellView);
		     console.log("element clicked");
		}
     });

    /**
     * Element has been added
     * change selection
     * show properties
     */
    $graph_flowgraphdiagram.on('add', function(cellView) {
    	if(!$_disable_events_flowgraphdiagram && cellView.attributes.type!=='pyro.PyroLink' && cellView.attributes.type!=='pyro.GlueLine' ){
    		update_selection($paper_flowgraphdiagram.findViewByModel(cellView),$paper_flowgraphdiagram,$graph_flowgraphdiagram);
	        //for each edge
	        if(cellView.attributes.type==='flowgraph.Transition') {
	            var link = $graph_flowgraphdiagram.getCell(cellView.attributes.id);
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
	            refresh_routing_flowgraphdiagram();
	            $cb_functions_flowgraphdiagram.cb_create_edge_transition_flowgraphdiagram(
	              source.attributes.attrs.id,
	              target.attributes.attrs.id,
	              cellView.attributes.id,
	              cellView.attributes.vertices
	            );
	        }
	        if(cellView.attributes.type==='flowgraph.LabeledTransition') {
	            var link = $graph_flowgraphdiagram.getCell(cellView.attributes.id);
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
	            refresh_routing_flowgraphdiagram();
	            $cb_functions_flowgraphdiagram.cb_create_edge_labeledtransition_flowgraphdiagram(
	              source.attributes.attrs.id,
	              target.attributes.attrs.id,
	              cellView.attributes.id,
	              cellView.attributes.vertices
	            );
	        }
	        console.log(cellView);
	        console.log("element added");
        }
    });

	function remove_cascade_node_flowgraphdiagram(cellView) {
		if(!$_disable_events_flowgraphdiagram){
			 deselect_all_elements(null,$paper_flowgraphdiagram,$graph_flowgraphdiagram);
			 //trigger callback
			 cb_graphmodel_selected();
			 //foreach node
			 if(cellView.attributes.type==='flowgraph.End'){
			     $cb_functions_flowgraphdiagram.cb_remove_node_end_flowgraphdiagram(cellView.attributes.attrs.id);
			 }
			 if(cellView.attributes.type==='flowgraph.Swimlane'){
			     $cb_functions_flowgraphdiagram.cb_remove_node_swimlane_flowgraphdiagram(cellView.attributes.attrs.id);
			 }
			 if(cellView.attributes.type==='flowgraph.SubFlowGraph'){
			     $cb_functions_flowgraphdiagram.cb_remove_node_subflowgraph_flowgraphdiagram(cellView.attributes.attrs.id);
			 }
			 if(cellView.attributes.type==='flowgraph.Start'){
			     $cb_functions_flowgraphdiagram.cb_remove_node_start_flowgraphdiagram(cellView.attributes.attrs.id);
			 }
			 if(cellView.attributes.type==='flowgraph.Activity'){
			     $cb_functions_flowgraphdiagram.cb_remove_node_activity_flowgraphdiagram(cellView.attributes.attrs.id);
			 }
			 fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
		}
	
	}

    /**
     * Element has been removed
     * change selection to the graphmodel
     * show properties
     */
    $graph_flowgraphdiagram.on('remove', function(cellView) {
    	if(cellView.attributes.type == 'pyro.GlueLine') {
    		return;
    	}
    	if(!$_disable_events_flowgraphdiagram){
	        deselect_all_elements(null,$paper_flowgraphdiagram,$graph_flowgraphdiagram);
	        //trigger callback
	        cb_graphmodel_selected();
	        //foreach edge
			if(cellView.attributes.type==='flowgraph.Transition'){
				cellView.attributes.attrs.isDeleted = true;
			    $cb_functions_flowgraphdiagram.cb_remove_edge_transition_flowgraphdiagram(cellView.attributes.attrs.id);
			}
			if(cellView.attributes.type==='flowgraph.LabeledTransition'){
				cellView.attributes.attrs.isDeleted = true;
			    $cb_functions_flowgraphdiagram.cb_remove_edge_labeledtransition_flowgraphdiagram(cellView.attributes.attrs.id);
			}
	        console.log(cellView);
	        console.log("element removed");
        }
    });
    
	$("html").off('dragend');
	$("html").on("dragend", function(event) {
	    event.preventDefault();  
	    event.stopPropagation();
	    unhighlight_all_element_valid_target($paper_flowgraphdiagram,$graph_flowgraphdiagram);
	});
    $(document).off('mouseup');
    $(document).mouseup(function (evt) {
        $mouse_clicked_menu=false;
        if($temp_link!==null && !$edge_menu_shown)
        {
           unhighlight_all_element_valid_target($paper_flowgraphdiagram,$graph_flowgraphdiagram);
           var rp = getRelativeScreenPosition(evt.clientX,evt.clientY,$paper_flowgraphdiagram);
           var views = $paper_flowgraphdiagram.findViewsFromPoint(rp);
           if(views.length > 0)
           {
             var sourceNode = $graph_flowgraphdiagram.getCell($temp_link.attributes.source.id);
             var sourceType = sourceNode.attributes.type;
             var targetNode = $graph_flowgraphdiagram.getCell(views[views.length-1].model.id);
             var targetType = targetNode.attributes.type;
             var outgoing = getOutgoing(sourceNode,$graph_flowgraphdiagram);
             var incoming = getIncoming(targetNode,$graph_flowgraphdiagram);
             //create the correct link
             var possibleEdges = {};
             //get possible edges
             //depends on cardinallity
             if(sourceType == 'flowgraph.End')
             {
             }
             if(sourceType == 'flowgraph.Swimlane')
             {
             }
             if(sourceType == 'flowgraph.SubFlowGraph')
             {
             	//check bound group condition
             	var groupSize0 = 0;
             		groupSize0 += filterEdgesByType(outgoing,'flowgraph.LabeledTransition').length;
             	//check cardinality
             	if(true)
             	{
             	   if(targetType == 'flowgraph.End')
             	   {
             	   	
             	   		var incommingGroupSize0 = 0;
             	   		incommingGroupSize0 += filterEdgesByType(incoming,'flowgraph.Transition').length;
             	   		incommingGroupSize0 += filterEdgesByType(incoming,'flowgraph.LabeledTransition').length;
             	   		if(true)
             	   		{
             	   			var link0 = new joint.shapes.flowgraph.LabeledTransition({
             	   			    source: { id: sourceNode.attributes.id }, target: { id: targetNode.attributes.id }
             	   			});
             	   			possibleEdges['flowgraph.LabeledTransition'] = {
             	   				name: 'flowgraph.LabeledTransition',
             	   				type: link0
             	   			};
             	   		}
             	   }
             	}
             }
             if(sourceType == 'flowgraph.Start')
             {
             	//check bound group condition
             	var groupSize0 = 0;
             		groupSize0 += filterEdgesByType(outgoing,'flowgraph.Transition').length;
             	//check cardinality
             	if(groupSize0<1)
             	{
             	   if(targetType == 'flowgraph.End')
             	   {
             	   	
             	   		var incommingGroupSize0 = 0;
             	   		incommingGroupSize0 += filterEdgesByType(incoming,'flowgraph.Transition').length;
             	   		incommingGroupSize0 += filterEdgesByType(incoming,'flowgraph.LabeledTransition').length;
             	   		if(true)
             	   		{
             	   			var link0 = new joint.shapes.flowgraph.Transition({
             	   			    source: { id: sourceNode.attributes.id }, target: { id: targetNode.attributes.id }
             	   			});
             	   			possibleEdges['flowgraph.Transition'] = {
             	   				name: 'flowgraph.Transition',
             	   				type: link0
             	   			};
             	   		}
             	   }
             	}
             }
             if(sourceType == 'flowgraph.Activity')
             {
             	//check bound group condition
             	var groupSize0 = 0;
             		groupSize0 += filterEdgesByType(outgoing,'flowgraph.LabeledTransition').length;
             	//check cardinality
             	if(true)
             	{
             	   if(targetType == 'flowgraph.End')
             	   {
             	   	
             	   		var incommingGroupSize0 = 0;
             	   		incommingGroupSize0 += filterEdgesByType(incoming,'flowgraph.Transition').length;
             	   		incommingGroupSize0 += filterEdgesByType(incoming,'flowgraph.LabeledTransition').length;
             	   		if(true)
             	   		{
             	   			var link0 = new joint.shapes.flowgraph.LabeledTransition({
             	   			    source: { id: sourceNode.attributes.id }, target: { id: targetNode.attributes.id }
             	   			});
             	   			possibleEdges['flowgraph.LabeledTransition'] = {
             	   				name: 'flowgraph.LabeledTransition',
             	   				type: link0
             	   			};
             	   		}
             	   }
             	}
             }
             var possibleEdgeSize = Object.keys(possibleEdges).length;
             if(possibleEdgeSize==1)
             {
             	//only one edge can be created
             	//so, create it
             	$temp_link_multi = $temp_link;
             	create_edge(targetNode,possibleEdges[Object.keys(possibleEdges)[0]].type,$paper_flowgraphdiagram,$graph_flowgraphdiagram,$map_paper_flowgraphdiagram);
             }
             else if(possibleEdgeSize>1)
             {
             	//multiple edge types possible
             	//show menu
             	create_edge_menu(targetNode,possibleEdges,evt.clientX,evt.clientY+$(document).scrollTop(),$paper_flowgraphdiagram,$graph_flowgraphdiagram);
             }
           }
           var pyroLink = $graph_flowgraphdiagram.getCell($temp_link.id);
           $graph_flowgraphdiagram.removeCells([pyroLink]);
           $temp_link=null;
        }
    });
    
    var disableRemove = [
    ];
	var disableResize = [
	];
	var disableEdge = [
		'flowgraph.End',
		'flowgraph.Swimlane'
	];

    init_event_system($paper_flowgraphdiagram,$graph_flowgraphdiagram,remove_cascade_node_flowgraphdiagram,disableRemove,disableResize,disableEdge,highlight_valid_containers_flowgraphdiagram);
    
    create_flowgraphdiagram_map();
    
    //key bindings
    $(window).off('keyup');
    $(window).keyup(function(evt){
    	// remove key
    	if(evt.which == 46) {
    		evt.preventDefault();
    		//delete selected element
    		cb_delete_selected();
    		deselect_all_elements(null,$paper_flowgraphdiagram,$graph_flowgraphdiagram);
    	}
    });

    //callback after initialization
    initialized();
    
}


function start_propagation_flowgraphdiagram() {
    block_user_interaction($paper_flowgraphdiagram);
    $_disable_events_flowgraphdiagram = true;
}
function end_propagation_flowgraphdiagram() {
    unblock_user_interaction($paper_flowgraphdiagram);
    $_disable_events_flowgraphdiagram = false;
}

function destroy_flowgraphdiagram() {
    block_user_interaction($paper_flowgraphdiagram);
    deselect_all_elements(null,$paper_flowgraphdiagram,$graph_flowgraphdiagram);
    $paper_flowgraphdiagram = null;
    $map_paper_flowgraphdiagram = null;
    $graph_flowgraphdiagram = null;
    $('#paper_flowgraphdiagram').empty();
    $('#paper_map_flowgraphdiagram').empty();
}

function highlight_valid_targets_flowgraphdiagram(cell) {
	var validTargets = $cb_functions_flowgraphdiagram.cb_get_valid_targets(cell.model.attributes.attrs.id);
	validTargets.forEach(function(vt){
		var elem = findElementById(vt,$graph_flowgraphdiagram);
		var cellView = $paper_flowgraphdiagram.findViewByModel(elem);
		highlight_cell_valid_target(cellView);
	});
}

function highlight_valid_containers_flowgraphdiagram(id,type) {
	var validContainers = $cb_functions_flowgraphdiagram.cb_get_valid_containers(id,type);
	validContainers.forEach(function(vt){
		var elem = findElementById(vt,$graph_flowgraphdiagram);
		var cellView = $paper_flowgraphdiagram.findViewByModel(elem);
		highlight_cell_valid_target(cellView);
	});
}

function refresh_checks_flowgraphdiagram(checkResults) {
	$checkResults_flowgraphdiagram = checkResults;
	unhighlight_all_elements_check($paper_flowgraphdiagram,$graph_flowgraphdiagram);
	checkResults.forEach(function(e) {
        var elem = findElementById(e['id'],$graph_flowgraphdiagram);
        if(elem == null) {
         return;
        }
        var cell = $paper_flowgraphdiagram.findViewByModel(elem);
		highlight_cell_check(cell,e['level'],e['errors'],$graph_flowgraphdiagram);
	});
}

function refresh_gluelines_flowgraphdiagram(status) {
	if(status) {
		$flowgraphdiagram_lib.enable();
	} else {
		$flowgraphdiagram_lib.disable();
	}
}


function create_flowgraphdiagram_map() {
	var map = $('#paper_map_flowgraphdiagram');
	if(map.length && $graph_flowgraphdiagram) {
		//create map
		$map_paper_flowgraphdiagram = new joint.dia.Paper({
		    el: map,
		    width: 300,
		    height: 300,
		    model: $graph_flowgraphdiagram,
		    gridSize: 1,
		    interactive:false
		});
		$graph_flowgraphdiagram.resetCells($graph_flowgraphdiagram.getCells());
		$map_paper_flowgraphdiagram.scaleContentToFit();
		$rebuild_map_fun = function rebuild_flowgraphdiagram_map_rect() {
		};
	}
}

/*
 Settings handling methods to be called from the NG-App
 */

function update_routing_flowgraphdiagram(routing,connector) {
	$router_flowgraphdiagram = routing;
	$connector_flowgraphdiagram = connector;
    refresh_routing_flowgraphdiagram();
}

function flowgraphdiagram_jump(id) { 
	jump_to_element(id,$graph_flowgraphdiagram,$paper_flowgraphdiagram,$cb_functions_flowgraphdiagram);
}

function refresh_routing_flowgraphdiagram() {
	update_edeg_routing($router_flowgraphdiagram,$connector_flowgraphdiagram,$graph_flowgraphdiagram);
}

function update_scale_flowgraphdiagram(scale) {
    $paper_flowgraphdiagram.scale(scale);
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
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
function update_element_flowgraphdiagram(cellId,id,styleArgs,information,label,graph,paper) {
	graph = (typeof graph !== 'undefined') ?  graph : $graph_flowgraphdiagram;
	if(styleArgs!==null) {
		var elem = findElementById(id,graph);
		if(cellId!=null&&elem==null){
		   elem =  graph.getCell(cellId);
		}
	    if(elem == null) {
	     return;
	    }
	    paper = (typeof paper !== 'undefined') ?  paper : $paper_flowgraphdiagram;
		var cell = paper.findViewByModel(elem);
		if(cell.model.attributes.type==='flowgraph.End') {
		}
		if(cell.model.attributes.type==='flowgraph.Swimlane') {
			cell.model.attr('text.pyro0x0tag/text',""+styleArgs[0]+"");
		}
		if(cell.model.attributes.type==='flowgraph.SubFlowGraph') {
			cell.model.attr('text.pyro0x0tag/text',""+styleArgs[0]+"");
		}
		if(cell.model.attributes.type==='flowgraph.Start') {
		}
		if(cell.model.attributes.type==='flowgraph.Activity') {
			cell.model.attr('text.pyro0x0tag/text',""+styleArgs[0]+"");
		}
		if(cell.model.attributes.type==='flowgraph.Transition') {
			cell.model.attributes.labels.forEach(function (label,idx) {
			});
		}
		if(cell.model.attributes.type==='flowgraph.LabeledTransition') {
			cell.model.attributes.labels.forEach(function (label,idx) {
			if(label.attrs.hasOwnProperty('text.pyro0link')){
				cell.model.prop(['labels',idx,'attrs','text.pyro0link','text'],""+styleArgs[0]+"");
			}
			});
		}
	}
    update_element_internal(cellId,id,styleArgs,information,label,graph);
}

function update_element_highlight_flowgraphdiagram(id,
		background_r,background_g,background_b,
		foreground_r,foreground_g,foreground_b
) {
	var elem = findElementById(id,$graph_flowgraphdiagram);
	var cell = $paper_flowgraphdiagram.findViewByModel(elem);
	if(cell.model.attributes.type==='flowgraph.End') {
		return update_node_highlight_internal(cell,'ellipse.pyrox0tag',
			background_r,background_g,background_b,
			foreground_r,foreground_g,foreground_b
		);
	}
	if(cell.model.attributes.type==='flowgraph.Swimlane') {
		return update_node_highlight_internal(cell,'rect.pyrox0tag',
			background_r,background_g,background_b,
			foreground_r,foreground_g,foreground_b
		);
	}
	if(cell.model.attributes.type==='flowgraph.SubFlowGraph') {
		return update_node_highlight_internal(cell,'rect.pyrox0tag',
			background_r,background_g,background_b,
			foreground_r,foreground_g,foreground_b
		);
	}
	if(cell.model.attributes.type==='flowgraph.Start') {
		return update_node_highlight_internal(cell,'ellipse.pyrox0tag',
			background_r,background_g,background_b,
			foreground_r,foreground_g,foreground_b
		);
	}
	if(cell.model.attributes.type==='flowgraph.Activity') {
		return update_node_highlight_internal(cell,'rect.pyrox0tag',
			background_r,background_g,background_b,
			foreground_r,foreground_g,foreground_b
		);
	}
	if(cell.model.attributes.type==='flowgraph.Transition') {
			return update_node_highlight_internal(
		    	cell,'.connection',
				background_r,background_g,background_b,
				foreground_r,foreground_g,foreground_b
			);
	}
	if(cell.model.attributes.type==='flowgraph.LabeledTransition') {
			return update_node_highlight_internal(
		    	cell,'.connection',
				background_r,background_g,background_b,
				foreground_r,foreground_g,foreground_b
			);
	}
}

function update_element_appearance_flowgraphdiagram(id,shapeId,
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
	var elem = findElementById(id,$graph_flowgraphdiagram);
	var cell = $paper_flowgraphdiagram.findViewByModel(elem);
	
}

/**
 * Build the WYSIWYG Palette for node type End
 */
function build_palette_end_flowgraphdiagram() {
	var graphP = new joint.dia.Graph();
	
	var paperP = new joint.dia.Paper({
	    el: $('#wysiwigflowgraph_End'),
	    width: '100%',
	    height: 50,
	    model: graphP,
	    gridSize: 1,
	    interactive:false,
	    elementView:constraint_element_view_palette()
	});
	
	var elem = new joint.shapes.flowgraph.End({
		position: {
	 	   x: 0,
	 	   y: 0
		},
	});
	graphP.addCell(elem);
	update_element_flowgraphdiagram(elem.attributes.id,-1,[],"","",graphP,paperP);
	paperP.scaleContentToFit({padding:15});
	
	enable_wysiwyg_palette(
		paperP,
		graphP,
		$paper_flowgraphdiagram,
		$graph_flowgraphdiagram,
		'flowgraph.End',
		create_node_flowgraphdiagram_after_drop
	);
}


/**
 * creates a End node in position
 * this method is called by th NG-App
 * @param x
 * @param y 
 * @param id
 * @param containerId
 * @param styleArgs
 * @returns {*}
 */
function create_node_end_flowgraphdiagram(x,y,width,height,id,containerId,styleArgs,information,label) {
    var elem = null;
    if(width != null && height != null) {
    	elem = new joint.shapes.flowgraph.End({
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
    		    disableMove:false,
    		    disableResize:false
    		}
    	});
    } else {
	    elem = new joint.shapes.flowgraph.End({
	        position: {
	            x: x,
	            y: y
	        },
	        attrs:{
	            id:id,
	            disableMove:false,
	            disableResize:false
	        }
	    });
    }
    add_node_internal(elem,$graph_flowgraphdiagram,$paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    var pos = {x:x,y:y};
    if(containerId>-1&&containerId!=$graphmodel_id_flowgraphdiagram){
    	var parent = findElementById(containerId,$graph_flowgraphdiagram);
    	parent.embed(elem);
    	pos.x -= parent.position().x;
    	pos.y -= parent.position().y;
	}
	update_element_flowgraphdiagram(elem.attributes.id,id,styleArgs,information,label);
    if(!$_disable_events_flowgraphdiagram){
    	$cb_functions_flowgraphdiagram.cb_create_node_end_flowgraphdiagram(Math.round(pos.x), Math.round(pos.y),Math.round(elem.attributes.size.width),Math.round(elem.attributes.size.height), elem.attributes.id,containerId);
    }
    return 'ready';
}

function move_node_end_flowgraphdiagram_hook(elem) {
	if(!$_disable_events_flowgraphdiagram){
		var parentId = $graphmodel_id_flowgraphdiagram;
		var pos = {x:elem.model.attributes.position.x,y:elem.model.attributes.position.y};
	    if(elem.model.attributes.parent != null){
	    	var parent = $graph_flowgraphdiagram.getCell(elem.model.attributes.parent);
	         parentId = parent.attributes.attrs.id;
	         pos.x -= parent.position().x;
	         pos.y -= parent.position().y;
	    }
	    //check if the container change was allowed
	    var valid = $cb_functions_flowgraphdiagram.cb_can_move_node(elem.model.attributes.attrs.id,parentId);
	    if(valid===true) {
	    	//movement has been valid
		    $cb_functions_flowgraphdiagram.cb_move_node_end_flowgraphdiagram(Math.round(pos.x),Math.round(pos.y),elem.model.attributes.attrs.id,parentId);
		    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
	    } else {
	    	//movement is not valid and has to be reseted
	    	var preX = valid['x'];
	    	var preY = valid['y'];
	    	var diffX = preX - elem.model.attributes.position.x;
	    	var diffY = preY - elem.model.attributes.position.y;
	    	var preContainerId = valid['containerId'];
	    	//remove the containement
	    	if(elem.model.attributes.parent != null) {
		    	$graph_flowgraphdiagram.getCell(elem.model.attributes.parent).unembed($graph_flowgraphdiagram.getCell(elem.model.id));
		    }
	    	//check if the pre container was not the graphmodel
	    	if(preContainerId!==$graphmodel_id_flowgraphdiagram)
	    	{
	    		//embed the node in the precontainer
		    	var parentCell = findElementById(preContainerId,$graph_flowgraphdiagram);
		    	parentCell.embed(elem.model);
		    	//move back
		    	elem.model.position(preX,preY,{ parentRealtive: true });
	    	}
	    	else {
		    	//move back
	    		elem.model.position(preX,preY);
	    	}
	    	
	    }
        console.log("node flowgraphdiagram change position");
    }
}


/**
 * moves the End node to another position, relative to its parent container
 * if the container id is provided (containerId != -1). the node is
 * embedded in the given container
 * this method is called by th NG-App
 * 
 * @param x
 * @param y
 * @param id
 * @param containerId
 */
function move_node_end_flowgraphdiagram(x,y,id,containerId) {
    if(containerId==$graphmodel_id_flowgraphdiagram){
        move_node_internal(x,y,id,-1,$graph_flowgraphdiagram);
    } else {
        move_node_internal(x,y,id,containerId,$graph_flowgraphdiagram);
    }
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * removes the End node by id
 * this method is called by th NG-App
 * @param id
 */
function remove_node_end_flowgraphdiagram(id) {
    remove_node_internal(id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * resizes the End node by id depended on the 
 * given absolute width and height
 * this method is called by th NG-App
 * @param width
 * @param height
 * @param id
 */
function resize_node_end_flowgraphdiagram(width,height,direction,id) {
    resize_node_internal(width,height,direction,id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    return 'ready';
}

/**
 * rotates the End node by id depended on the 
 * given on the absolute angle
 * this method is called by th NG-App
 * @param angle
 * @param id
 */
function rotate_node_end_flowgraphdiagram(angle,id) {
    rotate_node_internal(angle,id,$graph_flowgraphdiagram);
    return 'ready';
}
/**
 * Build the WYSIWYG Palette for node type Swimlane
 */
function build_palette_swimlane_flowgraphdiagram() {
	var graphP = new joint.dia.Graph();
	
	var paperP = new joint.dia.Paper({
	    el: $('#wysiwigflowgraph_Swimlane'),
	    width: '100%',
	    height: 50,
	    model: graphP,
	    gridSize: 1,
	    interactive:false,
	    elementView:constraint_element_view_palette()
	});
	
	var elem = new joint.shapes.flowgraph.Swimlane({
		position: {
	 	   x: 0,
	 	   y: 0
		},
	});
	graphP.addCell(elem);
	update_element_flowgraphdiagram(elem.attributes.id,-1,[],"","",graphP,paperP);
	paperP.scaleContentToFit({padding:15});
	
	enable_wysiwyg_palette(
		paperP,
		graphP,
		$paper_flowgraphdiagram,
		$graph_flowgraphdiagram,
		'flowgraph.Swimlane',
		create_node_flowgraphdiagram_after_drop
	);
}


/**
 * creates a Swimlane node in position
 * this method is called by th NG-App
 * @param x
 * @param y 
 * @param id
 * @param containerId
 * @param styleArgs
 * @returns {*}
 */
function create_node_swimlane_flowgraphdiagram(x,y,width,height,id,containerId,styleArgs,information,label) {
    var elem = null;
    if(width != null && height != null) {
    	elem = new joint.shapes.flowgraph.Swimlane({
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
    		    disableMove:false,
    		    disableResize:false
    		}
    	});
    } else {
	    elem = new joint.shapes.flowgraph.Swimlane({
	        position: {
	            x: x,
	            y: y
	        },
	        attrs:{
	            id:id,
	            disableMove:false,
	            disableResize:false
	        }
	    });
    }
    add_node_internal(elem,$graph_flowgraphdiagram,$paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    var pos = {x:x,y:y};
    if(containerId>-1&&containerId!=$graphmodel_id_flowgraphdiagram){
    	var parent = findElementById(containerId,$graph_flowgraphdiagram);
    	parent.embed(elem);
    	pos.x -= parent.position().x;
    	pos.y -= parent.position().y;
	}
	update_element_flowgraphdiagram(elem.attributes.id,id,styleArgs,information,label);
    if(!$_disable_events_flowgraphdiagram){
    	$cb_functions_flowgraphdiagram.cb_create_node_swimlane_flowgraphdiagram(Math.round(pos.x), Math.round(pos.y),Math.round(elem.attributes.size.width),Math.round(elem.attributes.size.height), elem.attributes.id,containerId);
    }
    return 'ready';
}

function move_node_swimlane_flowgraphdiagram_hook(elem) {
	if(!$_disable_events_flowgraphdiagram){
		var parentId = $graphmodel_id_flowgraphdiagram;
		var pos = {x:elem.model.attributes.position.x,y:elem.model.attributes.position.y};
	    if(elem.model.attributes.parent != null){
	    	var parent = $graph_flowgraphdiagram.getCell(elem.model.attributes.parent);
	         parentId = parent.attributes.attrs.id;
	         pos.x -= parent.position().x;
	         pos.y -= parent.position().y;
	    }
	    //check if the container change was allowed
	    var valid = $cb_functions_flowgraphdiagram.cb_can_move_node(elem.model.attributes.attrs.id,parentId);
	    if(valid===true) {
	    	//movement has been valid
		    $cb_functions_flowgraphdiagram.cb_move_node_swimlane_flowgraphdiagram(Math.round(pos.x),Math.round(pos.y),elem.model.attributes.attrs.id,parentId);
		    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
	    } else {
	    	//movement is not valid and has to be reseted
	    	var preX = valid['x'];
	    	var preY = valid['y'];
	    	var diffX = preX - elem.model.attributes.position.x;
	    	var diffY = preY - elem.model.attributes.position.y;
	    	var preContainerId = valid['containerId'];
	    	//remove the containement
	    	if(elem.model.attributes.parent != null) {
		    	$graph_flowgraphdiagram.getCell(elem.model.attributes.parent).unembed($graph_flowgraphdiagram.getCell(elem.model.id));
		    }
	    	//check if the pre container was not the graphmodel
	    	if(preContainerId!==$graphmodel_id_flowgraphdiagram)
	    	{
	    		//embed the node in the precontainer
		    	var parentCell = findElementById(preContainerId,$graph_flowgraphdiagram);
		    	parentCell.embed(elem.model);
		    	//move back
		    	elem.model.position(preX,preY,{ parentRealtive: true });
	    	}
	    	else {
		    	//move back
	    		elem.model.position(preX,preY);
	    	}
			//move all children
			
			elem.model.getEmbeddedCells({deep:true}).forEach(function(child){
				var childCell = $graph_flowgraphdiagram.getCell(child);
				if(!childCell.isLink()) {
					var childPos = childCell.position();
					childCell.position(childPos.x+diffX,childPos.y+diffY);
				}
			
			});
	    	
	    }
        console.log("node flowgraphdiagram change position");
    }
}


/**
 * moves the Swimlane node to another position, relative to its parent container
 * if the container id is provided (containerId != -1). the node is
 * embedded in the given container
 * this method is called by th NG-App
 * 
 * @param x
 * @param y
 * @param id
 * @param containerId
 */
function move_node_swimlane_flowgraphdiagram(x,y,id,containerId) {
    if(containerId==$graphmodel_id_flowgraphdiagram){
        move_node_internal(x,y,id,-1,$graph_flowgraphdiagram);
    } else {
        move_node_internal(x,y,id,containerId,$graph_flowgraphdiagram);
    }
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * removes the Swimlane node by id
 * this method is called by th NG-App
 * @param id
 */
function remove_node_swimlane_flowgraphdiagram(id) {
    remove_node_internal(id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * resizes the Swimlane node by id depended on the 
 * given absolute width and height
 * this method is called by th NG-App
 * @param width
 * @param height
 * @param id
 */
function resize_node_swimlane_flowgraphdiagram(width,height,direction,id) {
    resize_node_internal(width,height,direction,id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    return 'ready';
}

/**
 * rotates the Swimlane node by id depended on the 
 * given on the absolute angle
 * this method is called by th NG-App
 * @param angle
 * @param id
 */
function rotate_node_swimlane_flowgraphdiagram(angle,id) {
    rotate_node_internal(angle,id,$graph_flowgraphdiagram);
    return 'ready';
}
/**
 * Build the WYSIWYG Palette for node type SubFlowGraph
 */
function build_palette_subflowgraph_flowgraphdiagram() {
	var graphP = new joint.dia.Graph();
	
	var paperP = new joint.dia.Paper({
	    el: $('#wysiwigflowgraph_SubFlowGraph'),
	    width: '100%',
	    height: 50,
	    model: graphP,
	    gridSize: 1,
	    interactive:false,
	    elementView:constraint_element_view_palette()
	});
	
	var elem = new joint.shapes.flowgraph.SubFlowGraph({
		position: {
	 	   x: 0,
	 	   y: 0
		},
	});
	graphP.addCell(elem);
	update_element_flowgraphdiagram(elem.attributes.id,-1,[],"","",graphP,paperP);
	paperP.scaleContentToFit({padding:15});
	
	enable_wysiwyg_palette(
		paperP,
		graphP,
		$paper_flowgraphdiagram,
		$graph_flowgraphdiagram,
		'flowgraph.SubFlowGraph',
		create_node_flowgraphdiagram_after_drop
	);
}


/**
 * creates a SubFlowGraph node in position
 * this method is called by th NG-App
 * @param x
 * @param y 
 * @param id
 * @param containerId
 * @param styleArgs
 * @returns {*}
 */
function create_node_subflowgraph_flowgraphdiagram(x,y,width,height,id,containerId,styleArgs,information,label,primeId) {
    var elem = null;
    if(width != null && height != null) {
    	elem = new joint.shapes.flowgraph.SubFlowGraph({
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
    		    disableMove:false,
    		    disableResize:false
    		}
    	});
    } else {
	    elem = new joint.shapes.flowgraph.SubFlowGraph({
	        position: {
	            x: x,
	            y: y
	        },
	        attrs:{
	            id:id,
	            disableMove:false,
	            disableResize:false
	        }
	    });
    }
    add_node_internal(elem,$graph_flowgraphdiagram,$paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    var pos = {x:x,y:y};
    if(containerId>-1&&containerId!=$graphmodel_id_flowgraphdiagram){
    	var parent = findElementById(containerId,$graph_flowgraphdiagram);
    	parent.embed(elem);
    	pos.x -= parent.position().x;
    	pos.y -= parent.position().y;
	}
	update_element_flowgraphdiagram(elem.attributes.id,id,styleArgs,information,label);
    if(!$_disable_events_flowgraphdiagram){
    	$cb_functions_flowgraphdiagram.cb_create_node_subflowgraph_flowgraphdiagram(Math.round(pos.x), Math.round(pos.y),Math.round(elem.attributes.size.width),Math.round(elem.attributes.size.height), elem.attributes.id,containerId,parseInt(primeId));
    }
    return 'ready';
}

function move_node_subflowgraph_flowgraphdiagram_hook(elem) {
	if(!$_disable_events_flowgraphdiagram){
		var parentId = $graphmodel_id_flowgraphdiagram;
		var pos = {x:elem.model.attributes.position.x,y:elem.model.attributes.position.y};
	    if(elem.model.attributes.parent != null){
	    	var parent = $graph_flowgraphdiagram.getCell(elem.model.attributes.parent);
	         parentId = parent.attributes.attrs.id;
	         pos.x -= parent.position().x;
	         pos.y -= parent.position().y;
	    }
	    //check if the container change was allowed
	    var valid = $cb_functions_flowgraphdiagram.cb_can_move_node(elem.model.attributes.attrs.id,parentId);
	    if(valid===true) {
	    	//movement has been valid
		    $cb_functions_flowgraphdiagram.cb_move_node_subflowgraph_flowgraphdiagram(Math.round(pos.x),Math.round(pos.y),elem.model.attributes.attrs.id,parentId);
		    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
	    } else {
	    	//movement is not valid and has to be reseted
	    	var preX = valid['x'];
	    	var preY = valid['y'];
	    	var diffX = preX - elem.model.attributes.position.x;
	    	var diffY = preY - elem.model.attributes.position.y;
	    	var preContainerId = valid['containerId'];
	    	//remove the containement
	    	if(elem.model.attributes.parent != null) {
		    	$graph_flowgraphdiagram.getCell(elem.model.attributes.parent).unembed($graph_flowgraphdiagram.getCell(elem.model.id));
		    }
	    	//check if the pre container was not the graphmodel
	    	if(preContainerId!==$graphmodel_id_flowgraphdiagram)
	    	{
	    		//embed the node in the precontainer
		    	var parentCell = findElementById(preContainerId,$graph_flowgraphdiagram);
		    	parentCell.embed(elem.model);
		    	//move back
		    	elem.model.position(preX,preY,{ parentRealtive: true });
	    	}
	    	else {
		    	//move back
	    		elem.model.position(preX,preY);
	    	}
	    	
	    }
        console.log("node flowgraphdiagram change position");
    }
}


/**
 * moves the SubFlowGraph node to another position, relative to its parent container
 * if the container id is provided (containerId != -1). the node is
 * embedded in the given container
 * this method is called by th NG-App
 * 
 * @param x
 * @param y
 * @param id
 * @param containerId
 */
function move_node_subflowgraph_flowgraphdiagram(x,y,id,containerId) {
    if(containerId==$graphmodel_id_flowgraphdiagram){
        move_node_internal(x,y,id,-1,$graph_flowgraphdiagram);
    } else {
        move_node_internal(x,y,id,containerId,$graph_flowgraphdiagram);
    }
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * removes the SubFlowGraph node by id
 * this method is called by th NG-App
 * @param id
 */
function remove_node_subflowgraph_flowgraphdiagram(id) {
    remove_node_internal(id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * resizes the SubFlowGraph node by id depended on the 
 * given absolute width and height
 * this method is called by th NG-App
 * @param width
 * @param height
 * @param id
 */
function resize_node_subflowgraph_flowgraphdiagram(width,height,direction,id) {
    resize_node_internal(width,height,direction,id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    return 'ready';
}

/**
 * rotates the SubFlowGraph node by id depended on the 
 * given on the absolute angle
 * this method is called by th NG-App
 * @param angle
 * @param id
 */
function rotate_node_subflowgraph_flowgraphdiagram(angle,id) {
    rotate_node_internal(angle,id,$graph_flowgraphdiagram);
    return 'ready';
}
/**
 * Build the WYSIWYG Palette for node type Start
 */
function build_palette_start_flowgraphdiagram() {
	var graphP = new joint.dia.Graph();
	
	var paperP = new joint.dia.Paper({
	    el: $('#wysiwigflowgraph_Start'),
	    width: '100%',
	    height: 50,
	    model: graphP,
	    gridSize: 1,
	    interactive:false,
	    elementView:constraint_element_view_palette()
	});
	
	var elem = new joint.shapes.flowgraph.Start({
		position: {
	 	   x: 0,
	 	   y: 0
		},
	});
	graphP.addCell(elem);
	update_element_flowgraphdiagram(elem.attributes.id,-1,[],"","",graphP,paperP);
	paperP.scaleContentToFit({padding:15});
	
	enable_wysiwyg_palette(
		paperP,
		graphP,
		$paper_flowgraphdiagram,
		$graph_flowgraphdiagram,
		'flowgraph.Start',
		create_node_flowgraphdiagram_after_drop
	);
}


/**
 * creates a Start node in position
 * this method is called by th NG-App
 * @param x
 * @param y 
 * @param id
 * @param containerId
 * @param styleArgs
 * @returns {*}
 */
function create_node_start_flowgraphdiagram(x,y,width,height,id,containerId,styleArgs,information,label) {
    var elem = null;
    if(width != null && height != null) {
    	elem = new joint.shapes.flowgraph.Start({
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
    		    disableMove:false,
    		    disableResize:false
    		}
    	});
    } else {
	    elem = new joint.shapes.flowgraph.Start({
	        position: {
	            x: x,
	            y: y
	        },
	        attrs:{
	            id:id,
	            disableMove:false,
	            disableResize:false
	        }
	    });
    }
    add_node_internal(elem,$graph_flowgraphdiagram,$paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    var pos = {x:x,y:y};
    if(containerId>-1&&containerId!=$graphmodel_id_flowgraphdiagram){
    	var parent = findElementById(containerId,$graph_flowgraphdiagram);
    	parent.embed(elem);
    	pos.x -= parent.position().x;
    	pos.y -= parent.position().y;
	}
	update_element_flowgraphdiagram(elem.attributes.id,id,styleArgs,information,label);
    if(!$_disable_events_flowgraphdiagram){
    	$cb_functions_flowgraphdiagram.cb_create_node_start_flowgraphdiagram(Math.round(pos.x), Math.round(pos.y),Math.round(elem.attributes.size.width),Math.round(elem.attributes.size.height), elem.attributes.id,containerId);
    }
    return 'ready';
}

function move_node_start_flowgraphdiagram_hook(elem) {
	if(!$_disable_events_flowgraphdiagram){
		var parentId = $graphmodel_id_flowgraphdiagram;
		var pos = {x:elem.model.attributes.position.x,y:elem.model.attributes.position.y};
	    if(elem.model.attributes.parent != null){
	    	var parent = $graph_flowgraphdiagram.getCell(elem.model.attributes.parent);
	         parentId = parent.attributes.attrs.id;
	         pos.x -= parent.position().x;
	         pos.y -= parent.position().y;
	    }
	    //check if the container change was allowed
	    var valid = $cb_functions_flowgraphdiagram.cb_can_move_node(elem.model.attributes.attrs.id,parentId);
	    if(valid===true) {
	    	//movement has been valid
		    $cb_functions_flowgraphdiagram.cb_move_node_start_flowgraphdiagram(Math.round(pos.x),Math.round(pos.y),elem.model.attributes.attrs.id,parentId);
		    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
	    } else {
	    	//movement is not valid and has to be reseted
	    	var preX = valid['x'];
	    	var preY = valid['y'];
	    	var diffX = preX - elem.model.attributes.position.x;
	    	var diffY = preY - elem.model.attributes.position.y;
	    	var preContainerId = valid['containerId'];
	    	//remove the containement
	    	if(elem.model.attributes.parent != null) {
		    	$graph_flowgraphdiagram.getCell(elem.model.attributes.parent).unembed($graph_flowgraphdiagram.getCell(elem.model.id));
		    }
	    	//check if the pre container was not the graphmodel
	    	if(preContainerId!==$graphmodel_id_flowgraphdiagram)
	    	{
	    		//embed the node in the precontainer
		    	var parentCell = findElementById(preContainerId,$graph_flowgraphdiagram);
		    	parentCell.embed(elem.model);
		    	//move back
		    	elem.model.position(preX,preY,{ parentRealtive: true });
	    	}
	    	else {
		    	//move back
	    		elem.model.position(preX,preY);
	    	}
	    	
	    }
        console.log("node flowgraphdiagram change position");
    }
}


/**
 * moves the Start node to another position, relative to its parent container
 * if the container id is provided (containerId != -1). the node is
 * embedded in the given container
 * this method is called by th NG-App
 * 
 * @param x
 * @param y
 * @param id
 * @param containerId
 */
function move_node_start_flowgraphdiagram(x,y,id,containerId) {
    if(containerId==$graphmodel_id_flowgraphdiagram){
        move_node_internal(x,y,id,-1,$graph_flowgraphdiagram);
    } else {
        move_node_internal(x,y,id,containerId,$graph_flowgraphdiagram);
    }
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * removes the Start node by id
 * this method is called by th NG-App
 * @param id
 */
function remove_node_start_flowgraphdiagram(id) {
    remove_node_internal(id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * resizes the Start node by id depended on the 
 * given absolute width and height
 * this method is called by th NG-App
 * @param width
 * @param height
 * @param id
 */
function resize_node_start_flowgraphdiagram(width,height,direction,id) {
    resize_node_internal(width,height,direction,id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    return 'ready';
}

/**
 * rotates the Start node by id depended on the 
 * given on the absolute angle
 * this method is called by th NG-App
 * @param angle
 * @param id
 */
function rotate_node_start_flowgraphdiagram(angle,id) {
    rotate_node_internal(angle,id,$graph_flowgraphdiagram);
    return 'ready';
}
/**
 * Build the WYSIWYG Palette for node type Activity
 */
function build_palette_activity_flowgraphdiagram() {
	var graphP = new joint.dia.Graph();
	
	var paperP = new joint.dia.Paper({
	    el: $('#wysiwigflowgraph_Activity'),
	    width: '100%',
	    height: 50,
	    model: graphP,
	    gridSize: 1,
	    interactive:false,
	    elementView:constraint_element_view_palette()
	});
	
	var elem = new joint.shapes.flowgraph.Activity({
		position: {
	 	   x: 0,
	 	   y: 0
		},
	});
	graphP.addCell(elem);
	update_element_flowgraphdiagram(elem.attributes.id,-1,[],"","",graphP,paperP);
	paperP.scaleContentToFit({padding:15});
	
	enable_wysiwyg_palette(
		paperP,
		graphP,
		$paper_flowgraphdiagram,
		$graph_flowgraphdiagram,
		'flowgraph.Activity',
		create_node_flowgraphdiagram_after_drop
	);
}


/**
 * creates a Activity node in position
 * this method is called by th NG-App
 * @param x
 * @param y 
 * @param id
 * @param containerId
 * @param styleArgs
 * @returns {*}
 */
function create_node_activity_flowgraphdiagram(x,y,width,height,id,containerId,styleArgs,information,label) {
    var elem = null;
    if(width != null && height != null) {
    	elem = new joint.shapes.flowgraph.Activity({
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
    		    disableMove:false,
    		    disableResize:false
    		}
    	});
    } else {
	    elem = new joint.shapes.flowgraph.Activity({
	        position: {
	            x: x,
	            y: y
	        },
	        attrs:{
	            id:id,
	            disableMove:false,
	            disableResize:false
	        }
	    });
    }
    add_node_internal(elem,$graph_flowgraphdiagram,$paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    var pos = {x:x,y:y};
    if(containerId>-1&&containerId!=$graphmodel_id_flowgraphdiagram){
    	var parent = findElementById(containerId,$graph_flowgraphdiagram);
    	parent.embed(elem);
    	pos.x -= parent.position().x;
    	pos.y -= parent.position().y;
	}
	update_element_flowgraphdiagram(elem.attributes.id,id,styleArgs,information,label);
    if(!$_disable_events_flowgraphdiagram){
    	$cb_functions_flowgraphdiagram.cb_create_node_activity_flowgraphdiagram(Math.round(pos.x), Math.round(pos.y),Math.round(elem.attributes.size.width),Math.round(elem.attributes.size.height), elem.attributes.id,containerId);
    }
    return 'ready';
}

function move_node_activity_flowgraphdiagram_hook(elem) {
	if(!$_disable_events_flowgraphdiagram){
		var parentId = $graphmodel_id_flowgraphdiagram;
		var pos = {x:elem.model.attributes.position.x,y:elem.model.attributes.position.y};
	    if(elem.model.attributes.parent != null){
	    	var parent = $graph_flowgraphdiagram.getCell(elem.model.attributes.parent);
	         parentId = parent.attributes.attrs.id;
	         pos.x -= parent.position().x;
	         pos.y -= parent.position().y;
	    }
	    //check if the container change was allowed
	    var valid = $cb_functions_flowgraphdiagram.cb_can_move_node(elem.model.attributes.attrs.id,parentId);
	    if(valid===true) {
	    	//movement has been valid
		    $cb_functions_flowgraphdiagram.cb_move_node_activity_flowgraphdiagram(Math.round(pos.x),Math.round(pos.y),elem.model.attributes.attrs.id,parentId);
		    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
	    } else {
	    	//movement is not valid and has to be reseted
	    	var preX = valid['x'];
	    	var preY = valid['y'];
	    	var diffX = preX - elem.model.attributes.position.x;
	    	var diffY = preY - elem.model.attributes.position.y;
	    	var preContainerId = valid['containerId'];
	    	//remove the containement
	    	if(elem.model.attributes.parent != null) {
		    	$graph_flowgraphdiagram.getCell(elem.model.attributes.parent).unembed($graph_flowgraphdiagram.getCell(elem.model.id));
		    }
	    	//check if the pre container was not the graphmodel
	    	if(preContainerId!==$graphmodel_id_flowgraphdiagram)
	    	{
	    		//embed the node in the precontainer
		    	var parentCell = findElementById(preContainerId,$graph_flowgraphdiagram);
		    	parentCell.embed(elem.model);
		    	//move back
		    	elem.model.position(preX,preY,{ parentRealtive: true });
	    	}
	    	else {
		    	//move back
	    		elem.model.position(preX,preY);
	    	}
	    	
	    }
        console.log("node flowgraphdiagram change position");
    }
}


/**
 * moves the Activity node to another position, relative to its parent container
 * if the container id is provided (containerId != -1). the node is
 * embedded in the given container
 * this method is called by th NG-App
 * 
 * @param x
 * @param y
 * @param id
 * @param containerId
 */
function move_node_activity_flowgraphdiagram(x,y,id,containerId) {
    if(containerId==$graphmodel_id_flowgraphdiagram){
        move_node_internal(x,y,id,-1,$graph_flowgraphdiagram);
    } else {
        move_node_internal(x,y,id,containerId,$graph_flowgraphdiagram);
    }
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * removes the Activity node by id
 * this method is called by th NG-App
 * @param id
 */
function remove_node_activity_flowgraphdiagram(id) {
    remove_node_internal(id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
    return 'ready';
}

/**
 * resizes the Activity node by id depended on the 
 * given absolute width and height
 * this method is called by th NG-App
 * @param width
 * @param height
 * @param id
 */
function resize_node_activity_flowgraphdiagram(width,height,direction,id) {
    resize_node_internal(width,height,direction,id,$graph_flowgraphdiagram,$paper_flowgraphdiagram);
    return 'ready';
}

/**
 * rotates the Activity node by id depended on the 
 * given on the absolute angle
 * this method is called by th NG-App
 * @param angle
 * @param id
 */
function rotate_node_activity_flowgraphdiagram(angle,id) {
    rotate_node_internal(angle,id,$graph_flowgraphdiagram);
    return 'ready';
}

/**
* removes a edge with the given id from the canvas
* this method is called by th NG-App
* @param id
*/
function remove_edge__flowgraphdiagram_flowgraphdiagram(id) {
	remove_edge_internal(id,$graph_flowgraphdiagram);
	return 'ready';
}

/**
 * creates a Transition edge connecting
 * the nodes specified by the source and target id
 * registers the listener for reconnnection and bendpoints
 * this method is called by th NG-App
 * @param sourceId
 * @param targetId
 * @param id
 * @param styleArgs
 */
function create_edge_transition_flowgraphdiagram(sourceId,targetId,id,positions,styleArgs,information,label) {
    var sourceN = findElementById(sourceId,$graph_flowgraphdiagram);
    var targetN = findElementById(targetId,$graph_flowgraphdiagram);

    var link = new joint.shapes.flowgraph.Transition({
        attrs:{
            id:id,
            disableMove:false,
            disableResize:false,
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
    add_edge_internal(link,$graph_flowgraphdiagram,$router_flowgraphdiagram,$connector_flowgraphdiagram);
    update_element_flowgraphdiagram(link.attributes.id,id,styleArgs,information,label);
    return 'ready';
}

/**
 * removes the Transition edge with the given id from the canvas
 * this method is called by th NG-App
 * @param id
 */
function remove_edge_transition_flowgraphdiagram(id) {
    remove_edge_internal(id,$graph_flowgraphdiagram);
    return 'ready';
}

/**
 * reconnets the Transition edge to a new target and source node
 * specified by their id
 * this method is called by th NG-App
 * @param sourceId
 * @param targetId
 * @param id
 */
function reconnect_edge_transition_flowgraphdiagram(sourceId,targetId,id) {
    reconnect_edge_internal(sourceId,targetId,id,$graph_flowgraphdiagram);
    return 'ready';
}

function reconnect_edge_transition_flowgraphdiagram_hook(elem) {
	if(!$_disable_events_flowgraphdiagram){
		var edgeId = elem.model.attributes.attrs.id;
		var source = elem.model.attributes.source.id;
		var sourceId = $graph_flowgraphdiagram.getCell(source).attributes.attrs.id;
		var target = elem.model.attributes.target.id;
		var targetId = $graph_flowgraphdiagram.getCell(target).attributes.attrs.id;
	    //check if the container change was allowed
	    var valid = $cb_functions_flowgraphdiagram.cb_can_reconnect_edge(edgeId,sourceId,targetId);
	    if(valid===true) {
	    	//reconnection has been valid
		    $cb_functions_flowgraphdiagram.cb_reconnect_edge_transition_flowgraphdiagram(sourceId,targetId,edgeId);
	    } else {
	    	//movement is not valid and has to be reseted
	    	var preSource = valid.source;
	    	var preTarget = valid.target;
	    	reconnect_edge_internal(preSource,preTarget,edgeId,$graph_flowgraphdiagram);
	    	
	    }
    }
}

/**
 * creates a LabeledTransition edge connecting
 * the nodes specified by the source and target id
 * registers the listener for reconnnection and bendpoints
 * this method is called by th NG-App
 * @param sourceId
 * @param targetId
 * @param id
 * @param styleArgs
 */
function create_edge_labeledtransition_flowgraphdiagram(sourceId,targetId,id,positions,styleArgs,information,label) {
    var sourceN = findElementById(sourceId,$graph_flowgraphdiagram);
    var targetN = findElementById(targetId,$graph_flowgraphdiagram);

    var link = new joint.shapes.flowgraph.LabeledTransition({
        attrs:{
            id:id,
            disableMove:false,
            disableResize:false,
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
    add_edge_internal(link,$graph_flowgraphdiagram,$router_flowgraphdiagram,$connector_flowgraphdiagram);
    update_element_flowgraphdiagram(link.attributes.id,id,styleArgs,information,label);
    return 'ready';
}

/**
 * removes the LabeledTransition edge with the given id from the canvas
 * this method is called by th NG-App
 * @param id
 */
function remove_edge_labeledtransition_flowgraphdiagram(id) {
    remove_edge_internal(id,$graph_flowgraphdiagram);
    return 'ready';
}

/**
 * reconnets the LabeledTransition edge to a new target and source node
 * specified by their id
 * this method is called by th NG-App
 * @param sourceId
 * @param targetId
 * @param id
 */
function reconnect_edge_labeledtransition_flowgraphdiagram(sourceId,targetId,id) {
    reconnect_edge_internal(sourceId,targetId,id,$graph_flowgraphdiagram);
    return 'ready';
}

function reconnect_edge_labeledtransition_flowgraphdiagram_hook(elem) {
	if(!$_disable_events_flowgraphdiagram){
		var edgeId = elem.model.attributes.attrs.id;
		var source = elem.model.attributes.source.id;
		var sourceId = $graph_flowgraphdiagram.getCell(source).attributes.attrs.id;
		var target = elem.model.attributes.target.id;
		var targetId = $graph_flowgraphdiagram.getCell(target).attributes.attrs.id;
	    //check if the container change was allowed
	    var valid = $cb_functions_flowgraphdiagram.cb_can_reconnect_edge(edgeId,sourceId,targetId);
	    if(valid===true) {
	    	//reconnection has been valid
		    $cb_functions_flowgraphdiagram.cb_reconnect_edge_labeledtransition_flowgraphdiagram(sourceId,targetId,edgeId);
	    } else {
	    	//movement is not valid and has to be reseted
	    	var preSource = valid.source;
	    	var preTarget = valid.target;
	    	reconnect_edge_internal(preSource,preTarget,edgeId,$graph_flowgraphdiagram);
	    	
	    }
    }
}

/**
 * updates the edge verticles
 * specified by the edge id and all verticle positions
 * this method is called by th NG-App
 * @param point {x,y}
 * @param id
 */
function update_bendpoint_flowgraphdiagram(points,id) {
    update_bendpoint_internal(points, id,$graph_flowgraphdiagram);
    return 'ready';
}

/*
 *	Creation of nodes by drag and dropping from the palette
 */
function create_prime_node_menu_flowgraphdiagram(possibleNodes,x,y,absX,absY,containerId,elementId) {
	var btn_group = $('<div id="pyro_node_menu" class="btn-group-vertical btn-group-sm" style="position: absolute;z-index: 99999;top: '+absY+'px;left: '+absX+'px;"></div>');
	$('body').append(btn_group);
	for(var node in possibleNodes) {
		var button = $('<button type="button" class="btn">'+possibleNodes[node]+'</button>');
		
		btn_group.append(button);
		
		$(button).on('click',function () {
			switch(this.innerText){
				case 'flowgraph.SubFlowGraph':{
					create_node_subflowgraph_flowgraphdiagram(x,y,null,null,-1,containerId,"undefined",null,null,elementId);
				    break;
				}
			}
		    $('#pyro_node_menu').remove();
		});
	}
}

/**
 *
 * @param ev
 */
function drop_on_canvas_flowgraphdiagram(ev) {
	unhighlight_all_element_valid_target($paper_flowgraphdiagram,$graph_flowgraphdiagram);
	ev.preventDefault();
	var rp = getRelativeScreenPosition(ev.clientX,ev.clientY,$paper_flowgraphdiagram);
	var x = rp.x;
	var y = rp.y;
	var containerId = get_container_id_flowgraphdiagram(rp);
	var content = JSON.parse(ev.dataTransfer.getData("text"));
	var typeName = content.typename;
	var elementId = content.elementid;
	//check prime node
	if(typeof elementId !== 'undefined' && typeName != ''){
		var possibleNodes = [];
		//for all prime nodes
		//check prime referenced type and super types
		if(
			typeName == 'flowgraph.FlowGraphDiagram'
		)
		{
			if(is_containement_allowed_flowgraphdiagram(rp,'flowgraph.SubFlowGraph')) {
				possibleNodes[possibleNodes.length] = 'flowgraph.SubFlowGraph'; 
			}
		}
		if(possibleNodes.length==1){
			//one node possible
			switch (possibleNodes[0]) {
			//foreach node
			case 'flowgraph.SubFlowGraph':{
			create_node_subflowgraph_flowgraphdiagram(x,y,null,null,-1,containerId,null,null,null,elementId);
				    break;
				}
		    }
		}
		else{
			//multiple nodes possible
			//show selection
			 create_prime_node_menu_flowgraphdiagram(possibleNodes,x,y,ev.clientX,ev.clientY,containerId,elementId);
		}
		return;
	}
	if(typeName != ''){
	    // create node
	    create_node_flowgraphdiagram_after_drop(x,y,typeName);
	}
	fitContent($paper_flowgraphdiagram,$map_paper_flowgraphdiagram);
}

function create_node_flowgraphdiagram_after_drop(x,y,typeName) {
	if(is_containement_allowed_flowgraphdiagram({x:x,y:y},typeName)) {
		var containerId = get_container_id_flowgraphdiagram({x:x,y:y});
        switch (typeName) {
            //foreach node
			case 'flowgraph.End': {
				create_node_end_flowgraphdiagram(x,y,null,null,-1,containerId,null,-1);
			    break;
			}
			case 'flowgraph.Swimlane': {
				create_node_swimlane_flowgraphdiagram(x,y,null,null,-1,containerId,null,-1);
			    break;
			}
			case 'flowgraph.SubFlowGraph': {
				create_node_subflowgraph_flowgraphdiagram(x,y,null,null,-1,containerId,null,-1);
			    break;
			}
			case 'flowgraph.Start': {
				create_node_start_flowgraphdiagram(x,y,null,null,-1,containerId,null,-1);
			    break;
			}
			case 'flowgraph.Activity': {
				create_node_activity_flowgraphdiagram(x,y,null,null,-1,containerId,null,-1);
			    break;
			}
        }
    }
}

function get_container_id_flowgraphdiagram(rp) {
	var views = $paper_flowgraphdiagram.findViewsFromPoint(rp);
	if(views.length<=0){
		return $graphmodel_id_flowgraphdiagram;
	}
	return views[views.length-1].model.attributes.attrs.id;
}

function is_containement_allowed_flowgraphdiagram(rp,creatableTypeName) {
    var views = $paper_flowgraphdiagram.findViewsFromPoint(rp);
    if(views.length<=0){
    	var targetNode = null;
        //target is graphmodel
        //check if type can be contained in group
        if(
        	creatableTypeName === 'flowgraph.End'
        ) {
        	return true;
        }
        //check if type can be contained in group
        if(
        	creatableTypeName === 'flowgraph.Start'
        ) {
        	return true;
        }
        //check if type can be contained in group
        if(
        	creatableTypeName === 'flowgraph.SubFlowGraph'
        ) {
        	return true;
        }
        //check if type can be contained in group
        if(
        	creatableTypeName === 'flowgraph.Activity'
        ) {
        	return true;
        }
        //check if type can be contained in group
        if(
        	creatableTypeName === 'flowgraph.Swimlane'
        ) {
        	return true;
        }
        return false;
        
        
    }
	else {
	    var targetNode = views[views.length-1];
	    var targetType = targetNode.model.attributes.type;
		//foreach container
		if(targetType==='flowgraph.Swimlane')
		{
			//check if type can be contained in group
			if(
				creatableTypeName === 'flowgraph.End'
			) {
				return true;
			}
			//check if type can be contained in group
			if(
				creatableTypeName === 'flowgraph.Activity'
			) {
				return true;
			}
			//check if type can be contained in group
			if(
				creatableTypeName === 'flowgraph.Start'
			) {
				var group2Size = 0;
				group2Size += getContainedByType(targetNode,'flowgraph.Start',$graph_flowgraphdiagram).length;
				if(group2Size<1){
					return true;
				}
			}
			//check if type can be contained in group
			if(
				creatableTypeName === 'flowgraph.SubFlowGraph'
			) {
				return true;
			}
			return false;
			
			
		}
	}
    return false;
}

function confirm_drop_flowgraphdiagram(ev) {
    ev.preventDefault();
    ev.stopPropagation();
    var rp = getRelativeScreenPosition(ev.clientX,ev.clientY,$paper_flowgraphdiagram);
    var x = rp.x;
    var y = rp.y;
    var typeName = ev.dataTransfer.getData("typename");
    if(typeName != ''){
    	if(!is_containement_allowed_flowgraphdiagram(rp,typeName)) {
       	   		ev.dataTransfer.effectAllowed= 'none';
       	        ev.dataTransfer.dropEffect= 'none';
       	 }
    }
}
