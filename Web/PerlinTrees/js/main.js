var drawer = null;
var width, height;
var Tree = {
	rootX:0,
	rootY:0,
	branchWidth:2,
	branchLength:80
};
var selectedAngle, selectedOffset;
var minLength = 5;
var windGenerator;
var windStrength;

function onLoad() {
	var canvas = document.getElementById("mainCanvas");
	width = canvas.width;
	height = canvas.height;
	drawer = new Drawer(canvas.getContext("2d"));
	windGenerator = new SimplexNoise(Math);
	windGenerator.time = 0.01;
	// Context.beginPath();
	// Context.moveTo(10,10);
	// Context.lineTo(width/2, height - 10);
	// Context.lineTo(width  - 10, 10);
	// Context.lineTo(10, 10);
	// Context.stroke();
	var treeCount = 3;
	var trees = []
	Tree.rootX = (width / treeCount) / 2;
	Tree.rootY = height;

	for (var i = 0; i < treeCount; i++) {
		trees[i] = JSON.parse(JSON.stringify(Tree));
		Tree.rootX += width / treeCount;
	}

	drawTrees(trees);
}

var stroked = false;

function drawTrees(trees) {
	getInputData();
	drawer.getContext().clearRect(0, 0, width, height);

	drawBorder();

   //selectedOffset = Math.random(-1,1) * windStrength; // Uncomment for randomness
  windGenerator.time += 0.01;

	for (var i = 0; i < trees.length; i++) {
    selectedOffset = windGenerator.noise(windGenerator.time - (i*0.05), 0) * windStrength;
		drawer.beginPath();
		drawer.getContext().strokeStyle = "#660000";
		stroked = false;
		drawBranch(trees[i].rootX, trees[i].rootY, trees[i].branchLength, trees[i].branchWidth, 90, selectedAngle, selectedOffset);
		drawer.stroke();
	}

	setTimeout(function(){drawTrees(trees)}, 16);
}

function drawBorder () {
	drawer.getContext().strokeStyle = "#000";
	drawer.getContext().rect(0,0,width,height);
	drawer.stroke();
}

function drawBranch(sX, sY, length, width, rotation, sepAngle, offset) {

	if(length < minLength) return;

	var newX = length * Math.cos(rotation * Math.PI/180),
		newY = -length * Math.sin(rotation * Math.PI/180);


	if(length < 40 && !stroked) {
		stroked = true;
		drawer.stroke();
		drawer.beginPath();
		drawer.getContext().strokeStyle = "#00b300";
	}
	else {
		if(length>=40) {
			stroked = false;
			drawer.stroke();
			drawer.beginPath();
			drawer.getContext().strokeStyle = "#660000";
		}
	}

	drawer.getContext().lineWidth = width;
	drawer.moveTo(sX, sY);
	drawer.lineToRelative(newX, newY);

	drawBranch(sX + newX, sY + newY, length * 2/3, width, rotation - sepAngle + offset, sepAngle, offset);
	drawBranch(sX + newX, sY + newY, length * 2/3, width, rotation + sepAngle + offset, sepAngle, offset);
}

function getInputData() {
	selectedAngle = getSelectedAngle()
	selectedOffset = getSelectedOffset();
	windStrength = getSelectedWindStrength();
}

function getSelectedAngle() {
	var selector = document.getElementById("angleSelect");
	return parseInt(selector.options[selector.selectedIndex].value);
}

function getSelectedOffset() {
	var selector = document.getElementById("offsetSelect");
	return parseInt(selector.options[selector.selectedIndex].value);
}

function getSelectedWindStrength() {
	var selector = document.getElementById("windStrengthSelect");
	return parseInt(selector.options[selector.selectedIndex].value);
}

function angleChanged() {
	selectedAngle = getSelectedAngle();
}
