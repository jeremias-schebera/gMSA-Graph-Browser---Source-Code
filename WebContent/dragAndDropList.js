let defaultColor = "#fafafa";

$(function() {
	$("#unusedCS, #usedCS").sortable({
		connectWith: "#unusedCS, #usedCS",
		placeholder: "placeholder",
		delay: 150
	})
		.disableSelection()
		/*.dblclick(function(e) {
			var item = e.target;
			if (e.currentTarget.id === 'unusedCS' && $(item)[0].nodeName.toLowerCase() === "li") {
				//move from all to user
				$(item).fadeOut('fast', function() {
					$(item).appendTo($('#usedCS')).fadeIn('slow');
				});
			} else if (e.currentTarget.id === 'usedCS' && $(item)[0].nodeName.toLowerCase() === "li") {
				//move from user to all
				$(item).fadeOut('fast', function() {
					$(item).appendTo($('#unusedCS')).fadeIn('slow');
				});
			}
		});*/
});

/*$(function() {
	$("#allSequences, #highlightedSequences").sortable({
		connectWith: "#allSequences, #highlightedSequences",
		placeholder: "placeholder",
		delay: 150,
		receive: function( event, ui ) {
			if (ui.item.parent()[0].id === "highlightedSequences") {
				highlightAllEdgesFromSubStructure(ui.item);
			} else if (ui.item.parent()[0].id === "allSequences") {
				unhighlightAllEdgesFromSubStructure(ui.item, defaultColor);
			}
		},
		
		update: function ( event, ui ) {
			if (ui.item.parent()[0].id === "highlightedSequences") {
				drawGlyphs();
			}
		}
	})
		.disableSelection()
		.dblclick(function(e) {
			var item = e.target;
			if (e.currentTarget.id === 'allSequences' && $(item)[0].nodeName.toLowerCase() === "li") {
				//move from all to user
				$(item).fadeOut('fast', function() {
					$(item).appendTo($('#highlightedSequences')).fadeIn('slow');
					highlightAllEdgesFromSubStructure($(item));
				});
			} else if (e.currentTarget.id === 'highlightedSequences' && $(item)[0].nodeName.toLowerCase() === "li") {
				superHighlighting($(item));
				//move from user to all
				//$(item).fadeOut('fast', function() {
					//$(item).appendTo($('#allSequences')).fadeIn('slow');
					//unhighlightAllEdgesFromSubStructure($(item), defaultColor);
				//});
			}
		});
});*/

$(function() {
	$("#unusedSequences, #newSequenceOrder").sortable({
		connectWith: "#unusedSequences, #newSequenceOrder",
		placeholder: "placeholder",
		delay: 150,
		receive: function( event, ui ) {
			vertexUpdateCheckboxCheck();
		},
		update: function ( event, ui ) {
			vertexUpdateCheckboxCheck();
		}
	})
		.disableSelection()
		.dblclick(function(e) {
			var item = e.target;
			if (e.currentTarget.id === 'unusedSequences' && $(item)[0].nodeName.toLowerCase() === "li") {
				//move from all to user
				$(item).fadeOut('fast', function() {
					$(item).appendTo($('#newSequenceOrder')).fadeIn('slow');
					vertexUpdateCheckboxCheck();
					//highlightAllEdgesFromSubStructure($(item));
				});
			} else if (e.currentTarget.id === 'newSequenceOrder' && $(item)[0].nodeName.toLowerCase() === "li") {
				//superHighlighting($(item));
				//move from user to all
				$(item).fadeOut('fast', function() {
					$(item).appendTo($('#unusedSequences')).fadeIn('slow');
					vertexUpdateCheckboxCheck();
					//unhighlightAllEdgesFromSubStructure($(item), defaultColor);
				});
			}
		});
});

unusedSequences.innerHTML = "";
	newSequenceOrder.innerHTML = "";

$("#unusedCS, #usedCS #allSequences #highlightedSequences #unusedSequences #newSequenceOrder").hover(function() {
	$(this).css('cursor', 'pointer').attr('title', "Drag and drop to rearrange items within a list or between lists. Double-click to move item from one list to the bottom of the other.");
}, function() {
	$(this).css('cursor', 'auto');
});
