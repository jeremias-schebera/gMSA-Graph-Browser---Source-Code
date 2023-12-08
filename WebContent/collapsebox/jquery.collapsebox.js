/*
 * jQuery collapse (jQuery Plugin)
 *
 * Copyright (c) 2010 Tom Shimada
 *
 * Depends Script:
 *	js/jquery.js (1.3.2~)
 *	[use draggable (1.7.*)] ui.core.js
 *	[use draggable (1.7.*)] ui.draggable.js
 *	[use draggable (1.8.*)] ui.core.js
 *	[use draggable (1.8.*)] ui.widget.js
 *	[use draggable (1.8.*)] ui.mouse.js
 *	[use draggable (1.8.*)] ui.draggable.js
 */

(function($) {
  $.fn.collapsebox = function(configs) {
    var defaults = {
          fulcrumX: 'left',
          spaceX: 0,
          fulcrumY: 'top',
          spaceY: 0,
          zIndex: 9999,
          opacity: 1,
          handle: null, /* required */
          button: null, /* required */
          action: 'click',
          speed: 300,
          easing: 'swing',
          openButtonClass: 'collapsebox-open',
          closeButtonClass: 'collapsebox-close',
          openBeforeFunc: null,
          openAfterFunc: null,
          closeBeforeFunc: null,
          closeAfterFunc: null,
          draggable: false
        };
    if (!configs) return;
    configs = $.extend(defaults, configs);
    if (configs.fulcrumX != 'left' && configs.fulcrumX != 'right') return;
    if (configs.fulcrumY != 'top' && configs.fulcrumY != 'bottom') return;
    if (typeof(configs.spaceX) != 'number' || typeof(configs.spaceY) != 'number') return;
    if (!configs.button && !configs.handle) return;
    var selectors = {
          container: this,
          button: configs.button.nodeType ? configs.button : $(configs.button, this),
          handle: configs.handle.nodeType ? configs.handle : $(configs.handle, this),
          parent: $(this.parent().get(0))
        };
    if (!selectors.container.length || !selectors.button.length || !selectors.handle.length || !selectors.parent.length) return;
    // Todo: BODY直下の要素でないとバグる
    var nodeName;
    if (typeof selectors.parent.context === 'undefined') {
        nodeName = selectors.parent[0].nodeName.toUpperCase();
    } else {
        nodeName = selectors.parent.context.nodeName.toUpperCase();
    }
    if (nodeName !== 'BODY') return;

    var open_height = 0,
        close_height = 0,
        is_msie6 = (!$.support.style && typeof document.documentElement.style.maxHeight === 'undefined'),
        is_webkit = ($.support.checkOn !== undefined ? !$.support.checkOn : $.browser.safari);

    setPosition();
    if (configs.draggable === true) setDraggable();
    setAction();
    enable();

    function setPosition() {
      var cssObj = {};
      open_height = selectors.container.height();
      close_height = selectors.handle.outerHeight(true);
      if (open_height == close_height) {
        open_height = selectors.container.css({
          height: '',
          overflow: ''
        }).height();
        selectors.container.css({
          height: close_height,
          overflow: 'hidden'
        });
      }
      cssObj['height'] = open_height+'px';
      if (is_msie6 === false) {
        cssObj[configs.fulcrumX] = configs.spaceX;
        cssObj[configs.fulcrumY] = configs.spaceY;
        cssObj['position'] = 'fixed';
      } else {
        setMsIe6Position();
        if (configs.fulcrumX == 'right' || configs.fulcrumY == 'bottom') {
          var resizeTimer;
          $(window).resize(function(){
            if(resizeTimer)clearTimeout(resizeTimer);
            resizeTimer = setTimeout(function(){
              setMsIe6Position();
            }, 0);
          });
        }
        cssObj['position'] = 'absolute';
      }
      cssObj['zIndex'] = configs.zIndex;
      if (configs.opacity !== 1) {
        cssObj['opacity'] = configs.opacity;
      }
      selectors.container.css(cssObj);
    }
    function setMsIe6Position() {
      var $window = $(window),
          spaceX = configs.spaceX,
          spaceY = configs.spaceY;
      if (configs.fulcrumX == 'right') spaceX = $window.width() - configs.spaceX - selectors.container.outerWidth(false);
      if (configs.fulcrumY == 'bottom') spaceY = $window.height() - configs.spaceY - selectors.container.outerHeight(false);
      selectors.container.get(0).style.setExpression('left', 'eval('+spaceX+'+(document.body.scrollLeft||document.documentElement.scrollLeft))');
      selectors.container.get(0).style.setExpression('top', 'eval('+spaceY+'+(document.body.scrollTop||document.documentElement.scrollTop))');
    }

    function setDraggable() {
      var draggableObj;
      if (is_webkit === true && chkUiVersion() === false) {
        draggableObj = {
          start: function(e, ui){
            $(this).css('position', 'absolute');
          },
          stop: function(e, ui){
            var cssObj = getCorner();
            cssObj['position'] = 'fixed';
            $(this).css(cssObj);
          },
          containment: 'html',
          refreshPositions: true,
          handle: selectors.handle,
          zIndex: (configs.zIndex + 1),
          cursor: 'move',
          scroll: false
        };
      } else if (is_msie6 === true) {
        draggableObj = {
          stop: function(e, ui){
            var $window = $(window),
                offset = selectors.container.offset();
            if (configs.fulcrumX == 'right') {
              configs.spaceX = $window.width() - offset.left + $window.scrollLeft() - selectors.container.outerWidth(false);
            }
            if (configs.fulcrumY == 'bottom') {
              configs.spaceY = $window.height() - offset.top + $window.scrollTop() - selectors.container.outerHeight(false);
            }
            selectors.container.get(0).style.setExpression('left', 'eval('+(offset.left - $window.scrollLeft())+'+(document.body.scrollLeft||document.documentElement.scrollLeft))');
            selectors.container.get(0).style.setExpression('top', 'eval('+(offset.top - $window.scrollTop())+'+(document.body.scrollTop||document.documentElement.scrollTop))');
          },
          handle: selectors.handle,
          zIndex: (configs.zIndex + 1),
          cursor: 'move',
          scroll: false
        };
      } else {
        draggableObj = {
          stop: function(e, ui){
            var cssObj = getCorner();
            cssObj['position'] = 'fixed';
            $(this).css(cssObj);
          },
          handle: selectors.handle,
          zIndex: (configs.zIndex + 1),
          cursor: 'move',
          scroll: false
        };
      }
      selectors.handle.css('cursor', 'move');
      selectors.container.draggable(draggableObj);
    }

    function chkUiVersion() {
        var version = $.ui.version.split('.');
        if (parseInt(version[0]) > 0 && parseInt(version[1]) > 7) {
            return true;
        }
        return false;
    }

    function getCorner() {
      var $window = $(window),
          offset = selectors.container.offset(),
          position = {};
      if (configs.fulcrumX == 'left') {
        position[configs.fulcrumX] = offset.left - $window.scrollLeft();
        position['right'] = 'auto';
      } else {
        position[configs.fulcrumX] = $window.width() + $window.scrollLeft() - offset.left - selectors.container.outerWidth(false);
        position['left'] = 'auto';
      }

      if (configs.fulcrumY == 'top') {
        position[configs.fulcrumY] = offset.top - $window.scrollTop();
        position['bottom'] = 'auto';
      } else {
        position[configs.fulcrumY] = $window.height() + $window.scrollTop() - offset.top - selectors.container.outerHeight(false);
        position['top'] = 'auto';
      }
      return position;
    }

    function setAction() {
      selectors.button.unbind(configs.action).bind(configs.action, function() {
        toggle();
      });
    }

    function toggle() {
      if (!chkReady()) return;
      if (chkStatus() == 'open') close();
        else open();
    }

    function open() {
      if (!chkReady()) return;
      if (chkStatus() == 'open') return;
      disable();
      if ($.isFunction(configs.openBeforeFunc)) configs.openBeforeFunc(selectors.container, selectors.handle, selectors.button);
      if (is_msie6 === false) {
        var top = 'auto',
            bottom = 'auto';
        if (configs.fulcrumY == 'top') {
          top = selectors.container.css('top');
        } else {
          bottom = parseInt(selectors.container.css('bottom')) - (open_height - close_height);
          selectors.container.css({
            top: selectors.container.offset().top - $(window).scrollTop(),
            bottom: 'auto'
          });
        }
      }
      selectors.button.removeClass(configs.openButtonClass + ' ' + configs.closeButtonClass).addClass(configs.openButtonClass);
      selectors.container.animate(
        {
          height: open_height
        },
        configs.speed,
        configs.easing,
        function() {
          if (is_msie6 === false) {
            selectors.container.css({
              height: open_height+'px',
              overflow: '',
              top: top,
              bottom: bottom
            });
          }
          if ($.isFunction(configs.openAfterFunc)) configs.openAfterFunc(selectors.container, selectors.handle, selectors.button);
          enable();
        }
      );
    }

    function close() {
      if (!chkReady()) return;
      if (chkStatus() == 'close') close();
      disable();
      if ($.isFunction(configs.closeBeforeFunc)) configs.closeBeforeFunc(selectors.container, selectors.handle, selectors.button);

      if (is_msie6 === false) {
        var top = 'auto',
            bottom = 'auto';
        if (configs.fulcrumY == 'top') {
          top = selectors.container.css('top');
        } else {
          bottom = parseInt(selectors.container.css('bottom')) + (open_height - close_height);
          selectors.container.css({
            top: selectors.container.offset().top - $(window).scrollTop(),
            bottom: 'auto'
          });
        }
      }
      selectors.button.removeClass(configs.openButtonClass + ' ' + configs.closeButtonClass).addClass(configs.closeButtonClass);
      selectors.container.css({
        overflow: 'hidden'
      }).animate(
        {
          height: close_height
        },
        configs.speed,
        configs.easing,
        function() {
          if (is_msie6 === false) {
            selectors.container.css({
              top: top,
              bottom: bottom
            });
          }
          if ($.isFunction(configs.closeAfterFunc)) configs.closeAfterFunc(selectors.container, selectors.handle, selectors.button);
          enable();
        }
      );
    }

    function chkReady() {
      return selectors.button.hasClass('collapsebox-ready');
    }

    function enable() {
      selectors.button.removeClass('collapsebox-ready').addClass('collapsebox-ready');
    }

    function disable() {
      selectors.button.removeClass('collapsebox-ready');
    }

    function chkStatus() {
        var status;
        if (selectors.button.hasClass(configs.closeButtonClass)) {
          status = 'close';
        } else {
          status = 'open';
        }
        return status;
    }
  }
})(jQuery);
