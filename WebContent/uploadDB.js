function toggleLoading() {
	//console.log("toggle Loading");
	let divOverlay = document.getElementById("overlay");
	let divLoader = document.getElementById("loader");
	divOverlay.classList.toggle("hideLoader");
	divLoader.classList.toggle("hideLoader");
	//divOverlay.style.display = "block";
	//divLoader.style.display = "block";
    /*divOverlay.id = "overlay";
	divLoader.id = "loader";
    document.body.appendChild(divOverlay);
	document.body.appendChild(divLoader);*/
}

function status1() {
	let enableElements = document.querySelectorAll('.status1');
	enableElements.forEach((element) => {
	  element.removeAttribute("disabled");
	});
	let otherStatusElements = document.querySelectorAll('.status2, .status3');
	otherStatusElements.forEach((element) => {
	  element.setAttribute("disabled", "");
	  element.setAttribute("style", "pointer-events:none");
	});
	console.log("status1");
}

function status2() {
	let enableElements = document.querySelectorAll('.status1, .status2');
	enableElements.forEach((element) => {
	  element.removeAttribute("disabled");
	  element.removeAttribute("style");
	});
	let otherStatusElements = document.querySelectorAll('.status3');
	otherStatusElements.forEach((element) => {
	  element.setAttribute("disabled", "");
	  element.setAttribute("style", "pointer-events:none");
	});
	console.log("status2");
}

function status3() {
	let enableElements = document.querySelectorAll('.status1, .status2, .status3');
	enableElements.forEach((element) => {
	  element.removeAttribute("disabled");
	  element.removeAttribute("style");
	});
	console.log("status3");
}

let creationWebSocket;
let host = document.location.host;
let pathName = document.location.pathname;

let vertexData = [];
let edgeData = [];
let arrowData = [];
let subStructureMap = new Map();
let daten;
let svg;
let svgZoom;
let g;
let legendHolder;
let svgSelectionRect;
let miniMapSvg;
let miniMapG;
let miniMapSvgZoom;
let miniMapRect;
let transformation;
let vertexHeightGraph = 0;
let oldTransformEvent = {x : undefined, y : undefined, k : undefined, kX : undefined, kY : undefined};
let treeMap;
let treeMapOriginal;
let treeMapData;
let treeMapSelection = [];
let highlightList = [];

let vertexSelection = [];

let selectedSuperHighlightedItem;

//Define the div for Highlight tooltip
let checkboxes = [{name : "Color All Sequences", function : "colorAllSequences(this.checked);", title: "When selected, the edges of all substructures are highlighted by color."} ,{name : "Color Sequence", function : "colorSequenceChange(this.checked);", title: "When selected, the edges of the substructure are highlighted  by color."}, {name : "Highlight Sequence", function : "highlightChange(this.checked);", title: "When selected, the edges of the substructure are highlighted in color and extra thick. "}];
let highlightTooltipSubStructure;
let divHighlightTooltip = d3.select("body").append("div")
	.attr("class", "tooltip container action");
divHighlightTooltip.selectAll("checkboxes")
	.data(checkboxes)
	.enter()
	.append("label")
	.style("margin-right","20px")
	.style("margin-left","20px")
	.append("text")
	.text(function(d) { return d.name})
	.attr("title", function(d) { return d.title;})
	.append("input")
	.attr("type", "checkbox")
	.attr("id", function(d) { return "checkbox" + d.name.replaceAll(" ", ""); })
	.style("margin-left","5px")
	.attr("onclick", function(d) { return d.function;});
	
divHighlightTooltip.append("button")
	.attr("class", "close-icon tooltipCloseButton")
	.attr("onclick", "hideTooltip(divHighlightTooltip, true)");

// Define the div for the Info tooltip
let divInfoTooltip = d3.select("body").append("div")
	.attr("class", "tooltip container info");
divInfoTooltip.append("button")
	.attr("class", "close-icon tooltipCloseButton")
	.attr("onclick", "hideTooltip(divActionTooltip, true)");

// Define the div for the Change tooltip
let divChangeOrderTooltip = d3.select("#changeOrderTooltip");
document.getElementById("vertexUpdateCheckbox").checked = true;
    
// Define the div for the Action tooltip
let actions = [{name : "Show Infos", function : "showInfo();", title: "Display additional information in a tooltip."}];
let divActionTooltip = d3.select("body").append("div")
    .attr("class", "tooltip container action");
//<button class="close-icon status3" onclick="resetForm(document.getElementById('CSTreeContainerFilterInput'));" disabled></button>
divActionTooltip.append("button")
	.attr("class", "close-icon tooltipCloseButton")
	.attr("onclick", "hideTooltip(divActionTooltip, true)");

divActionTooltip
	// .append("form")
	.selectAll("actions")
	.data(actions)
	.enter().append("input")
	.attr("type", "button")
	.attr("class", "button tooltipButton")
	.attr("id", function(d) { return "btn" + d.name.replaceAll(" ", ""); })
	.attr("onclick", function(d) { return d.function + "hideTooltip(divActionTooltip, true);" })
	.attr("value", function(d) { return d.name; })
	.style("margin-right", function(d, i) {
		console.log(d, i);
		if (i == actions.length - 1) {
			return "25px";
		} else {
			return "10px";
		}
	})
	.attr("title", function(d) { return d.title;});

document.getElementById("miniMapCheckBox").checked = false;

function showInfo(){
	showTooltip(divInfoTooltip, true);
}

function showChangeTooltip() {
	showTooltip(divChangeOrderTooltip, true);
}

function hideChangeTooltip() {
	hideTooltip(divChangeOrderTooltip, true);
}

function vertexUpdateCheckboxCheck() {
	let vertexUpdateCheckboxItem = document.getElementById('vertexUpdateCheckbox');
	vertexUpdateCheckbox(vertexUpdateCheckboxItem);
}

function vertexUpdateCheckbox(vertexUpdateCheckbox) {
	let checked = vertexUpdateCheckbox.checked;
	let newSequenceOrder = document.getElementById("newSequenceOrder");
	if (!checked) {
		if (newSequenceOrder.childNodes[0].innerHTML.startsWith("Super_Genome")) {
		} else {
			let superGenomeLi = null;
			for (li of newSequenceOrder.childNodes) {
				if (li.innerHTML.startsWith("Super_Genome")) {
					superGenomeLi = li;
				}
			}
			if (superGenomeLi == null) {
				window.alert("Super Genome must be present in New Sequence Order List!");
				vertexUpdateCheckbox.checked = true;
			} else {
				newSequenceOrder.prepend(superGenomeLi);
				//newSequenceOrder.removeChild(superGenomeLi);
			}
		}
	}	
}

function showTooltip(toolTipDiv, action = false, offset = [0,0]) {
	vertexUpdateCheckbox(document.getElementById("vertexUpdateCheckbox"))
	let x = event.pageX + offset[0];
	let y = event.pageY + offset[1];
	let body = document.body;
	if (event.pageX + toolTipDiv.node().getBoundingClientRect().width > body.getBoundingClientRect().width) {
		let x = body.getBoundingClientRect().width - toolTipDiv.node().getBoundingClientRect().width;
	}
	
	toolTipDiv.transition()		
		.duration(200)		
		.style("opacity", .9)
		.style("left", (event.pageX) + "px")
		.style("top", (event.pageY - 28) + "px");
	
	if (action) {
		toolTipDiv.style("pointer-events", "auto");
	}
}

function hideTooltip(toolTipDiv, action = false) {
	toolTipDiv.transition()		
		.duration(500)		
		.style("opacity", 0);
	
	if (action) {
		toolTipDiv.style("pointer-events", "none");
	}
}

/*
var r = new Resumable({
	target: pathName + 'upload',
	chunkSize: 1 * 1024 * 1024,
	simultaneousUploads: 4,
	testChunks: true,
	throttleProgressCallbacks: 1,
	method: "octet",
	fileType: ["zip"],
});
r.assignBrowse(document.getElementById('LoadDBbutton'));
*/

//skip Upload
closeIfOpen();
connectUploadWebSocket();
//skip Upload

/*
// Resumable.js isn't supported, fall back on a different method
if (!r.support) {
	console.log("Resumable is not supported...");
} else {
	// Handle file add event
	r.on('fileAdded', function(file, event) {
		toggleLoading();
		closeIfOpen();
		connectUploadWebSocket();
		r.upload();
	});
	r.on('complete', function() {
		console.log("Resumable: File was uploaded completly...");
	});
	r.on('fileSuccess', function(file) {
		console.log("Resumable: File was uploaded successfully...");
		console.log(file.relativePath);
		let message = { "status": "finishedUpload", "fileName": file.relativePath };
		creationWebSocket.send(JSON.stringify(message));

	});
	r.on('fileError', function(file, message) {
		console.log("Resumable: File could not be uploaded: " + message);
	});
}
*/

function fillVertex(d) {
	if(d.isJoined) {
		return "orange";
	} else {
		return "blue";
	}
}

function resizeGraphSVG(svg, drawingAreaDiv, size = undefined) {
	if (svg != undefined) {
		//let drawingAreaDiv = document.getElementById("drawingArea");
		let computedStyle = getComputedStyle(drawingAreaDiv);
		let width = (size == undefined) ? drawingAreaDiv.clientWidth : size;
		let height = (size == undefined) ? drawingAreaDiv.clientHeight : size;
		width -= parseFloat(computedStyle.paddingLeft) + parseFloat(computedStyle.paddingRight);
		height -= parseFloat(computedStyle.paddingTop) + parseFloat(computedStyle.paddingBottom);
		svg.attr("width", width).attr("height", height);
	}
}

document.getElementById("toggleCollapseLeft").addEventListener("click", function() { 
	document.getElementById("sideNav").classList.toggle("collapsed")
	if (document.getElementById("sideNav").classList.contains("collapsed")) {
		document.getElementById("toggleCollapseLeft").innerHTML = "&#129094;";
		resizeGraphSVG(svg, document.getElementById("drawingArea"));
	} else {
		resizeGraphSVG(svg, document.getElementById("drawingArea"), 0);
		document.getElementById("toggleCollapseLeft").innerHTML = "&#129092;";
		resizeGraphSVG(svg, document.getElementById("drawingArea"));
	}
});



function connectUploadWebSocket() {
	creationWebSocket = new WebSocket("ws://" + host + pathName + "creationController");
	
	console.log("ws://" + host + pathName + "creationController");
	
	creationWebSocket.binaryType = "blob";
	creationWebSocket.onopen = function() {
		console.log('Creation Controller Websocket: Connected');
		//skip Upload
		let message = { "status": "finishedUpload", "fileName": "graph.db_tunicate.zip" };
		creationWebSocket.send(JSON.stringify(message));
		//skip Upload
	};

	creationWebSocket.onmessage = function(evt) {
		var json_obj = JSON.parse(evt.data);
		//console.log(json_obj);
		if (json_obj.status == "genomeList") {
			//skip upload
			//toggleLoading();
			let genomeSelect = document.getElementById("GSgenomeSelection");
			removeOptions(genomeSelect);
			for (let genome of json_obj.selectionList) {
				var option = document.createElement("option");
				option.text = genome;
				genomeSelect.add(option);
			}
			changeSelection("genome", genomeSelect.value);
			status1();
		} else if (json_obj.status == "sessionID") {
			console.log(json_obj.sessionID);
		} else if (json_obj.status == "subStructureList") {
			let subStructureSelect = document.getElementById("GSsubStructureSelection");
			removeOptions(subStructureSelect);
			for (let subStructure of json_obj.selectionList) {
				var option = document.createElement("option");
				option.text = subStructure;
				subStructureSelect.add(option);
			}
			changeSelection("subStructure", subStructureSelect.value);
			status1();
		} else if (json_obj.status == "boundaries") {
			updateBoundariesLengthFilter(json_obj.minValue, json_obj.maxValue);
			status1();
			//document.getElementById("LoadGSbutton").removeAttribute('disabled');
		} else if (json_obj.status == "extremes") {
			updateExtremesLengthFilter(json_obj.minValue, json_obj.maxValue);
			status2();
			//document.getElementById("LoadCSbutton").removeAttribute('disabled');
			toggleLoading();
		} else if (json_obj.status == "compareabelSubStructureList") {
			status3();
			//resetForm();
			fillCSlist(json_obj.selectionList);
			updateLabel(document.getElementById('spaceLabel'), 'Space', document.getElementById('space'));
			updateLabel(document.getElementById('thicknessLabel'), 'Thickness', document.getElementById('thickness'));
			toggleLoading();
		} else if (json_obj.status == "compareabelSubStructureMap") {
			status3();
			let data = JSON.parse(json_obj.jSonString);
			let usedCSlist = document.getElementById("usedCS");
			usedCSlist.innerHTML = "";
			document.getElementById("CSTreeContainer").innerHTML = ""
			treeMapSelection = [];
			treeMap = new Tree('.TreeContainer', {
	        	data: [],
			});
			treeMapOriginal = new Tree('.TreeContainer', {
	        	data: [],
			});
			treeMapOriginal = null;
			
			treeMapData = data;
			updateExtremesBlockCountFilter(data);
			fillCSTreeMap(data, treeMapSelection);
			updateLabel(document.getElementById('spaceLabel'), 'Space', document.getElementById('space'));
			updateLabel(document.getElementById('thicknessLabel'), 'Thickness', document.getElementById('thickness'));
			resetForm(document.getElementById('CSTreeContainerFilterInput'), 2);
			toggleLoading();
		} else if (json_obj.status == "addVertex") {
			//console.log(json_obj.jSonString);
			let vertex = JSON.parse(json_obj.jSonString);
			vertex.text = vertexData.length;
			vertexData.push(vertex);
			//console.log(vertex);

			//let element = cy.add(JSON.parse(json_obj.jSonString));

			/*         for (var i = 0; i < 10; i++) {
			 cy.add({
			 data: { id: 'node' + i }
			 }
			 );
			 var source = 'node' + i;
			 cy.add({
			 data: {
			 id: 'edge' + i,
			 source: source,
			 target: (i % 2 == 0 ? 'a' : 'b')
			 }
			 }); 
			 }*/

		} else if (json_obj.status == "addEdge") {
			//console.log(json_obj.jSonString);
			edgeData.push(JSON.parse(json_obj.jSonString));
		} else if (json_obj.status == "addArrow") {
			//console.log(json_obj.jSonString);
			arrowData.push(JSON.parse(json_obj.jSonString));
		} else if (json_obj.status == "allVerticesSend") {			
						
			//svg.attr("width", 3000).attr("height", 3000);
			vertexHeightGraph = vertexData[0].height;
			console.log(vertexData);
			g.selectAll()
				.data(vertexData)
				.enter()
				//.append("g")
				.append("rect")
				.attr("x", function(d) { return d.x - (0.5 * d.width); })
				.attr("y", function(d) { return d.y; })
				.attr("width", function(d) { return d.width; })
				.attr("height", function(d) { return d.height; })
				.attr("class", function(d) { return ("vertex " + "id_" + d.id + " " + d.subStructures); })
				//.attr("transform", "translate(10, 0)")
				.style("fill", function(d) { return fillVertex(d)})
				.on('mouseover', function(event, d) { //fügt highlight bei mouseover hinzu
					let currentRec = d3.select(this);
					//console.log(currentRec.attr("class"));
					//currentRec.style('fill', "red"); ////selektiert das zum Event gehörige Element und ändert die opacity
					
					//showTooltip(divInfoTooltip, true);
					
					divInfoTooltip
				      .style("opacity", 0.9);
				    d3.select(this)
				      .style("stroke", "red")
				      .style("stroke-width", 5);
					
					let content = "";
					for (let block of d.localSeqAlignmentData) {
						let table = "<table><tr><th>substructure</th><th>start</th><th>length</th><th>strand</th></tr>";
						for (const [key, value] of Object.entries(block)) {
							if (subStructureMap.get(key) != undefined) {
								table = table + "<tr style=\"color:" + subStructureMap.get(key).color + "\">";
							} else {
								table = table + "<tr>";
							}
							
							//table = table + "<tr>";
							
							table = table + "<th>";
							table = table + key;
							table = table + "</th>";
							
							table = table + "<th>";
							table = table + value.start;
							table = table + "</th>";
							
							table = table + "<th>";
							table = table + value.length;
							table = table + "</th>";
							
							table = table + "<th>";
							table = table + value.strand;
							table = table + "</th>";
							
							table = table + "</tr>";
							
						}
						table = table + "</table>";
						content = content + table;
					}
					
		            
		            if (d.isJoined) {
						divInfoTooltip.html("ID: " + d.id + "</br>isJoined: " + d.isJoined + "<br>with " + d.localSeqAlignmentData.length + " blocks<br>" + content);	
					} else {
						divInfoTooltip.html("ID: " + d.id + "</br>isJoined: " + d.isJoined + "<br>" + content);	
					}
					
					divInfoTooltip.append("button")
						.attr("class", "close-icon tooltipCloseButton")
						.attr("onclick", "hideTooltip(divInfoTooltip, true)");
		            
				})
				.on('mouseleave', function(event, d) {
					//d3.select(this).style("fill", function(d) { return fillVertex(d)});
					divInfoTooltip.style("opacity", 0);
				    d3.select(this)
				      .style("stroke", "none");
					
					//hideTooltip(divInfoTooltip);
				})
				.on('mousemove', function(event, d) {
					divInfoTooltip
						.style("left", (event.pageX + 70) + "px")
      					.style("top", (event.pageY) + "px");
				})
				.on('click', function(event, d) {
					//console.log(d.id);
					hideTooltip(divInfoTooltip);	

					let selectedID = d.id;
					
					g.selectAll("rect").attr("stroke", null);
					
					d3.select(this).attr("stroke", "red");
					d3.select(this).attr("stroke-width", "5");
					
					showTooltip(divActionTooltip, true);
					
					vertexSelection = [selectedID];
					
					//document.getElementById("btnViewLocalAlignment").onclick = function() { viewLocalAlignment([selectedID]); };
				});
				//.append("title")
				//.text("Additional actions can be performed by clicking on the vertex.");
				
//				g.selectAll("g")
//					.append("text")
//					.attr("x", function(d) { return d.x; })
//					.attr("y", function(d) { return d.y + (0.9 * d.height); })
//					//.attr("dy", 15)
//					.attr("fill", "white")
//					.attr("font-size", 30)
//					.text(function(d) { return d.text; })
//					
//				g.selectAll("g").selectAll("text")
//					.attr("dx", function(d, j) { 
//						return -(0.5 * this.getComputedTextLength());  
//					})
				
				
		} else if (json_obj.status == "allEdgesSend") {			
			g.selectAll()
				.data(edgeData)
				.enter()
				.append("polyline")
				.attr("points", function(d) { return d.points; })
				.attr("class", function(d) { return ("edge " + d.subStructure); })
				.attr("fill", "none")
				.attr("stroke", "black")
				.attr("stroke-width", function(d) { return d.thickness; })
				.attr("stroke-linejoin", "round")
				.on('mouseover', function() { //fügt highlight bei mouseover hinzu
					let currentEdge = d3.select(this);
					//console.log(currentEdge.attr("class"));
				});				
		} else if (json_obj.status == "allArrowsSend") {			
			g.selectAll()
				.data(arrowData)
				.enter()
				.append("polygon")
				.attr("points", function(d) { return d.points; })
				.attr("class", function(d) { return ("arrow " + d.subStructures); })
				.attr("fill", "black")
				.attr("stroke", "black")
				.attr("stroke-linejoin", "round")
				.attr("stroke-width", 1);
		} else if (json_obj.status == "drawingArea") {
			resizeGraphSVG(svg, document.getElementById("drawingArea"));
			//let width = drawingArea.clientWidth;//(json_obj.minValue + 200);
			//let height = drawingArea.clientHeight;//(json_obj.maxValue + 200);
			//svg.attr("width", width).attr("height", height);
			//svg.attr("transform", "translate(0, 0) scale(1)");
			//g.attr("transform", "translate(0, 0) scale(1)");
			//svg.call(d3.zoom.transform, d3.zoomIdentity);
		} else if (json_obj.status == "subStructureColor") {			
			subStructureList = JSON.parse(json_obj.jSonString);
			for (i = 0; i < subStructureList.subStructureJSclasses.length; i++) {
				subStructureClass = JSON.parse(subStructureList.subStructureJSclasses[i]);
				subStructureClass.selected = false;
				//console.log(subStructureClass.composedName);
				subStructureClass.highlighted = false;
				subStructureMap.set(subStructureClass.composedName, subStructureClass);
			}
			fillOrderList(subStructureMap.keys());
			createLegend();
			
			duplicateSVG();
			startZoomingGraph();
			initZoom(svg, g, svgZoom);
			
			toggleLoading();
		}
		
		//toggleLoading();
	};

	creationWebSocket.onclose = function() {
		console.log('Creation Controller Websocket: Closed');
	};
	creationWebSocket.onerror = function(evt) {
		console.log('Creation Controller Websocket: ' + evt.msg);
	};
}

/*function zoomed(event) {
    const {transform} = event;
    svg.attr("transform", transform).attr("stroke-width", 5 / transform.k);
    gx.call(xAxis, transform.rescaleX(x));
	gy.call(yAxis, transform.rescaleY(y));
}*/

function myTextFilter(array, searchPattern) {
	
	searchPattern = searchPattern.toLowerCase();
	
	let newArray = JSON.parse(JSON.stringify(array)); 
	
  	newArray = newArray.filter(function(element) { 
		if (element.children.some((child) => child.text.toLowerCase().includes(searchPattern))) {
			element.children = element.children.filter(function(childElement) {
				return childElement.text.toLowerCase().includes(searchPattern);
			});
			return true;
		} else if (element.text.toLowerCase().includes(searchPattern)) {
			return true;
		} else {
			return false;
		}
	
  
  	});
  
  	return newArray;
};

function myFilter(array, searchPattern, blockCountMin, blockCountMax) {
	
	//console.log(searchPattern);
	
	if (searchPattern == null) {
		searchPattern = document.getElementById("CSTreeContainerFilterInput").value.toLowerCase();
	}
	
	if (blockCountMin == null || blockCountMax == null) {
		blockCountMin = parseInt(document.getElementById("BlockCountMin").value);
		blockCountMax = parseInt(document.getElementById("BlockCountMax").value);
	}
		
	let newArray = JSON.parse(JSON.stringify(array)); 
	
  	newArray = newArray.filter((element) => {
		if (element.children.some((child) => (child.blockCount <= blockCountMax && child.blockCount >= blockCountMin))) {
			element.children = element.children.filter(function(childElement) {
				return (childElement.blockCount <= blockCountMax && childElement.blockCount >= blockCountMin);
			});
			return true;
		}
  	});

  	newArray = newArray.filter(function(element) { 
		if (element.children.some((child) => child.text.toLowerCase().includes(searchPattern))) {
			element.children = element.children.filter(function(childElement) {
				return childElement.text.toLowerCase().includes(searchPattern);
			});
			return true;
		} else if (element.text.toLowerCase().includes(searchPattern)) {
			return true;
		} else {
			return false;
		}
  	});
  
  	return newArray;
};

function resetForm(element, depth = 3) {
    element.value = ""; //Reset manually the form
    let newArray = myFilter(treeMapData, "", null, null);
    fillCSTreeMap(newArray, treeMapSelection, depth);
}


function textFilterCSTreeContainer(textInputField) {
	//console.log(textInputField);
	let filterText = textInputField.value;
	//console.log(filterText);
	let newArray = myFilter(treeMapData, filterText, null, null);
	//newArray.forEach(removeParent);
	//newArray.forEach(element => element.children.forEach(removeParent));
	//console.log(newArray);
	fillCSTreeMap(newArray, treeMapSelection, 3);
}

function fillCSTreeMap(data, selection, closeDepth = 2) {
	//console.log(data);
	//console.log(selection)
	
	if (treeMapOriginal === null) {
		treeMapOriginal = new Tree('.TreeContainer', {
        	data: [{ id: '-1', text: 'All', children: data }],
		});
	}
	
	treeMap = new Tree('.TreeContainer', {
        data: [{ id: '-1', text: 'All', children: data }],
        closeDepth: closeDepth,
        values: selection,
        loaded: function () {
        },
        onChange: function () {
			let usedCSlist = document.getElementById("usedCS");
			//usedCSlist.innerHTML = "";
			
			let deleteList = Array.from(usedCSlist.childNodes).map(element => element.id);
			treeMapSelection = migrateArrayEntries(treeMapSelection, this.values, this)
			//console.log("Selection " + treeMapSelection);

			for (let checkedIndex of treeMapSelection){
				let selectedNode = treeMapOriginal.nodesById[checkedIndex];
				let subStructure = selectedNode.text + " (" + selectedNode.parent.text + ")";
												
				if (deleteList.includes(subStructure)) {
					//Remove substructe from deleteList
					deleteList = deleteList.filter(function (element) {
						return element != subStructure; 
					});
				} else {					let li = document.createElement("li");
					li.setAttribute("class", "facet");
					li.setAttribute("id", subStructure);
					li.innerHTML = subStructure;
					usedCSlist.appendChild(li)
				}	
			}
						
			for (let i = usedCSlist.childNodes.length - 1; i >= 0; i--) {
				child = usedCSlist.childNodes[i];
				//console.log("del: " + deleteList);
				if (deleteList.includes(child.id)) {
					usedCSlist.removeChild(child);
				}
			}
			
        }
    });
}

function migrateArrayEntries(targetArray, sourceArray, sourceTreeMap) {
	let deleteArray = [];
		
	targetArray.forEach(function (targetElement) {
		if (!sourceArray.includes(targetElement) && sourceTreeMap.nodesById[targetElement]) {
			deleteArray.push(targetElement);
		}
	});
	
	sourceArray.forEach(function (sourceElement) {
		if (!targetArray.includes(sourceElement)) {
			targetArray.push(sourceElement);
		}
	});
	
	targetArray = targetArray.filter(el => !deleteArray.includes(el));
		
	return targetArray;
}

function startZoomingGraph() {
	svgZoom = d3.zoom()
		.on("zoom", function(event) {
			transformation = event.transform;
			g.attr("transform", transformation);
			
			drawRectMiniMap(transformation);
		});
	svg.call(svgZoom);
	svg.on("dblclick.zoom", null);
}

function initZoom(svg, g, svgZoom, paddingPercent = 1) {
	//let x = - g.select("rect").attr("x") + 20;
	//let y = - g.select("rect").attr("y") + (Number(svg.attr("height")) * 0.5);
	
	let bounds = g.node().getBBox();
	let parent = svg.node().parentElement;
	let fullWidth = parent.clientWidth,
	    fullHeight = parent.clientHeight;
	let width = bounds.width,
	    height = bounds.height;
	let midX = bounds.x + width / 2,
	    midY = bounds.y + height / 2;
	if (width == 0 || height == 0) return; // nothing to fit
	let scale = (paddingPercent) / Math.max(width / fullWidth, height / fullHeight);
	let translate = [fullWidth / 2 - scale * midX, fullHeight / 2 - scale * midY];
	
	//console.log("zoomFit", translate, scale);
	svg.call(svgZoom.transform,d3.zoomIdentity.translate(translate[0], translate[1]).scale(scale))

}

function miniMap() {
	let checked = document.getElementById("miniMapCheckBox").checked;
	let miniMapContainer = document.getElementById("miniMapContainer");
	
	if (checked === true) {
		miniMapContainer.classList.remove("collapsed");
		duplicateSVG();
		drawRectMiniMap(transformation);
	} else {
		miniMapContainer.classList.add("collapsed");
	}
}

function duplicateSVG() {
    let content = document.getElementById("drawingArea").innerHTML;
    let div = document.getElementById("miniMapContent");
	div.innerHTML = content;
	miniMapSvg = d3.select("#miniMapContent").select("svg");
	miniMapSvg.select("#legend").attr("display", "none");
	miniMapG = miniMapSvg.select("g");
	miniMapSvgZoom = d3.zoom()
		.on("zoom", function(event) {
			let transformation = event.transform;
			miniMapG.attr("transform", transformation);
			initDrawRectMiniMap(transformation.k);
		});
	resizeGraphSVG(miniMapSvg, div);
	initZoom(miniMapSvg, miniMapG, miniMapSvgZoom, 0.99);
	
	//document.body.appendChild(div);
	//console.log(div);
}

function initDrawRectMiniMap(scale) {
	let strokeWidth = 1 / scale;
	miniMapRect = miniMapG.append("rect")
		.attr("x", 0)
		.attr("y", 0)
		.attr("width", 0)
		.attr("height", 0)
		.attr("stroke", "red")
		.attr("stroke-width", strokeWidth)
		.attr("fill", "none");
}

function drawRectMiniMap(transformation) {
	let svgWidth = Number(svg.attr("width")); 
	let svgHeight = Number(svg.attr("height")); 
	let x = (0 - transformation.x) / transformation.k;
	let y = (0 - transformation.y) / transformation.k;
	let width = ((svgWidth - transformation.x) / transformation.k) - x;
	let height = ((svgHeight - transformation.y) / transformation.k) - y;
	if (miniMapRect != undefined) {
		miniMapRect.attr("x", x)
			.attr("y", y)
			.attr("width", width)
			.attr("height", height);
	}
}

function drawGraph() {
	
	toggleLoading();
	
	/*cy = cytoscape({
				container : document.getElementById('cy'),
				//elements: [
				// nodes
				 { data: { id: 'a'}, positions: { x:100, y:100 } },
				{ data: { id: 'b'}, positions: { x:200, y:100 } },
				{ data: { id: 'c'}, positions: { x:300, y:100 } },
				{ data: { id: 'd'}, positions: { x:400, y:100 } },
				{ data: { id: 'e'}, positions: { x:500, y:100 } },
				{ data: { id: 'f'}, positions: { x:600, y:100 } }, */
	// edges
	/* {
	  data: {
		id: 'ab',
		source: 'a',
		target: 'b'
	  }
	},
	{
	  data: {
		id: 'cd',
		source: 'c',
		target: 'd'
	  }
	},
	{
	  data: {
		id: 'ef',
		source: 'e',
		target: 'f'
	  }
	},
	{
	  data: {
		id: 'ac',
		source: 'a',
		target: 'c'
	  }
	},
	{
	  data: {
		id: 'be',
		source: 'b',
		target: 'e'
	  }
	} 
	//],
	style : [ {
		selector : 'node',
		style : {
			shape : 'rectangle',
			'background-color' : 'red',
			label : 'data(id)'
		},
		selector : 'edge',
		style : {
			'curve-style' : 'taxi',
			'edge-distances' : 'node-position',
			'taxi-direction' : 'horizontal',
			'target-arrow-shape' : 'triangle'
		}
	} ],

	layout : [ {
		name : 'preset'
	}],
});
	
cy.collection();*/

	if (svg == undefined) {
		svg = d3.select("#drawingArea").append("svg:svg");//.attr("preserveAspectRatio", "xMinYMin meet");//.call(d3.zoom().on("zoom", zoomed));	
		g = svg.append("g")
      			.attr("cursor", "grab");
		//svg.attr("viewBox", [0, 0, width, height]);
		legendHolder = svg.append("g").attr("id", "legend");
	}

	g.selectAll("*").remove();
	legendHolder.selectAll("*").remove();
	//svg.selectAll("*").remove();
	     //.attr("width", 3000)
		 //.attr("height", 3000);

	//const daten = [{x : 5, id : 1}, {x : 10, id : 2}, {x : 15, id : 3}]

	//return svg.node();

	vertexData = [];
	edgeData = [];
	arrowData = [];
	subStructureMap = new Map();
	selectedSuperHighlightedItem = "";
	highlightList = [];

	let usedCSlist = document.getElementById("usedCS");
	let spaceField = document.getElementById("space");
	let thicknessField = document.getElementById("thickness");
	let join = document.getElementById("join");

	let minDrawLengthVertex = document.getElementById("minDrawLengthVertex");
	let maxDrawLengthVertex = document.getElementById("maxDrawLengthVertex");
	let minDrawHeightVertex = document.getElementById("minDrawHeightVertex");
	let minHorizontalInterSpace = document.getElementById("minHorizontalInterSpace");
	let minTriangleHeight = document.getElementById("minTriangleHeight");
	let triangleLength = document.getElementById("triangleLength");

	let selectedCS = [];
	for (li of usedCSlist.childNodes) {
		selectedCS.push(li.innerHTML);
	}

	let parameterMessage = JSON.stringify({"minDrawLengthVertex" : minDrawLengthVertex.value, "maxDrawLengthVertex" : maxDrawLengthVertex.value, "minDrawHeightVertex" : minDrawHeightVertex.value, "minHorizontalInterSpace" : minHorizontalInterSpace.value, "minTriangleHeight" : minTriangleHeight.value, "triangleLength" : triangleLength.value});
	let message = { "status": "drawGraph", "selectionList": selectedCS, "minValue": spaceField.value, "maxValue": thicknessField.value, "bool": join.checked, "jSonString" : parameterMessage};
	creationWebSocket.send(JSON.stringify(message));
}

function colorAllSequences(value) {
	d3.select("#checkboxColorSequence").property("checked", value);
	
	subStructureList = Array.from(subStructureMap.keys());
	for (let selectedSubStructure of subStructureList) {
		console.log(selectedSubStructure);
		highlightTooltipSubStructure = selectedSubStructure;
		if (subStructureMap.get(highlightTooltipSubStructure).selected != value) {
			colorSequenceChange(value);
		}
	}
}

function checkAllSequencesColored() {
	let allSelected = true;
	for (let selectedSubStructure of Array.from(subStructureMap.keys())) {
		if (!subStructureMap.get(selectedSubStructure).selected) {
			allSelected = false;
		}
	}
	
	d3.select("#checkboxColorAllSequences").property("checked", allSelected);
}

function colorSequenceChange(value) {
	subStructure = subStructureMap.get(highlightTooltipSubStructure);
	subStructure.selected = value;
	
	if (value) {
		highlightAllEdgesFromSubStructure();
	} else {
		if (subStructure.highlighted == true) {
			superHighlighting();
			subStructure.highlighted = false;
			d3.select("#checkboxHighlightSequence").property("checked", subStructure.highlighted);
		}
		unhighlightAllEdgesFromSubStructure();
	}
	
	checkAllSequencesColored();
	console.log(highlightTooltipSubStructure, value);
}

function highlightChange(value) {
	subStructure = subStructureMap.get(highlightTooltipSubStructure);
	subStructure.highlighted = value;
	
	if (value) {
		if (selectedSuperHighlightedItem != "") {
			subStructureMap.get(selectedSuperHighlightedItem).highlighted = false;
		}
		if (subStructure.selected == false) {
			highlightAllEdgesFromSubStructure();
			d3.select("#checkboxColorSequence").property("checked", subStructure.selected);
		}
		superHighlighting();
	} else {
		superHighlighting();
	}
	
	console.log(highlightTooltipSubStructure, value);
}

function createLegend() {
	let size = 30;
	
	let xStart = 20;
	let yStart = 20;
	
	let defaultPoints = "" + xStart + "," + yStart + " " + (xStart + size) + "," + yStart + " " + (xStart + (0.5 *size)) + "," + (yStart + size);
		
	let collapseGroup = legendHolder.append("g")
		.attr("id", "collapseGroup")
		.on('click', function(event, d) {
			if (legendHolder.selectAll("#subStructureList").attr("display") == null) {
				/*legendOpacity = 0;
				legendHolder.selectAll(".groups")
					.style("opacity", legendOpacity)*/
				//d3.select(this).select("polyline").attr("points", switchedPoints);
				legendHolder.selectAll("#subStructureList").attr("display", "none");
				//legendHolder.selectAll(".groups").select("text").property("display", "none")
			} else {
				/*legendOpacity = 1;
				legendHolder.selectAll(".groups")
					.style("opacity", legendOpacity)*/
				//d3.select(this).select("polyline").attr("points", defaultPoints);
				legendHolder.selectAll("#subStructureList").attr("display", null);
				//legendHolder.selectAll(".groups").select("text").property("display", null);
			}
		});
	
	
	collapseGroup
		.append("polyline")
		.attr("points", defaultPoints)
		.attr("fill", "black")
		.attr("stroke", "black")
		.attr("stroke-linejoin", "round")
		.attr("stroke-width", 1);
		
	collapseGroup
	  .append("text")
	    .attr("x", xStart + size*1.2)
	    .attr("y", function(d,i){ return yStart + (size/2) + 10}) // 100 is where the first dot appears. 25 is the distance between dots
	    .style("fill", "black")
	    .text("Legend")
	    .attr("text-anchor", "left")
	    .attr("font-size", 30)
	    .style("alignment-baseline", "middle");
	    
	collapseGroup
		.append("title")
		.text("Click to collapse the legend.");
	
	yStart = yStart + size + 10;
	
	let i = 0;
	for (subStructre of subStructureMap) {
		subStructre[1].rectX = xStart;
		subStructre[1].rectY = yStart + i*(size+5);
		subStructre[1].textX = xStart + size*1.2;
		subStructre[1].textY = yStart + i*(size+5) + (size/2) + 10;
		i = i + 1;
	}
	
	let subStructureList = legendHolder.append("g")
		.attr("id", "subStructureList")
	
	subStructureList
		.append("title")
		.text("Clicking on a substructure opens a Tooltip. In it, the selected substructure can be colored or even strongly highlighted.");
	
	let groups = subStructureList.selectAll(".groups")
		.data(subStructureMap)
		.enter()
		.append("g")
		.style("fill", "black")
		.on('click', function(event, d) {
			highlightTooltipSubStructure = d[0];
			subStructure = subStructureMap.get(highlightTooltipSubStructure);
			d3.select("#checkboxColorSequence").property("checked", subStructure.selected);
			d3.select("#checkboxHighlightSequence").property("checked", subStructure.highlighted);
			
			showTooltip(divHighlightTooltip, true, [50, 0]);
/*			if (d[1].selected) {
				if (!selectedSuperHighlightedItem == "") {
					superHighlighting(d[0]);
				} else {
					d3.select(this).select("rect").style("fill", "black");
					d3.select(this).select("text").style("fill", "black");
					unhighlightAllEdgesFromSubStructure(d[0]);
				}
			} else {
				d3.select(this).select("rect").style("fill", d[1].color);
				d3.select(this).select("text").style("fill", d[1].color);
				highlightAllEdgesFromSubStructure(d[0]);
			}*/
		})
		.on('dblclick', function(event, d) {
/*			if (d[1].selected) {
				superHighlighting(d[0]);
			} else {
				d3.select(this).select("rect").style("fill", d[1].color);
				d3.select(this).select("text").style("fill", d[1].color);
				highlightAllEdgesFromSubStructure(d[0]);
				superHighlighting(d[0]);
			}*/
		})
		.attr("class", "groups");
			
	groups
	  	.append("rect")
	    	.attr("x", function(d){ return d[1].rectX})
	    	.attr("y", function(d){ return d[1].rectY}) // 100 is where the first dot appears. 25 is the distance between dots
	    	.attr("width", size)
	    	.attr("height", size)
	    	.style("fill", function(d) {return d[1].color;})
	    	.on('click', function(event, d) {
				console.log(d)
				console.log(d3.select(this));
			});
	    		
	groups
	  .append("text")
	    .attr("x", function(d){ return d[1].textX})
	    .attr("y", function(d){ return d[1].textY}) // 100 is where the first dot appears. 25 is the distance between dots
	    .style("fill", function(d) {return d[1].color;})
	    .text(function(d){ return d[0]})
	    .attr("text-anchor", "left")
	    .attr("font-size", 30)
	    .style("alignment-baseline", "middle");
}

function fillCSlist(comparableSequences) {
	let unusedCSlist = document.getElementById("unusedCS");
	let usedCSlist = document.getElementById("usedCS");
	unusedCSlist.innerHTML = "";
	usedCSlist.innerHTML = "";

	for (let subStructure of comparableSequences) {
		let li = document.createElement("li");
		li.setAttribute("class", "facet");
		li.setAttribute("id", subStructure);
		li.innerHTML = subStructure;
		unusedCSlist.appendChild(li);
	}
}

function fillOrderList(chromosomes) {
	let unusedSequences = document.getElementById("unusedSequences");
	let newSequenceOrder = document.getElementById("newSequenceOrder");
	unusedSequences.innerHTML = "";
	newSequenceOrder.innerHTML = "";

	for (let chromosomeName of chromosomes) {
		let selectedSubStructureColor = subStructureMap.get(chromosomeName).color;
		
		let liNewOrder = document.createElement("li");
		liNewOrder.setAttribute("class", "facet");
		liNewOrder.setAttribute("id", chromosomeName + "NewOrder");
		liNewOrder.innerHTML = chromosomeName;
		newSequenceOrder.appendChild(liNewOrder);
		liNewOrder.style.border="5px solid " + selectedSubStructureColor;
	}
}

function loadGS() {
	let startField = document.getElementById("GSstart");
	let endField = document.getElementById("GSend");
	
	if (startField.value && endField.value) {
		if ((startField.value <= startField.max && startField.value >= startField.min) && (endField.value <= endField.max && endField.value >= endField.min)) {
			let message = { "status": "loadGS", "minValue": startField.value, "maxValue": endField.value };
			creationWebSocket.send(JSON.stringify(message));
			toggleLoading();
		} else {
			alert("The selected limits are not in the correct range.")
		}
	} else {
		alert("The selected limits are not in the correct range.")
	}
}

function loadCS() {
	let lengthField = document.getElementById("LengthFilter");
	let message = { "status": "lengthFilter", "minValue": lengthField.value };
	creationWebSocket.send(JSON.stringify(message));
	toggleLoading();
}

function updateBoundariesLengthFilter(start, end) {
	let label = document.getElementById("GSpossibleRangeLable");
	let startField = document.getElementById("GSstart");
	let endField = document.getElementById("GSend");
	label.innerHTML = "Selecte a range for the GS between " + start + " and " + end;
	startField.min = start;
	startField.max = parseInt(end) - 1;
	endField.min = parseInt(start) + 1;
	endField.max = end;
	startField.value = start;
	showVal("Start");
	endField.value = end;
	showVal("End");
}

function updateBoundariesBlockCountFilter(min, max) {
	let label = document.getElementById("CSBlockCountFilterLable");
	let startField = document.getElementById("BlockCountMin");
	let endField = document.getElementById("BlockCountMax");
	label.innerHTML = "Alignment Block Count Filter between " + min + " and " + max;
	startField.min = min;
	startField.max = parseInt(max) - 1;
	endField.min = parseInt(min) + 1;
	endField.max = max;
	startField.value = min;
	endField.value = max;
	showValBlockCount("Start");
	showValBlockCount("End");
}

function updateExtremesLengthFilter(min, max) {
	let label = document.getElementById("LengthFilterLabel");
	let lengthField = document.getElementById("LengthFilter");
	lengthField.min = min;
	lengthField.max = max;
	lengthField.value = min;
	updateLabel(label, "Minimum Block Length (GS)", lengthField);
}

function updateExtremesBlockCountFilter(data) {
	let min = Number.MAX_SAFE_INTEGER;
	let max = Number.MIN_SAFE_INTEGER;
	data.forEach(el => el.children.forEach(function (childrenEl) {
		if (childrenEl.blockCount < min) {
			min = childrenEl.blockCount;
		}
		if (childrenEl.blockCount > max) {
			max = childrenEl.blockCount;
		}
	}));
	/*let label = document.getElementById("BlockCountFilterLabel");
	let blockCountField = document.getElementById("BlockCountFilter");
	blockCountField.min = min;
	blockCountField.max = max;
	blockCountField.value = max;
	updateLabel(label, "Block Filter", blockCountField);*/
	updateBoundariesBlockCountFilter(min, max);
}

function showVal(initText) {
	let startField = document.getElementById("GSstart");
	let endField = document.getElementById("GSend");

	let startLabel = document.getElementById("GSstartLabel");
	let endLabel = document.getElementById("GSendLabel");

	let currentStartValue = parseInt(startField.value);
	let currentEndValue = parseInt(endField.value);

	if (initText === "End") {

//		if (currentStartValue >= currentEndValue) {
//			startField.value = currentEndValue - 1;
//			updateLabel(startLabel, "Start", startField);
//		}

		updateLabel(endLabel, "End", endField);
	} else {
//		if (currentEndValue <= currentStartValue) {
//			endField.value = currentStartValue + 1;
//			updateLabel(endLabel, "End", endField);
//		}

		updateLabel(startLabel, "Start", startField);
	}
	status1();
}

function showValBlockCount(initText) {
	let startField = document.getElementById("BlockCountMin");
	let endField = document.getElementById("BlockCountMax");

	let startLabel = document.getElementById("BlockCountMinLabel");
	let endLabel = document.getElementById("BlockCountMaxLabel");

	let currentStartValue = parseInt(startField.value);
	let currentEndValue = parseInt(endField.value);

	if (initText === "End") {

		if (currentStartValue >= currentEndValue) {
			startField.value = currentEndValue - 1;
			updateLabel(startLabel, "Min", startField);
		}

		updateLabel(endLabel, "Max", endField);
	} else {
		if (currentEndValue <= currentStartValue) {
			endField.value = currentStartValue + 1;
			updateLabel(endLabel, "Max", endField);
		}

		updateLabel(startLabel, "Min", startField);
	}
	
	let newArray = myFilter(treeMapData, null, currentStartValue, currentEndValue);
	fillCSTreeMap(newArray, treeMapSelection, 3);
}

function updateLabel(label, initText, field) {
	label.innerHTML = initText + ": " + field.value;
}

function closeIfOpen() {
	if (creationWebSocket != undefined) {
		//console.log(creationWebSocket.readyState);
		if (creationWebSocket.readyState === 0 || creationWebSocket.readyState === 1) {
			creationWebSocket.close();
		}
	}
}

function removeOptions(selectElement) {
	var i, L = selectElement.options.length - 1;
	for (i = L; i >= 0; i--) {
		selectElement.remove(i);
	}
}

function changeSelection(status, selectedOption) {
	let message = { "status": status + "Selected", "selection": selectedOption };
	creationWebSocket.send(JSON.stringify(message));
}

function escapePoint(text) {
	text = text.replace(/\./g, "\\.");
	return text;
}

function highlightAllEdgesFromSubStructure() {
	console.log("Select: " + highlightTooltipSubStructure);
	let subStructureName = highlightTooltipSubStructure;
	let selectedSubStructure = subStructureMap.get(subStructureName);
	selectedSubStructure.selected = true;
	let color = selectedSubStructure.color;
	//selectedItem.css("border", ("5px solid " + color));
	
	let words = subStructureName.split(" ");
	
	g.selectAll(".edge." + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, '')))
		.attr("stroke", color);
		
	highlightList.push(subStructureName);
		
	drawGlyphs();
}

function unhighlightAllEdgesFromSubStructure() {
	let subStructureName = highlightTooltipSubStructure;
	let selectedSubStructure = subStructureMap.get(subStructureName);
	subStructureMap.get(subStructureName).selected = false;
	let color = selectedSubStructure.color;
	//selectedItem.css("border",  "3px solid " + color);
	
	let words = subStructureName.split(" ");
	
	g.selectAll(".edge." + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, '')))
		.attr("stroke", "black");
		
	if (selectedSuperHighlightedItem == highlightTooltipSubStructure) {
		g.selectAll("*")
			.attr("opacity", 1)
			.attr("fill-opacity", 1);
		g.selectAll(".edge")
			.attr("stroke-width", function(d) {return d.thickness;});
	}
			
	highlightList = highlightList.filter(item => item !== subStructureName);

	drawGlyphs();
}

function superHighlighting() {
	let subStructureName = highlightTooltipSubStructure;
	let words = subStructureName.split(" ");
	
	console.log(escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, '')));
	
	g.selectAll(".edge")
			.attr("stroke-width", function(d) {return d.thickness;});
	
	if (selectedSuperHighlightedItem != highlightTooltipSubStructure) {
		g.selectAll("*")
			.attr("opacity", 0.5)
			.attr("fill-opacity", 0.5);
		g.selectAll("." + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, '')))
			.attr("opacity", 1)
			.attr("fill-opacity", 1);
		g.selectAll(".edge." + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, '')))
			.attr("stroke-width", function(d) {return + d.thickness + (d.thickness * d.thickness);});
		
		selectedSuperHighlightedItem = highlightTooltipSubStructure;
	} else {
		selectedSuperHighlightedItem = "";
		g.selectAll("*")
			.attr("opacity", 1)
			.attr("fill-opacity", 1);
		g.selectAll(".edge." + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, '')))
			.attr("stroke-width", function(d) {return d.thickness;});
	}
}	

function drawGlyphs() {
	g.selectAll(".glyph").remove();
		
	polylineStringStartGlyph();
	
	polylineStringEndGlyph();
	
	polylineStringDirectionGlyphs();
}

function polylineStringDirectionGlyphs() {
	level = highlightList.length - 1;
	size = 10;
	
	for (let i = 0; i <= level; i++) {
		let subStructureName = highlightList[i];
		let selectedSubStructure = subStructureMap.get(subStructureName);
		let color = selectedSubStructure.color;
		let words = subStructureName.split(" ");
		let className = escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, ''));
		let string = "";
		g.selectAll(".vertex." + className).each(function(d) {
			let posExists = false;
			let negExists = false;
			
			
			for (let localAlignmentBlock of d.localSeqAlignmentData) {
				if (subStructureName in localAlignmentBlock) {
					if (localAlignmentBlock[subStructureName].strand == "TRUE") {
						posExists = true;
					} else if (localAlignmentBlock[subStructureName].strand == "FALSE") {
						negExists = true;
					}
				}
				
				if (posExists && negExists) {
					break;
				}
			}
			
			let x = parseFloat(d.x);
			let y = parseFloat(d.y);
			let lengthBase = d.width * 0.5 - 2 - size;
			y = y + (size * (i + 1));
		    
		    //plus-Strand
		    if (posExists) {
			    string = x + "," + y + " ";
				string += (x + lengthBase) + "," + y + " ";
				string += (x + lengthBase + size) + "," + (y + (0.5 * size)) + " ";
				string += (x + lengthBase) + "," + (y  + size) + " ";
				string += x + "," + (y + size) + " ";
				
				g.append("polyline")
					.attr("points", string)
					.attr("class", ("glyph direction positive " + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, ''))))
					.attr("fill", color)
					.attr("stroke", color)
					.attr("stroke-linejoin", "round")
					.attr("stroke-width", 1);
			}
			
			if (negExists) {
				//minus-Strand
				x = x + 1;
				string = x + "," + y + " ";
				string += (x - lengthBase) + "," + y + " ";
				string += (x - lengthBase - size) + "," + (y + (0.5 * size)) + " ";
				string += (x - lengthBase) + "," + (y  + size) + " ";
				string += x + "," + (y + size) + " ";
				
				g.append("polyline")
					.attr("points", string)
					.attr("class", ("glyph direction negative " + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, ''))))
					.attr("fill", color)
					.attr("stroke", color)
					.attr("stroke-linejoin", "round")
					.attr("stroke-width", 1);
			}
		});
	}
	


}


function polylineStringStartGlyph() {
	level = highlightList.length - 1;
	maxlevel = legendHolder.selectAll(".groups").size();
	size = 10;
	
	for (let i = 0; i <= level; i++) {
		let subStructureName = highlightList[i];
		let selectedSubStructure = subStructureMap.get(subStructureName);
		let startRect = g.select(".vertex.id_" + selectedSubStructure.startVertexID);
		let x = startRect.attr("x");
		let y = startRect.attr("y");
		let color = selectedSubStructure.color;
		let words = subStructureName.split(" ");
		
		x = + x + maxlevel - i;
		string = x + "," + y + " ";
		string += x + "," + (y - (i * size)) + " ";
		string += x + "," + (y - ((i + 1) * size)) + " ";
		string += ( + x + size) + "," + (y - ((i + 0.5) * size)) + " ";
		string += x + "," + (y - (i * size));
		
		g.append("polyline")
			.attr("points", string)
			.attr("class", ("glyph start " + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, ''))))
			.attr("fill", color)
			.attr("stroke", color)
			.attr("stroke-linejoin", "round")
			.attr("stroke-width", 1);
	}
}

function polylineStringEndGlyph() {
	level = highlightList.length - 1;
	maxlevel = legendHolder.selectAll(".groups").size();
	size = 10;
	
	for (let i = 0; i <= level; i++) {
		let subStructureName = highlightList[i];
		let selectedSubStructure = subStructureMap.get(subStructureName);
		let endRect = g.select(".vertex.id_" + selectedSubStructure.endVertexID);
		let x = (1 * endRect.attr("x") + 1 * endRect.attr("width"));
		let y = endRect.attr("y");
		let color = selectedSubStructure.color;
		let words = subStructureName.split(" ");
		
		x = + x - maxlevel + i;
		string = x + "," + y + " ";
		string += x + "," + (y - (i * size)) + " ";
		string += x + "," + (y - ((i + 1) * size)) + " ";
		string += ( + x - size) + "," + (y - ((i + 0.5) * size)) + " ";
		string += x + "," + (y - (i * size));
		
		
		g.append("polyline")
			.attr("points", string)
			.attr("class", ("glyph end " + escapePoint("s" + words[0]) + "g" + escapePoint(words[1].replace(/[()]/g, ''))))
			.attr("fill", color)
			.attr("stroke", color)
			.attr("stroke-linejoin", "round")
			.attr("stroke-width", 1);
	}
}
