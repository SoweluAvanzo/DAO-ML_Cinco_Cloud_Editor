    var globalActivity = null;

    $('#scaledwrapper').click(function(e) {
        		isHit(e.pageX,e.pageY,$( this ).width(),$( this ).height());
    });


    function isHit(x,y,v,h)
    {
        var objects = getActivities();
        var xratio = x/v, yratio = y/h;
        for(var i=0; i< objects.length;i++)
        {
        		var entry = objects[i];
        		if(entry.activity === globalActivity && entry.type==='screen'){
                var clickAreas = entry.clickAreas;
                
                for(var j=0;j<clickAreas.length;j++)
                {
                		var area = clickAreas[j];
                		switch(area.type) {
                        case 'rectangle':
                            if(checkRectangeHit(area,xratio,yratio))
                            {
                            		next(area.nextActivity);
                            		return;
                        		}
                            break;
                        case 'ellipse':
                            if(checkEllipseHit(area,xratio,yratio))
                            {
                            		next(area.nextActivity);
                            		return;
                        		}
                            break;
                        default:
                             console.log('unkown click area! '+area);
                    }
                }
            }
        }
    }
    
    function loadTexts(screen)
    {
		$('#imageContainer').empty();
    		screen.textAreas.forEach(function(entry){
    			$('#imageContainer').append('<div style="font-size: '+entry.size+'vw; color:'+entry.color+'; position: absolute;top: '+entry.y*100+'%;left: '+entry.x*100+'%;width: '+entry.width*100+'%;height: '+entry.height*100+'%;">'+entry.text+'</div>')
    		});
    }

    function checkRectangeHit(area,x,y)
    {
        var x1 = area.x;
        var x2 = x1 + area.width;
        var y1 = area.y;
        var y2 = y1 + area.height;
        return x > x1 && x < x2 && y > y1 && y < y2;
    }

    function checkEllipseHit(area,x,y)
    {
        var ellw = area.width;
        if (ellw <= 0.0) {
            return false;
        }
        var normx = (x - area.x) / ellw - 0.5;
        var ellh = area.height;
        if (ellh <= 0.0) {
            return false;
        }
        var normy = (y - area.y) / ellh - 0.5;
        return (normx * normx + normy * normy) < 0.25;

    }

    function loadImage(imgPath)
    {
        $('#scaledwrapper').empty();
        $('#scaledwrapper').html('<div id="imageContainer" class="scaledcontainer" style="background-image:url(' + imgPath + ')"></div>');
    }


    function triggerScreen(screen){
        loadImage(screen.imagePath);
        loadTexts(screen);
    }


    function triggerCondition(condition){
        if(eval(condition.condition)){
            next(condition.trueSuccessor);
        }
        else{
            next(condition.falseSuccessor);
        }

    }

    function triggerAssignment(assignment){
        eval(assignment.assignee + '=' + assignment.assignment);
        next(assignment.successor);
    }

    function next(nextActivityNumber){
        globalActivity = nextActivityNumber;
        var objects = getActivities();
        objects.forEach(function(entry){
            if(entry.activity === nextActivityNumber){
                switch(entry.type) {
                    case 'screen':
                        triggerScreen(entry);
                        break;
                    case 'condition':
                        triggerCondition(entry);
                        break;
                    case 'assignment':
                        triggerAssignment(entry);
                        break;
                    default:
                        console.log('invalid activity type! '+entry);
                }
                return;
            }
        });
    }

    $(document).ready(function () {
        //Trigger start activity
        if(globalActivity != null)
        {
            next(globalActivity);
        }
    });