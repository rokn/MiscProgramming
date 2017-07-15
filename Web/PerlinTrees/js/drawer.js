function Drawer(context){
	this.context = context;
	this.currX = 0;
	this.currY = 0;

	this.setContext = function(context){
		this.context = context;
	};

	this.moveTo = function(x,y){
		context.moveTo(x,y);
		this.currX = x;
		this.currY = y;
	};

	this.moveToRelative = function(x,y) {
		this.currX += x;
		this.currY += y;
		this.context.moveTo(this.currX, this.currY);
	};

	this.lineTo = function(x,y){
		this.context.lineTo(x,y);
		this.currX = x;
		this.currY = y;
	};

	this.lineToRelative = function(x,y) {
		this.currX += x;
		this.currY += y;
		this.context.lineTo(this.currX, this.currY);
	};

	this.beginPath = function(){
		this.context.beginPath();
	};

	this.getContext = function () {
		return this.context;
	};

	this.stroke = function() {
		this.context.stroke();
	};
}