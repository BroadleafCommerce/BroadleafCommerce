/*! jQuery UI - v1.12.1 - 2021-08-16
* http://jqueryui.com
* Includes: widget.js, data.js, keycode.js, scroll-parent.js, widgets/sortable.js, widgets/datepicker.js, widgets/mouse.js, widgets/slider.js
* Copyright jQuery Foundation and other contributors; Licensed MIT */

(function( factory ) {
    if ( typeof define === "function" && define.amd ) {

        // AMD. Register as an anonymous module.
        define([ "jquery" ], factory );
    } else {

        // Browser globals
        factory( jQuery );
    }
}(function( $ ) {

    $.ui = $.ui || {};

    var version = $.ui.version = "1.12.1";


    /*!
 * jQuery UI Widget 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

//>>label: Widget
//>>group: Core
//>>description: Provides a factory for creating stateful widgets with a common API.
//>>docs: http://api.jqueryui.com/jQuery.widget/
//>>demos: http://jqueryui.com/widget/



    var widgetUuid = 0;
    var widgetSlice = Array.prototype.slice;

    $.cleanData = ( function( orig ) {
        return function( elems ) {
            var events, elem, i;
            for ( i = 0; ( elem = elems[ i ] ) != null; i++ ) {
                try {

                    // Only trigger remove when necessary to save time
                    events = $._data( elem, "events" );
                    if ( events && events.remove ) {
                        $( elem ).triggerHandler( "remove" );
                    }

                    // Http://bugs.jquery.com/ticket/8235
                } catch ( e ) {}
            }
            orig( elems );
        };
    } )( $.cleanData );

    $.widget = function( name, base, prototype ) {
        var existingConstructor, constructor, basePrototype;

        // ProxiedPrototype allows the provided prototype to remain unmodified
        // so that it can be used as a mixin for multiple widgets (#8876)
        var proxiedPrototype = {};

        var namespace = name.split( "." )[ 0 ];
        name = name.split( "." )[ 1 ];
        var fullName = namespace + "-" + name;

        if ( !prototype ) {
            prototype = base;
            base = $.Widget;
        }

        if ( $.isArray( prototype ) ) {
            prototype = $.extend.apply( null, [ {} ].concat( prototype ) );
        }

        // Create selector for plugin
        $.expr[ ":" ][ fullName.toLowerCase() ] = function( elem ) {
            return !!$.data( elem, fullName );
        };

        $[ namespace ] = $[ namespace ] || {};
        existingConstructor = $[ namespace ][ name ];
        constructor = $[ namespace ][ name ] = function( options, element ) {

            // Allow instantiation without "new" keyword
            if ( !this._createWidget ) {
                return new constructor( options, element );
            }

            // Allow instantiation without initializing for simple inheritance
            // must use "new" keyword (the code above always passes args)
            if ( arguments.length ) {
                this._createWidget( options, element );
            }
        };

        // Extend with the existing constructor to carry over any static properties
        $.extend( constructor, existingConstructor, {
            version: prototype.version,

            // Copy the object used to create the prototype in case we need to
            // redefine the widget later
            _proto: $.extend( {}, prototype ),

            // Track widgets that inherit from this widget in case this widget is
            // redefined after a widget inherits from it
            _childConstructors: []
        } );

        basePrototype = new base();

        // We need to make the options hash a property directly on the new instance
        // otherwise we'll modify the options hash on the prototype that we're
        // inheriting from
        basePrototype.options = $.widget.extend( {}, basePrototype.options );
        $.each( prototype, function( prop, value ) {
            if ( !$.isFunction( value ) ) {
                proxiedPrototype[ prop ] = value;
                return;
            }
            proxiedPrototype[ prop ] = ( function() {
                function _super() {
                    return base.prototype[ prop ].apply( this, arguments );
                }

                function _superApply( args ) {
                    return base.prototype[ prop ].apply( this, args );
                }

                return function() {
                    var __super = this._super;
                    var __superApply = this._superApply;
                    var returnValue;

                    this._super = _super;
                    this._superApply = _superApply;

                    returnValue = value.apply( this, arguments );

                    this._super = __super;
                    this._superApply = __superApply;

                    return returnValue;
                };
            } )();
        } );
        constructor.prototype = $.widget.extend( basePrototype, {

            // TODO: remove support for widgetEventPrefix
            // always use the name + a colon as the prefix, e.g., draggable:start
            // don't prefix for widgets that aren't DOM-based
            widgetEventPrefix: existingConstructor ? ( basePrototype.widgetEventPrefix || name ) : name
        }, proxiedPrototype, {
            constructor: constructor,
            namespace: namespace,
            widgetName: name,
            widgetFullName: fullName
        } );

        // If this widget is being redefined then we need to find all widgets that
        // are inheriting from it and redefine all of them so that they inherit from
        // the new version of this widget. We're essentially trying to replace one
        // level in the prototype chain.
        if ( existingConstructor ) {
            $.each( existingConstructor._childConstructors, function( i, child ) {
                var childPrototype = child.prototype;

                // Redefine the child widget using the same prototype that was
                // originally used, but inherit from the new version of the base
                $.widget( childPrototype.namespace + "." + childPrototype.widgetName, constructor,
                    child._proto );
            } );

            // Remove the list of existing child constructors from the old constructor
            // so the old child constructors can be garbage collected
            delete existingConstructor._childConstructors;
        } else {
            base._childConstructors.push( constructor );
        }

        $.widget.bridge( name, constructor );

        return constructor;
    };

    $.widget.extend = function( target ) {
        var input = widgetSlice.call( arguments, 1 );
        var inputIndex = 0;
        var inputLength = input.length;
        var key;
        var value;

        for ( ; inputIndex < inputLength; inputIndex++ ) {
            for ( key in input[ inputIndex ] ) {
                value = input[ inputIndex ][ key ];
                if ( input[ inputIndex ].hasOwnProperty( key ) && value !== undefined ) {

                    // Clone objects
                    if ( $.isPlainObject( value ) ) {
                        target[ key ] = $.isPlainObject( target[ key ] ) ?
                            $.widget.extend( {}, target[ key ], value ) :

                            // Don't extend strings, arrays, etc. with objects
                            $.widget.extend( {}, value );

                        // Copy everything else by reference
                    } else {
                        target[ key ] = value;
                    }
                }
            }
        }
        return target;
    };

    $.widget.bridge = function( name, object ) {
        var fullName = object.prototype.widgetFullName || name;
        $.fn[ name ] = function( options ) {
            var isMethodCall = typeof options === "string";
            var args = widgetSlice.call( arguments, 1 );
            var returnValue = this;

            if ( isMethodCall ) {

                // If this is an empty collection, we need to have the instance method
                // return undefined instead of the jQuery instance
                if ( !this.length && options === "instance" ) {
                    returnValue = undefined;
                } else {
                    this.each( function() {
                        var methodValue;
                        var instance = $.data( this, fullName );

                        if ( options === "instance" ) {
                            returnValue = instance;
                            return false;
                        }

                        if ( !instance ) {
                            return $.error( "cannot call methods on " + name +
                                " prior to initialization; " +
                                "attempted to call method '" + options + "'" );
                        }

                        if ( !$.isFunction( instance[ options ] ) || options.charAt( 0 ) === "_" ) {
                            return $.error( "no such method '" + options + "' for " + name +
                                " widget instance" );
                        }

                        methodValue = instance[ options ].apply( instance, args );

                        if ( methodValue !== instance && methodValue !== undefined ) {
                            returnValue = methodValue && methodValue.jquery ?
                                returnValue.pushStack( methodValue.get() ) :
                                methodValue;
                            return false;
                        }
                    } );
                }
            } else {

                // Allow multiple hashes to be passed on init
                if ( args.length ) {
                    options = $.widget.extend.apply( null, [ options ].concat( args ) );
                }

                this.each( function() {
                    var instance = $.data( this, fullName );
                    if ( instance ) {
                        instance.option( options || {} );
                        if ( instance._init ) {
                            instance._init();
                        }
                    } else {
                        $.data( this, fullName, new object( options, this ) );
                    }
                } );
            }

            return returnValue;
        };
    };

    $.Widget = function( /* options, element */ ) {};
    $.Widget._childConstructors = [];

    $.Widget.prototype = {
        widgetName: "widget",
        widgetEventPrefix: "",
        defaultElement: "<div>",

        options: {
            classes: {},
            disabled: false,

            // Callbacks
            create: null
        },

        _createWidget: function( options, element ) {
            element = $( element || this.defaultElement || this )[ 0 ];
            this.element = $( element );
            this.uuid = widgetUuid++;
            this.eventNamespace = "." + this.widgetName + this.uuid;

            this.bindings = $();
            this.hoverable = $();
            this.focusable = $();
            this.classesElementLookup = {};

            if ( element !== this ) {
                $.data( element, this.widgetFullName, this );
                this._on( true, this.element, {
                    remove: function( event ) {
                        if ( event.target === element ) {
                            this.destroy();
                        }
                    }
                } );
                this.document = $( element.style ?

                    // Element within the document
                    element.ownerDocument :

                    // Element is window or document
                    element.document || element );
                this.window = $( this.document[ 0 ].defaultView || this.document[ 0 ].parentWindow );
            }

            this.options = $.widget.extend( {},
                this.options,
                this._getCreateOptions(),
                options );

            this._create();

            if ( this.options.disabled ) {
                this._setOptionDisabled( this.options.disabled );
            }

            this._trigger( "create", null, this._getCreateEventData() );
            this._init();
        },

        _getCreateOptions: function() {
            return {};
        },

        _getCreateEventData: $.noop,

        _create: $.noop,

        _init: $.noop,

        destroy: function() {
            var that = this;

            this._destroy();
            $.each( this.classesElementLookup, function( key, value ) {
                that._removeClass( value, key );
            } );

            // We can probably remove the unbind calls in 2.0
            // all event bindings should go through this._on()
            this.element
                .off( this.eventNamespace )
                .removeData( this.widgetFullName );
            this.widget()
                .off( this.eventNamespace )
                .removeAttr( "aria-disabled" );

            // Clean up events and states
            this.bindings.off( this.eventNamespace );
        },

        _destroy: $.noop,

        widget: function() {
            return this.element;
        },

        option: function( key, value ) {
            var options = key;
            var parts;
            var curOption;
            var i;

            if ( arguments.length === 0 ) {

                // Don't return a reference to the internal hash
                return $.widget.extend( {}, this.options );
            }

            if ( typeof key === "string" ) {

                // Handle nested keys, e.g., "foo.bar" => { foo: { bar: ___ } }
                options = {};
                parts = key.split( "." );
                key = parts.shift();
                if ( parts.length ) {
                    curOption = options[ key ] = $.widget.extend( {}, this.options[ key ] );
                    for ( i = 0; i < parts.length - 1; i++ ) {
                        curOption[ parts[ i ] ] = curOption[ parts[ i ] ] || {};
                        curOption = curOption[ parts[ i ] ];
                    }
                    key = parts.pop();
                    if ( arguments.length === 1 ) {
                        return curOption[ key ] === undefined ? null : curOption[ key ];
                    }
                    curOption[ key ] = value;
                } else {
                    if ( arguments.length === 1 ) {
                        return this.options[ key ] === undefined ? null : this.options[ key ];
                    }
                    options[ key ] = value;
                }
            }

            this._setOptions( options );

            return this;
        },

        _setOptions: function( options ) {
            var key;

            for ( key in options ) {
                this._setOption( key, options[ key ] );
            }

            return this;
        },

        _setOption: function( key, value ) {
            if ( key === "classes" ) {
                this._setOptionClasses( value );
            }

            this.options[ key ] = value;

            if ( key === "disabled" ) {
                this._setOptionDisabled( value );
            }

            return this;
        },

        _setOptionClasses: function( value ) {
            var classKey, elements, currentElements;

            for ( classKey in value ) {
                currentElements = this.classesElementLookup[ classKey ];
                if ( value[ classKey ] === this.options.classes[ classKey ] ||
                    !currentElements ||
                    !currentElements.length ) {
                    continue;
                }

                // We are doing this to create a new jQuery object because the _removeClass() call
                // on the next line is going to destroy the reference to the current elements being
                // tracked. We need to save a copy of this collection so that we can add the new classes
                // below.
                elements = $( currentElements.get() );
                this._removeClass( currentElements, classKey );

                // We don't use _addClass() here, because that uses this.options.classes
                // for generating the string of classes. We want to use the value passed in from
                // _setOption(), this is the new value of the classes option which was passed to
                // _setOption(). We pass this value directly to _classes().
                elements.addClass( this._classes( {
                    element: elements,
                    keys: classKey,
                    classes: value,
                    add: true
                } ) );
            }
        },

        _setOptionDisabled: function( value ) {
            this._toggleClass( this.widget(), this.widgetFullName + "-disabled", null, !!value );

            // If the widget is becoming disabled, then nothing is interactive
            if ( value ) {
                this._removeClass( this.hoverable, null, "ui-state-hover" );
                this._removeClass( this.focusable, null, "ui-state-focus" );
            }
        },

        enable: function() {
            return this._setOptions( { disabled: false } );
        },

        disable: function() {
            return this._setOptions( { disabled: true } );
        },

        _classes: function( options ) {
            var full = [];
            var that = this;

            options = $.extend( {
                element: this.element,
                classes: this.options.classes || {}
            }, options );

            function processClassString( classes, checkOption ) {
                var current, i;
                for ( i = 0; i < classes.length; i++ ) {
                    current = that.classesElementLookup[ classes[ i ] ] || $();
                    if ( options.add ) {
                        current = $( $.unique( current.get().concat( options.element.get() ) ) );
                    } else {
                        current = $( current.not( options.element ).get() );
                    }
                    that.classesElementLookup[ classes[ i ] ] = current;
                    full.push( classes[ i ] );
                    if ( checkOption && options.classes[ classes[ i ] ] ) {
                        full.push( options.classes[ classes[ i ] ] );
                    }
                }
            }

            this._on( options.element, {
                "remove": "_untrackClassesElement"
            } );

            if ( options.keys ) {
                processClassString( options.keys.match( /\S+/g ) || [], true );
            }
            if ( options.extra ) {
                processClassString( options.extra.match( /\S+/g ) || [] );
            }

            return full.join( " " );
        },

        _untrackClassesElement: function( event ) {
            var that = this;
            $.each( that.classesElementLookup, function( key, value ) {
                if ( $.inArray( event.target, value ) !== -1 ) {
                    that.classesElementLookup[ key ] = $( value.not( event.target ).get() );
                }
            } );
        },

        _removeClass: function( element, keys, extra ) {
            return this._toggleClass( element, keys, extra, false );
        },

        _addClass: function( element, keys, extra ) {
            return this._toggleClass( element, keys, extra, true );
        },

        _toggleClass: function( element, keys, extra, add ) {
            add = ( typeof add === "boolean" ) ? add : extra;
            var shift = ( typeof element === "string" || element === null ),
                options = {
                    extra: shift ? keys : extra,
                    keys: shift ? element : keys,
                    element: shift ? this.element : element,
                    add: add
                };
            options.element.toggleClass( this._classes( options ), add );
            return this;
        },

        _on: function( suppressDisabledCheck, element, handlers ) {
            var delegateElement;
            var instance = this;

            // No suppressDisabledCheck flag, shuffle arguments
            if ( typeof suppressDisabledCheck !== "boolean" ) {
                handlers = element;
                element = suppressDisabledCheck;
                suppressDisabledCheck = false;
            }

            // No element argument, shuffle and use this.element
            if ( !handlers ) {
                handlers = element;
                element = this.element;
                delegateElement = this.widget();
            } else {
                element = delegateElement = $( element );
                this.bindings = this.bindings.add( element );
            }

            $.each( handlers, function( event, handler ) {
                function handlerProxy() {

                    // Allow widgets to customize the disabled handling
                    // - disabled as an array instead of boolean
                    // - disabled class as method for disabling individual parts
                    if ( !suppressDisabledCheck &&
                        ( instance.options.disabled === true ||
                            $( this ).hasClass( "ui-state-disabled" ) ) ) {
                        return;
                    }
                    return ( typeof handler === "string" ? instance[ handler ] : handler )
                        .apply( instance, arguments );
                }

                // Copy the guid so direct unbinding works
                if ( typeof handler !== "string" ) {
                    handlerProxy.guid = handler.guid =
                        handler.guid || handlerProxy.guid || $.guid++;
                }

                var match = event.match( /^([\w:-]*)\s*(.*)$/ );
                var eventName = match[ 1 ] + instance.eventNamespace;
                var selector = match[ 2 ];

                if ( selector ) {
                    delegateElement.on( eventName, selector, handlerProxy );
                } else {
                    element.on( eventName, handlerProxy );
                }
            } );
        },

        _off: function( element, eventName ) {
            eventName = ( eventName || "" ).split( " " ).join( this.eventNamespace + " " ) +
                this.eventNamespace;
            element.off( eventName ).off( eventName );

            // Clear the stack to avoid memory leaks (#10056)
            this.bindings = $( this.bindings.not( element ).get() );
            this.focusable = $( this.focusable.not( element ).get() );
            this.hoverable = $( this.hoverable.not( element ).get() );
        },

        _delay: function( handler, delay ) {
            function handlerProxy() {
                return ( typeof handler === "string" ? instance[ handler ] : handler )
                    .apply( instance, arguments );
            }
            var instance = this;
            return setTimeout( handlerProxy, delay || 0 );
        },

        _hoverable: function( element ) {
            this.hoverable = this.hoverable.add( element );
            this._on( element, {
                mouseenter: function( event ) {
                    this._addClass( $( event.currentTarget ), null, "ui-state-hover" );
                },
                mouseleave: function( event ) {
                    this._removeClass( $( event.currentTarget ), null, "ui-state-hover" );
                }
            } );
        },

        _focusable: function( element ) {
            this.focusable = this.focusable.add( element );
            this._on( element, {
                focusin: function( event ) {
                    this._addClass( $( event.currentTarget ), null, "ui-state-focus" );
                },
                focusout: function( event ) {
                    this._removeClass( $( event.currentTarget ), null, "ui-state-focus" );
                }
            } );
        },

        _trigger: function( type, event, data ) {
            var prop, orig;
            var callback = this.options[ type ];

            data = data || {};
            event = $.Event( event );
            event.type = ( type === this.widgetEventPrefix ?
                type :
                this.widgetEventPrefix + type ).toLowerCase();

            // The original event may come from any element
            // so we need to reset the target on the new event
            event.target = this.element[ 0 ];

            // Copy original event properties over to the new event
            orig = event.originalEvent;
            if ( orig ) {
                for ( prop in orig ) {
                    if ( !( prop in event ) ) {
                        event[ prop ] = orig[ prop ];
                    }
                }
            }

            this.element.trigger( event, data );
            return !( $.isFunction( callback ) &&
                callback.apply( this.element[ 0 ], [ event ].concat( data ) ) === false ||
                event.isDefaultPrevented() );
        }
    };

    $.each( { show: "fadeIn", hide: "fadeOut" }, function( method, defaultEffect ) {
        $.Widget.prototype[ "_" + method ] = function( element, options, callback ) {
            if ( typeof options === "string" ) {
                options = { effect: options };
            }

            var hasOptions;
            var effectName = !options ?
                method :
                options === true || typeof options === "number" ?
                    defaultEffect :
                    options.effect || defaultEffect;

            options = options || {};
            if ( typeof options === "number" ) {
                options = { duration: options };
            }

            hasOptions = !$.isEmptyObject( options );
            options.complete = callback;

            if ( options.delay ) {
                element.delay( options.delay );
            }

            if ( hasOptions && $.effects && $.effects.effect[ effectName ] ) {
                element[ method ]( options );
            } else if ( effectName !== method && element[ effectName ] ) {
                element[ effectName ]( options.duration, options.easing, callback );
            } else {
                element.queue( function( next ) {
                    $( this )[ method ]();
                    if ( callback ) {
                        callback.call( element[ 0 ] );
                    }
                    next();
                } );
            }
        };
    } );

    var widget = $.widget;


    /*!
 * jQuery UI :data 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

//>>label: :data Selector
//>>group: Core
//>>description: Selects elements which have data stored under the specified key.
//>>docs: http://api.jqueryui.com/data-selector/


    var data = $.extend( $.expr[ ":" ], {
        data: $.expr.createPseudo ?
            $.expr.createPseudo( function( dataName ) {
                return function( elem ) {
                    return !!$.data( elem, dataName );
                };
            } ) :

            // Support: jQuery <1.8
            function( elem, i, match ) {
                return !!$.data( elem, match[ 3 ] );
            }
    } );

    /*!
 * jQuery UI Keycode 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

//>>label: Keycode
//>>group: Core
//>>description: Provide keycodes as keynames
//>>docs: http://api.jqueryui.com/jQuery.ui.keyCode/


    var keycode = $.ui.keyCode = {
        BACKSPACE: 8,
        COMMA: 188,
        DELETE: 46,
        DOWN: 40,
        END: 35,
        ENTER: 13,
        ESCAPE: 27,
        HOME: 36,
        LEFT: 37,
        PAGE_DOWN: 34,
        PAGE_UP: 33,
        PERIOD: 190,
        RIGHT: 39,
        SPACE: 32,
        TAB: 9,
        UP: 38
    };


    /*!
 * jQuery UI Scroll Parent 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

//>>label: scrollParent
//>>group: Core
//>>description: Get the closest ancestor element that is scrollable.
//>>docs: http://api.jqueryui.com/scrollParent/



    var scrollParent = $.fn.scrollParent = function( includeHidden ) {
        var position = this.css( "position" ),
            excludeStaticParent = position === "absolute",
            overflowRegex = includeHidden ? /(auto|scroll|hidden)/ : /(auto|scroll)/,
            scrollParent = this.parents().filter( function() {
                var parent = $( this );
                if ( excludeStaticParent && parent.css( "position" ) === "static" ) {
                    return false;
                }
                return overflowRegex.test( parent.css( "overflow" ) + parent.css( "overflow-y" ) +
                    parent.css( "overflow-x" ) );
            } ).eq( 0 );

        return position === "fixed" || !scrollParent.length ?
            $( this[ 0 ].ownerDocument || document ) :
            scrollParent;
    };




// This file is deprecated
    var ie = $.ui.ie = !!/msie [\w.]+/.exec( navigator.userAgent.toLowerCase() );

    /*!
 * jQuery UI Mouse 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

//>>label: Mouse
//>>group: Widgets
//>>description: Abstracts mouse-based interactions to assist in creating certain widgets.
//>>docs: http://api.jqueryui.com/mouse/



    var mouseHandled = false;
    $( document ).on( "mouseup", function() {
        mouseHandled = false;
    } );

    var widgetsMouse = $.widget( "ui.mouse", {
        version: "1.12.1",
        options: {
            cancel: "input, textarea, button, select, option",
            distance: 1,
            delay: 0
        },
        _mouseInit: function() {
            var that = this;

            this.element
                .on( "mousedown." + this.widgetName, function( event ) {
                    return that._mouseDown( event );
                } )
                .on( "click." + this.widgetName, function( event ) {
                    if ( true === $.data( event.target, that.widgetName + ".preventClickEvent" ) ) {
                        $.removeData( event.target, that.widgetName + ".preventClickEvent" );
                        event.stopImmediatePropagation();
                        return false;
                    }
                } );

            this.started = false;
        },

        // TODO: make sure destroying one instance of mouse doesn't mess with
        // other instances of mouse
        _mouseDestroy: function() {
            this.element.off( "." + this.widgetName );
            if ( this._mouseMoveDelegate ) {
                this.document
                    .off( "mousemove." + this.widgetName, this._mouseMoveDelegate )
                    .off( "mouseup." + this.widgetName, this._mouseUpDelegate );
            }
        },

        _mouseDown: function( event ) {

            // don't let more than one widget handle mouseStart
            if ( mouseHandled ) {
                return;
            }

            this._mouseMoved = false;

            // We may have missed mouseup (out of window)
            ( this._mouseStarted && this._mouseUp( event ) );

            this._mouseDownEvent = event;

            var that = this,
                btnIsLeft = ( event.which === 1 ),

                // event.target.nodeName works around a bug in IE 8 with
                // disabled inputs (#7620)
                elIsCancel = ( typeof this.options.cancel === "string" && event.target.nodeName ?
                    $( event.target ).closest( this.options.cancel ).length : false );
            if ( !btnIsLeft || elIsCancel || !this._mouseCapture( event ) ) {
                return true;
            }

            this.mouseDelayMet = !this.options.delay;
            if ( !this.mouseDelayMet ) {
                this._mouseDelayTimer = setTimeout( function() {
                    that.mouseDelayMet = true;
                }, this.options.delay );
            }

            if ( this._mouseDistanceMet( event ) && this._mouseDelayMet( event ) ) {
                this._mouseStarted = ( this._mouseStart( event ) !== false );
                if ( !this._mouseStarted ) {
                    event.preventDefault();
                    return true;
                }
            }

            // Click event may never have fired (Gecko & Opera)
            if ( true === $.data( event.target, this.widgetName + ".preventClickEvent" ) ) {
                $.removeData( event.target, this.widgetName + ".preventClickEvent" );
            }

            // These delegates are required to keep context
            this._mouseMoveDelegate = function( event ) {
                return that._mouseMove( event );
            };
            this._mouseUpDelegate = function( event ) {
                return that._mouseUp( event );
            };

            this.document
                .on( "mousemove." + this.widgetName, this._mouseMoveDelegate )
                .on( "mouseup." + this.widgetName, this._mouseUpDelegate );

            event.preventDefault();

            mouseHandled = true;
            return true;
        },

        _mouseMove: function( event ) {

            // Only check for mouseups outside the document if you've moved inside the document
            // at least once. This prevents the firing of mouseup in the case of IE<9, which will
            // fire a mousemove event if content is placed under the cursor. See #7778
            // Support: IE <9
            if ( this._mouseMoved ) {

                // IE mouseup check - mouseup happened when mouse was out of window
                if ( $.ui.ie && ( !document.documentMode || document.documentMode < 9 ) &&
                    !event.button ) {
                    return this._mouseUp( event );

                    // Iframe mouseup check - mouseup occurred in another document
                } else if ( !event.which ) {

                    // Support: Safari <=8 - 9
                    // Safari sets which to 0 if you press any of the following keys
                    // during a drag (#14461)
                    if ( event.originalEvent.altKey || event.originalEvent.ctrlKey ||
                        event.originalEvent.metaKey || event.originalEvent.shiftKey ) {
                        this.ignoreMissingWhich = true;
                    } else if ( !this.ignoreMissingWhich ) {
                        return this._mouseUp( event );
                    }
                }
            }

            if ( event.which || event.button ) {
                this._mouseMoved = true;
            }

            if ( this._mouseStarted ) {
                this._mouseDrag( event );
                return event.preventDefault();
            }

            if ( this._mouseDistanceMet( event ) && this._mouseDelayMet( event ) ) {
                this._mouseStarted =
                    ( this._mouseStart( this._mouseDownEvent, event ) !== false );
                ( this._mouseStarted ? this._mouseDrag( event ) : this._mouseUp( event ) );
            }

            return !this._mouseStarted;
        },

        _mouseUp: function( event ) {
            this.document
                .off( "mousemove." + this.widgetName, this._mouseMoveDelegate )
                .off( "mouseup." + this.widgetName, this._mouseUpDelegate );

            if ( this._mouseStarted ) {
                this._mouseStarted = false;

                if ( event.target === this._mouseDownEvent.target ) {
                    $.data( event.target, this.widgetName + ".preventClickEvent", true );
                }

                this._mouseStop( event );
            }

            if ( this._mouseDelayTimer ) {
                clearTimeout( this._mouseDelayTimer );
                delete this._mouseDelayTimer;
            }

            this.ignoreMissingWhich = false;
            mouseHandled = false;
            event.preventDefault();
        },

        _mouseDistanceMet: function( event ) {
            return ( Math.max(
                    Math.abs( this._mouseDownEvent.pageX - event.pageX ),
                    Math.abs( this._mouseDownEvent.pageY - event.pageY )
                ) >= this.options.distance
            );
        },

        _mouseDelayMet: function( /* event */ ) {
            return this.mouseDelayMet;
        },

        // These are placeholder methods, to be overriden by extending plugin
        _mouseStart: function( /* event */ ) {},
        _mouseDrag: function( /* event */ ) {},
        _mouseStop: function( /* event */ ) {},
        _mouseCapture: function( /* event */ ) { return true; }
    } );


    /*!
 * jQuery UI Sortable 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

//>>label: Sortable
//>>group: Interactions
//>>description: Enables items in a list to be sorted using the mouse.
//>>docs: http://api.jqueryui.com/sortable/
//>>demos: http://jqueryui.com/sortable/
//>>css.structure: ../../themes/base/sortable.css



    var widgetsSortable = $.widget( "ui.sortable", $.ui.mouse, {
        version: "1.12.1",
        widgetEventPrefix: "sort",
        ready: false,
        options: {
            appendTo: "parent",
            axis: false,
            connectWith: false,
            containment: false,
            cursor: "auto",
            cursorAt: false,
            dropOnEmpty: true,
            forcePlaceholderSize: false,
            forceHelperSize: false,
            grid: false,
            handle: false,
            helper: "original",
            items: "> *",
            opacity: false,
            placeholder: false,
            revert: false,
            scroll: true,
            scrollSensitivity: 20,
            scrollSpeed: 20,
            scope: "default",
            tolerance: "intersect",
            zIndex: 1000,

            // Callbacks
            activate: null,
            beforeStop: null,
            change: null,
            deactivate: null,
            out: null,
            over: null,
            receive: null,
            remove: null,
            sort: null,
            start: null,
            stop: null,
            update: null
        },

        _isOverAxis: function( x, reference, size ) {
            return ( x >= reference ) && ( x < ( reference + size ) );
        },

        _isFloating: function( item ) {
            return ( /left|right/ ).test( item.css( "float" ) ) ||
                ( /inline|table-cell/ ).test( item.css( "display" ) );
        },

        _create: function() {
            this.containerCache = {};
            this._addClass( "ui-sortable" );

            //Get the items
            this.refresh();

            //Let's determine the parent's offset
            this.offset = this.element.offset();

            //Initialize mouse events for interaction
            this._mouseInit();

            this._setHandleClassName();

            //We're ready to go
            this.ready = true;

        },

        _setOption: function( key, value ) {
            this._super( key, value );

            if ( key === "handle" ) {
                this._setHandleClassName();
            }
        },

        _setHandleClassName: function() {
            var that = this;
            this._removeClass( this.element.find( ".ui-sortable-handle" ), "ui-sortable-handle" );
            $.each( this.items, function() {
                that._addClass(
                    this.instance.options.handle ?
                        this.item.find( this.instance.options.handle ) :
                        this.item,
                    "ui-sortable-handle"
                );
            } );
        },

        _destroy: function() {
            this._mouseDestroy();

            for ( var i = this.items.length - 1; i >= 0; i-- ) {
                this.items[ i ].item.removeData( this.widgetName + "-item" );
            }

            return this;
        },

        _mouseCapture: function( event, overrideHandle ) {
            var currentItem = null,
                validHandle = false,
                that = this;

            if ( this.reverting ) {
                return false;
            }

            if ( this.options.disabled || this.options.type === "static" ) {
                return false;
            }

            //We have to refresh the items data once first
            this._refreshItems( event );

            //Find out if the clicked node (or one of its parents) is a actual item in this.items
            $( event.target ).parents().each( function() {
                if ( $.data( this, that.widgetName + "-item" ) === that ) {
                    currentItem = $( this );
                    return false;
                }
            } );
            if ( $.data( event.target, that.widgetName + "-item" ) === that ) {
                currentItem = $( event.target );
            }

            if ( !currentItem ) {
                return false;
            }
            if ( this.options.handle && !overrideHandle ) {
                $( this.options.handle, currentItem ).find( "*" ).addBack().each( function() {
                    if ( this === event.target ) {
                        validHandle = true;
                    }
                } );
                if ( !validHandle ) {
                    return false;
                }
            }

            this.currentItem = currentItem;
            this._removeCurrentsFromItems();
            return true;

        },

        _mouseStart: function( event, overrideHandle, noActivation ) {

            var i, body,
                o = this.options;

            this.currentContainer = this;

            //We only need to call refreshPositions, because the refreshItems call has been moved to
            // mouseCapture
            this.refreshPositions();

            //Create and append the visible helper
            this.helper = this._createHelper( event );

            //Cache the helper size
            this._cacheHelperProportions();

            /*
		 * - Position generation -
		 * This block generates everything position related - it's the core of draggables.
		 */

            //Cache the margins of the original element
            this._cacheMargins();

            //Get the next scrolling parent
            this.scrollParent = this.helper.scrollParent();

            //The element's absolute position on the page minus margins
            this.offset = this.currentItem.offset();
            this.offset = {
                top: this.offset.top - this.margins.top,
                left: this.offset.left - this.margins.left
            };

            $.extend( this.offset, {
                click: { //Where the click happened, relative to the element
                    left: event.pageX - this.offset.left,
                    top: event.pageY - this.offset.top
                },
                parent: this._getParentOffset(),

                // This is a relative to absolute position minus the actual position calculation -
                // only used for relative positioned helper
                relative: this._getRelativeOffset()
            } );

            // Only after we got the offset, we can change the helper's position to absolute
            // TODO: Still need to figure out a way to make relative sorting possible
            this.helper.css( "position", "absolute" );
            this.cssPosition = this.helper.css( "position" );

            //Generate the original position
            this.originalPosition = this._generatePosition( event );
            this.originalPageX = event.pageX;
            this.originalPageY = event.pageY;

            //Adjust the mouse offset relative to the helper if "cursorAt" is supplied
            ( o.cursorAt && this._adjustOffsetFromHelper( o.cursorAt ) );

            //Cache the former DOM position
            this.domPosition = {
                prev: this.currentItem.prev()[ 0 ],
                parent: this.currentItem.parent()[ 0 ]
            };

            // If the helper is not the original, hide the original so it's not playing any role during
            // the drag, won't cause anything bad this way
            if ( this.helper[ 0 ] !== this.currentItem[ 0 ] ) {
                this.currentItem.hide();
            }

            //Create the placeholder
            this._createPlaceholder();

            //Set a containment if given in the options
            if ( o.containment ) {
                this._setContainment();
            }

            if ( o.cursor && o.cursor !== "auto" ) { // cursor option
                body = this.document.find( "body" );

                // Support: IE
                this.storedCursor = body.css( "cursor" );
                body.css( "cursor", o.cursor );

                this.storedStylesheet =
                    $( "<style>*{ cursor: " + o.cursor + " !important; }</style>" ).appendTo( body );
            }

            if ( o.opacity ) { // opacity option
                if ( this.helper.css( "opacity" ) ) {
                    this._storedOpacity = this.helper.css( "opacity" );
                }
                this.helper.css( "opacity", o.opacity );
            }

            if ( o.zIndex ) { // zIndex option
                if ( this.helper.css( "zIndex" ) ) {
                    this._storedZIndex = this.helper.css( "zIndex" );
                }
                this.helper.css( "zIndex", o.zIndex );
            }

            //Prepare scrolling
            if ( this.scrollParent[ 0 ] !== this.document[ 0 ] &&
                this.scrollParent[ 0 ].tagName !== "HTML" ) {
                this.overflowOffset = this.scrollParent.offset();
            }

            //Call callbacks
            this._trigger( "start", event, this._uiHash() );

            //Recache the helper size
            if ( !this._preserveHelperProportions ) {
                this._cacheHelperProportions();
            }

            //Post "activate" events to possible containers
            if ( !noActivation ) {
                for ( i = this.containers.length - 1; i >= 0; i-- ) {
                    this.containers[ i ]._trigger( "activate", event, this._uiHash( this ) );
                }
            }

            //Prepare possible droppables
            if ( $.ui.ddmanager ) {
                $.ui.ddmanager.current = this;
            }

            if ( $.ui.ddmanager && !o.dropBehaviour ) {
                $.ui.ddmanager.prepareOffsets( this, event );
            }

            this.dragging = true;

            this._addClass( this.helper, "ui-sortable-helper" );

            // Execute the drag once - this causes the helper not to be visiblebefore getting its
            // correct position
            this._mouseDrag( event );
            return true;

        },

        _mouseDrag: function( event ) {
            var i, item, itemElement, intersection,
                o = this.options,
                scrolled = false;

            //Compute the helpers position
            this.position = this._generatePosition( event );
            this.positionAbs = this._convertPositionTo( "absolute" );

            if ( !this.lastPositionAbs ) {
                this.lastPositionAbs = this.positionAbs;
            }

            //Do scrolling
            if ( this.options.scroll ) {
                if ( this.scrollParent[ 0 ] !== this.document[ 0 ] &&
                    this.scrollParent[ 0 ].tagName !== "HTML" ) {

                    if ( ( this.overflowOffset.top + this.scrollParent[ 0 ].offsetHeight ) -
                        event.pageY < o.scrollSensitivity ) {
                        this.scrollParent[ 0 ].scrollTop =
                            scrolled = this.scrollParent[ 0 ].scrollTop + o.scrollSpeed;
                    } else if ( event.pageY - this.overflowOffset.top < o.scrollSensitivity ) {
                        this.scrollParent[ 0 ].scrollTop =
                            scrolled = this.scrollParent[ 0 ].scrollTop - o.scrollSpeed;
                    }

                    if ( ( this.overflowOffset.left + this.scrollParent[ 0 ].offsetWidth ) -
                        event.pageX < o.scrollSensitivity ) {
                        this.scrollParent[ 0 ].scrollLeft = scrolled =
                            this.scrollParent[ 0 ].scrollLeft + o.scrollSpeed;
                    } else if ( event.pageX - this.overflowOffset.left < o.scrollSensitivity ) {
                        this.scrollParent[ 0 ].scrollLeft = scrolled =
                            this.scrollParent[ 0 ].scrollLeft - o.scrollSpeed;
                    }

                } else {

                    if ( event.pageY - this.document.scrollTop() < o.scrollSensitivity ) {
                        scrolled = this.document.scrollTop( this.document.scrollTop() - o.scrollSpeed );
                    } else if ( this.window.height() - ( event.pageY - this.document.scrollTop() ) <
                        o.scrollSensitivity ) {
                        scrolled = this.document.scrollTop( this.document.scrollTop() + o.scrollSpeed );
                    }

                    if ( event.pageX - this.document.scrollLeft() < o.scrollSensitivity ) {
                        scrolled = this.document.scrollLeft(
                            this.document.scrollLeft() - o.scrollSpeed
                        );
                    } else if ( this.window.width() - ( event.pageX - this.document.scrollLeft() ) <
                        o.scrollSensitivity ) {
                        scrolled = this.document.scrollLeft(
                            this.document.scrollLeft() + o.scrollSpeed
                        );
                    }

                }

                if ( scrolled !== false && $.ui.ddmanager && !o.dropBehaviour ) {
                    $.ui.ddmanager.prepareOffsets( this, event );
                }
            }

            //Regenerate the absolute position used for position checks
            this.positionAbs = this._convertPositionTo( "absolute" );

            //Set the helper position
            if ( !this.options.axis || this.options.axis !== "y" ) {
                this.helper[ 0 ].style.left = this.position.left + "px";
            }
            if ( !this.options.axis || this.options.axis !== "x" ) {
                this.helper[ 0 ].style.top = this.position.top + "px";
            }

            //Rearrange
            for ( i = this.items.length - 1; i >= 0; i-- ) {

                //Cache variables and intersection, continue if no intersection
                item = this.items[ i ];
                itemElement = item.item[ 0 ];
                intersection = this._intersectsWithPointer( item );
                if ( !intersection ) {
                    continue;
                }

                // Only put the placeholder inside the current Container, skip all
                // items from other containers. This works because when moving
                // an item from one container to another the
                // currentContainer is switched before the placeholder is moved.
                //
                // Without this, moving items in "sub-sortables" can cause
                // the placeholder to jitter between the outer and inner container.
                if ( item.instance !== this.currentContainer ) {
                    continue;
                }

                // Cannot intersect with itself
                // no useless actions that have been done before
                // no action if the item moved is the parent of the item checked
                if ( itemElement !== this.currentItem[ 0 ] &&
                    this.placeholder[ intersection === 1 ? "next" : "prev" ]()[ 0 ] !== itemElement &&
                    !$.contains( this.placeholder[ 0 ], itemElement ) &&
                    ( this.options.type === "semi-dynamic" ?
                            !$.contains( this.element[ 0 ], itemElement ) :
                            true
                    )
                ) {

                    this.direction = intersection === 1 ? "down" : "up";

                    if ( this.options.tolerance === "pointer" || this._intersectsWithSides( item ) ) {
                        this._rearrange( event, item );
                    } else {
                        break;
                    }

                    this._trigger( "change", event, this._uiHash() );
                    break;
                }
            }

            //Post events to containers
            this._contactContainers( event );

            //Interconnect with droppables
            if ( $.ui.ddmanager ) {
                $.ui.ddmanager.drag( this, event );
            }

            //Call callbacks
            this._trigger( "sort", event, this._uiHash() );

            this.lastPositionAbs = this.positionAbs;
            return false;

        },

        _mouseStop: function( event, noPropagation ) {

            if ( !event ) {
                return;
            }

            //If we are using droppables, inform the manager about the drop
            if ( $.ui.ddmanager && !this.options.dropBehaviour ) {
                $.ui.ddmanager.drop( this, event );
            }

            if ( this.options.revert ) {
                var that = this,
                    cur = this.placeholder.offset(),
                    axis = this.options.axis,
                    animation = {};

                if ( !axis || axis === "x" ) {
                    animation.left = cur.left - this.offset.parent.left - this.margins.left +
                        ( this.offsetParent[ 0 ] === this.document[ 0 ].body ?
                                0 :
                                this.offsetParent[ 0 ].scrollLeft
                        );
                }
                if ( !axis || axis === "y" ) {
                    animation.top = cur.top - this.offset.parent.top - this.margins.top +
                        ( this.offsetParent[ 0 ] === this.document[ 0 ].body ?
                                0 :
                                this.offsetParent[ 0 ].scrollTop
                        );
                }
                this.reverting = true;
                $( this.helper ).animate(
                    animation,
                    parseInt( this.options.revert, 10 ) || 500,
                    function() {
                        that._clear( event );
                    }
                );
            } else {
                this._clear( event, noPropagation );
            }

            return false;

        },

        cancel: function() {

            if ( this.dragging ) {

                this._mouseUp( new $.Event( "mouseup", { target: null } ) );

                if ( this.options.helper === "original" ) {
                    this.currentItem.css( this._storedCSS );
                    this._removeClass( this.currentItem, "ui-sortable-helper" );
                } else {
                    this.currentItem.show();
                }

                //Post deactivating events to containers
                for ( var i = this.containers.length - 1; i >= 0; i-- ) {
                    this.containers[ i ]._trigger( "deactivate", null, this._uiHash( this ) );
                    if ( this.containers[ i ].containerCache.over ) {
                        this.containers[ i ]._trigger( "out", null, this._uiHash( this ) );
                        this.containers[ i ].containerCache.over = 0;
                    }
                }

            }

            if ( this.placeholder ) {

                //$(this.placeholder[0]).remove(); would have been the jQuery way - unfortunately,
                // it unbinds ALL events from the original node!
                if ( this.placeholder[ 0 ].parentNode ) {
                    this.placeholder[ 0 ].parentNode.removeChild( this.placeholder[ 0 ] );
                }
                if ( this.options.helper !== "original" && this.helper &&
                    this.helper[ 0 ].parentNode ) {
                    this.helper.remove();
                }

                $.extend( this, {
                    helper: null,
                    dragging: false,
                    reverting: false,
                    _noFinalSort: null
                } );

                if ( this.domPosition.prev ) {
                    $( this.domPosition.prev ).after( this.currentItem );
                } else {
                    $( this.domPosition.parent ).prepend( this.currentItem );
                }
            }

            return this;

        },

        serialize: function( o ) {

            var items = this._getItemsAsjQuery( o && o.connected ),
                str = [];
            o = o || {};

            $( items ).each( function() {
                var res = ( $( o.item || this ).attr( o.attribute || "id" ) || "" )
                    .match( o.expression || ( /(.+)[\-=_](.+)/ ) );
                if ( res ) {
                    str.push(
                        ( o.key || res[ 1 ] + "[]" ) +
                        "=" + ( o.key && o.expression ? res[ 1 ] : res[ 2 ] ) );
                }
            } );

            if ( !str.length && o.key ) {
                str.push( o.key + "=" );
            }

            return str.join( "&" );

        },

        toArray: function( o ) {

            var items = this._getItemsAsjQuery( o && o.connected ),
                ret = [];

            o = o || {};

            items.each( function() {
                ret.push( $( o.item || this ).attr( o.attribute || "id" ) || "" );
            } );
            return ret;

        },

        /* Be careful with the following core functions */
        _intersectsWith: function( item ) {

            var x1 = this.positionAbs.left,
                x2 = x1 + this.helperProportions.width,
                y1 = this.positionAbs.top,
                y2 = y1 + this.helperProportions.height,
                l = item.left,
                r = l + item.width,
                t = item.top,
                b = t + item.height,
                dyClick = this.offset.click.top,
                dxClick = this.offset.click.left,
                isOverElementHeight = ( this.options.axis === "x" ) || ( ( y1 + dyClick ) > t &&
                    ( y1 + dyClick ) < b ),
                isOverElementWidth = ( this.options.axis === "y" ) || ( ( x1 + dxClick ) > l &&
                    ( x1 + dxClick ) < r ),
                isOverElement = isOverElementHeight && isOverElementWidth;

            if ( this.options.tolerance === "pointer" ||
                this.options.forcePointerForContainers ||
                ( this.options.tolerance !== "pointer" &&
                    this.helperProportions[ this.floating ? "width" : "height" ] >
                    item[ this.floating ? "width" : "height" ] )
            ) {
                return isOverElement;
            } else {

                return ( l < x1 + ( this.helperProportions.width / 2 ) && // Right Half
                    x2 - ( this.helperProportions.width / 2 ) < r && // Left Half
                    t < y1 + ( this.helperProportions.height / 2 ) && // Bottom Half
                    y2 - ( this.helperProportions.height / 2 ) < b ); // Top Half

            }
        },

        _intersectsWithPointer: function( item ) {
            var verticalDirection, horizontalDirection,
                isOverElementHeight = ( this.options.axis === "x" ) ||
                    this._isOverAxis(
                        this.positionAbs.top + this.offset.click.top, item.top, item.height ),
                isOverElementWidth = ( this.options.axis === "y" ) ||
                    this._isOverAxis(
                        this.positionAbs.left + this.offset.click.left, item.left, item.width ),
                isOverElement = isOverElementHeight && isOverElementWidth;

            if ( !isOverElement ) {
                return false;
            }

            verticalDirection = this._getDragVerticalDirection();
            horizontalDirection = this._getDragHorizontalDirection();

            return this.floating ?
                ( ( horizontalDirection === "right" || verticalDirection === "down" ) ? 2 : 1 )
                : ( verticalDirection && ( verticalDirection === "down" ? 2 : 1 ) );

        },

        _intersectsWithSides: function( item ) {

            var isOverBottomHalf = this._isOverAxis( this.positionAbs.top +
                    this.offset.click.top, item.top + ( item.height / 2 ), item.height ),
                isOverRightHalf = this._isOverAxis( this.positionAbs.left +
                    this.offset.click.left, item.left + ( item.width / 2 ), item.width ),
                verticalDirection = this._getDragVerticalDirection(),
                horizontalDirection = this._getDragHorizontalDirection();

            if ( this.floating && horizontalDirection ) {
                return ( ( horizontalDirection === "right" && isOverRightHalf ) ||
                    ( horizontalDirection === "left" && !isOverRightHalf ) );
            } else {
                return verticalDirection && ( ( verticalDirection === "down" && isOverBottomHalf ) ||
                    ( verticalDirection === "up" && !isOverBottomHalf ) );
            }

        },

        _getDragVerticalDirection: function() {
            var delta = this.positionAbs.top - this.lastPositionAbs.top;
            return delta !== 0 && ( delta > 0 ? "down" : "up" );
        },

        _getDragHorizontalDirection: function() {
            var delta = this.positionAbs.left - this.lastPositionAbs.left;
            return delta !== 0 && ( delta > 0 ? "right" : "left" );
        },

        refresh: function( event ) {
            this._refreshItems( event );
            this._setHandleClassName();
            this.refreshPositions();
            return this;
        },

        _connectWith: function() {
            var options = this.options;
            return options.connectWith.constructor === String ?
                [ options.connectWith ] :
                options.connectWith;
        },

        _getItemsAsjQuery: function( connected ) {

            var i, j, cur, inst,
                items = [],
                queries = [],
                connectWith = this._connectWith();

            if ( connectWith && connected ) {
                for ( i = connectWith.length - 1; i >= 0; i-- ) {
                    cur = $( connectWith[ i ], this.document[ 0 ] );
                    for ( j = cur.length - 1; j >= 0; j-- ) {
                        inst = $.data( cur[ j ], this.widgetFullName );
                        if ( inst && inst !== this && !inst.options.disabled ) {
                            queries.push( [ $.isFunction( inst.options.items ) ?
                                inst.options.items.call( inst.element ) :
                                $( inst.options.items, inst.element )
                                    .not( ".ui-sortable-helper" )
                                    .not( ".ui-sortable-placeholder" ), inst ] );
                        }
                    }
                }
            }

            queries.push( [ $.isFunction( this.options.items ) ?
                this.options.items
                    .call( this.element, null, { options: this.options, item: this.currentItem } ) :
                $( this.options.items, this.element )
                    .not( ".ui-sortable-helper" )
                    .not( ".ui-sortable-placeholder" ), this ] );

            function addItems() {
                items.push( this );
            }
            for ( i = queries.length - 1; i >= 0; i-- ) {
                queries[ i ][ 0 ].each( addItems );
            }

            return $( items );

        },

        _removeCurrentsFromItems: function() {

            var list = this.currentItem.find( ":data(" + this.widgetName + "-item)" );

            this.items = $.grep( this.items, function( item ) {
                for ( var j = 0; j < list.length; j++ ) {
                    if ( list[ j ] === item.item[ 0 ] ) {
                        return false;
                    }
                }
                return true;
            } );

        },

        _refreshItems: function( event ) {

            this.items = [];
            this.containers = [ this ];

            var i, j, cur, inst, targetData, _queries, item, queriesLength,
                items = this.items,
                queries = [ [ $.isFunction( this.options.items ) ?
                    this.options.items.call( this.element[ 0 ], event, { item: this.currentItem } ) :
                    $( this.options.items, this.element ), this ] ],
                connectWith = this._connectWith();

            //Shouldn't be run the first time through due to massive slow-down
            if ( connectWith && this.ready ) {
                for ( i = connectWith.length - 1; i >= 0; i-- ) {
                    cur = $( connectWith[ i ], this.document[ 0 ] );
                    for ( j = cur.length - 1; j >= 0; j-- ) {
                        inst = $.data( cur[ j ], this.widgetFullName );
                        if ( inst && inst !== this && !inst.options.disabled ) {
                            queries.push( [ $.isFunction( inst.options.items ) ?
                                inst.options.items
                                    .call( inst.element[ 0 ], event, { item: this.currentItem } ) :
                                $( inst.options.items, inst.element ), inst ] );
                            this.containers.push( inst );
                        }
                    }
                }
            }

            for ( i = queries.length - 1; i >= 0; i-- ) {
                targetData = queries[ i ][ 1 ];
                _queries = queries[ i ][ 0 ];

                for ( j = 0, queriesLength = _queries.length; j < queriesLength; j++ ) {
                    item = $( _queries[ j ] );

                    // Data for target checking (mouse manager)
                    item.data( this.widgetName + "-item", targetData );

                    items.push( {
                        item: item,
                        instance: targetData,
                        width: 0, height: 0,
                        left: 0, top: 0
                    } );
                }
            }

        },

        refreshPositions: function( fast ) {

            // Determine whether items are being displayed horizontally
            this.floating = this.items.length ?
                this.options.axis === "x" || this._isFloating( this.items[ 0 ].item ) :
                false;

            //This has to be redone because due to the item being moved out/into the offsetParent,
            // the offsetParent's position will change
            if ( this.offsetParent && this.helper ) {
                this.offset.parent = this._getParentOffset();
            }

            var i, item, t, p;

            for ( i = this.items.length - 1; i >= 0; i-- ) {
                item = this.items[ i ];

                //We ignore calculating positions of all connected containers when we're not over them
                if ( item.instance !== this.currentContainer && this.currentContainer &&
                    item.item[ 0 ] !== this.currentItem[ 0 ] ) {
                    continue;
                }

                t = this.options.toleranceElement ?
                    $( this.options.toleranceElement, item.item ) :
                    item.item;

                if ( !fast ) {
                    item.width = t.outerWidth();
                    item.height = t.outerHeight();
                }

                p = t.offset();
                item.left = p.left;
                item.top = p.top;
            }

            if ( this.options.custom && this.options.custom.refreshContainers ) {
                this.options.custom.refreshContainers.call( this );
            } else {
                for ( i = this.containers.length - 1; i >= 0; i-- ) {
                    p = this.containers[ i ].element.offset();
                    this.containers[ i ].containerCache.left = p.left;
                    this.containers[ i ].containerCache.top = p.top;
                    this.containers[ i ].containerCache.width =
                        this.containers[ i ].element.outerWidth();
                    this.containers[ i ].containerCache.height =
                        this.containers[ i ].element.outerHeight();
                }
            }

            return this;
        },

        _createPlaceholder: function( that ) {
            that = that || this;
            var className,
                o = that.options;

            if ( !o.placeholder || o.placeholder.constructor === String ) {
                className = o.placeholder;
                o.placeholder = {
                    element: function() {

                        var nodeName = that.currentItem[ 0 ].nodeName.toLowerCase(),
                            element = $( "<" + nodeName + ">", that.document[ 0 ] );

                        that._addClass( element, "ui-sortable-placeholder",
                            className || that.currentItem[ 0 ].className )
                            ._removeClass( element, "ui-sortable-helper" );

                        if ( nodeName === "tbody" ) {
                            that._createTrPlaceholder(
                                that.currentItem.find( "tr" ).eq( 0 ),
                                $( "<tr>", that.document[ 0 ] ).appendTo( element )
                            );
                        } else if ( nodeName === "tr" ) {
                            that._createTrPlaceholder( that.currentItem, element );
                        } else if ( nodeName === "img" ) {
                            element.attr( "src", that.currentItem.attr( "src" ) );
                        }

                        if ( !className ) {
                            element.css( "visibility", "hidden" );
                        }

                        return element;
                    },
                    update: function( container, p ) {

                        // 1. If a className is set as 'placeholder option, we don't force sizes -
                        // the class is responsible for that
                        // 2. The option 'forcePlaceholderSize can be enabled to force it even if a
                        // class name is specified
                        if ( className && !o.forcePlaceholderSize ) {
                            return;
                        }

                        //If the element doesn't have a actual height by itself (without styles coming
                        // from a stylesheet), it receives the inline height from the dragged item
                        if ( !p.height() ) {
                            p.height(
                                that.currentItem.innerHeight() -
                                parseInt( that.currentItem.css( "paddingTop" ) || 0, 10 ) -
                                parseInt( that.currentItem.css( "paddingBottom" ) || 0, 10 ) );
                        }
                        if ( !p.width() ) {
                            p.width(
                                that.currentItem.innerWidth() -
                                parseInt( that.currentItem.css( "paddingLeft" ) || 0, 10 ) -
                                parseInt( that.currentItem.css( "paddingRight" ) || 0, 10 ) );
                        }
                    }
                };
            }

            //Create the placeholder
            that.placeholder = $( o.placeholder.element.call( that.element, that.currentItem ) );

            //Append it after the actual current item
            that.currentItem.after( that.placeholder );

            //Update the size of the placeholder (TODO: Logic to fuzzy, see line 316/317)
            o.placeholder.update( that, that.placeholder );

        },

        _createTrPlaceholder: function( sourceTr, targetTr ) {
            var that = this;

            sourceTr.children().each( function() {
                $( "<td>&#160;</td>", that.document[ 0 ] )
                    .attr( "colspan", $( this ).attr( "colspan" ) || 1 )
                    .appendTo( targetTr );
            } );
        },

        _contactContainers: function( event ) {
            var i, j, dist, itemWithLeastDistance, posProperty, sizeProperty, cur, nearBottom,
                floating, axis,
                innermostContainer = null,
                innermostIndex = null;

            // Get innermost container that intersects with item
            for ( i = this.containers.length - 1; i >= 0; i-- ) {

                // Never consider a container that's located within the item itself
                if ( $.contains( this.currentItem[ 0 ], this.containers[ i ].element[ 0 ] ) ) {
                    continue;
                }

                if ( this._intersectsWith( this.containers[ i ].containerCache ) ) {

                    // If we've already found a container and it's more "inner" than this, then continue
                    if ( innermostContainer &&
                        $.contains(
                            this.containers[ i ].element[ 0 ],
                            innermostContainer.element[ 0 ] ) ) {
                        continue;
                    }

                    innermostContainer = this.containers[ i ];
                    innermostIndex = i;

                } else {

                    // container doesn't intersect. trigger "out" event if necessary
                    if ( this.containers[ i ].containerCache.over ) {
                        this.containers[ i ]._trigger( "out", event, this._uiHash( this ) );
                        this.containers[ i ].containerCache.over = 0;
                    }
                }

            }

            // If no intersecting containers found, return
            if ( !innermostContainer ) {
                return;
            }

            // Move the item into the container if it's not there already
            if ( this.containers.length === 1 ) {
                if ( !this.containers[ innermostIndex ].containerCache.over ) {
                    this.containers[ innermostIndex ]._trigger( "over", event, this._uiHash( this ) );
                    this.containers[ innermostIndex ].containerCache.over = 1;
                }
            } else {

                // When entering a new container, we will find the item with the least distance and
                // append our item near it
                dist = 10000;
                itemWithLeastDistance = null;
                floating = innermostContainer.floating || this._isFloating( this.currentItem );
                posProperty = floating ? "left" : "top";
                sizeProperty = floating ? "width" : "height";
                axis = floating ? "pageX" : "pageY";

                for ( j = this.items.length - 1; j >= 0; j-- ) {
                    if ( !$.contains(
                        this.containers[ innermostIndex ].element[ 0 ], this.items[ j ].item[ 0 ] )
                    ) {
                        continue;
                    }
                    if ( this.items[ j ].item[ 0 ] === this.currentItem[ 0 ] ) {
                        continue;
                    }

                    cur = this.items[ j ].item.offset()[ posProperty ];
                    nearBottom = false;
                    if ( event[ axis ] - cur > this.items[ j ][ sizeProperty ] / 2 ) {
                        nearBottom = true;
                    }

                    if ( Math.abs( event[ axis ] - cur ) < dist ) {
                        dist = Math.abs( event[ axis ] - cur );
                        itemWithLeastDistance = this.items[ j ];
                        this.direction = nearBottom ? "up" : "down";
                    }
                }

                //Check if dropOnEmpty is enabled
                if ( !itemWithLeastDistance && !this.options.dropOnEmpty ) {
                    return;
                }

                if ( this.currentContainer === this.containers[ innermostIndex ] ) {
                    if ( !this.currentContainer.containerCache.over ) {
                        this.containers[ innermostIndex ]._trigger( "over", event, this._uiHash() );
                        this.currentContainer.containerCache.over = 1;
                    }
                    return;
                }

                itemWithLeastDistance ?
                    this._rearrange( event, itemWithLeastDistance, null, true ) :
                    this._rearrange( event, null, this.containers[ innermostIndex ].element, true );
                this._trigger( "change", event, this._uiHash() );
                this.containers[ innermostIndex ]._trigger( "change", event, this._uiHash( this ) );
                this.currentContainer = this.containers[ innermostIndex ];

                //Update the placeholder
                this.options.placeholder.update( this.currentContainer, this.placeholder );

                this.containers[ innermostIndex ]._trigger( "over", event, this._uiHash( this ) );
                this.containers[ innermostIndex ].containerCache.over = 1;
            }

        },

        _createHelper: function( event ) {

            var o = this.options,
                helper = $.isFunction( o.helper ) ?
                    $( o.helper.apply( this.element[ 0 ], [ event, this.currentItem ] ) ) :
                    ( o.helper === "clone" ? this.currentItem.clone() : this.currentItem );

            //Add the helper to the DOM if that didn't happen already
            if ( !helper.parents( "body" ).length ) {
                $( o.appendTo !== "parent" ?
                    o.appendTo :
                    this.currentItem[ 0 ].parentNode )[ 0 ].appendChild( helper[ 0 ] );
            }

            if ( helper[ 0 ] === this.currentItem[ 0 ] ) {
                this._storedCSS = {
                    width: this.currentItem[ 0 ].style.width,
                    height: this.currentItem[ 0 ].style.height,
                    position: this.currentItem.css( "position" ),
                    top: this.currentItem.css( "top" ),
                    left: this.currentItem.css( "left" )
                };
            }

            if ( !helper[ 0 ].style.width || o.forceHelperSize ) {
                helper.width( this.currentItem.width() );
            }
            if ( !helper[ 0 ].style.height || o.forceHelperSize ) {
                helper.height( this.currentItem.height() );
            }

            return helper;

        },

        _adjustOffsetFromHelper: function( obj ) {
            if ( typeof obj === "string" ) {
                obj = obj.split( " " );
            }
            if ( $.isArray( obj ) ) {
                obj = { left: +obj[ 0 ], top: +obj[ 1 ] || 0 };
            }
            if ( "left" in obj ) {
                this.offset.click.left = obj.left + this.margins.left;
            }
            if ( "right" in obj ) {
                this.offset.click.left = this.helperProportions.width - obj.right + this.margins.left;
            }
            if ( "top" in obj ) {
                this.offset.click.top = obj.top + this.margins.top;
            }
            if ( "bottom" in obj ) {
                this.offset.click.top = this.helperProportions.height - obj.bottom + this.margins.top;
            }
        },

        _getParentOffset: function() {

            //Get the offsetParent and cache its position
            this.offsetParent = this.helper.offsetParent();
            var po = this.offsetParent.offset();

            // This is a special case where we need to modify a offset calculated on start, since the
            // following happened:
            // 1. The position of the helper is absolute, so it's position is calculated based on the
            // next positioned parent
            // 2. The actual offset parent is a child of the scroll parent, and the scroll parent isn't
            // the document, which means that the scroll is included in the initial calculation of the
            // offset of the parent, and never recalculated upon drag
            if ( this.cssPosition === "absolute" && this.scrollParent[ 0 ] !== this.document[ 0 ] &&
                $.contains( this.scrollParent[ 0 ], this.offsetParent[ 0 ] ) ) {
                po.left += this.scrollParent.scrollLeft();
                po.top += this.scrollParent.scrollTop();
            }

            // This needs to be actually done for all browsers, since pageX/pageY includes this
            // information with an ugly IE fix
            if ( this.offsetParent[ 0 ] === this.document[ 0 ].body ||
                ( this.offsetParent[ 0 ].tagName &&
                    this.offsetParent[ 0 ].tagName.toLowerCase() === "html" && $.ui.ie ) ) {
                po = { top: 0, left: 0 };
            }

            return {
                top: po.top + ( parseInt( this.offsetParent.css( "borderTopWidth" ), 10 ) || 0 ),
                left: po.left + ( parseInt( this.offsetParent.css( "borderLeftWidth" ), 10 ) || 0 )
            };

        },

        _getRelativeOffset: function() {

            if ( this.cssPosition === "relative" ) {
                var p = this.currentItem.position();
                return {
                    top: p.top - ( parseInt( this.helper.css( "top" ), 10 ) || 0 ) +
                        this.scrollParent.scrollTop(),
                    left: p.left - ( parseInt( this.helper.css( "left" ), 10 ) || 0 ) +
                        this.scrollParent.scrollLeft()
                };
            } else {
                return { top: 0, left: 0 };
            }

        },

        _cacheMargins: function() {
            this.margins = {
                left: ( parseInt( this.currentItem.css( "marginLeft" ), 10 ) || 0 ),
                top: ( parseInt( this.currentItem.css( "marginTop" ), 10 ) || 0 )
            };
        },

        _cacheHelperProportions: function() {
            this.helperProportions = {
                width: this.helper.outerWidth(),
                height: this.helper.outerHeight()
            };
        },

        _setContainment: function() {

            var ce, co, over,
                o = this.options;
            if ( o.containment === "parent" ) {
                o.containment = this.helper[ 0 ].parentNode;
            }
            if ( o.containment === "document" || o.containment === "window" ) {
                this.containment = [
                    0 - this.offset.relative.left - this.offset.parent.left,
                    0 - this.offset.relative.top - this.offset.parent.top,
                    o.containment === "document" ?
                        this.document.width() :
                        this.window.width() - this.helperProportions.width - this.margins.left,
                    ( o.containment === "document" ?
                            ( this.document.height() || document.body.parentNode.scrollHeight ) :
                            this.window.height() || this.document[ 0 ].body.parentNode.scrollHeight
                    ) - this.helperProportions.height - this.margins.top
                ];
            }

            if ( !( /^(document|window|parent)$/ ).test( o.containment ) ) {
                ce = $( o.containment )[ 0 ];
                co = $( o.containment ).offset();
                over = ( $( ce ).css( "overflow" ) !== "hidden" );

                this.containment = [
                    co.left + ( parseInt( $( ce ).css( "borderLeftWidth" ), 10 ) || 0 ) +
                    ( parseInt( $( ce ).css( "paddingLeft" ), 10 ) || 0 ) - this.margins.left,
                    co.top + ( parseInt( $( ce ).css( "borderTopWidth" ), 10 ) || 0 ) +
                    ( parseInt( $( ce ).css( "paddingTop" ), 10 ) || 0 ) - this.margins.top,
                    co.left + ( over ? Math.max( ce.scrollWidth, ce.offsetWidth ) : ce.offsetWidth ) -
                    ( parseInt( $( ce ).css( "borderLeftWidth" ), 10 ) || 0 ) -
                    ( parseInt( $( ce ).css( "paddingRight" ), 10 ) || 0 ) -
                    this.helperProportions.width - this.margins.left,
                    co.top + ( over ? Math.max( ce.scrollHeight, ce.offsetHeight ) : ce.offsetHeight ) -
                    ( parseInt( $( ce ).css( "borderTopWidth" ), 10 ) || 0 ) -
                    ( parseInt( $( ce ).css( "paddingBottom" ), 10 ) || 0 ) -
                    this.helperProportions.height - this.margins.top
                ];
            }

        },

        _convertPositionTo: function( d, pos ) {

            if ( !pos ) {
                pos = this.position;
            }
            var mod = d === "absolute" ? 1 : -1,
                scroll = this.cssPosition === "absolute" &&
                !( this.scrollParent[ 0 ] !== this.document[ 0 ] &&
                    $.contains( this.scrollParent[ 0 ], this.offsetParent[ 0 ] ) ) ?
                    this.offsetParent :
                    this.scrollParent,
                scrollIsRootNode = ( /(html|body)/i ).test( scroll[ 0 ].tagName );

            return {
                top: (

                    // The absolute mouse position
                    pos.top	+

                    // Only for relative positioned nodes: Relative offset from element to offset parent
                    this.offset.relative.top * mod +

                    // The offsetParent's offset without borders (offset + border)
                    this.offset.parent.top * mod -
                    ( ( this.cssPosition === "fixed" ?
                        -this.scrollParent.scrollTop() :
                        ( scrollIsRootNode ? 0 : scroll.scrollTop() ) ) * mod )
                ),
                left: (

                    // The absolute mouse position
                    pos.left +

                    // Only for relative positioned nodes: Relative offset from element to offset parent
                    this.offset.relative.left * mod +

                    // The offsetParent's offset without borders (offset + border)
                    this.offset.parent.left * mod	-
                    ( ( this.cssPosition === "fixed" ?
                        -this.scrollParent.scrollLeft() : scrollIsRootNode ? 0 :
                            scroll.scrollLeft() ) * mod )
                )
            };

        },

        _generatePosition: function( event ) {

            var top, left,
                o = this.options,
                pageX = event.pageX,
                pageY = event.pageY,
                scroll = this.cssPosition === "absolute" &&
                !( this.scrollParent[ 0 ] !== this.document[ 0 ] &&
                    $.contains( this.scrollParent[ 0 ], this.offsetParent[ 0 ] ) ) ?
                    this.offsetParent :
                    this.scrollParent,
                scrollIsRootNode = ( /(html|body)/i ).test( scroll[ 0 ].tagName );

            // This is another very weird special case that only happens for relative elements:
            // 1. If the css position is relative
            // 2. and the scroll parent is the document or similar to the offset parent
            // we have to refresh the relative offset during the scroll so there are no jumps
            if ( this.cssPosition === "relative" && !( this.scrollParent[ 0 ] !== this.document[ 0 ] &&
                this.scrollParent[ 0 ] !== this.offsetParent[ 0 ] ) ) {
                this.offset.relative = this._getRelativeOffset();
            }

            /*
		 * - Position constraining -
		 * Constrain the position to a mix of grid, containment.
		 */

            if ( this.originalPosition ) { //If we are not dragging yet, we won't check for options

                if ( this.containment ) {
                    if ( event.pageX - this.offset.click.left < this.containment[ 0 ] ) {
                        pageX = this.containment[ 0 ] + this.offset.click.left;
                    }
                    if ( event.pageY - this.offset.click.top < this.containment[ 1 ] ) {
                        pageY = this.containment[ 1 ] + this.offset.click.top;
                    }
                    if ( event.pageX - this.offset.click.left > this.containment[ 2 ] ) {
                        pageX = this.containment[ 2 ] + this.offset.click.left;
                    }
                    if ( event.pageY - this.offset.click.top > this.containment[ 3 ] ) {
                        pageY = this.containment[ 3 ] + this.offset.click.top;
                    }
                }

                if ( o.grid ) {
                    top = this.originalPageY + Math.round( ( pageY - this.originalPageY ) /
                        o.grid[ 1 ] ) * o.grid[ 1 ];
                    pageY = this.containment ?
                        ( ( top - this.offset.click.top >= this.containment[ 1 ] &&
                            top - this.offset.click.top <= this.containment[ 3 ] ) ?
                            top :
                            ( ( top - this.offset.click.top >= this.containment[ 1 ] ) ?
                                top - o.grid[ 1 ] : top + o.grid[ 1 ] ) ) :
                        top;

                    left = this.originalPageX + Math.round( ( pageX - this.originalPageX ) /
                        o.grid[ 0 ] ) * o.grid[ 0 ];
                    pageX = this.containment ?
                        ( ( left - this.offset.click.left >= this.containment[ 0 ] &&
                            left - this.offset.click.left <= this.containment[ 2 ] ) ?
                            left :
                            ( ( left - this.offset.click.left >= this.containment[ 0 ] ) ?
                                left - o.grid[ 0 ] : left + o.grid[ 0 ] ) ) :
                        left;
                }

            }

            return {
                top: (

                    // The absolute mouse position
                    pageY -

                    // Click offset (relative to the element)
                    this.offset.click.top -

                    // Only for relative positioned nodes: Relative offset from element to offset parent
                    this.offset.relative.top -

                    // The offsetParent's offset without borders (offset + border)
                    this.offset.parent.top +
                    ( ( this.cssPosition === "fixed" ?
                        -this.scrollParent.scrollTop() :
                        ( scrollIsRootNode ? 0 : scroll.scrollTop() ) ) )
                ),
                left: (

                    // The absolute mouse position
                    pageX -

                    // Click offset (relative to the element)
                    this.offset.click.left -

                    // Only for relative positioned nodes: Relative offset from element to offset parent
                    this.offset.relative.left -

                    // The offsetParent's offset without borders (offset + border)
                    this.offset.parent.left +
                    ( ( this.cssPosition === "fixed" ?
                        -this.scrollParent.scrollLeft() :
                        scrollIsRootNode ? 0 : scroll.scrollLeft() ) )
                )
            };

        },

        _rearrange: function( event, i, a, hardRefresh ) {

            a ? a[ 0 ].appendChild( this.placeholder[ 0 ] ) :
                i.item[ 0 ].parentNode.insertBefore( this.placeholder[ 0 ],
                    ( this.direction === "down" ? i.item[ 0 ] : i.item[ 0 ].nextSibling ) );

            //Various things done here to improve the performance:
            // 1. we create a setTimeout, that calls refreshPositions
            // 2. on the instance, we have a counter variable, that get's higher after every append
            // 3. on the local scope, we copy the counter variable, and check in the timeout,
            // if it's still the same
            // 4. this lets only the last addition to the timeout stack through
            this.counter = this.counter ? ++this.counter : 1;
            var counter = this.counter;

            this._delay( function() {
                if ( counter === this.counter ) {

                    //Precompute after each DOM insertion, NOT on mousemove
                    this.refreshPositions( !hardRefresh );
                }
            } );

        },

        _clear: function( event, noPropagation ) {

            this.reverting = false;

            // We delay all events that have to be triggered to after the point where the placeholder
            // has been removed and everything else normalized again
            var i,
                delayedTriggers = [];

            // We first have to update the dom position of the actual currentItem
            // Note: don't do it if the current item is already removed (by a user), or it gets
            // reappended (see #4088)
            if ( !this._noFinalSort && this.currentItem.parent().length ) {
                this.placeholder.before( this.currentItem );
            }
            this._noFinalSort = null;

            if ( this.helper[ 0 ] === this.currentItem[ 0 ] ) {
                for ( i in this._storedCSS ) {
                    if ( this._storedCSS[ i ] === "auto" || this._storedCSS[ i ] === "static" ) {
                        this._storedCSS[ i ] = "";
                    }
                }
                this.currentItem.css( this._storedCSS );
                this._removeClass( this.currentItem, "ui-sortable-helper" );
            } else {
                this.currentItem.show();
            }

            if ( this.fromOutside && !noPropagation ) {
                delayedTriggers.push( function( event ) {
                    this._trigger( "receive", event, this._uiHash( this.fromOutside ) );
                } );
            }
            if ( ( this.fromOutside ||
                this.domPosition.prev !==
                this.currentItem.prev().not( ".ui-sortable-helper" )[ 0 ] ||
                this.domPosition.parent !== this.currentItem.parent()[ 0 ] ) && !noPropagation ) {

                // Trigger update callback if the DOM position has changed
                delayedTriggers.push( function( event ) {
                    this._trigger( "update", event, this._uiHash() );
                } );
            }

            // Check if the items Container has Changed and trigger appropriate
            // events.
            if ( this !== this.currentContainer ) {
                if ( !noPropagation ) {
                    delayedTriggers.push( function( event ) {
                        this._trigger( "remove", event, this._uiHash() );
                    } );
                    delayedTriggers.push( ( function( c ) {
                        return function( event ) {
                            c._trigger( "receive", event, this._uiHash( this ) );
                        };
                    } ).call( this, this.currentContainer ) );
                    delayedTriggers.push( ( function( c ) {
                        return function( event ) {
                            c._trigger( "update", event, this._uiHash( this ) );
                        };
                    } ).call( this, this.currentContainer ) );
                }
            }

            //Post events to containers
            function delayEvent( type, instance, container ) {
                return function( event ) {
                    container._trigger( type, event, instance._uiHash( instance ) );
                };
            }
            for ( i = this.containers.length - 1; i >= 0; i-- ) {
                if ( !noPropagation ) {
                    delayedTriggers.push( delayEvent( "deactivate", this, this.containers[ i ] ) );
                }
                if ( this.containers[ i ].containerCache.over ) {
                    delayedTriggers.push( delayEvent( "out", this, this.containers[ i ] ) );
                    this.containers[ i ].containerCache.over = 0;
                }
            }

            //Do what was originally in plugins
            if ( this.storedCursor ) {
                this.document.find( "body" ).css( "cursor", this.storedCursor );
                this.storedStylesheet.remove();
            }
            if ( this._storedOpacity ) {
                this.helper.css( "opacity", this._storedOpacity );
            }
            if ( this._storedZIndex ) {
                this.helper.css( "zIndex", this._storedZIndex === "auto" ? "" : this._storedZIndex );
            }

            this.dragging = false;

            if ( !noPropagation ) {
                this._trigger( "beforeStop", event, this._uiHash() );
            }

            //$(this.placeholder[0]).remove(); would have been the jQuery way - unfortunately,
            // it unbinds ALL events from the original node!
            this.placeholder[ 0 ].parentNode.removeChild( this.placeholder[ 0 ] );

            if ( !this.cancelHelperRemoval ) {
                if ( this.helper[ 0 ] !== this.currentItem[ 0 ] ) {
                    this.helper.remove();
                }
                this.helper = null;
            }

            if ( !noPropagation ) {
                for ( i = 0; i < delayedTriggers.length; i++ ) {

                    // Trigger all delayed events
                    delayedTriggers[ i ].call( this, event );
                }
                this._trigger( "stop", event, this._uiHash() );
            }

            this.fromOutside = false;
            return !this.cancelHelperRemoval;

        },

        _trigger: function() {
            if ( $.Widget.prototype._trigger.apply( this, arguments ) === false ) {
                this.cancel();
            }
        },

        _uiHash: function( _inst ) {
            var inst = _inst || this;
            return {
                helper: inst.helper,
                placeholder: inst.placeholder || $( [] ),
                position: inst.position,
                originalPosition: inst.originalPosition,
                offset: inst.positionAbs,
                item: inst.currentItem,
                sender: _inst ? _inst.element : null
            };
        }

    } );


// jscs:disable maximumLineLength
    /* jscs:disable requireCamelCaseOrUpperCaseIdentifiers */
    /*!
 * jQuery UI Datepicker 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

//>>label: Datepicker
//>>group: Widgets
//>>description: Displays a calendar from an input or inline for selecting dates.
//>>docs: http://api.jqueryui.com/datepicker/
//>>demos: http://jqueryui.com/datepicker/
//>>css.structure: ../../themes/base/core.css
//>>css.structure: ../../themes/base/datepicker.css
//>>css.theme: ../../themes/base/theme.css



    $.extend( $.ui, { datepicker: { version: "1.12.1" } } );

    var datepicker_instActive;

    function datepicker_getZindex( elem ) {
        var position, value;
        while ( elem.length && elem[ 0 ] !== document ) {

            // Ignore z-index if position is set to a value where z-index is ignored by the browser
            // This makes behavior of this function consistent across browsers
            // WebKit always returns auto if the element is positioned
            position = elem.css( "position" );
            if ( position === "absolute" || position === "relative" || position === "fixed" ) {

                // IE returns 0 when zIndex is not specified
                // other browsers return a string
                // we ignore the case of nested elements with an explicit value of 0
                // <div style="z-index: -10;"><div style="z-index: 0;"></div></div>
                value = parseInt( elem.css( "zIndex" ), 10 );
                if ( !isNaN( value ) && value !== 0 ) {
                    return value;
                }
            }
            elem = elem.parent();
        }

        return 0;
    }
    /* Date picker manager.
   Use the singleton instance of this class, $.datepicker, to interact with the date picker.
   Settings for (groups of) date pickers are maintained in an instance object,
   allowing multiple different settings on the same page. */

    function Datepicker() {
        this._curInst = null; // The current instance in use
        this._keyEvent = false; // If the last event was a key event
        this._disabledInputs = []; // List of date picker inputs that have been disabled
        this._datepickerShowing = false; // True if the popup picker is showing , false if not
        this._inDialog = false; // True if showing within a "dialog", false if not
        this._mainDivId = "ui-datepicker-div"; // The ID of the main datepicker division
        this._inlineClass = "ui-datepicker-inline"; // The name of the inline marker class
        this._appendClass = "ui-datepicker-append"; // The name of the append marker class
        this._triggerClass = "ui-datepicker-trigger"; // The name of the trigger marker class
        this._dialogClass = "ui-datepicker-dialog"; // The name of the dialog marker class
        this._disableClass = "ui-datepicker-disabled"; // The name of the disabled covering marker class
        this._unselectableClass = "ui-datepicker-unselectable"; // The name of the unselectable cell marker class
        this._currentClass = "ui-datepicker-current-day"; // The name of the current day marker class
        this._dayOverClass = "ui-datepicker-days-cell-over"; // The name of the day hover marker class
        this.regional = []; // Available regional settings, indexed by language code
        this.regional[ "" ] = { // Default regional settings
            closeText: "Done", // Display text for close link
            prevText: "Prev", // Display text for previous month link
            nextText: "Next", // Display text for next month link
            currentText: "Today", // Display text for current month link
            monthNames: [ "January","February","March","April","May","June",
                "July","August","September","October","November","December" ], // Names of months for drop-down and formatting
            monthNamesShort: [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ], // For formatting
            dayNames: [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ], // For formatting
            dayNamesShort: [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" ], // For formatting
            dayNamesMin: [ "Su","Mo","Tu","We","Th","Fr","Sa" ], // Column headings for days starting at Sunday
            weekHeader: "Wk", // Column header for week of the year
            dateFormat: "mm/dd/yy", // See format options on parseDate
            firstDay: 0, // The first day of the week, Sun = 0, Mon = 1, ...
            isRTL: false, // True if right-to-left language, false if left-to-right
            showMonthAfterYear: false, // True if the year select precedes month, false for month then year
            yearSuffix: "" // Additional text to append to the year in the month headers
        };
        this._defaults = { // Global defaults for all the date picker instances
            showOn: "focus", // "focus" for popup on focus,
            // "button" for trigger button, or "both" for either
            showAnim: "fadeIn", // Name of jQuery animation for popup
            showOptions: {}, // Options for enhanced animations
            defaultDate: null, // Used when field is blank: actual date,
            // +/-number for offset from today, null for today
            appendText: "", // Display text following the input box, e.g. showing the format
            buttonText: "...", // Text for trigger button
            buttonImage: "", // URL for trigger button image
            buttonImageOnly: false, // True if the image appears alone, false if it appears on a button
            hideIfNoPrevNext: false, // True to hide next/previous month links
            // if not applicable, false to just disable them
            navigationAsDateFormat: false, // True if date formatting applied to prev/today/next links
            gotoCurrent: false, // True if today link goes back to current selection instead
            changeMonth: false, // True if month can be selected directly, false if only prev/next
            changeYear: false, // True if year can be selected directly, false if only prev/next
            yearRange: "c-10:c+10", // Range of years to display in drop-down,
            // either relative to today's year (-nn:+nn), relative to currently displayed year
            // (c-nn:c+nn), absolute (nnnn:nnnn), or a combination of the above (nnnn:-n)
            showOtherMonths: false, // True to show dates in other months, false to leave blank
            selectOtherMonths: false, // True to allow selection of dates in other months, false for unselectable
            showWeek: false, // True to show week of the year, false to not show it
            calculateWeek: this.iso8601Week, // How to calculate the week of the year,
            // takes a Date and returns the number of the week for it
            shortYearCutoff: "+10", // Short year values < this are in the current century,
            // > this are in the previous century,
            // string value starting with "+" for current year + value
            minDate: null, // The earliest selectable date, or null for no limit
            maxDate: null, // The latest selectable date, or null for no limit
            duration: "fast", // Duration of display/closure
            beforeShowDay: null, // Function that takes a date and returns an array with
            // [0] = true if selectable, false if not, [1] = custom CSS class name(s) or "",
            // [2] = cell title (optional), e.g. $.datepicker.noWeekends
            beforeShow: null, // Function that takes an input field and
            // returns a set of custom settings for the date picker
            onSelect: null, // Define a callback function when a date is selected
            onChangeMonthYear: null, // Define a callback function when the month or year is changed
            onClose: null, // Define a callback function when the datepicker is closed
            numberOfMonths: 1, // Number of months to show at a time
            showCurrentAtPos: 0, // The position in multipe months at which to show the current month (starting at 0)
            stepMonths: 1, // Number of months to step back/forward
            stepBigMonths: 12, // Number of months to step back/forward for the big links
            altField: "", // Selector for an alternate field to store selected dates into
            altFormat: "", // The date format to use for the alternate field
            constrainInput: true, // The input is constrained by the current date format
            showButtonPanel: false, // True to show button panel, false to not show it
            autoSize: false, // True to size the input for the date format, false to leave as is
            disabled: false // The initial disabled state
        };
        $.extend( this._defaults, this.regional[ "" ] );
        this.regional.en = $.extend( true, {}, this.regional[ "" ] );
        this.regional[ "en-US" ] = $.extend( true, {}, this.regional.en );
        this.dpDiv = datepicker_bindHover( $( "<div id='" + this._mainDivId + "' class='ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all'></div>" ) );
    }

    $.extend( Datepicker.prototype, {
        /* Class name added to elements to indicate already configured with a date picker. */
        markerClassName: "hasDatepicker",

        //Keep track of the maximum number of rows displayed (see #7043)
        maxRows: 4,

        // TODO rename to "widget" when switching to widget factory
        _widgetDatepicker: function() {
            return this.dpDiv;
        },

        /* Override the default settings for all instances of the date picker.
	 * @param  settings  object - the new settings to use as defaults (anonymous object)
	 * @return the manager object
	 */
        setDefaults: function( settings ) {
            datepicker_extendRemove( this._defaults, settings || {} );
            return this;
        },

        /* Attach the date picker to a jQuery selection.
	 * @param  target	element - the target input field or division or span
	 * @param  settings  object - the new settings to use for this date picker instance (anonymous)
	 */
        _attachDatepicker: function( target, settings ) {
            var nodeName, inline, inst;
            nodeName = target.nodeName.toLowerCase();
            inline = ( nodeName === "div" || nodeName === "span" );
            if ( !target.id ) {
                this.uuid += 1;
                target.id = "dp" + this.uuid;
            }
            inst = this._newInst( $( target ), inline );
            inst.settings = $.extend( {}, settings || {} );
            if ( nodeName === "input" ) {
                this._connectDatepicker( target, inst );
            } else if ( inline ) {
                this._inlineDatepicker( target, inst );
            }
        },

        /* Create a new instance object. */
        _newInst: function( target, inline ) {
            var id = target[ 0 ].id.replace( /([^A-Za-z0-9_\-])/g, "\\\\$1" ); // escape jQuery meta chars
            return { id: id, input: target, // associated target
                selectedDay: 0, selectedMonth: 0, selectedYear: 0, // current selection
                drawMonth: 0, drawYear: 0, // month being drawn
                inline: inline, // is datepicker inline or not
                dpDiv: ( !inline ? this.dpDiv : // presentation div
                    datepicker_bindHover( $( "<div class='" + this._inlineClass + " ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all'></div>" ) ) ) };
        },

        /* Attach the date picker to an input field. */
        _connectDatepicker: function( target, inst ) {
            var input = $( target );
            inst.append = $( [] );
            inst.trigger = $( [] );
            if ( input.hasClass( this.markerClassName ) ) {
                return;
            }
            this._attachments( input, inst );
            input.addClass( this.markerClassName ).on( "keydown", this._doKeyDown ).
            on( "keypress", this._doKeyPress ).on( "keyup", this._doKeyUp );
            this._autoSize( inst );
            $.data( target, "datepicker", inst );

            //If disabled option is true, disable the datepicker once it has been attached to the input (see ticket #5665)
            if ( inst.settings.disabled ) {
                this._disableDatepicker( target );
            }
        },

        /* Make attachments based on settings. */
        _attachments: function( input, inst ) {
            var showOn, buttonText, buttonImage,
                appendText = this._get( inst, "appendText" ),
                isRTL = this._get( inst, "isRTL" );

            if ( inst.append ) {
                inst.append.remove();
            }
            if ( appendText ) {
                inst.append = $( "<span class='" + this._appendClass + "'>" + appendText + "</span>" );
                input[ isRTL ? "before" : "after" ]( inst.append );
            }

            input.off( "focus", this._showDatepicker );

            if ( inst.trigger ) {
                inst.trigger.remove();
            }

            showOn = this._get( inst, "showOn" );
            if ( showOn === "focus" || showOn === "both" ) { // pop-up date picker when in the marked field
                input.on( "focus", this._showDatepicker );
            }
            if ( showOn === "button" || showOn === "both" ) { // pop-up date picker when button clicked
                buttonText = this._get( inst, "buttonText" );
                buttonImage = this._get( inst, "buttonImage" );
                inst.trigger = $( this._get( inst, "buttonImageOnly" ) ?
                    $( "<img/>" ).addClass( this._triggerClass ).
                    attr( { src: buttonImage, alt: buttonText, title: buttonText } ) :
                    $( "<button type='button'></button>" ).addClass( this._triggerClass ).
                    html( !buttonImage ? buttonText : $( "<img/>" ).attr(
                        { src:buttonImage, alt:buttonText, title:buttonText } ) ) );
                input[ isRTL ? "before" : "after" ]( inst.trigger );
                inst.trigger.on( "click", function() {
                    if ( $.datepicker._datepickerShowing && $.datepicker._lastInput === input[ 0 ] ) {
                        $.datepicker._hideDatepicker();
                    } else if ( $.datepicker._datepickerShowing && $.datepicker._lastInput !== input[ 0 ] ) {
                        $.datepicker._hideDatepicker();
                        $.datepicker._showDatepicker( input[ 0 ] );
                    } else {
                        $.datepicker._showDatepicker( input[ 0 ] );
                    }
                    return false;
                } );
            }
        },

        /* Apply the maximum length for the date format. */
        _autoSize: function( inst ) {
            if ( this._get( inst, "autoSize" ) && !inst.inline ) {
                var findMax, max, maxI, i,
                    date = new Date( 2009, 12 - 1, 20 ), // Ensure double digits
                    dateFormat = this._get( inst, "dateFormat" );

                if ( dateFormat.match( /[DM]/ ) ) {
                    findMax = function( names ) {
                        max = 0;
                        maxI = 0;
                        for ( i = 0; i < names.length; i++ ) {
                            if ( names[ i ].length > max ) {
                                max = names[ i ].length;
                                maxI = i;
                            }
                        }
                        return maxI;
                    };
                    date.setMonth( findMax( this._get( inst, ( dateFormat.match( /MM/ ) ?
                        "monthNames" : "monthNamesShort" ) ) ) );
                    date.setDate( findMax( this._get( inst, ( dateFormat.match( /DD/ ) ?
                        "dayNames" : "dayNamesShort" ) ) ) + 20 - date.getDay() );
                }
                inst.input.attr( "size", this._formatDate( inst, date ).length );
            }
        },

        /* Attach an inline date picker to a div. */
        _inlineDatepicker: function( target, inst ) {
            var divSpan = $( target );
            if ( divSpan.hasClass( this.markerClassName ) ) {
                return;
            }
            divSpan.addClass( this.markerClassName ).append( inst.dpDiv );
            $.data( target, "datepicker", inst );
            this._setDate( inst, this._getDefaultDate( inst ), true );
            this._updateDatepicker( inst );
            this._updateAlternate( inst );

            //If disabled option is true, disable the datepicker before showing it (see ticket #5665)
            if ( inst.settings.disabled ) {
                this._disableDatepicker( target );
            }

            // Set display:block in place of inst.dpDiv.show() which won't work on disconnected elements
            // http://bugs.jqueryui.com/ticket/7552 - A Datepicker created on a detached div has zero height
            inst.dpDiv.css( "display", "block" );
        },

        /* Pop-up the date picker in a "dialog" box.
	 * @param  input element - ignored
	 * @param  date	string or Date - the initial date to display
	 * @param  onSelect  function - the function to call when a date is selected
	 * @param  settings  object - update the dialog date picker instance's settings (anonymous object)
	 * @param  pos int[2] - coordinates for the dialog's position within the screen or
	 *					event - with x/y coordinates or
	 *					leave empty for default (screen centre)
	 * @return the manager object
	 */
        _dialogDatepicker: function( input, date, onSelect, settings, pos ) {
            var id, browserWidth, browserHeight, scrollX, scrollY,
                inst = this._dialogInst; // internal instance

            if ( !inst ) {
                this.uuid += 1;
                id = "dp" + this.uuid;
                this._dialogInput = $( "<input type='text' id='" + id +
                    "' style='position: absolute; top: -100px; width: 0px;'/>" );
                this._dialogInput.on( "keydown", this._doKeyDown );
                $( "body" ).append( this._dialogInput );
                inst = this._dialogInst = this._newInst( this._dialogInput, false );
                inst.settings = {};
                $.data( this._dialogInput[ 0 ], "datepicker", inst );
            }
            datepicker_extendRemove( inst.settings, settings || {} );
            date = ( date && date.constructor === Date ? this._formatDate( inst, date ) : date );
            this._dialogInput.val( date );

            this._pos = ( pos ? ( pos.length ? pos : [ pos.pageX, pos.pageY ] ) : null );
            if ( !this._pos ) {
                browserWidth = document.documentElement.clientWidth;
                browserHeight = document.documentElement.clientHeight;
                scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
                scrollY = document.documentElement.scrollTop || document.body.scrollTop;
                this._pos = // should use actual width/height below
                    [ ( browserWidth / 2 ) - 100 + scrollX, ( browserHeight / 2 ) - 150 + scrollY ];
            }

            // Move input on screen for focus, but hidden behind dialog
            this._dialogInput.css( "left", ( this._pos[ 0 ] + 20 ) + "px" ).css( "top", this._pos[ 1 ] + "px" );
            inst.settings.onSelect = onSelect;
            this._inDialog = true;
            this.dpDiv.addClass( this._dialogClass );
            this._showDatepicker( this._dialogInput[ 0 ] );
            if ( $.blockUI ) {
                $.blockUI( this.dpDiv );
            }
            $.data( this._dialogInput[ 0 ], "datepicker", inst );
            return this;
        },

        /* Detach a datepicker from its control.
	 * @param  target	element - the target input field or division or span
	 */
        _destroyDatepicker: function( target ) {
            var nodeName,
                $target = $( target ),
                inst = $.data( target, "datepicker" );

            if ( !$target.hasClass( this.markerClassName ) ) {
                return;
            }

            nodeName = target.nodeName.toLowerCase();
            $.removeData( target, "datepicker" );
            if ( nodeName === "input" ) {
                inst.append.remove();
                inst.trigger.remove();
                $target.removeClass( this.markerClassName ).
                off( "focus", this._showDatepicker ).
                off( "keydown", this._doKeyDown ).
                off( "keypress", this._doKeyPress ).
                off( "keyup", this._doKeyUp );
            } else if ( nodeName === "div" || nodeName === "span" ) {
                $target.removeClass( this.markerClassName ).empty();
            }

            if ( datepicker_instActive === inst ) {
                datepicker_instActive = null;
            }
        },

        /* Enable the date picker to a jQuery selection.
	 * @param  target	element - the target input field or division or span
	 */
        _enableDatepicker: function( target ) {
            var nodeName, inline,
                $target = $( target ),
                inst = $.data( target, "datepicker" );

            if ( !$target.hasClass( this.markerClassName ) ) {
                return;
            }

            nodeName = target.nodeName.toLowerCase();
            if ( nodeName === "input" ) {
                target.disabled = false;
                inst.trigger.filter( "button" ).
                each( function() { this.disabled = false; } ).end().
                filter( "img" ).css( { opacity: "1.0", cursor: "" } );
            } else if ( nodeName === "div" || nodeName === "span" ) {
                inline = $target.children( "." + this._inlineClass );
                inline.children().removeClass( "ui-state-disabled" );
                inline.find( "select.ui-datepicker-month, select.ui-datepicker-year" ).
                prop( "disabled", false );
            }
            this._disabledInputs = $.map( this._disabledInputs,
                function( value ) { return ( value === target ? null : value ); } ); // delete entry
        },

        /* Disable the date picker to a jQuery selection.
	 * @param  target	element - the target input field or division or span
	 */
        _disableDatepicker: function( target ) {
            var nodeName, inline,
                $target = $( target ),
                inst = $.data( target, "datepicker" );

            if ( !$target.hasClass( this.markerClassName ) ) {
                return;
            }

            nodeName = target.nodeName.toLowerCase();
            if ( nodeName === "input" ) {
                target.disabled = true;
                inst.trigger.filter( "button" ).
                each( function() { this.disabled = true; } ).end().
                filter( "img" ).css( { opacity: "0.5", cursor: "default" } );
            } else if ( nodeName === "div" || nodeName === "span" ) {
                inline = $target.children( "." + this._inlineClass );
                inline.children().addClass( "ui-state-disabled" );
                inline.find( "select.ui-datepicker-month, select.ui-datepicker-year" ).
                prop( "disabled", true );
            }
            this._disabledInputs = $.map( this._disabledInputs,
                function( value ) { return ( value === target ? null : value ); } ); // delete entry
            this._disabledInputs[ this._disabledInputs.length ] = target;
        },

        /* Is the first field in a jQuery collection disabled as a datepicker?
	 * @param  target	element - the target input field or division or span
	 * @return boolean - true if disabled, false if enabled
	 */
        _isDisabledDatepicker: function( target ) {
            if ( !target ) {
                return false;
            }
            for ( var i = 0; i < this._disabledInputs.length; i++ ) {
                if ( this._disabledInputs[ i ] === target ) {
                    return true;
                }
            }
            return false;
        },

        /* Retrieve the instance data for the target control.
	 * @param  target  element - the target input field or division or span
	 * @return  object - the associated instance data
	 * @throws  error if a jQuery problem getting data
	 */
        _getInst: function( target ) {
            try {
                return $.data( target, "datepicker" );
            }
            catch ( err ) {
                throw "Missing instance data for this datepicker";
            }
        },

        /* Update or retrieve the settings for a date picker attached to an input field or division.
	 * @param  target  element - the target input field or division or span
	 * @param  name	object - the new settings to update or
	 *				string - the name of the setting to change or retrieve,
	 *				when retrieving also "all" for all instance settings or
	 *				"defaults" for all global defaults
	 * @param  value   any - the new value for the setting
	 *				(omit if above is an object or to retrieve a value)
	 */
        _optionDatepicker: function( target, name, value ) {
            var settings, date, minDate, maxDate,
                inst = this._getInst( target );

            if ( arguments.length === 2 && typeof name === "string" ) {
                return ( name === "defaults" ? $.extend( {}, $.datepicker._defaults ) :
                    ( inst ? ( name === "all" ? $.extend( {}, inst.settings ) :
                        this._get( inst, name ) ) : null ) );
            }

            settings = name || {};
            if ( typeof name === "string" ) {
                settings = {};
                settings[ name ] = value;
            }

            if ( inst ) {
                if ( this._curInst === inst ) {
                    this._hideDatepicker();
                }

                date = this._getDateDatepicker( target, true );
                minDate = this._getMinMaxDate( inst, "min" );
                maxDate = this._getMinMaxDate( inst, "max" );
                datepicker_extendRemove( inst.settings, settings );

                // reformat the old minDate/maxDate values if dateFormat changes and a new minDate/maxDate isn't provided
                if ( minDate !== null && settings.dateFormat !== undefined && settings.minDate === undefined ) {
                    inst.settings.minDate = this._formatDate( inst, minDate );
                }
                if ( maxDate !== null && settings.dateFormat !== undefined && settings.maxDate === undefined ) {
                    inst.settings.maxDate = this._formatDate( inst, maxDate );
                }
                if ( "disabled" in settings ) {
                    if ( settings.disabled ) {
                        this._disableDatepicker( target );
                    } else {
                        this._enableDatepicker( target );
                    }
                }
                this._attachments( $( target ), inst );
                this._autoSize( inst );
                this._setDate( inst, date );
                this._updateAlternate( inst );
                this._updateDatepicker( inst );
            }
        },

        // Change method deprecated
        _changeDatepicker: function( target, name, value ) {
            this._optionDatepicker( target, name, value );
        },

        /* Redraw the date picker attached to an input field or division.
	 * @param  target  element - the target input field or division or span
	 */
        _refreshDatepicker: function( target ) {
            var inst = this._getInst( target );
            if ( inst ) {
                this._updateDatepicker( inst );
            }
        },

        /* Set the dates for a jQuery selection.
	 * @param  target element - the target input field or division or span
	 * @param  date	Date - the new date
	 */
        _setDateDatepicker: function( target, date ) {
            var inst = this._getInst( target );
            if ( inst ) {
                this._setDate( inst, date );
                this._updateDatepicker( inst );
                this._updateAlternate( inst );
            }
        },

        /* Get the date(s) for the first entry in a jQuery selection.
	 * @param  target element - the target input field or division or span
	 * @param  noDefault boolean - true if no default date is to be used
	 * @return Date - the current date
	 */
        _getDateDatepicker: function( target, noDefault ) {
            var inst = this._getInst( target );
            if ( inst && !inst.inline ) {
                this._setDateFromField( inst, noDefault );
            }
            return ( inst ? this._getDate( inst ) : null );
        },

        /* Handle keystrokes. */
        _doKeyDown: function( event ) {
            var onSelect, dateStr, sel,
                inst = $.datepicker._getInst( event.target ),
                handled = true,
                isRTL = inst.dpDiv.is( ".ui-datepicker-rtl" );

            inst._keyEvent = true;
            if ( $.datepicker._datepickerShowing ) {
                switch ( event.keyCode ) {
                    case 9: $.datepicker._hideDatepicker();
                        handled = false;
                        break; // hide on tab out
                    case 13: sel = $( "td." + $.datepicker._dayOverClass + ":not(." +
                        $.datepicker._currentClass + ")", inst.dpDiv );
                        if ( sel[ 0 ] ) {
                            $.datepicker._selectDay( event.target, inst.selectedMonth, inst.selectedYear, sel[ 0 ] );
                        }

                        onSelect = $.datepicker._get( inst, "onSelect" );
                        if ( onSelect ) {
                            dateStr = $.datepicker._formatDate( inst );

                            // Trigger custom callback
                            onSelect.apply( ( inst.input ? inst.input[ 0 ] : null ), [ dateStr, inst ] );
                        } else {
                            $.datepicker._hideDatepicker();
                        }

                        return false; // don't submit the form
                    case 27: $.datepicker._hideDatepicker();
                        break; // hide on escape
                    case 33: $.datepicker._adjustDate( event.target, ( event.ctrlKey ?
                        -$.datepicker._get( inst, "stepBigMonths" ) :
                        -$.datepicker._get( inst, "stepMonths" ) ), "M" );
                        break; // previous month/year on page up/+ ctrl
                    case 34: $.datepicker._adjustDate( event.target, ( event.ctrlKey ?
                        +$.datepicker._get( inst, "stepBigMonths" ) :
                        +$.datepicker._get( inst, "stepMonths" ) ), "M" );
                        break; // next month/year on page down/+ ctrl
                    case 35: if ( event.ctrlKey || event.metaKey ) {
                        $.datepicker._clearDate( event.target );
                    }
                        handled = event.ctrlKey || event.metaKey;
                        break; // clear on ctrl or command +end
                    case 36: if ( event.ctrlKey || event.metaKey ) {
                        $.datepicker._gotoToday( event.target );
                    }
                        handled = event.ctrlKey || event.metaKey;
                        break; // current on ctrl or command +home
                    case 37: if ( event.ctrlKey || event.metaKey ) {
                        $.datepicker._adjustDate( event.target, ( isRTL ? +1 : -1 ), "D" );
                    }
                        handled = event.ctrlKey || event.metaKey;

                        // -1 day on ctrl or command +left
                        if ( event.originalEvent.altKey ) {
                            $.datepicker._adjustDate( event.target, ( event.ctrlKey ?
                                -$.datepicker._get( inst, "stepBigMonths" ) :
                                -$.datepicker._get( inst, "stepMonths" ) ), "M" );
                        }

                        // next month/year on alt +left on Mac
                        break;
                    case 38: if ( event.ctrlKey || event.metaKey ) {
                        $.datepicker._adjustDate( event.target, -7, "D" );
                    }
                        handled = event.ctrlKey || event.metaKey;
                        break; // -1 week on ctrl or command +up
                    case 39: if ( event.ctrlKey || event.metaKey ) {
                        $.datepicker._adjustDate( event.target, ( isRTL ? -1 : +1 ), "D" );
                    }
                        handled = event.ctrlKey || event.metaKey;

                        // +1 day on ctrl or command +right
                        if ( event.originalEvent.altKey ) {
                            $.datepicker._adjustDate( event.target, ( event.ctrlKey ?
                                +$.datepicker._get( inst, "stepBigMonths" ) :
                                +$.datepicker._get( inst, "stepMonths" ) ), "M" );
                        }

                        // next month/year on alt +right
                        break;
                    case 40: if ( event.ctrlKey || event.metaKey ) {
                        $.datepicker._adjustDate( event.target, +7, "D" );
                    }
                        handled = event.ctrlKey || event.metaKey;
                        break; // +1 week on ctrl or command +down
                    default: handled = false;
                }
            } else if ( event.keyCode === 36 && event.ctrlKey ) { // display the date picker on ctrl+home
                $.datepicker._showDatepicker( this );
            } else {
                handled = false;
            }

            if ( handled ) {
                event.preventDefault();
                event.stopPropagation();
            }
        },

        /* Filter entered characters - based on date format. */
        _doKeyPress: function( event ) {
            var chars, chr,
                inst = $.datepicker._getInst( event.target );

            if ( $.datepicker._get( inst, "constrainInput" ) ) {
                chars = $.datepicker._possibleChars( $.datepicker._get( inst, "dateFormat" ) );
                chr = String.fromCharCode( event.charCode == null ? event.keyCode : event.charCode );
                return event.ctrlKey || event.metaKey || ( chr < " " || !chars || chars.indexOf( chr ) > -1 );
            }
        },

        /* Synchronise manual entry and field/alternate field. */
        _doKeyUp: function( event ) {
            var date,
                inst = $.datepicker._getInst( event.target );

            if ( inst.input.val() !== inst.lastVal ) {
                try {
                    date = $.datepicker.parseDate( $.datepicker._get( inst, "dateFormat" ),
                        ( inst.input ? inst.input.val() : null ),
                        $.datepicker._getFormatConfig( inst ) );

                    if ( date ) { // only if valid
                        $.datepicker._setDateFromField( inst );
                        $.datepicker._updateAlternate( inst );
                        $.datepicker._updateDatepicker( inst );
                    }
                }
                catch ( err ) {
                }
            }
            return true;
        },

        /* Pop-up the date picker for a given input field.
	 * If false returned from beforeShow event handler do not show.
	 * @param  input  element - the input field attached to the date picker or
	 *					event - if triggered by focus
	 */
        _showDatepicker: function( input ) {
            input = input.target || input;
            if ( input.nodeName.toLowerCase() !== "input" ) { // find from button/image trigger
                input = $( "input", input.parentNode )[ 0 ];
            }

            if ( $.datepicker._isDisabledDatepicker( input ) || $.datepicker._lastInput === input ) { // already here
                return;
            }

            var inst, beforeShow, beforeShowSettings, isFixed,
                offset, showAnim, duration;

            inst = $.datepicker._getInst( input );
            if ( $.datepicker._curInst && $.datepicker._curInst !== inst ) {
                $.datepicker._curInst.dpDiv.stop( true, true );
                if ( inst && $.datepicker._datepickerShowing ) {
                    $.datepicker._hideDatepicker( $.datepicker._curInst.input[ 0 ] );
                }
            }

            beforeShow = $.datepicker._get( inst, "beforeShow" );
            beforeShowSettings = beforeShow ? beforeShow.apply( input, [ input, inst ] ) : {};
            if ( beforeShowSettings === false ) {
                return;
            }
            datepicker_extendRemove( inst.settings, beforeShowSettings );

            inst.lastVal = null;
            $.datepicker._lastInput = input;
            $.datepicker._setDateFromField( inst );

            if ( $.datepicker._inDialog ) { // hide cursor
                input.value = "";
            }
            if ( !$.datepicker._pos ) { // position below input
                $.datepicker._pos = $.datepicker._findPos( input );
                $.datepicker._pos[ 1 ] += input.offsetHeight; // add the height
            }

            isFixed = false;
            $( input ).parents().each( function() {
                isFixed |= $( this ).css( "position" ) === "fixed";
                return !isFixed;
            } );

            offset = { left: $.datepicker._pos[ 0 ], top: $.datepicker._pos[ 1 ] };
            $.datepicker._pos = null;

            //to avoid flashes on Firefox
            inst.dpDiv.empty();

            // determine sizing offscreen
            inst.dpDiv.css( { position: "absolute", display: "block", top: "-1000px" } );
            $.datepicker._updateDatepicker( inst );

            // fix width for dynamic number of date pickers
            // and adjust position before showing
            offset = $.datepicker._checkOffset( inst, offset, isFixed );
            inst.dpDiv.css( { position: ( $.datepicker._inDialog && $.blockUI ?
                    "static" : ( isFixed ? "fixed" : "absolute" ) ), display: "none",
                left: offset.left + "px", top: offset.top + "px" } );

            if ( !inst.inline ) {
                showAnim = $.datepicker._get( inst, "showAnim" );
                duration = $.datepicker._get( inst, "duration" );
                inst.dpDiv.css( "z-index", datepicker_getZindex( $( input ) ) + 1 );
                $.datepicker._datepickerShowing = true;

                if ( $.effects && $.effects.effect[ showAnim ] ) {
                    inst.dpDiv.show( showAnim, $.datepicker._get( inst, "showOptions" ), duration );
                } else {
                    inst.dpDiv[ showAnim || "show" ]( showAnim ? duration : null );
                }

                if ( $.datepicker._shouldFocusInput( inst ) ) {
                    inst.input.trigger( "focus" );
                }

                $.datepicker._curInst = inst;
            }
        },

        /* Generate the date picker content. */
        _updateDatepicker: function( inst ) {
            this.maxRows = 4; //Reset the max number of rows being displayed (see #7043)
            datepicker_instActive = inst; // for delegate hover events
            inst.dpDiv.empty().append( this._generateHTML( inst ) );
            this._attachHandlers( inst );

            var origyearshtml,
                numMonths = this._getNumberOfMonths( inst ),
                cols = numMonths[ 1 ],
                width = 17,
                activeCell = inst.dpDiv.find( "." + this._dayOverClass + " a" );

            if ( activeCell.length > 0 ) {
                datepicker_handleMouseover.apply( activeCell.get( 0 ) );
            }

            inst.dpDiv.removeClass( "ui-datepicker-multi-2 ui-datepicker-multi-3 ui-datepicker-multi-4" ).width( "" );
            if ( cols > 1 ) {
                inst.dpDiv.addClass( "ui-datepicker-multi-" + cols ).css( "width", ( width * cols ) + "em" );
            }
            inst.dpDiv[ ( numMonths[ 0 ] !== 1 || numMonths[ 1 ] !== 1 ? "add" : "remove" ) +
            "Class" ]( "ui-datepicker-multi" );
            inst.dpDiv[ ( this._get( inst, "isRTL" ) ? "add" : "remove" ) +
            "Class" ]( "ui-datepicker-rtl" );

            if ( inst === $.datepicker._curInst && $.datepicker._datepickerShowing && $.datepicker._shouldFocusInput( inst ) ) {
                inst.input.trigger( "focus" );
            }

            // Deffered render of the years select (to avoid flashes on Firefox)
            if ( inst.yearshtml ) {
                origyearshtml = inst.yearshtml;
                setTimeout( function() {

                    //assure that inst.yearshtml didn't change.
                    if ( origyearshtml === inst.yearshtml && inst.yearshtml ) {
                        inst.dpDiv.find( "select.ui-datepicker-year:first" ).replaceWith( inst.yearshtml );
                    }
                    origyearshtml = inst.yearshtml = null;
                }, 0 );
            }
        },

        // #6694 - don't focus the input if it's already focused
        // this breaks the change event in IE
        // Support: IE and jQuery <1.9
        _shouldFocusInput: function( inst ) {
            return inst.input && inst.input.is( ":visible" ) && !inst.input.is( ":disabled" ) && !inst.input.is( ":focus" );
        },

        /* Check positioning to remain on screen. */
        _checkOffset: function( inst, offset, isFixed ) {
            var dpWidth = inst.dpDiv.outerWidth(),
                dpHeight = inst.dpDiv.outerHeight(),
                inputWidth = inst.input ? inst.input.outerWidth() : 0,
                inputHeight = inst.input ? inst.input.outerHeight() : 0,
                viewWidth = document.documentElement.clientWidth + ( isFixed ? 0 : $( document ).scrollLeft() ),
                viewHeight = document.documentElement.clientHeight + ( isFixed ? 0 : $( document ).scrollTop() );

            offset.left -= ( this._get( inst, "isRTL" ) ? ( dpWidth - inputWidth ) : 0 );
            offset.left -= ( isFixed && offset.left === inst.input.offset().left ) ? $( document ).scrollLeft() : 0;
            offset.top -= ( isFixed && offset.top === ( inst.input.offset().top + inputHeight ) ) ? $( document ).scrollTop() : 0;

            // Now check if datepicker is showing outside window viewport - move to a better place if so.
            offset.left -= Math.min( offset.left, ( offset.left + dpWidth > viewWidth && viewWidth > dpWidth ) ?
                Math.abs( offset.left + dpWidth - viewWidth ) : 0 );
            offset.top -= Math.min( offset.top, ( offset.top + dpHeight > viewHeight && viewHeight > dpHeight ) ?
                Math.abs( dpHeight + inputHeight ) : 0 );

            return offset;
        },

        /* Find an object's position on the screen. */
        _findPos: function( obj ) {
            var position,
                inst = this._getInst( obj ),
                isRTL = this._get( inst, "isRTL" );

            while ( obj && ( obj.type === "hidden" || obj.nodeType !== 1 || $.expr.filters.hidden( obj ) ) ) {
                obj = obj[ isRTL ? "previousSibling" : "nextSibling" ];
            }

            position = $( obj ).offset();
            return [ position.left, position.top ];
        },

        /* Hide the date picker from view.
	 * @param  input  element - the input field attached to the date picker
	 */
        _hideDatepicker: function( input ) {
            var showAnim, duration, postProcess, onClose,
                inst = this._curInst;

            if ( !inst || ( input && inst !== $.data( input, "datepicker" ) ) ) {
                return;
            }

            if ( this._datepickerShowing ) {
                showAnim = this._get( inst, "showAnim" );
                duration = this._get( inst, "duration" );
                postProcess = function() {
                    $.datepicker._tidyDialog( inst );
                };

                // DEPRECATED: after BC for 1.8.x $.effects[ showAnim ] is not needed
                if ( $.effects && ( $.effects.effect[ showAnim ] || $.effects[ showAnim ] ) ) {
                    inst.dpDiv.hide( showAnim, $.datepicker._get( inst, "showOptions" ), duration, postProcess );
                } else {
                    inst.dpDiv[ ( showAnim === "slideDown" ? "slideUp" :
                        ( showAnim === "fadeIn" ? "fadeOut" : "hide" ) ) ]( ( showAnim ? duration : null ), postProcess );
                }

                if ( !showAnim ) {
                    postProcess();
                }
                this._datepickerShowing = false;

                onClose = this._get( inst, "onClose" );
                if ( onClose ) {
                    onClose.apply( ( inst.input ? inst.input[ 0 ] : null ), [ ( inst.input ? inst.input.val() : "" ), inst ] );
                }

                this._lastInput = null;
                if ( this._inDialog ) {
                    this._dialogInput.css( { position: "absolute", left: "0", top: "-100px" } );
                    if ( $.blockUI ) {
                        $.unblockUI();
                        $( "body" ).append( this.dpDiv );
                    }
                }
                this._inDialog = false;
            }
        },

        /* Tidy up after a dialog display. */
        _tidyDialog: function( inst ) {
            inst.dpDiv.removeClass( this._dialogClass ).off( ".ui-datepicker-calendar" );
        },

        /* Close date picker if clicked elsewhere. */
        _checkExternalClick: function( event ) {
            if ( !$.datepicker._curInst ) {
                return;
            }

            var $target = $( event.target ),
                inst = $.datepicker._getInst( $target[ 0 ] );

            if ( ( ( $target[ 0 ].id !== $.datepicker._mainDivId &&
                    $target.parents( "#" + $.datepicker._mainDivId ).length === 0 &&
                    !$target.hasClass( $.datepicker.markerClassName ) &&
                    !$target.closest( "." + $.datepicker._triggerClass ).length &&
                    $.datepicker._datepickerShowing && !( $.datepicker._inDialog && $.blockUI ) ) ) ||
                ( $target.hasClass( $.datepicker.markerClassName ) && $.datepicker._curInst !== inst ) ) {
                $.datepicker._hideDatepicker();
            }
        },

        /* Adjust one of the date sub-fields. */
        _adjustDate: function( id, offset, period ) {
            var target = $( id ),
                inst = this._getInst( target[ 0 ] );

            if ( this._isDisabledDatepicker( target[ 0 ] ) ) {
                return;
            }
            this._adjustInstDate( inst, offset +
                ( period === "M" ? this._get( inst, "showCurrentAtPos" ) : 0 ), // undo positioning
                period );
            this._updateDatepicker( inst );
        },

        /* Action for current link. */
        _gotoToday: function( id ) {
            var date,
                target = $( id ),
                inst = this._getInst( target[ 0 ] );

            if ( this._get( inst, "gotoCurrent" ) && inst.currentDay ) {
                inst.selectedDay = inst.currentDay;
                inst.drawMonth = inst.selectedMonth = inst.currentMonth;
                inst.drawYear = inst.selectedYear = inst.currentYear;
            } else {
                date = new Date();
                inst.selectedDay = date.getDate();
                inst.drawMonth = inst.selectedMonth = date.getMonth();
                inst.drawYear = inst.selectedYear = date.getFullYear();
            }
            this._notifyChange( inst );
            this._adjustDate( target );
        },

        /* Action for selecting a new month/year. */
        _selectMonthYear: function( id, select, period ) {
            var target = $( id ),
                inst = this._getInst( target[ 0 ] );

            inst[ "selected" + ( period === "M" ? "Month" : "Year" ) ] =
                inst[ "draw" + ( period === "M" ? "Month" : "Year" ) ] =
                    parseInt( select.options[ select.selectedIndex ].value, 10 );

            this._notifyChange( inst );
            this._adjustDate( target );
        },

        /* Action for selecting a day. */
        _selectDay: function( id, month, year, td ) {
            var inst,
                target = $( id );

            if ( $( td ).hasClass( this._unselectableClass ) || this._isDisabledDatepicker( target[ 0 ] ) ) {
                return;
            }

            inst = this._getInst( target[ 0 ] );
            inst.selectedDay = inst.currentDay = $( "a", td ).html();
            inst.selectedMonth = inst.currentMonth = month;
            inst.selectedYear = inst.currentYear = year;
            this._selectDate( id, this._formatDate( inst,
                inst.currentDay, inst.currentMonth, inst.currentYear ) );
        },

        /* Erase the input field and hide the date picker. */
        _clearDate: function( id ) {
            var target = $( id );
            this._selectDate( target, "" );
        },

        /* Update the input field with the selected date. */
        _selectDate: function( id, dateStr ) {
            var onSelect,
                target = $( id ),
                inst = this._getInst( target[ 0 ] );

            dateStr = ( dateStr != null ? dateStr : this._formatDate( inst ) );
            if ( inst.input ) {
                inst.input.val( dateStr );
            }
            this._updateAlternate( inst );

            onSelect = this._get( inst, "onSelect" );
            if ( onSelect ) {
                onSelect.apply( ( inst.input ? inst.input[ 0 ] : null ), [ dateStr, inst ] );  // trigger custom callback
            } else if ( inst.input ) {
                inst.input.trigger( "change" ); // fire the change event
            }

            if ( inst.inline ) {
                this._updateDatepicker( inst );
            } else {
                this._hideDatepicker();
                this._lastInput = inst.input[ 0 ];
                if ( typeof( inst.input[ 0 ] ) !== "object" ) {
                    inst.input.trigger( "focus" ); // restore focus
                }
                this._lastInput = null;
            }
        },

        /* Update any alternate field to synchronise with the main field. */
        _updateAlternate: function( inst ) {
            var altFormat, date, dateStr,
                altField = this._get( inst, "altField" );

            if ( altField ) { // update alternate field too
                altFormat = this._get( inst, "altFormat" ) || this._get( inst, "dateFormat" );
                date = this._getDate( inst );
                dateStr = this.formatDate( altFormat, date, this._getFormatConfig( inst ) );
                $( altField ).val( dateStr );
            }
        },

        /* Set as beforeShowDay function to prevent selection of weekends.
	 * @param  date  Date - the date to customise
	 * @return [boolean, string] - is this date selectable?, what is its CSS class?
	 */
        noWeekends: function( date ) {
            var day = date.getDay();
            return [ ( day > 0 && day < 6 ), "" ];
        },

        /* Set as calculateWeek to determine the week of the year based on the ISO 8601 definition.
	 * @param  date  Date - the date to get the week for
	 * @return  number - the number of the week within the year that contains this date
	 */
        iso8601Week: function( date ) {
            var time,
                checkDate = new Date( date.getTime() );

            // Find Thursday of this week starting on Monday
            checkDate.setDate( checkDate.getDate() + 4 - ( checkDate.getDay() || 7 ) );

            time = checkDate.getTime();
            checkDate.setMonth( 0 ); // Compare with Jan 1
            checkDate.setDate( 1 );
            return Math.floor( Math.round( ( time - checkDate ) / 86400000 ) / 7 ) + 1;
        },

        /* Parse a string value into a date object.
	 * See formatDate below for the possible formats.
	 *
	 * @param  format string - the expected format of the date
	 * @param  value string - the date in the above format
	 * @param  settings Object - attributes include:
	 *					shortYearCutoff  number - the cutoff year for determining the century (optional)
	 *					dayNamesShort	string[7] - abbreviated names of the days from Sunday (optional)
	 *					dayNames		string[7] - names of the days from Sunday (optional)
	 *					monthNamesShort string[12] - abbreviated names of the months (optional)
	 *					monthNames		string[12] - names of the months (optional)
	 * @return  Date - the extracted date value or null if value is blank
	 */
        parseDate: function( format, value, settings ) {
            if ( format == null || value == null ) {
                throw "Invalid arguments";
            }

            value = ( typeof value === "object" ? value.toString() : value + "" );
            if ( value === "" ) {
                return null;
            }

            var iFormat, dim, extra,
                iValue = 0,
                shortYearCutoffTemp = ( settings ? settings.shortYearCutoff : null ) || this._defaults.shortYearCutoff,
                shortYearCutoff = ( typeof shortYearCutoffTemp !== "string" ? shortYearCutoffTemp :
                    new Date().getFullYear() % 100 + parseInt( shortYearCutoffTemp, 10 ) ),
                dayNamesShort = ( settings ? settings.dayNamesShort : null ) || this._defaults.dayNamesShort,
                dayNames = ( settings ? settings.dayNames : null ) || this._defaults.dayNames,
                monthNamesShort = ( settings ? settings.monthNamesShort : null ) || this._defaults.monthNamesShort,
                monthNames = ( settings ? settings.monthNames : null ) || this._defaults.monthNames,
                year = -1,
                month = -1,
                day = -1,
                doy = -1,
                literal = false,
                date,

                // Check whether a format character is doubled
                lookAhead = function( match ) {
                    var matches = ( iFormat + 1 < format.length && format.charAt( iFormat + 1 ) === match );
                    if ( matches ) {
                        iFormat++;
                    }
                    return matches;
                },

                // Extract a number from the string value
                getNumber = function( match ) {
                    var isDoubled = lookAhead( match ),
                        size = ( match === "@" ? 14 : ( match === "!" ? 20 :
                            ( match === "y" && isDoubled ? 4 : ( match === "o" ? 3 : 2 ) ) ) ),
                        minSize = ( match === "y" ? size : 1 ),
                        digits = new RegExp( "^\\d{" + minSize + "," + size + "}" ),
                        num = value.substring( iValue ).match( digits );
                    if ( !num ) {
                        throw "Missing number at position " + iValue;
                    }
                    iValue += num[ 0 ].length;
                    return parseInt( num[ 0 ], 10 );
                },

                // Extract a name from the string value and convert to an index
                getName = function( match, shortNames, longNames ) {
                    var index = -1,
                        names = $.map( lookAhead( match ) ? longNames : shortNames, function( v, k ) {
                            return [ [ k, v ] ];
                        } ).sort( function( a, b ) {
                            return -( a[ 1 ].length - b[ 1 ].length );
                        } );

                    $.each( names, function( i, pair ) {
                        var name = pair[ 1 ];
                        if ( value.substr( iValue, name.length ).toLowerCase() === name.toLowerCase() ) {
                            index = pair[ 0 ];
                            iValue += name.length;
                            return false;
                        }
                    } );
                    if ( index !== -1 ) {
                        return index + 1;
                    } else {
                        throw "Unknown name at position " + iValue;
                    }
                },

                // Confirm that a literal character matches the string value
                checkLiteral = function() {
                    if ( value.charAt( iValue ) !== format.charAt( iFormat ) ) {
                        throw "Unexpected literal at position " + iValue;
                    }
                    iValue++;
                };

            for ( iFormat = 0; iFormat < format.length; iFormat++ ) {
                if ( literal ) {
                    if ( format.charAt( iFormat ) === "'" && !lookAhead( "'" ) ) {
                        literal = false;
                    } else {
                        checkLiteral();
                    }
                } else {
                    switch ( format.charAt( iFormat ) ) {
                        case "d":
                            day = getNumber( "d" );
                            break;
                        case "D":
                            getName( "D", dayNamesShort, dayNames );
                            break;
                        case "o":
                            doy = getNumber( "o" );
                            break;
                        case "m":
                            month = getNumber( "m" );
                            break;
                        case "M":
                            month = getName( "M", monthNamesShort, monthNames );
                            break;
                        case "y":
                            year = getNumber( "y" );
                            break;
                        case "@":
                            date = new Date( getNumber( "@" ) );
                            year = date.getFullYear();
                            month = date.getMonth() + 1;
                            day = date.getDate();
                            break;
                        case "!":
                            date = new Date( ( getNumber( "!" ) - this._ticksTo1970 ) / 10000 );
                            year = date.getFullYear();
                            month = date.getMonth() + 1;
                            day = date.getDate();
                            break;
                        case "'":
                            if ( lookAhead( "'" ) ) {
                                checkLiteral();
                            } else {
                                literal = true;
                            }
                            break;
                        default:
                            checkLiteral();
                    }
                }
            }

            if ( iValue < value.length ) {
                extra = value.substr( iValue );
                if ( !/^\s+/.test( extra ) ) {
                    throw "Extra/unparsed characters found in date: " + extra;
                }
            }

            if ( year === -1 ) {
                year = new Date().getFullYear();
            } else if ( year < 100 ) {
                year += new Date().getFullYear() - new Date().getFullYear() % 100 +
                    ( year <= shortYearCutoff ? 0 : -100 );
            }

            if ( doy > -1 ) {
                month = 1;
                day = doy;
                do {
                    dim = this._getDaysInMonth( year, month - 1 );
                    if ( day <= dim ) {
                        break;
                    }
                    month++;
                    day -= dim;
                } while ( true );
            }

            date = this._daylightSavingAdjust( new Date( year, month - 1, day ) );
            if ( date.getFullYear() !== year || date.getMonth() + 1 !== month || date.getDate() !== day ) {
                throw "Invalid date"; // E.g. 31/02/00
            }
            return date;
        },

        /* Standard date formats. */
        ATOM: "yy-mm-dd", // RFC 3339 (ISO 8601)
        COOKIE: "D, dd M yy",
        ISO_8601: "yy-mm-dd",
        RFC_822: "D, d M y",
        RFC_850: "DD, dd-M-y",
        RFC_1036: "D, d M y",
        RFC_1123: "D, d M yy",
        RFC_2822: "D, d M yy",
        RSS: "D, d M y", // RFC 822
        TICKS: "!",
        TIMESTAMP: "@",
        W3C: "yy-mm-dd", // ISO 8601

        _ticksTo1970: ( ( ( 1970 - 1 ) * 365 + Math.floor( 1970 / 4 ) - Math.floor( 1970 / 100 ) +
            Math.floor( 1970 / 400 ) ) * 24 * 60 * 60 * 10000000 ),

        /* Format a date object into a string value.
	 * The format can be combinations of the following:
	 * d  - day of month (no leading zero)
	 * dd - day of month (two digit)
	 * o  - day of year (no leading zeros)
	 * oo - day of year (three digit)
	 * D  - day name short
	 * DD - day name long
	 * m  - month of year (no leading zero)
	 * mm - month of year (two digit)
	 * M  - month name short
	 * MM - month name long
	 * y  - year (two digit)
	 * yy - year (four digit)
	 * @ - Unix timestamp (ms since 01/01/1970)
	 * ! - Windows ticks (100ns since 01/01/0001)
	 * "..." - literal text
	 * '' - single quote
	 *
	 * @param  format string - the desired format of the date
	 * @param  date Date - the date value to format
	 * @param  settings Object - attributes include:
	 *					dayNamesShort	string[7] - abbreviated names of the days from Sunday (optional)
	 *					dayNames		string[7] - names of the days from Sunday (optional)
	 *					monthNamesShort string[12] - abbreviated names of the months (optional)
	 *					monthNames		string[12] - names of the months (optional)
	 * @return  string - the date in the above format
	 */
        formatDate: function( format, date, settings ) {
            if ( !date ) {
                return "";
            }

            var iFormat,
                dayNamesShort = ( settings ? settings.dayNamesShort : null ) || this._defaults.dayNamesShort,
                dayNames = ( settings ? settings.dayNames : null ) || this._defaults.dayNames,
                monthNamesShort = ( settings ? settings.monthNamesShort : null ) || this._defaults.monthNamesShort,
                monthNames = ( settings ? settings.monthNames : null ) || this._defaults.monthNames,

                // Check whether a format character is doubled
                lookAhead = function( match ) {
                    var matches = ( iFormat + 1 < format.length && format.charAt( iFormat + 1 ) === match );
                    if ( matches ) {
                        iFormat++;
                    }
                    return matches;
                },

                // Format a number, with leading zero if necessary
                formatNumber = function( match, value, len ) {
                    var num = "" + value;
                    if ( lookAhead( match ) ) {
                        while ( num.length < len ) {
                            num = "0" + num;
                        }
                    }
                    return num;
                },

                // Format a name, short or long as requested
                formatName = function( match, value, shortNames, longNames ) {
                    return ( lookAhead( match ) ? longNames[ value ] : shortNames[ value ] );
                },
                output = "",
                literal = false;

            if ( date ) {
                for ( iFormat = 0; iFormat < format.length; iFormat++ ) {
                    if ( literal ) {
                        if ( format.charAt( iFormat ) === "'" && !lookAhead( "'" ) ) {
                            literal = false;
                        } else {
                            output += format.charAt( iFormat );
                        }
                    } else {
                        switch ( format.charAt( iFormat ) ) {
                            case "d":
                                output += formatNumber( "d", date.getDate(), 2 );
                                break;
                            case "D":
                                output += formatName( "D", date.getDay(), dayNamesShort, dayNames );
                                break;
                            case "o":
                                output += formatNumber( "o",
                                    Math.round( ( new Date( date.getFullYear(), date.getMonth(), date.getDate() ).getTime() - new Date( date.getFullYear(), 0, 0 ).getTime() ) / 86400000 ), 3 );
                                break;
                            case "m":
                                output += formatNumber( "m", date.getMonth() + 1, 2 );
                                break;
                            case "M":
                                output += formatName( "M", date.getMonth(), monthNamesShort, monthNames );
                                break;
                            case "y":
                                output += ( lookAhead( "y" ) ? date.getFullYear() :
                                    ( date.getFullYear() % 100 < 10 ? "0" : "" ) + date.getFullYear() % 100 );
                                break;
                            case "@":
                                output += date.getTime();
                                break;
                            case "!":
                                output += date.getTime() * 10000 + this._ticksTo1970;
                                break;
                            case "'":
                                if ( lookAhead( "'" ) ) {
                                    output += "'";
                                } else {
                                    literal = true;
                                }
                                break;
                            default:
                                output += format.charAt( iFormat );
                        }
                    }
                }
            }
            return output;
        },

        /* Extract all possible characters from the date format. */
        _possibleChars: function( format ) {
            var iFormat,
                chars = "",
                literal = false,

                // Check whether a format character is doubled
                lookAhead = function( match ) {
                    var matches = ( iFormat + 1 < format.length && format.charAt( iFormat + 1 ) === match );
                    if ( matches ) {
                        iFormat++;
                    }
                    return matches;
                };

            for ( iFormat = 0; iFormat < format.length; iFormat++ ) {
                if ( literal ) {
                    if ( format.charAt( iFormat ) === "'" && !lookAhead( "'" ) ) {
                        literal = false;
                    } else {
                        chars += format.charAt( iFormat );
                    }
                } else {
                    switch ( format.charAt( iFormat ) ) {
                        case "d": case "m": case "y": case "@":
                            chars += "0123456789";
                            break;
                        case "D": case "M":
                            return null; // Accept anything
                        case "'":
                            if ( lookAhead( "'" ) ) {
                                chars += "'";
                            } else {
                                literal = true;
                            }
                            break;
                        default:
                            chars += format.charAt( iFormat );
                    }
                }
            }
            return chars;
        },

        /* Get a setting value, defaulting if necessary. */
        _get: function( inst, name ) {
            return inst.settings[ name ] !== undefined ?
                inst.settings[ name ] : this._defaults[ name ];
        },

        /* Parse existing date and initialise date picker. */
        _setDateFromField: function( inst, noDefault ) {
            if ( inst.input.val() === inst.lastVal ) {
                return;
            }

            var dateFormat = this._get( inst, "dateFormat" ),
                dates = inst.lastVal = inst.input ? inst.input.val() : null,
                defaultDate = this._getDefaultDate( inst ),
                date = defaultDate,
                settings = this._getFormatConfig( inst );

            try {
                date = this.parseDate( dateFormat, dates, settings ) || defaultDate;
            } catch ( event ) {
                dates = ( noDefault ? "" : dates );
            }
            inst.selectedDay = date.getDate();
            inst.drawMonth = inst.selectedMonth = date.getMonth();
            inst.drawYear = inst.selectedYear = date.getFullYear();
            inst.currentDay = ( dates ? date.getDate() : 0 );
            inst.currentMonth = ( dates ? date.getMonth() : 0 );
            inst.currentYear = ( dates ? date.getFullYear() : 0 );
            this._adjustInstDate( inst );
        },

        /* Retrieve the default date shown on opening. */
        _getDefaultDate: function( inst ) {
            return this._restrictMinMax( inst,
                this._determineDate( inst, this._get( inst, "defaultDate" ), new Date() ) );
        },

        /* A date may be specified as an exact value or a relative one. */
        _determineDate: function( inst, date, defaultDate ) {
            var offsetNumeric = function( offset ) {
                    var date = new Date();
                    date.setDate( date.getDate() + offset );
                    return date;
                },
                offsetString = function( offset ) {
                    try {
                        return $.datepicker.parseDate( $.datepicker._get( inst, "dateFormat" ),
                            offset, $.datepicker._getFormatConfig( inst ) );
                    }
                    catch ( e ) {

                        // Ignore
                    }

                    var date = ( offset.toLowerCase().match( /^c/ ) ?
                            $.datepicker._getDate( inst ) : null ) || new Date(),
                        year = date.getFullYear(),
                        month = date.getMonth(),
                        day = date.getDate(),
                        pattern = /([+\-]?[0-9]+)\s*(d|D|w|W|m|M|y|Y)?/g,
                        matches = pattern.exec( offset );

                    while ( matches ) {
                        switch ( matches[ 2 ] || "d" ) {
                            case "d" : case "D" :
                                day += parseInt( matches[ 1 ], 10 ); break;
                            case "w" : case "W" :
                                day += parseInt( matches[ 1 ], 10 ) * 7; break;
                            case "m" : case "M" :
                                month += parseInt( matches[ 1 ], 10 );
                                day = Math.min( day, $.datepicker._getDaysInMonth( year, month ) );
                                break;
                            case "y": case "Y" :
                                year += parseInt( matches[ 1 ], 10 );
                                day = Math.min( day, $.datepicker._getDaysInMonth( year, month ) );
                                break;
                        }
                        matches = pattern.exec( offset );
                    }
                    return new Date( year, month, day );
                },
                newDate = ( date == null || date === "" ? defaultDate : ( typeof date === "string" ? offsetString( date ) :
                    ( typeof date === "number" ? ( isNaN( date ) ? defaultDate : offsetNumeric( date ) ) : new Date( date.getTime() ) ) ) );

            newDate = ( newDate && newDate.toString() === "Invalid Date" ? defaultDate : newDate );
            if ( newDate ) {
                newDate.setHours( 0 );
                newDate.setMinutes( 0 );
                newDate.setSeconds( 0 );
                newDate.setMilliseconds( 0 );
            }
            return this._daylightSavingAdjust( newDate );
        },

        /* Handle switch to/from daylight saving.
	 * Hours may be non-zero on daylight saving cut-over:
	 * > 12 when midnight changeover, but then cannot generate
	 * midnight datetime, so jump to 1AM, otherwise reset.
	 * @param  date  (Date) the date to check
	 * @return  (Date) the corrected date
	 */
        _daylightSavingAdjust: function( date ) {
            if ( !date ) {
                return null;
            }
            date.setHours( date.getHours() > 12 ? date.getHours() + 2 : 0 );
            return date;
        },

        /* Set the date(s) directly. */
        _setDate: function( inst, date, noChange ) {
            var clear = !date,
                origMonth = inst.selectedMonth,
                origYear = inst.selectedYear,
                newDate = this._restrictMinMax( inst, this._determineDate( inst, date, new Date() ) );

            inst.selectedDay = inst.currentDay = newDate.getDate();
            inst.drawMonth = inst.selectedMonth = inst.currentMonth = newDate.getMonth();
            inst.drawYear = inst.selectedYear = inst.currentYear = newDate.getFullYear();
            if ( ( origMonth !== inst.selectedMonth || origYear !== inst.selectedYear ) && !noChange ) {
                this._notifyChange( inst );
            }
            this._adjustInstDate( inst );
            if ( inst.input ) {
                inst.input.val( clear ? "" : this._formatDate( inst ) );
            }
        },

        /* Retrieve the date(s) directly. */
        _getDate: function( inst ) {
            var startDate = ( !inst.currentYear || ( inst.input && inst.input.val() === "" ) ? null :
                this._daylightSavingAdjust( new Date(
                    inst.currentYear, inst.currentMonth, inst.currentDay ) ) );
            return startDate;
        },

        /* Attach the onxxx handlers.  These are declared statically so
	 * they work with static code transformers like Caja.
	 */
        _attachHandlers: function( inst ) {
            var stepMonths = this._get( inst, "stepMonths" ),
                id = "#" + inst.id.replace( /\\\\/g, "\\" );
            inst.dpDiv.find( "[data-handler]" ).map( function() {
                var handler = {
                    prev: function() {
                        $.datepicker._adjustDate( id, -stepMonths, "M" );
                    },
                    next: function() {
                        $.datepicker._adjustDate( id, +stepMonths, "M" );
                    },
                    hide: function() {
                        $.datepicker._hideDatepicker();
                    },
                    today: function() {
                        $.datepicker._gotoToday( id );
                    },
                    selectDay: function() {
                        $.datepicker._selectDay( id, +this.getAttribute( "data-month" ), +this.getAttribute( "data-year" ), this );
                        return false;
                    },
                    selectMonth: function() {
                        $.datepicker._selectMonthYear( id, this, "M" );
                        return false;
                    },
                    selectYear: function() {
                        $.datepicker._selectMonthYear( id, this, "Y" );
                        return false;
                    }
                };
                $( this ).on( this.getAttribute( "data-event" ), handler[ this.getAttribute( "data-handler" ) ] );
            } );
        },

        /* Generate the HTML for the current state of the date picker. */
        _generateHTML: function( inst ) {
            var maxDraw, prevText, prev, nextText, next, currentText, gotoDate,
                controls, buttonPanel, firstDay, showWeek, dayNames, dayNamesMin,
                monthNames, monthNamesShort, beforeShowDay, showOtherMonths,
                selectOtherMonths, defaultDate, html, dow, row, group, col, selectedDate,
                cornerClass, calender, thead, day, daysInMonth, leadDays, curRows, numRows,
                printDate, dRow, tbody, daySettings, otherMonth, unselectable,
                tempDate = new Date(),
                today = this._daylightSavingAdjust(
                    new Date( tempDate.getFullYear(), tempDate.getMonth(), tempDate.getDate() ) ), // clear time
                isRTL = this._get( inst, "isRTL" ),
                showButtonPanel = this._get( inst, "showButtonPanel" ),
                hideIfNoPrevNext = this._get( inst, "hideIfNoPrevNext" ),
                navigationAsDateFormat = this._get( inst, "navigationAsDateFormat" ),
                numMonths = this._getNumberOfMonths( inst ),
                showCurrentAtPos = this._get( inst, "showCurrentAtPos" ),
                stepMonths = this._get( inst, "stepMonths" ),
                isMultiMonth = ( numMonths[ 0 ] !== 1 || numMonths[ 1 ] !== 1 ),
                currentDate = this._daylightSavingAdjust( ( !inst.currentDay ? new Date( 9999, 9, 9 ) :
                    new Date( inst.currentYear, inst.currentMonth, inst.currentDay ) ) ),
                minDate = this._getMinMaxDate( inst, "min" ),
                maxDate = this._getMinMaxDate( inst, "max" ),
                drawMonth = inst.drawMonth - showCurrentAtPos,
                drawYear = inst.drawYear;

            if ( drawMonth < 0 ) {
                drawMonth += 12;
                drawYear--;
            }
            if ( maxDate ) {
                maxDraw = this._daylightSavingAdjust( new Date( maxDate.getFullYear(),
                    maxDate.getMonth() - ( numMonths[ 0 ] * numMonths[ 1 ] ) + 1, maxDate.getDate() ) );
                maxDraw = ( minDate && maxDraw < minDate ? minDate : maxDraw );
                while ( this._daylightSavingAdjust( new Date( drawYear, drawMonth, 1 ) ) > maxDraw ) {
                    drawMonth--;
                    if ( drawMonth < 0 ) {
                        drawMonth = 11;
                        drawYear--;
                    }
                }
            }
            inst.drawMonth = drawMonth;
            inst.drawYear = drawYear;

            prevText = this._get( inst, "prevText" );
            prevText = ( !navigationAsDateFormat ? prevText : this.formatDate( prevText,
                this._daylightSavingAdjust( new Date( drawYear, drawMonth - stepMonths, 1 ) ),
                this._getFormatConfig( inst ) ) );

            prev = ( this._canAdjustMonth( inst, -1, drawYear, drawMonth ) ?
                "<a class='ui-datepicker-prev ui-corner-all' data-handler='prev' data-event='click'" +
                " title='" + prevText + "'><span class='ui-icon ui-icon-circle-triangle-" + ( isRTL ? "e" : "w" ) + "'>" + prevText + "</span></a>" :
                ( hideIfNoPrevNext ? "" : "<a class='ui-datepicker-prev ui-corner-all ui-state-disabled' title='" + prevText + "'><span class='ui-icon ui-icon-circle-triangle-" + ( isRTL ? "e" : "w" ) + "'>" + prevText + "</span></a>" ) );

            nextText = this._get( inst, "nextText" );
            nextText = ( !navigationAsDateFormat ? nextText : this.formatDate( nextText,
                this._daylightSavingAdjust( new Date( drawYear, drawMonth + stepMonths, 1 ) ),
                this._getFormatConfig( inst ) ) );

            next = ( this._canAdjustMonth( inst, +1, drawYear, drawMonth ) ?
                "<a class='ui-datepicker-next ui-corner-all' data-handler='next' data-event='click'" +
                " title='" + nextText + "'><span class='ui-icon ui-icon-circle-triangle-" + ( isRTL ? "w" : "e" ) + "'>" + nextText + "</span></a>" :
                ( hideIfNoPrevNext ? "" : "<a class='ui-datepicker-next ui-corner-all ui-state-disabled' title='" + nextText + "'><span class='ui-icon ui-icon-circle-triangle-" + ( isRTL ? "w" : "e" ) + "'>" + nextText + "</span></a>" ) );

            currentText = this._get( inst, "currentText" );
            gotoDate = ( this._get( inst, "gotoCurrent" ) && inst.currentDay ? currentDate : today );
            currentText = ( !navigationAsDateFormat ? currentText :
                this.formatDate( currentText, gotoDate, this._getFormatConfig( inst ) ) );

            controls = ( !inst.inline ? "<button type='button' class='ui-datepicker-close ui-state-default ui-priority-primary ui-corner-all' data-handler='hide' data-event='click'>" +
                this._get( inst, "closeText" ) + "</button>" : "" );

            buttonPanel = ( showButtonPanel ) ? "<div class='ui-datepicker-buttonpane ui-widget-content'>" + ( isRTL ? controls : "" ) +
                ( this._isInRange( inst, gotoDate ) ? "<button type='button' class='ui-datepicker-current ui-state-default ui-priority-secondary ui-corner-all' data-handler='today' data-event='click'" +
                    ">" + currentText + "</button>" : "" ) + ( isRTL ? "" : controls ) + "</div>" : "";

            firstDay = parseInt( this._get( inst, "firstDay" ), 10 );
            firstDay = ( isNaN( firstDay ) ? 0 : firstDay );

            showWeek = this._get( inst, "showWeek" );
            dayNames = this._get( inst, "dayNames" );
            dayNamesMin = this._get( inst, "dayNamesMin" );
            monthNames = this._get( inst, "monthNames" );
            monthNamesShort = this._get( inst, "monthNamesShort" );
            beforeShowDay = this._get( inst, "beforeShowDay" );
            showOtherMonths = this._get( inst, "showOtherMonths" );
            selectOtherMonths = this._get( inst, "selectOtherMonths" );
            defaultDate = this._getDefaultDate( inst );
            html = "";

            for ( row = 0; row < numMonths[ 0 ]; row++ ) {
                group = "";
                this.maxRows = 4;
                for ( col = 0; col < numMonths[ 1 ]; col++ ) {
                    selectedDate = this._daylightSavingAdjust( new Date( drawYear, drawMonth, inst.selectedDay ) );
                    cornerClass = " ui-corner-all";
                    calender = "";
                    if ( isMultiMonth ) {
                        calender += "<div class='ui-datepicker-group";
                        if ( numMonths[ 1 ] > 1 ) {
                            switch ( col ) {
                                case 0: calender += " ui-datepicker-group-first";
                                    cornerClass = " ui-corner-" + ( isRTL ? "right" : "left" ); break;
                                case numMonths[ 1 ] - 1: calender += " ui-datepicker-group-last";
                                    cornerClass = " ui-corner-" + ( isRTL ? "left" : "right" ); break;
                                default: calender += " ui-datepicker-group-middle"; cornerClass = ""; break;
                            }
                        }
                        calender += "'>";
                    }
                    calender += "<div class='ui-datepicker-header ui-widget-header ui-helper-clearfix" + cornerClass + "'>" +
                        ( /all|left/.test( cornerClass ) && row === 0 ? ( isRTL ? next : prev ) : "" ) +
                        ( /all|right/.test( cornerClass ) && row === 0 ? ( isRTL ? prev : next ) : "" ) +
                        this._generateMonthYearHeader( inst, drawMonth, drawYear, minDate, maxDate,
                            row > 0 || col > 0, monthNames, monthNamesShort ) + // draw month headers
                        "</div><table class='ui-datepicker-calendar'><thead>" +
                        "<tr>";
                    thead = ( showWeek ? "<th class='ui-datepicker-week-col'>" + this._get( inst, "weekHeader" ) + "</th>" : "" );
                    for ( dow = 0; dow < 7; dow++ ) { // days of the week
                        day = ( dow + firstDay ) % 7;
                        thead += "<th scope='col'" + ( ( dow + firstDay + 6 ) % 7 >= 5 ? " class='ui-datepicker-week-end'" : "" ) + ">" +
                            "<span title='" + dayNames[ day ] + "'>" + dayNamesMin[ day ] + "</span></th>";
                    }
                    calender += thead + "</tr></thead><tbody>";
                    daysInMonth = this._getDaysInMonth( drawYear, drawMonth );
                    if ( drawYear === inst.selectedYear && drawMonth === inst.selectedMonth ) {
                        inst.selectedDay = Math.min( inst.selectedDay, daysInMonth );
                    }
                    leadDays = ( this._getFirstDayOfMonth( drawYear, drawMonth ) - firstDay + 7 ) % 7;
                    curRows = Math.ceil( ( leadDays + daysInMonth ) / 7 ); // calculate the number of rows to generate
                    numRows = ( isMultiMonth ? this.maxRows > curRows ? this.maxRows : curRows : curRows ); //If multiple months, use the higher number of rows (see #7043)
                    this.maxRows = numRows;
                    printDate = this._daylightSavingAdjust( new Date( drawYear, drawMonth, 1 - leadDays ) );
                    for ( dRow = 0; dRow < numRows; dRow++ ) { // create date picker rows
                        calender += "<tr>";
                        tbody = ( !showWeek ? "" : "<td class='ui-datepicker-week-col'>" +
                            this._get( inst, "calculateWeek" )( printDate ) + "</td>" );
                        for ( dow = 0; dow < 7; dow++ ) { // create date picker days
                            daySettings = ( beforeShowDay ?
                                beforeShowDay.apply( ( inst.input ? inst.input[ 0 ] : null ), [ printDate ] ) : [ true, "" ] );
                            otherMonth = ( printDate.getMonth() !== drawMonth );
                            unselectable = ( otherMonth && !selectOtherMonths ) || !daySettings[ 0 ] ||
                                ( minDate && printDate < minDate ) || ( maxDate && printDate > maxDate );
                            tbody += "<td class='" +
                                ( ( dow + firstDay + 6 ) % 7 >= 5 ? " ui-datepicker-week-end" : "" ) + // highlight weekends
                                ( otherMonth ? " ui-datepicker-other-month" : "" ) + // highlight days from other months
                                ( ( printDate.getTime() === selectedDate.getTime() && drawMonth === inst.selectedMonth && inst._keyEvent ) || // user pressed key
                                ( defaultDate.getTime() === printDate.getTime() && defaultDate.getTime() === selectedDate.getTime() ) ?

                                    // or defaultDate is current printedDate and defaultDate is selectedDate
                                    " " + this._dayOverClass : "" ) + // highlight selected day
                                ( unselectable ? " " + this._unselectableClass + " ui-state-disabled" : "" ) +  // highlight unselectable days
                                ( otherMonth && !showOtherMonths ? "" : " " + daySettings[ 1 ] + // highlight custom dates
                                    ( printDate.getTime() === currentDate.getTime() ? " " + this._currentClass : "" ) + // highlight selected day
                                    ( printDate.getTime() === today.getTime() ? " ui-datepicker-today" : "" ) ) + "'" + // highlight today (if different)
                                ( ( !otherMonth || showOtherMonths ) && daySettings[ 2 ] ? " title='" + daySettings[ 2 ].replace( /'/g, "&#39;" ) + "'" : "" ) + // cell title
                                ( unselectable ? "" : " data-handler='selectDay' data-event='click' data-month='" + printDate.getMonth() + "' data-year='" + printDate.getFullYear() + "'" ) + ">" + // actions
                                ( otherMonth && !showOtherMonths ? "&#xa0;" : // display for other months
                                    ( unselectable ? "<span class='ui-state-default'>" + printDate.getDate() + "</span>" : "<a class='ui-state-default" +
                                        ( printDate.getTime() === today.getTime() ? " ui-state-highlight" : "" ) +
                                        ( printDate.getTime() === currentDate.getTime() ? " ui-state-active" : "" ) + // highlight selected day
                                        ( otherMonth ? " ui-priority-secondary" : "" ) + // distinguish dates from other months
                                        "' href='#'>" + printDate.getDate() + "</a>" ) ) + "</td>"; // display selectable date
                            printDate.setDate( printDate.getDate() + 1 );
                            printDate = this._daylightSavingAdjust( printDate );
                        }
                        calender += tbody + "</tr>";
                    }
                    drawMonth++;
                    if ( drawMonth > 11 ) {
                        drawMonth = 0;
                        drawYear++;
                    }
                    calender += "</tbody></table>" + ( isMultiMonth ? "</div>" +
                        ( ( numMonths[ 0 ] > 0 && col === numMonths[ 1 ] - 1 ) ? "<div class='ui-datepicker-row-break'></div>" : "" ) : "" );
                    group += calender;
                }
                html += group;
            }
            html += buttonPanel;
            inst._keyEvent = false;
            return html;
        },

        /* Generate the month and year header. */
        _generateMonthYearHeader: function( inst, drawMonth, drawYear, minDate, maxDate,
                                            secondary, monthNames, monthNamesShort ) {

            var inMinYear, inMaxYear, month, years, thisYear, determineYear, year, endYear,
                changeMonth = this._get( inst, "changeMonth" ),
                changeYear = this._get( inst, "changeYear" ),
                showMonthAfterYear = this._get( inst, "showMonthAfterYear" ),
                html = "<div class='ui-datepicker-title'>",
                monthHtml = "";

            // Month selection
            if ( secondary || !changeMonth ) {
                monthHtml += "<span class='ui-datepicker-month'>" + monthNames[ drawMonth ] + "</span>";
            } else {
                inMinYear = ( minDate && minDate.getFullYear() === drawYear );
                inMaxYear = ( maxDate && maxDate.getFullYear() === drawYear );
                monthHtml += "<select class='ui-datepicker-month' data-handler='selectMonth' data-event='change'>";
                for ( month = 0; month < 12; month++ ) {
                    if ( ( !inMinYear || month >= minDate.getMonth() ) && ( !inMaxYear || month <= maxDate.getMonth() ) ) {
                        monthHtml += "<option value='" + month + "'" +
                            ( month === drawMonth ? " selected='selected'" : "" ) +
                            ">" + monthNamesShort[ month ] + "</option>";
                    }
                }
                monthHtml += "</select>";
            }

            if ( !showMonthAfterYear ) {
                html += monthHtml + ( secondary || !( changeMonth && changeYear ) ? "&#xa0;" : "" );
            }

            // Year selection
            if ( !inst.yearshtml ) {
                inst.yearshtml = "";
                if ( secondary || !changeYear ) {
                    html += "<span class='ui-datepicker-year'>" + drawYear + "</span>";
                } else {

                    // determine range of years to display
                    years = this._get( inst, "yearRange" ).split( ":" );
                    thisYear = new Date().getFullYear();
                    determineYear = function( value ) {
                        var year = ( value.match( /c[+\-].*/ ) ? drawYear + parseInt( value.substring( 1 ), 10 ) :
                            ( value.match( /[+\-].*/ ) ? thisYear + parseInt( value, 10 ) :
                                parseInt( value, 10 ) ) );
                        return ( isNaN( year ) ? thisYear : year );
                    };
                    year = determineYear( years[ 0 ] );
                    endYear = Math.max( year, determineYear( years[ 1 ] || "" ) );
                    year = ( minDate ? Math.max( year, minDate.getFullYear() ) : year );
                    endYear = ( maxDate ? Math.min( endYear, maxDate.getFullYear() ) : endYear );
                    inst.yearshtml += "<select class='ui-datepicker-year' data-handler='selectYear' data-event='change'>";
                    for ( ; year <= endYear; year++ ) {
                        inst.yearshtml += "<option value='" + year + "'" +
                            ( year === drawYear ? " selected='selected'" : "" ) +
                            ">" + year + "</option>";
                    }
                    inst.yearshtml += "</select>";

                    html += inst.yearshtml;
                    inst.yearshtml = null;
                }
            }

            html += this._get( inst, "yearSuffix" );
            if ( showMonthAfterYear ) {
                html += ( secondary || !( changeMonth && changeYear ) ? "&#xa0;" : "" ) + monthHtml;
            }
            html += "</div>"; // Close datepicker_header
            return html;
        },

        /* Adjust one of the date sub-fields. */
        _adjustInstDate: function( inst, offset, period ) {
            var year = inst.selectedYear + ( period === "Y" ? offset : 0 ),
                month = inst.selectedMonth + ( period === "M" ? offset : 0 ),
                day = Math.min( inst.selectedDay, this._getDaysInMonth( year, month ) ) + ( period === "D" ? offset : 0 ),
                date = this._restrictMinMax( inst, this._daylightSavingAdjust( new Date( year, month, day ) ) );

            inst.selectedDay = date.getDate();
            inst.drawMonth = inst.selectedMonth = date.getMonth();
            inst.drawYear = inst.selectedYear = date.getFullYear();
            if ( period === "M" || period === "Y" ) {
                this._notifyChange( inst );
            }
        },

        /* Ensure a date is within any min/max bounds. */
        _restrictMinMax: function( inst, date ) {
            var minDate = this._getMinMaxDate( inst, "min" ),
                maxDate = this._getMinMaxDate( inst, "max" ),
                newDate = ( minDate && date < minDate ? minDate : date );
            return ( maxDate && newDate > maxDate ? maxDate : newDate );
        },

        /* Notify change of month/year. */
        _notifyChange: function( inst ) {
            var onChange = this._get( inst, "onChangeMonthYear" );
            if ( onChange ) {
                onChange.apply( ( inst.input ? inst.input[ 0 ] : null ),
                    [ inst.selectedYear, inst.selectedMonth + 1, inst ] );
            }
        },

        /* Determine the number of months to show. */
        _getNumberOfMonths: function( inst ) {
            var numMonths = this._get( inst, "numberOfMonths" );
            return ( numMonths == null ? [ 1, 1 ] : ( typeof numMonths === "number" ? [ 1, numMonths ] : numMonths ) );
        },

        /* Determine the current maximum date - ensure no time components are set. */
        _getMinMaxDate: function( inst, minMax ) {
            return this._determineDate( inst, this._get( inst, minMax + "Date" ), null );
        },

        /* Find the number of days in a given month. */
        _getDaysInMonth: function( year, month ) {
            return 32 - this._daylightSavingAdjust( new Date( year, month, 32 ) ).getDate();
        },

        /* Find the day of the week of the first of a month. */
        _getFirstDayOfMonth: function( year, month ) {
            return new Date( year, month, 1 ).getDay();
        },

        /* Determines if we should allow a "next/prev" month display change. */
        _canAdjustMonth: function( inst, offset, curYear, curMonth ) {
            var numMonths = this._getNumberOfMonths( inst ),
                date = this._daylightSavingAdjust( new Date( curYear,
                    curMonth + ( offset < 0 ? offset : numMonths[ 0 ] * numMonths[ 1 ] ), 1 ) );

            if ( offset < 0 ) {
                date.setDate( this._getDaysInMonth( date.getFullYear(), date.getMonth() ) );
            }
            return this._isInRange( inst, date );
        },

        /* Is the given date in the accepted range? */
        _isInRange: function( inst, date ) {
            var yearSplit, currentYear,
                minDate = this._getMinMaxDate( inst, "min" ),
                maxDate = this._getMinMaxDate( inst, "max" ),
                minYear = null,
                maxYear = null,
                years = this._get( inst, "yearRange" );
            if ( years ) {
                yearSplit = years.split( ":" );
                currentYear = new Date().getFullYear();
                minYear = parseInt( yearSplit[ 0 ], 10 );
                maxYear = parseInt( yearSplit[ 1 ], 10 );
                if ( yearSplit[ 0 ].match( /[+\-].*/ ) ) {
                    minYear += currentYear;
                }
                if ( yearSplit[ 1 ].match( /[+\-].*/ ) ) {
                    maxYear += currentYear;
                }
            }

            return ( ( !minDate || date.getTime() >= minDate.getTime() ) &&
                ( !maxDate || date.getTime() <= maxDate.getTime() ) &&
                ( !minYear || date.getFullYear() >= minYear ) &&
                ( !maxYear || date.getFullYear() <= maxYear ) );
        },

        /* Provide the configuration settings for formatting/parsing. */
        _getFormatConfig: function( inst ) {
            var shortYearCutoff = this._get( inst, "shortYearCutoff" );
            shortYearCutoff = ( typeof shortYearCutoff !== "string" ? shortYearCutoff :
                new Date().getFullYear() % 100 + parseInt( shortYearCutoff, 10 ) );
            return { shortYearCutoff: shortYearCutoff,
                dayNamesShort: this._get( inst, "dayNamesShort" ), dayNames: this._get( inst, "dayNames" ),
                monthNamesShort: this._get( inst, "monthNamesShort" ), monthNames: this._get( inst, "monthNames" ) };
        },

        /* Format the given date for display. */
        _formatDate: function( inst, day, month, year ) {
            if ( !day ) {
                inst.currentDay = inst.selectedDay;
                inst.currentMonth = inst.selectedMonth;
                inst.currentYear = inst.selectedYear;
            }
            var date = ( day ? ( typeof day === "object" ? day :
                    this._daylightSavingAdjust( new Date( year, month, day ) ) ) :
                this._daylightSavingAdjust( new Date( inst.currentYear, inst.currentMonth, inst.currentDay ) ) );
            return this.formatDate( this._get( inst, "dateFormat" ), date, this._getFormatConfig( inst ) );
        }
    } );

    /*
 * Bind hover events for datepicker elements.
 * Done via delegate so the binding only occurs once in the lifetime of the parent div.
 * Global datepicker_instActive, set by _updateDatepicker allows the handlers to find their way back to the active picker.
 */
    function datepicker_bindHover( dpDiv ) {
        var selector = "button, .ui-datepicker-prev, .ui-datepicker-next, .ui-datepicker-calendar td a";
        return dpDiv.on( "mouseout", selector, function() {
            $( this ).removeClass( "ui-state-hover" );
            if ( this.className.indexOf( "ui-datepicker-prev" ) !== -1 ) {
                $( this ).removeClass( "ui-datepicker-prev-hover" );
            }
            if ( this.className.indexOf( "ui-datepicker-next" ) !== -1 ) {
                $( this ).removeClass( "ui-datepicker-next-hover" );
            }
        } )
            .on( "mouseover", selector, datepicker_handleMouseover );
    }

    function datepicker_handleMouseover() {
        if ( !$.datepicker._isDisabledDatepicker( datepicker_instActive.inline ? datepicker_instActive.dpDiv.parent()[ 0 ] : datepicker_instActive.input[ 0 ] ) ) {
            $( this ).parents( ".ui-datepicker-calendar" ).find( "a" ).removeClass( "ui-state-hover" );
            $( this ).addClass( "ui-state-hover" );
            if ( this.className.indexOf( "ui-datepicker-prev" ) !== -1 ) {
                $( this ).addClass( "ui-datepicker-prev-hover" );
            }
            if ( this.className.indexOf( "ui-datepicker-next" ) !== -1 ) {
                $( this ).addClass( "ui-datepicker-next-hover" );
            }
        }
    }

    /* jQuery extend now ignores nulls! */
    function datepicker_extendRemove( target, props ) {
        $.extend( target, props );
        for ( var name in props ) {
            if ( props[ name ] == null ) {
                target[ name ] = props[ name ];
            }
        }
        return target;
    }

    /* Invoke the datepicker functionality.
   @param  options  string - a command, optionally followed by additional parameters or
					Object - settings for attaching new datepicker functionality
   @return  jQuery object */
    $.fn.datepicker = function( options ) {

        /* Verify an empty collection wasn't passed - Fixes #6976 */
        if ( !this.length ) {
            return this;
        }

        /* Initialise the date picker. */
        if ( !$.datepicker.initialized ) {
            $( document ).on( "mousedown", $.datepicker._checkExternalClick );
            $.datepicker.initialized = true;
        }

        /* Append datepicker main container to body if not exist. */
        if ( $( "#" + $.datepicker._mainDivId ).length === 0 ) {
            $( "body" ).append( $.datepicker.dpDiv );
        }

        var otherArgs = Array.prototype.slice.call( arguments, 1 );
        if ( typeof options === "string" && ( options === "isDisabled" || options === "getDate" || options === "widget" ) ) {
            return $.datepicker[ "_" + options + "Datepicker" ].
            apply( $.datepicker, [ this[ 0 ] ].concat( otherArgs ) );
        }
        if ( options === "option" && arguments.length === 2 && typeof arguments[ 1 ] === "string" ) {
            return $.datepicker[ "_" + options + "Datepicker" ].
            apply( $.datepicker, [ this[ 0 ] ].concat( otherArgs ) );
        }
        return this.each( function() {
            typeof options === "string" ?
                $.datepicker[ "_" + options + "Datepicker" ].
                apply( $.datepicker, [ this ].concat( otherArgs ) ) :
                $.datepicker._attachDatepicker( this, options );
        } );
    };

    $.datepicker = new Datepicker(); // singleton instance
    $.datepicker.initialized = false;
    $.datepicker.uuid = new Date().getTime();
    $.datepicker.version = "1.12.1";

    var widgetsDatepicker = $.datepicker;


    /*!
 * jQuery UI Slider 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */

//>>label: Slider
//>>group: Widgets
//>>description: Displays a flexible slider with ranges and accessibility via keyboard.
//>>docs: http://api.jqueryui.com/slider/
//>>demos: http://jqueryui.com/slider/
//>>css.structure: ../../themes/base/core.css
//>>css.structure: ../../themes/base/slider.css
//>>css.theme: ../../themes/base/theme.css



    var widgetsSlider = $.widget( "ui.slider", $.ui.mouse, {
        version: "1.12.1",
        widgetEventPrefix: "slide",

        options: {
            animate: false,
            classes: {
                "ui-slider": "ui-corner-all",
                "ui-slider-handle": "ui-corner-all",

                // Note: ui-widget-header isn't the most fittingly semantic framework class for this
                // element, but worked best visually with a variety of themes
                "ui-slider-range": "ui-corner-all ui-widget-header"
            },
            distance: 0,
            max: 100,
            min: 0,
            orientation: "horizontal",
            range: false,
            step: 1,
            value: 0,
            values: null,

            // Callbacks
            change: null,
            slide: null,
            start: null,
            stop: null
        },

        // Number of pages in a slider
        // (how many times can you page up/down to go through the whole range)
        numPages: 5,

        _create: function() {
            this._keySliding = false;
            this._mouseSliding = false;
            this._animateOff = true;
            this._handleIndex = null;
            this._detectOrientation();
            this._mouseInit();
            this._calculateNewMax();

            this._addClass( "ui-slider ui-slider-" + this.orientation,
                "ui-widget ui-widget-content" );

            this._refresh();

            this._animateOff = false;
        },

        _refresh: function() {
            this._createRange();
            this._createHandles();
            this._setupEvents();
            this._refreshValue();
        },

        _createHandles: function() {
            var i, handleCount,
                options = this.options,
                existingHandles = this.element.find( ".ui-slider-handle" ),
                handle = "<span tabindex='0'></span>",
                handles = [];

            handleCount = ( options.values && options.values.length ) || 1;

            if ( existingHandles.length > handleCount ) {
                existingHandles.slice( handleCount ).remove();
                existingHandles = existingHandles.slice( 0, handleCount );
            }

            for ( i = existingHandles.length; i < handleCount; i++ ) {
                handles.push( handle );
            }

            this.handles = existingHandles.add( $( handles.join( "" ) ).appendTo( this.element ) );

            this._addClass( this.handles, "ui-slider-handle", "ui-state-default" );

            this.handle = this.handles.eq( 0 );

            this.handles.each( function( i ) {
                $( this )
                    .data( "ui-slider-handle-index", i )
                    .attr( "tabIndex", 0 );
            } );
        },

        _createRange: function() {
            var options = this.options;

            if ( options.range ) {
                if ( options.range === true ) {
                    if ( !options.values ) {
                        options.values = [ this._valueMin(), this._valueMin() ];
                    } else if ( options.values.length && options.values.length !== 2 ) {
                        options.values = [ options.values[ 0 ], options.values[ 0 ] ];
                    } else if ( $.isArray( options.values ) ) {
                        options.values = options.values.slice( 0 );
                    }
                }

                if ( !this.range || !this.range.length ) {
                    this.range = $( "<div>" )
                        .appendTo( this.element );

                    this._addClass( this.range, "ui-slider-range" );
                } else {
                    this._removeClass( this.range, "ui-slider-range-min ui-slider-range-max" );

                    // Handle range switching from true to min/max
                    this.range.css( {
                        "left": "",
                        "bottom": ""
                    } );
                }
                if ( options.range === "min" || options.range === "max" ) {
                    this._addClass( this.range, "ui-slider-range-" + options.range );
                }
            } else {
                if ( this.range ) {
                    this.range.remove();
                }
                this.range = null;
            }
        },

        _setupEvents: function() {
            this._off( this.handles );
            this._on( this.handles, this._handleEvents );
            this._hoverable( this.handles );
            this._focusable( this.handles );
        },

        _destroy: function() {
            this.handles.remove();
            if ( this.range ) {
                this.range.remove();
            }

            this._mouseDestroy();
        },

        _mouseCapture: function( event ) {
            var position, normValue, distance, closestHandle, index, allowed, offset, mouseOverHandle,
                that = this,
                o = this.options;

            if ( o.disabled ) {
                return false;
            }

            this.elementSize = {
                width: this.element.outerWidth(),
                height: this.element.outerHeight()
            };
            this.elementOffset = this.element.offset();

            position = { x: event.pageX, y: event.pageY };
            normValue = this._normValueFromMouse( position );
            distance = this._valueMax() - this._valueMin() + 1;
            this.handles.each( function( i ) {
                var thisDistance = Math.abs( normValue - that.values( i ) );
                if ( ( distance > thisDistance ) ||
                    ( distance === thisDistance &&
                        ( i === that._lastChangedValue || that.values( i ) === o.min ) ) ) {
                    distance = thisDistance;
                    closestHandle = $( this );
                    index = i;
                }
            } );

            allowed = this._start( event, index );
            if ( allowed === false ) {
                return false;
            }
            this._mouseSliding = true;

            this._handleIndex = index;

            this._addClass( closestHandle, null, "ui-state-active" );
            closestHandle.trigger( "focus" );

            offset = closestHandle.offset();
            mouseOverHandle = !$( event.target ).parents().addBack().is( ".ui-slider-handle" );
            this._clickOffset = mouseOverHandle ? { left: 0, top: 0 } : {
                left: event.pageX - offset.left - ( closestHandle.width() / 2 ),
                top: event.pageY - offset.top -
                    ( closestHandle.height() / 2 ) -
                    ( parseInt( closestHandle.css( "borderTopWidth" ), 10 ) || 0 ) -
                    ( parseInt( closestHandle.css( "borderBottomWidth" ), 10 ) || 0 ) +
                    ( parseInt( closestHandle.css( "marginTop" ), 10 ) || 0 )
            };

            if ( !this.handles.hasClass( "ui-state-hover" ) ) {
                this._slide( event, index, normValue );
            }
            this._animateOff = true;
            return true;
        },

        _mouseStart: function() {
            return true;
        },

        _mouseDrag: function( event ) {
            var position = { x: event.pageX, y: event.pageY },
                normValue = this._normValueFromMouse( position );

            this._slide( event, this._handleIndex, normValue );

            return false;
        },

        _mouseStop: function( event ) {
            this._removeClass( this.handles, null, "ui-state-active" );
            this._mouseSliding = false;

            this._stop( event, this._handleIndex );
            this._change( event, this._handleIndex );

            this._handleIndex = null;
            this._clickOffset = null;
            this._animateOff = false;

            return false;
        },

        _detectOrientation: function() {
            this.orientation = ( this.options.orientation === "vertical" ) ? "vertical" : "horizontal";
        },

        _normValueFromMouse: function( position ) {
            var pixelTotal,
                pixelMouse,
                percentMouse,
                valueTotal,
                valueMouse;

            if ( this.orientation === "horizontal" ) {
                pixelTotal = this.elementSize.width;
                pixelMouse = position.x - this.elementOffset.left -
                    ( this._clickOffset ? this._clickOffset.left : 0 );
            } else {
                pixelTotal = this.elementSize.height;
                pixelMouse = position.y - this.elementOffset.top -
                    ( this._clickOffset ? this._clickOffset.top : 0 );
            }

            percentMouse = ( pixelMouse / pixelTotal );
            if ( percentMouse > 1 ) {
                percentMouse = 1;
            }
            if ( percentMouse < 0 ) {
                percentMouse = 0;
            }
            if ( this.orientation === "vertical" ) {
                percentMouse = 1 - percentMouse;
            }

            valueTotal = this._valueMax() - this._valueMin();
            valueMouse = this._valueMin() + percentMouse * valueTotal;

            return this._trimAlignValue( valueMouse );
        },

        _uiHash: function( index, value, values ) {
            var uiHash = {
                handle: this.handles[ index ],
                handleIndex: index,
                value: value !== undefined ? value : this.value()
            };

            if ( this._hasMultipleValues() ) {
                uiHash.value = value !== undefined ? value : this.values( index );
                uiHash.values = values || this.values();
            }

            return uiHash;
        },

        _hasMultipleValues: function() {
            return this.options.values && this.options.values.length;
        },

        _start: function( event, index ) {
            return this._trigger( "start", event, this._uiHash( index ) );
        },

        _slide: function( event, index, newVal ) {
            var allowed, otherVal,
                currentValue = this.value(),
                newValues = this.values();

            if ( this._hasMultipleValues() ) {
                otherVal = this.values( index ? 0 : 1 );
                currentValue = this.values( index );

                if ( this.options.values.length === 2 && this.options.range === true ) {
                    newVal =  index === 0 ? Math.min( otherVal, newVal ) : Math.max( otherVal, newVal );
                }

                newValues[ index ] = newVal;
            }

            if ( newVal === currentValue ) {
                return;
            }

            allowed = this._trigger( "slide", event, this._uiHash( index, newVal, newValues ) );

            // A slide can be canceled by returning false from the slide callback
            if ( allowed === false ) {
                return;
            }

            if ( this._hasMultipleValues() ) {
                this.values( index, newVal );
            } else {
                this.value( newVal );
            }
        },

        _stop: function( event, index ) {
            this._trigger( "stop", event, this._uiHash( index ) );
        },

        _change: function( event, index ) {
            if ( !this._keySliding && !this._mouseSliding ) {

                //store the last changed value index for reference when handles overlap
                this._lastChangedValue = index;
                this._trigger( "change", event, this._uiHash( index ) );
            }
        },

        value: function( newValue ) {
            if ( arguments.length ) {
                this.options.value = this._trimAlignValue( newValue );
                this._refreshValue();
                this._change( null, 0 );
                return;
            }

            return this._value();
        },

        values: function( index, newValue ) {
            var vals,
                newValues,
                i;

            if ( arguments.length > 1 ) {
                this.options.values[ index ] = this._trimAlignValue( newValue );
                this._refreshValue();
                this._change( null, index );
                return;
            }

            if ( arguments.length ) {
                if ( $.isArray( arguments[ 0 ] ) ) {
                    vals = this.options.values;
                    newValues = arguments[ 0 ];
                    for ( i = 0; i < vals.length; i += 1 ) {
                        vals[ i ] = this._trimAlignValue( newValues[ i ] );
                        this._change( null, i );
                    }
                    this._refreshValue();
                } else {
                    if ( this._hasMultipleValues() ) {
                        return this._values( index );
                    } else {
                        return this.value();
                    }
                }
            } else {
                return this._values();
            }
        },

        _setOption: function( key, value ) {
            var i,
                valsLength = 0;

            if ( key === "range" && this.options.range === true ) {
                if ( value === "min" ) {
                    this.options.value = this._values( 0 );
                    this.options.values = null;
                } else if ( value === "max" ) {
                    this.options.value = this._values( this.options.values.length - 1 );
                    this.options.values = null;
                }
            }

            if ( $.isArray( this.options.values ) ) {
                valsLength = this.options.values.length;
            }

            this._super( key, value );

            switch ( key ) {
                case "orientation":
                    this._detectOrientation();
                    this._removeClass( "ui-slider-horizontal ui-slider-vertical" )
                        ._addClass( "ui-slider-" + this.orientation );
                    this._refreshValue();
                    if ( this.options.range ) {
                        this._refreshRange( value );
                    }

                    // Reset positioning from previous orientation
                    this.handles.css( value === "horizontal" ? "bottom" : "left", "" );
                    break;
                case "value":
                    this._animateOff = true;
                    this._refreshValue();
                    this._change( null, 0 );
                    this._animateOff = false;
                    break;
                case "values":
                    this._animateOff = true;
                    this._refreshValue();

                    // Start from the last handle to prevent unreachable handles (#9046)
                    for ( i = valsLength - 1; i >= 0; i-- ) {
                        this._change( null, i );
                    }
                    this._animateOff = false;
                    break;
                case "step":
                case "min":
                case "max":
                    this._animateOff = true;
                    this._calculateNewMax();
                    this._refreshValue();
                    this._animateOff = false;
                    break;
                case "range":
                    this._animateOff = true;
                    this._refresh();
                    this._animateOff = false;
                    break;
            }
        },

        _setOptionDisabled: function( value ) {
            this._super( value );

            this._toggleClass( null, "ui-state-disabled", !!value );
        },

        //internal value getter
        // _value() returns value trimmed by min and max, aligned by step
        _value: function() {
            var val = this.options.value;
            val = this._trimAlignValue( val );

            return val;
        },

        //internal values getter
        // _values() returns array of values trimmed by min and max, aligned by step
        // _values( index ) returns single value trimmed by min and max, aligned by step
        _values: function( index ) {
            var val,
                vals,
                i;

            if ( arguments.length ) {
                val = this.options.values[ index ];
                val = this._trimAlignValue( val );

                return val;
            } else if ( this._hasMultipleValues() ) {

                // .slice() creates a copy of the array
                // this copy gets trimmed by min and max and then returned
                vals = this.options.values.slice();
                for ( i = 0; i < vals.length; i += 1 ) {
                    vals[ i ] = this._trimAlignValue( vals[ i ] );
                }

                return vals;
            } else {
                return [];
            }
        },

        // Returns the step-aligned value that val is closest to, between (inclusive) min and max
        _trimAlignValue: function( val ) {
            if ( val <= this._valueMin() ) {
                return this._valueMin();
            }
            if ( val >= this._valueMax() ) {
                return this._valueMax();
            }
            var step = ( this.options.step > 0 ) ? this.options.step : 1,
                valModStep = ( val - this._valueMin() ) % step,
                alignValue = val - valModStep;

            if ( Math.abs( valModStep ) * 2 >= step ) {
                alignValue += ( valModStep > 0 ) ? step : ( -step );
            }

            // Since JavaScript has problems with large floats, round
            // the final value to 5 digits after the decimal point (see #4124)
            return parseFloat( alignValue.toFixed( 5 ) );
        },

        _calculateNewMax: function() {
            var max = this.options.max,
                min = this._valueMin(),
                step = this.options.step,
                aboveMin = Math.round( ( max - min ) / step ) * step;
            max = aboveMin + min;
            if ( max > this.options.max ) {

                //If max is not divisible by step, rounding off may increase its value
                max -= step;
            }
            this.max = parseFloat( max.toFixed( this._precision() ) );
        },

        _precision: function() {
            var precision = this._precisionOf( this.options.step );
            if ( this.options.min !== null ) {
                precision = Math.max( precision, this._precisionOf( this.options.min ) );
            }
            return precision;
        },

        _precisionOf: function( num ) {
            var str = num.toString(),
                decimal = str.indexOf( "." );
            return decimal === -1 ? 0 : str.length - decimal - 1;
        },

        _valueMin: function() {
            return this.options.min;
        },

        _valueMax: function() {
            return this.max;
        },

        _refreshRange: function( orientation ) {
            if ( orientation === "vertical" ) {
                this.range.css( { "width": "", "left": "" } );
            }
            if ( orientation === "horizontal" ) {
                this.range.css( { "height": "", "bottom": "" } );
            }
        },

        _refreshValue: function() {
            var lastValPercent, valPercent, value, valueMin, valueMax,
                oRange = this.options.range,
                o = this.options,
                that = this,
                animate = ( !this._animateOff ) ? o.animate : false,
                _set = {};

            if ( this._hasMultipleValues() ) {
                this.handles.each( function( i ) {
                    valPercent = ( that.values( i ) - that._valueMin() ) / ( that._valueMax() -
                        that._valueMin() ) * 100;
                    _set[ that.orientation === "horizontal" ? "left" : "bottom" ] = valPercent + "%";
                    $( this ).stop( 1, 1 )[ animate ? "animate" : "css" ]( _set, o.animate );
                    if ( that.options.range === true ) {
                        if ( that.orientation === "horizontal" ) {
                            if ( i === 0 ) {
                                that.range.stop( 1, 1 )[ animate ? "animate" : "css" ]( {
                                    left: valPercent + "%"
                                }, o.animate );
                            }
                            if ( i === 1 ) {
                                that.range[ animate ? "animate" : "css" ]( {
                                    width: ( valPercent - lastValPercent ) + "%"
                                }, {
                                    queue: false,
                                    duration: o.animate
                                } );
                            }
                        } else {
                            if ( i === 0 ) {
                                that.range.stop( 1, 1 )[ animate ? "animate" : "css" ]( {
                                    bottom: ( valPercent ) + "%"
                                }, o.animate );
                            }
                            if ( i === 1 ) {
                                that.range[ animate ? "animate" : "css" ]( {
                                    height: ( valPercent - lastValPercent ) + "%"
                                }, {
                                    queue: false,
                                    duration: o.animate
                                } );
                            }
                        }
                    }
                    lastValPercent = valPercent;
                } );
            } else {
                value = this.value();
                valueMin = this._valueMin();
                valueMax = this._valueMax();
                valPercent = ( valueMax !== valueMin ) ?
                    ( value - valueMin ) / ( valueMax - valueMin ) * 100 :
                    0;
                _set[ this.orientation === "horizontal" ? "left" : "bottom" ] = valPercent + "%";
                this.handle.stop( 1, 1 )[ animate ? "animate" : "css" ]( _set, o.animate );

                if ( oRange === "min" && this.orientation === "horizontal" ) {
                    this.range.stop( 1, 1 )[ animate ? "animate" : "css" ]( {
                        width: valPercent + "%"
                    }, o.animate );
                }
                if ( oRange === "max" && this.orientation === "horizontal" ) {
                    this.range.stop( 1, 1 )[ animate ? "animate" : "css" ]( {
                        width: ( 100 - valPercent ) + "%"
                    }, o.animate );
                }
                if ( oRange === "min" && this.orientation === "vertical" ) {
                    this.range.stop( 1, 1 )[ animate ? "animate" : "css" ]( {
                        height: valPercent + "%"
                    }, o.animate );
                }
                if ( oRange === "max" && this.orientation === "vertical" ) {
                    this.range.stop( 1, 1 )[ animate ? "animate" : "css" ]( {
                        height: ( 100 - valPercent ) + "%"
                    }, o.animate );
                }
            }
        },

        _handleEvents: {
            keydown: function( event ) {
                var allowed, curVal, newVal, step,
                    index = $( event.target ).data( "ui-slider-handle-index" );

                switch ( event.keyCode ) {
                    case $.ui.keyCode.HOME:
                    case $.ui.keyCode.END:
                    case $.ui.keyCode.PAGE_UP:
                    case $.ui.keyCode.PAGE_DOWN:
                    case $.ui.keyCode.UP:
                    case $.ui.keyCode.RIGHT:
                    case $.ui.keyCode.DOWN:
                    case $.ui.keyCode.LEFT:
                        event.preventDefault();
                        if ( !this._keySliding ) {
                            this._keySliding = true;
                            this._addClass( $( event.target ), null, "ui-state-active" );
                            allowed = this._start( event, index );
                            if ( allowed === false ) {
                                return;
                            }
                        }
                        break;
                }

                step = this.options.step;
                if ( this._hasMultipleValues() ) {
                    curVal = newVal = this.values( index );
                } else {
                    curVal = newVal = this.value();
                }

                switch ( event.keyCode ) {
                    case $.ui.keyCode.HOME:
                        newVal = this._valueMin();
                        break;
                    case $.ui.keyCode.END:
                        newVal = this._valueMax();
                        break;
                    case $.ui.keyCode.PAGE_UP:
                        newVal = this._trimAlignValue(
                            curVal + ( ( this._valueMax() - this._valueMin() ) / this.numPages )
                        );
                        break;
                    case $.ui.keyCode.PAGE_DOWN:
                        newVal = this._trimAlignValue(
                            curVal - ( ( this._valueMax() - this._valueMin() ) / this.numPages ) );
                        break;
                    case $.ui.keyCode.UP:
                    case $.ui.keyCode.RIGHT:
                        if ( curVal === this._valueMax() ) {
                            return;
                        }
                        newVal = this._trimAlignValue( curVal + step );
                        break;
                    case $.ui.keyCode.DOWN:
                    case $.ui.keyCode.LEFT:
                        if ( curVal === this._valueMin() ) {
                            return;
                        }
                        newVal = this._trimAlignValue( curVal - step );
                        break;
                }

                this._slide( event, index, newVal );
            },
            keyup: function( event ) {
                var index = $( event.target ).data( "ui-slider-handle-index" );

                if ( this._keySliding ) {
                    this._keySliding = false;
                    this._stop( event, index );
                    this._change( event, index );
                    this._removeClass( $( event.target ), null, "ui-state-active" );
                }
            }
        }
    } );




}));