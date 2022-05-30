
// The current position of mouse
let x = 0;
let y = 0;

// Width of left side
let leftWidth = 0;
let rightWidth = 0;
const limitX = 210;

var resizer = null;
var leftSide = null;
var rightSide = null;

var currentType = null;

// Handle the mousedown event
// that's triggered when user drags the resizer
const mouseDownHandler = function (e) {
    // fetch current width
    rightSide = document.getElementById('canvas-area');
    leftSide = document.getElementById('pyro-micro-menu');
    if(!rightSide || !leftSide) {
        return;
    }

    leftWidth = leftSide.getBoundingClientRect().width;
    rightWidth = rightSide.getBoundingClientRect().width;
    // Get the current mouse position
    x = e.clientX;
    y = e.clientY;

    // Attach the listeners to `document`
    document.addEventListener('mousemove', mouseMoveHandler);
    document.addEventListener('mouseup', mouseUpHandler);
};

const mouseMoveHandler = function (e) {
    // fetch current width
    rightSide = document.getElementById('canvas-area');
    leftSide = document.getElementById('pyro-micro-menu');
    if(!rightSide || !leftSide) {
        return;
    }
    const w = resizer.parentNode.getBoundingClientRect().width;

    // How far the mouse has been moved
    const dx = e.clientX - x;

    // calculate new width
    var newLeftWidth;
    var newRightWidth;
    if(leftWidth + dx < limitX) {
        // limit size
        const limitedDx = limitX - leftWidth;
        newLeftWidth = ((limitX) * 100) / w;
        newRightWidth = ((rightWidth - limitedDx) * 100) / w;
    } else {
        newLeftWidth = ((leftWidth + dx) * 100) / w;
        newRightWidth = ((rightWidth - dx) * 100) / w;
    }

    // apply
    leftSide.style.width = `${newLeftWidth}%`;
    rightSide.style.width = `${newRightWidth}%`;

    // readjust map
    if(currentType) {
        window["reaAdjustDimensions_"+currentType]();
    }
};

const mouseUpHandler = function () {
    // Remove the handlers of `mousemove` and `mouseup`
    document.removeEventListener('mousemove', mouseMoveHandler);
    document.removeEventListener('mouseup', mouseUpHandler);
};

$( document ).ready(function() {
    initializeResizeParameter();
});
$( window ).on( "change", function() {
    initializeResizeParameter();
});

function initializeResizeParameter(currentType) {
    this.currentType = currentType;

    // Query the element
    resizer = document.getElementById('separator');
    if(resizer) {
       resizer.addEventListener('mousedown', mouseDownHandler);
    }

    // readjust map
    if(currentType) {
        window["reaAdjustDimensions_"+currentType]();
    }
}