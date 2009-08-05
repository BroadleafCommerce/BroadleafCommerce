/*!
 * jQuery UI Stars v2.1.1
 * http://plugins.jquery.com/project/Star_Rating_widget
 *
 * Copyright (c) 2009 Orkan (orkans@gmail.com)
 * Dual licensed under the MIT and GPL licenses.
 * http://docs.jquery.com/License
 *
 * $Rev: 114 $
 * $Date:: 2009-06-12 #$
 * $Build: 32 (2009-06-12)
 *
 * Depends:
 *  ui.core.js
 *
 */
(function($) {

$.widget("ui.stars",
{
  _init: function() {
    var self = this, o = this.options, id = 0;

    o.isSelect = o.inputType == "select";
    this.$form = $(this.element).closest("form");
    this.$selec = o.isSelect ? $("select", this.element)  : null;
    this.$rboxs = o.isSelect ? $("option", this.$selec)   : $(":radio", this.element);

    /*
     * Map all inputs from $rboxs array to Stars elements
     */
    this.$stars = this.$rboxs.map(function(i)
    {
      var el = {
        value:      this.value,
        title:      (o.isSelect ? this.text : this.title) || this.value,
        isDefault:  (o.isSelect && this.defaultSelected) || this.defaultChecked
      };

      if(i==0) {
        o.split = typeof o.split != "number" ? 0 : o.split;
        o.val2id = [];
        o.id2val = [];
        o.id2title = [];
        o.name = o.isSelect ? self.$selec.get(0).name : this.name;
        o.disabled = o.disabled || (o.isSelect ? $(self.$selec).attr("disabled") : $(this).attr("disabled"));
      }

      /*
       * Consider it as a Cancel button?
       */
      if(el.value == o.cancelValue) {
        o.cancelTitle = el.title;
        return null;
      }

      o.val2id[el.value] = id;
      o.id2val[id] = el.value;
      o.id2title[id] = el.title;

      if(el.isDefault) {
        o.checked = id;
        o.value = o.defaultValue = el.value;
        o.title = el.title;
      }

      var $s = $("<div/>").addClass(o.starClass);
      var $a = $('<a/>').attr("title", o.showTitles ? el.title : "").text(el.value);

      /*
       * Prepare division settings
       */
      if(o.split) {
        var oddeven = (id % o.split);
        var stwidth = Math.floor(o.starWidth / o.split);
        $s.width(stwidth);
        $a.css("margin-left", "-" + (oddeven * stwidth) + "px");
      }

      id++;
      return $s.append($a).get(0);
    });

    /*
     * How many Stars?
     */
    o.items = id;

    /*
     * Remove old content
     */
    o.isSelect ? this.$selec.remove() : this.$rboxs.remove();

    /*
     * Append Stars interface
     */
    this.$cancel = $("<div/>").addClass(o.cancelClass).append( $("<a/>").attr("title", o.showTitles ? o.cancelTitle : "").text(o.cancelValue) );
    o.cancelShow &= !o.disabled && !o.oneVoteOnly;
    o.cancelShow && this.element.append(this.$cancel);
    this.element.append(this.$stars);

    /*
     * Initial selection
     */
    if(o.checked === undefined) {
      o.checked = -1;
      o.value = o.defaultValue = o.cancelValue;
      o.title = "";
    }

    this.$value = $('<input type="hidden" name="'+o.name+'" value="'+o.value+'" />');
    this.element.append(this.$value);


    /*
     * Attach stars event handler
     */
    this.$stars.bind("click.stars", function(e) {
      if(!o.forceSelect && o.disabled) return false;

      var i = self.$stars.index(this);
      o.checked = i;
      o.value = o.id2val[i];
      o.title = o.id2title[i];
      self.$value.attr({disabled: o.disabled ? "disabled" : "", value: o.value});

      fillTo(i, false);
      self._disableCancel();

      !o.forceSelect && self.callback(e, "star");
    })
    .bind("mouseover.stars", function() {
      if(o.disabled) return false;
      var i = self.$stars.index(this);
      fillTo(i, true);
    })
    .bind("mouseout.stars", function() {
      if(o.disabled) return false;
      fillTo(self.options.checked, false);
    });


    /*
     * Attach cancel event handler
     */
    this.$cancel.bind("click.stars", function(e) {
      if(!o.forceSelect && (o.disabled || o.value == o.cancelValue)) return false;

      o.checked = -1;
      o.value = o.cancelValue;
      o.title = "";
      self.$value.val(o.value).attr({disabled: "disabled"});

      fillNone();
      self._disableCancel();

      !o.forceSelect && self.callback(e, "cancel");
    })
    .bind("mouseover.stars", function() {
      if(self._disableCancel()) return false;
      self.$cancel.addClass(o.cancelHoverClass);
      fillNone();
      self._showCap(o.cancelTitle);
    })
    .bind("mouseout.stars", function() {
      if(self._disableCancel()) return false;
      self.$cancel.removeClass(o.cancelHoverClass);
      self.$stars.triggerHandler("mouseout.stars");
    });


    /*
     * Attach onReset event handler to the parent FORM
     */
    this.$form.bind("reset.stars", function(){
      !o.disabled && self.select(o.defaultValue);
    });


    /*
     * Clean up to avoid memory leaks in certain versions of IE 6
     */
    $(window).unload(function(){
      self.$cancel.unbind(".stars");
      self.$stars.unbind(".stars");
      self.$form.unbind(".stars");
      self.$selec = self.$rboxs = self.$stars = self.$value = self.$cancel = self.$form = null;
    });


    /*
     * Star selection helpers
     */
    function fillTo(index, hover) {
      if(index != -1) {
        var addClass = hover ? o.starHoverClass : o.starOnClass;
        var remClass = hover ? o.starOnClass    : o.starHoverClass;
        self.$stars.eq(index).prevAll("." + o.starClass).andSelf().removeClass(remClass).addClass(addClass);
        self.$stars.eq(index).nextAll("." + o.starClass).removeClass(o.starHoverClass + " " + o.starOnClass);
        self._showCap(o.id2title[index]);
      }
      else fillNone();
    };
    function fillNone() {
      self.$stars.removeClass(o.starOnClass + " " + o.starHoverClass);
      self._showCap("");
    };


    /*
     * Finally, set up the Stars
     */
    this.select(o.value);
    o.disabled && this.disable();

  },

  /*
   * Private functions
   */
  _disableCancel: function() {
    var o = this.options, disabled = o.disabled || o.oneVoteOnly || (o.value == o.cancelValue);
    if(disabled)  this.$cancel.removeClass(o.cancelHoverClass).addClass(o.cancelDisabledClass);
    else          this.$cancel.removeClass(o.cancelDisabledClass);
    this.$cancel.css("opacity", disabled ? 0.5 : 1);
    return disabled;
  },
  _disableAll: function() {
    var o = this.options;
    this._disableCancel();
    if(o.disabled)  this.$stars.filter("div").addClass(o.starDisabledClass);
    else            this.$stars.filter("div").removeClass(o.starDisabledClass);
  },
  _showCap: function(s) {
    var o = this.options;
    if(o.captionEl) o.captionEl.text(s);
  },

  /*
   * Public functions
   */
  value: function() {
    return this.options.value;
  },
  select: function(val) {
    var o = this.options, e = (val == o.cancelValue) ? this.$cancel : this.$stars.eq(o.val2id[val]);
    o.forceSelect = true;
    e.triggerHandler("click.stars");
    o.forceSelect = false;
  },
  selectID: function(id) {
    var o = this.options, e = (id == -1) ? this.$cancel : this.$stars.eq(id);
    o.forceSelect = true;
    e.triggerHandler("click.stars");
    o.forceSelect = false;
  },
  enable: function() {
    this.options.disabled = false;
    this._disableAll();
  },
  disable: function() {
    this.options.disabled = true;
    this._disableAll();
  },
  destroy: function() {
    this.options.isSelect ? this.$selec.appendTo(this.element) : this.$rboxs.appendTo(this.element);
    this.$form.unbind(".stars");
    this.$cancel.unbind(".stars").remove();
    this.$stars.unbind(".stars").remove();
    this.$value.remove();
    this.element.unbind(".stars").removeData("stars");
  },
  callback: function(e, type) {
    var o = this.options;
    o.callback && o.callback(this, type, o.value, e);
    o.oneVoteOnly && !o.disabled && this.disable();
  }
});

$.extend($.ui.stars, {
  version: "2.1.1",
  getter: "value",
  defaults: {
    inputType: "radio", // radio|select
    split: 0,
    disabled: false,
    cancelTitle: "Cancel Rating",
    cancelValue: 0,
    cancelShow: true,
    oneVoteOnly: false,
    showTitles: false,
    captionEl: null,
    callback: null, // function(ui, type, value, event)

    /*
     * CSS classes
     */
    starWidth: 16,
    cancelClass: 'ui-stars-cancel',
    starClass: 'ui-stars-star',
    starOnClass: 'ui-stars-star-on',
    starHoverClass: 'ui-stars-star-hover',
    starDisabledClass: 'ui-stars-star-disabled',
    cancelHoverClass: 'ui-stars-cancel-hover',
    cancelDisabledClass: 'ui-stars-cancel-disabled'
  }
});

})(jQuery);
