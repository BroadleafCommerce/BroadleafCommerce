// AIRMenuBuilder version 1.0

// Copyright 2007-2008 Adobe Systems Incorporated.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//   * Redistributions of source code must retain the above copyright notice,
//     this list of conditions and the following disclaimer.
//   * Redistributions in binary form must reproduce the above copyright notice,
//     this list of conditions and the following disclaimer in the documentation
//     and/or other materials provided with the distribution.
//   * Neither the name of Adobe Systems Incorporated nor the names of its
//     contributors may be used to endorse or promote products derived from this
//     software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

(function AIRMenuBuilder () {
	
	function constructor ( ) {
		window ['air'] = window ['air'] || {};
		window.air['ui'] = window.air['ui'] || {};
		window.air.ui['Menu'] = new Menu();
		registry = new FieldsRegistry();
		currentOS = runtime.flash.system.Capabilities.os;
	}
	
	var currentOS = null;
	var registry  = null;
	
	var File = runtime.flash.filesystem.File;
	var FileStream = runtime.flash.filesystem.FileStream;
	var FileMode = runtime.flash.filesystem.FileMode;
	var NativeMenu = runtime.flash.display.NativeMenu;
	var NativeMenuItem = runtime.flash.display.NativeMenuItem;
	var SELECT = runtime.flash.events.Event.SELECT;
	var KEYBOARD = runtime.flash.ui.Keyboard;
	var COMPLETE = runtime.flash.events.Event.COMPLETE;
	var IO_ERROR = runtime.flash.events.IOErrorEvent.IO_ERROR;
    var NativeApplication = runtime.flash.desktop.NativeApplication;
    var NativeWindow = runtime.flash.display.NativeWindow;
    var Loader = runtime.flash.display.Loader;
    var URLRequest = runtime.flash.net.URLRequest;
    var BitmapData = runtime.flash.display.BitmapData;
	
	/**
	 * CLASS FieldsRegistry
	 * @class
	 * @private
	 */
	function FieldsRegistry () {
		
		this.proof  = function (name, value) {
			if (!validateName(name)) { return null };
			switch (name) {
				case FieldsRegistry.ENABLED:
				case FieldsRegistry.ALT_KEY:
				case FieldsRegistry.SHIFT_KEY:
				case FieldsRegistry.CMD_KEY:
				case FieldsRegistry.CTRL_KEY:
				case FieldsRegistry.TOGGLED:
				case FieldsRegistry.DEFAULT_KEY:
					return (typeof value == 'boolean')? 
						value: (typeof value == 'string')? 
						(value.toLowerCase() == 'false')? false : 
						true : getDefault (name);
				case FieldsRegistry.KEY_EQUIVALENT:
					var d;
					return (typeof value == 'string')?
						(value.length == 1)? value : 
						getDefault (name) : getDefault (name);
				case FieldsRegistry.LABEL:
					return (typeof value == 'string')?
					(value.length != 0)? value: 
					getDefault (name) : getDefault (name);
				case FieldsRegistry.MNEMONIC_INDEX:
					var n;
					return (typeof value == 'number')? 
						value: (typeof value == 'string')?
						(!isNaN ( n = parseInt(value) ))? n : 
						getDefault (name) : getDefault (name);
				case FieldsRegistry.TYPE:
					return (typeof value == 'string') ?
						(validateType(value))? value :
						getDefault (name) : getDefault (name);
				case FieldsRegistry.ON_SELECT:
					var f;
					return (typeof value == 'function')?
						value : (typeof value == 'string')?
						(typeof (f = window[value]) == 'function')?
						f : getDefault (name) : getDefault (name);
			}
		}
		
		this.iterateFields = function (callback, scope) {
			var f, n, fr = FieldsRegistry;
			for (f in fr) {
				n = fr [f] !== fr.prototype? fr [f] : null;
				if (n && !validateType(n))  { 
					callback.call ( scope || window, n )
				};
			}
		}
		
		var validateType = function (type) {
			return type == FieldsRegistry.REGULAR ||
				type == FieldsRegistry.SEPARATOR ||
				type == FieldsRegistry.CHECK;
		}
		
		var validateName = function (fieldName) {
			for (var f in FieldsRegistry) {
				if (FieldsRegistry[f] == fieldName) { return true };
			}
			return false;
		}
		
		
		var getDefault = function (fieldName) {
			switch (fieldName) {
				case FieldsRegistry.ALT_KEY:
				case FieldsRegistry.SHIFT_KEY:
				case FieldsRegistry.TOGGLED:
					return false;
				case FieldsRegistry.ENABLED:
				case FieldsRegistry.DEFAULT_KEY:
					return true;
				case FieldsRegistry.KEY_EQUIVALENT:
				case FieldsRegistry.ON_SELECT:
					return null;
				case FieldsRegistry.LABEL:
					return ' ';
				case FieldsRegistry.MNEMONIC_INDEX:
					return -1;
				case FieldsRegistry.TYPE:
					return FieldsRegistry.REGULAR;
				case FieldsRegistry.CMD_KEY:	
				case FieldsRegistry.CTRL_KEY:
				default:
					return null;
			}
		}
		
	}
	FieldsRegistry.ALT_KEY = 'altKey';
	FieldsRegistry.CMD_KEY = 'cmdKey';
	FieldsRegistry.CTRL_KEY = 'ctrlKey';
	FieldsRegistry.ENABLED = 'enabled';
	FieldsRegistry.KEY_EQUIVALENT = 'keyEquivalent';
	FieldsRegistry.LABEL = 'label';
	FieldsRegistry.MNEMONIC_INDEX = 'mnemonicIndex';
	FieldsRegistry.SHIFT_KEY = 'shiftKey';
	FieldsRegistry.TOGGLED = 'toggled';
	FieldsRegistry.TYPE = 'type';
	FieldsRegistry.ON_SELECT = 'onSelect';
	FieldsRegistry.DEFAULT_KEY = 'defaultKeyEquivalentModifiers';
	FieldsRegistry.SEPARATOR = 'separator';
	FieldsRegistry.CHECK = 'check';
	FieldsRegistry.REGULAR = 'regular';
	
	/**
	 * CLASS Menu
	 * Description
	 * 		Loads a user menu defined as XML or JSON, and sets it as one of the
	 * 		supported menu types.
	 * @class
	 * @author ciacob
	 */
	function Menu() {
		
		var buildMenu = function (source, type) {
			var b = new Builder();
			b.loadData (source, type);
			return b.build();
		}
		
		var attachMenu = function (menu, type, target, icons) {
			var s = new Shell();
			s.link(menu, type, target, icons);
		}
		
		/**
		 * Load a menu defined in XML format.
		 * @param source
		 * 		An object containing XML menu(s) to be loaded for various OS-es.
		 * @return
		 * 		A NativeMenu object built from the given XML source.
		 */
		this.createFromXML = function ( source ) {
			return buildMenu ( source, Builder.XML );
		}
		
		/**
		 * Same as air.ui.Menu.fromXML, except it handles JSON data.
		 */
		this.createFromJSON = function ( source ) {
			return buildMenu ( source, Builder.JSON );
		}
		
		/**
		 * - on Windows: sets the given nativeMenu object as the NativeWindow's 
		 *   menu;
		 * - on Mac: inserts the items of the given nativeMenu object between 
		 *   the 'Edit' and 'Window' default menus;
		 * @param nativeMenu
		 * 		A NativeMenu returned by one of the air.ui.Menu.from... 
		 * 		functions.
		 * @param overwrite
		 * 		A boolean that will change the behavior on Mac. If true, the 
		 * 		default menus will be replaced entirely by the given nativeMenu
		 */
		this.setAsMenu = function ( nativeMenu, overwrite ) {
			if (!arguments.length) {
				throw (new Error( 
					"No argument given for the 'setAsMenu()' method."
				));
			}
			var style = overwrite? Shell.MENU | Shell.OVERWRITE : Shell.MENU;
			attachMenu (nativeMenu, style);
		}
		
		/**
		 * Displays the given menu as a contextual menu when the user right 
		 * clicks a certain DOM element.
		 * @param nativeMenu
		 * 		A NativeMenu returned by one of the air.ui.Menu.from... 
		 * 		functions.
		 * @param domElement
		 * 		The DOM Element to link with the given nativeMenu. The 
		 * 		contextual menu will only show when the user right clicks over 
		 * 		domElement. This attribute is optional. If missing, the context
		 * 		menu will display on every right-click over the application.
		 */
		this.setAsContextMenu = function ( nativeMenu, domElement ) {
			if (!arguments.length) {
				throw (new Error( 
					"No argument given for the 'setAsContextMenu()' method."
				));
			}
			if (arguments.length < 2) { domElement = Shell.UNSPECIFIED };
			attachMenu (nativeMenu, Shell.CONTEXT, domElement);
		}
		
		/**
		 * Sets the given nativeMenu as the 
		 * ''NativeApplication.nativeApplication.icon.menu'' property.
		 * @param nativeMenu
		 * 		A NativeMenu returned by one of the air.ui.Menu.from... 
		 * 		functions.
		 * @param icons
		 * 		An array holding icon file paths or bitmap data objects.
		 * 		If specified, these will be used as the application's
		 * 		tray/dock icons.
		 * @throws
		 * 		If no bitmap data was set for the ''icon'' object and no default
		 * 		icons are specified in the application descriptor.
		 */
		this.setAsIconMenu = function ( nativeMenu, icons ) {
			if (!arguments.length) {
				throw (new Error( 
					"No argument given for the 'setAsIconMenu()' method."
				));
			}
			attachMenu (nativeMenu, Shell.ICON, null, icons);
		}
		
	}
	
	/**
	 * CLASS DataSource
	 * @public
	 * @abstract
	 */
	function DataSource() {
		var _this = this;
		
		var legalExtensions = ['xml', 'js'];
		
		var rSeed = null;
		
		var DATA_OBJECT = 1;
		var INLINE_STRING = 2;
		var FILE_PATH = 3;
		var FILE_OBJECT = 4;
		var ILLEGAL_TYPE = 5;
		
		function getFileContent (file) {
	    	var ret = '';
	        var fileStream = new FileStream();
	        fileStream.open(file, FileMode.READ);
		    try {
	            ret = fileStream.readUTFBytes(file.size);
		    } catch(e) {
		    	throw(
		    		new Error(["Error\n", "ID: ", e.errorID, "\n", "Message: ",
		    			e.message, "\n"].join(''))
		    	);
		    }
	        fileStream.close();
	        return ret;
		}
		
	    function checkExtension (url, whiteList) {
	        var match = url.match(/\.([^\.]*)$/);
	        var extension = match? match[1] : null;
	        for(var i=0; i<whiteList.length; i++) {
	            if (whiteList[i] == extension) {
	                return true;
	            }
	        }
	        return false;
	    }
	    
	    function sniffSource(src) {
	    	if (typeof src == "object") {
	    		if (src.constructor === (new File()).constructor) {
	    			return FILE_OBJECT;
	    		}
	    		if (src.nodeType && src.nodeType == src.DOCUMENT_NODE) {
		    		return DATA_OBJECT;
	    		}
	    	}
	    	if (typeof src == "string") {
	    		if (checkExtension(src, legalExtensions)) {
	    			return FILE_PATH;
	    		}
	    		return INLINE_STRING;
	    	}
	    	return ILLEGAL_TYPE;
	    }
	    
	    this.document = null;
	    
	    this.type = null;
	    
		this.$DataSource = function(rawSource) {
			if(rawSource) {
				var srcType = sniffSource(rawSource);
				if (srcType == ILLEGAL_TYPE) {
					throw (new Error(['Could not instantiate DataSource class:',
						'An illegal data was provided. Legal types are:',
						'- JavaScript Object',
						'- Inline JSON or XML String',
						'- *.XML or *.JS app root-relative file path',
						'- flash.filesystem.File object, pointing to the above' 
					].join('\n')));
				}
				var parsableCnt = _this.getParsableContent(rawSource, srcType);
				if (srcType != DATA_OBJECT) {
					this.parseContent(parsableCnt);
				} else {
					this.document = parsableCnt;
				}
			} else {
				throw (new Error(['Could not instantiate DataSource class:',
					'Data provided is null.'].join(' ')));
			}
		}
		
		this.getParsableContent = function (rawSource, sourceType) {
			var url = null;
			switch (sourceType) {
				case DATA_OBJECT:
				case INLINE_STRING:
					return rawSource;
				case FILE_OBJECT:
					url = rawSource.url;
				case FILE_PATH:
					if (!url) { url = rawSource };
					var localFile = Shell.resolve(url);
					if (!localFile.exists) {
						throw (new Error([
						'Could not instantiate DataSource class.',
						'Could not resolve this path:', url
						].join('\n')));
						return null;
					}
					var cnt = getFileContent(localFile);
					return cnt;
			}
		}
		
		this.generateUID = function() {
			if (!rSeed) {
				var r = Math.floor(Math.random() * 1e5);
				rSeed = r;
				return ['id', r].join('');
			}
			var add = Math.floor(Math.random() * 10) + 1;
			rSeed += add;
			return ['id', rSeed].join('');
		}
		
		this.getSummary = function (node) {
			var ret = {};
			var func = function (fieldName) {
				ret[fieldName] = this.getProperty (node, fieldName);
			}
			registry.iterateFields (func, this);
			return ret;
		}
		
		this.parseContent = function (content) {
			// subclass must overwrite;
		}
	
		this.getRoot = function() {
			// subclass must overwrite;
		}
		
		this.getChildren = function(node) {
			// subclass must overwrite;
		}
		
		this.getNextSibling = function(node) {
			// subclass must overwrite;
		}
		
		this.getParent = function(node) {
			// subclass must overwrite;
		}
		
		this.hasChildren = function(node) {
			// subclass must overwrite;
		}
		
		this.addChildAt = function(node, newChild, index) {
			// subclass must overwrite;
		}
		
		this.removeChildAt = function(node, index) {
			// subclass must overwrite;
		}
	
		this.createNode = function(node, index) {
			// subclass must overwrite;		
		}
			
		this.getProperty = function(node, propName) {
			// subclass must overwrite;
		}
		
		this.setProperty = function(node, propName, propValue) {
			// subclass must overwrite;
		}
	}
	
	/**
	 * CLASS XMLDataSource inherits DataSource
	 * @private
	 * @class
	 */
	function XMLDataSource() {
		
		this.__proto__ = new DataSource();
	
		this.$XMLDataSource = function (rawSource) {
			this.__proto__.$DataSource.call (this.__proto__, rawSource);
			that.type = Builder.XML;
		}
		
		var that = this.__proto__;
		
		that.parseContent = function (content) {
	    	if (content) {
	    		var p = new DOMParser();
	    		var doc = p.parseFromString(content, "text/xml");
	    		var err = 'parsererror';
	    		var r = doc.documentElement;
	    		var isError = (r.nodeName == err) ||
	    			(doc.getElementsByTagName(err).length > 0);
	    		if (isError) {
	    			var errText = doc.getElementsByTagName(err)[0].innerText;
	    			var msg = errText.split(':');
	    			msg.length -= 1;
	    			msg = msg.join(':\n');
	    			throw (new Error ([
	    				'Could not parse data: malformed XML file.', msg
	    			].join('\n')));
	    		}
	    		that.document = doc;
	    	}
		}
	
		that.getRoot = function() {
			return that.document.documentElement;
		}
		
		that.getChildren = function (node) {
	    	var ret = [];
	    	if(node) {
	    		if(node.hasChildNodes && node.hasChildNodes()){
	    			var children = node.childNodes;
	    			for(var i=0; i<children.length; i++) {
	    				var child = children.item(i);
						if(child.nodeType == child.ELEMENT_NODE) {
							if (that.getProperty (child, 'id') == null) {
								that.setProperty (child, 'id', 
									that.generateUID());
							}
                            ret.push(child);
	                    }
	    			}
	    		}
	    	}
	        return ret;
		}
		
		that.getNextSibling = function (node) {
	        if(node) {
	        	var checkIfLegalType = function(el) {
	                return (el.nodeType == el.ELEMENT_NODE);
	        	}
	            var isLegalType = checkIfLegalType(node);
	            if(isLegalType) {
	            	var testNode = node;
	            	while(testNode = testNode.nextSibling) {
	            		var isNextLegal = checkIfLegalType(testNode);
	            		if(isNextLegal) { return testNode };
	            	}
	            }
	        }
	        return null;
		}
		
		that.getParent = function (node) {
	    	if(node) {
	    		var isLegalType = (node.nodeType == node.ELEMENT_NODE);
	    		if(isLegalType) {
	    			if (node === that.getRoot()) { return null };
	    			// make it headless, to accommodate the JSON
	    			if (node.parentNode === that.getRoot()) { return null };
	    			return node.parentNode;
	    		}
	    	}
	    	return null;
		}
		
		that.hasChildren = function (node) {
	    	if (node && (node.nodeType == node.ELEMENT_NODE)) {
		    	if(!node.hasChildNodes()) { return false };
		    	var childElements = node.getElementsByTagName('*');
			    if (childElements.length) { return true };
	    	}
	    	return null;
		}
		
		that.addChildAt = function (node, newChild, index) {
			if (node && newChild && (typeof index != "undefined")) {
				var nodeIsLegal = node.nodeType &&
					(node.nodeType == node.ELEMENT_NODE);
				var newIsLegal = newChild.nodeType &&
					(newChild.nodeType == newChild.ELEMENT_NODE);
				var indexIsLegal = !isNaN(parseInt(index));
				if (nodeIsLegal && newIsLegal && indexIsLegal) {
					var children = that.getChildren(node);
					index = Math.min(Math.max(0, index), children.length);
					var refNode = children [index+1] || null;
					var success = false;
					try {
						node.insertBefore (newChild, refNode);
						success = true;
					} catch (e) {
						throw ( new Error([
							'Could not add new child. A DOM error has occured:',
							e.message
						].join('\n')) );
					}
					return success;
				}
			}
	    	return null;
		}
		
		that.removeChildAt = function (node, index) {
			if (node && (typeof index!= "undefined")) {
				var nodeIsLegal = node.nodeType &&
					(node.nodeType == node.ELEMENT_NODE);
				var indexIsLegal = !isNaN(parseInt(index));
				if (nodeIsLegal && indexIsLegal) {
		        	var children = that.getChildren(node);
					index = Math.min(Math.max(0, index), children.length-1);
					try {
						return node.removeChild (children[index]);
					} catch (e) {
						throw ( new Error([
							'Could not remove child. A DOM error has occured:',
							e.message
						].join('\n')) );
					}
				}
			}
	        return null;
		}
		
		that.createNode = function (properties) {
			var node = that.document.createElement('menuItem');
			for (var p in properties) {
				that.setProperty (node, p, properties[p])
			}
			if (that.getProperty (node, 'id') == null) {
				that.setProperty (node, 'id', that.generateUID());
			}
			return node;
		}
		
		
		
		that.getProperty = function (node, propName) {
			if (node) {
				var nodeIsLegal = node.nodeType && 
					(node.nodeType == node.ELEMENT_NODE);
				if (nodeIsLegal) {
					return registry.proof(propName, node.getAttribute(propName));
				}
			}
			return null;
		}
		
		that.setProperty = function (node, propName, propValue) {
			if (node) {
				var nodeIsLegal = node.nodeType && 
					(node.nodeType == node.ELEMENT_NODE);
				if (nodeIsLegal) {
					var val = registry.proof(propName, propValue);
					node.setAttribute(propName, val);
				}
			}
		}
		
		this.$XMLDataSource.apply (this, arguments);
	}

	/**
	 * CLASS JSONDataSource inherits DataSource
	 * @private
	 * @class
	 */
	function JSONDataSource() {
		
		this.__proto__ = new DataSource();
		
		this.$JSONDataSource = function (rawSource) {
			this.__proto__.$DataSource.call (this.__proto__, rawSource);
			that.type = Builder.XML;
		}
		
		var that = this.__proto__;
		
		that.parseContent = function (content) {
			var doc = null;
			if(content) {
				try {
					doc = eval(content);
					this.document = doc;
				} catch (e) {
					var specificErr = null;
					if (e instanceof ReferenceError) {
						specificErr = [
						  'Unknown reference given.',
					      'Common mistakes include specifying non-global',
					      'function names for the onSelect field.'].join('\n');
					} else if (e instanceof SyntaxError) {
						specificErr = "Your JSON string is malformed";
					}
					var err = [
						e.message,
						['on line:', e.line].join(' ')
					];
					if(specificErr) {
						err.reverse();
						err.push(specificErr);
						err.reverse();
					}
					throw (new Error(err.join('\n')));
				}
			}
		}
		
		that.getRoot = function() {
			return that.document;
		}
		
		that.getChildren = function (node) {
	    	var ret = [];
			if (node) {
				var iterable = (node === that.getRoot())? node:
					(node ['items'])? node ['items']: null;
				if (iterable) {
					var par = (node === that.getRoot())? null: node;
					for (var i=0; i<iterable.length; i++) {
						var child = iterable[i];
						if (that.getProperty (child, 'id') == null) {
							that.setProperty (child, 'id', that.generateUID())};
						ret[i] = child;
						child['parent'] = par;
						if (i > 0) {
							var prev = iterable[i-1];
							prev['nextSibling'] = child;
						}
					}
				}
			}
			return ret;
		}
		
		that.getNextSibling = function (node) {
			if (node) {
				if(node !== that.getRoot()) {
					if (node['nextSibling']) { return node['nextSibling'] };
				}
			}
			return null;
		}
		
		that.getParent = function (node) {
			if (node) {
				if(node !== that.getRoot()) {
					if (node['parent']) { return node['parent'] };
				}
			}
			return null;
		}
		
		that.hasChildren = function (node) {
			if (node) {
				var iterable = (node === that.getRoot())? node:
					(node ['items'])? node ['items']: null;
				if (iterable) {
					return iterable.length && iterable.length > 0;
				}
				return false;
			}
			return false;
		}
		
		that.addChildAt = function (node, newChild, index) {
			if (node && newChild) {
				var children = that.getChildren (node) || (function() {
					node['items'] = [];
					return node['items'];
				})();
				index = Math.min(Math.max(0, index), children.length);
				children.splice (index, 0, newChild);
				if (index > 0) {	
					children [index-1]['nextSibling'] = children [index]
				};
				if (index < children.length-1) {
					children[index]['nextSibling'] = children [index]+1
				};
				node['items'] = children;
			}
		}
		
		that.removeChildAt = function (node, index) {
			if (node) {
				var children = that.getChildren (node) || (function() {
					node['items'] = [];
					return node['items'];
				})();
				index = Math.min(Math.max(0, index), children.length);
				var removed = children [index];
				children.splice (index, 1);
				if(index > 0 && index < children.length) {
					children [index-1]['nextSibling'] = children [index];
				}
				node['items'] = children;
				return removed;
			}
			return null;
		}
		
		that.createNode = function (properties) {
			var node = {};
			for (var p in properties) { 
				that.setProperty (node, p, properties[p]) 
			};
			if (that.getProperty (node, 'id') == null) {
				that.setProperty (node, 'id', that.generateUID());
			}
			return node;
		}
		
		that.getProperty = function (node, propName) {
			if (node) { return registry.proof(propName, node[propName]) };
			return null;
		}
		
		that.setProperty = function (node, propName, propValue) {
			if (node) {
				node[propName] = registry.proof(propName, propValue);
			}
		}
		
		this.$JSONDataSource.apply (this, arguments);
	}
	
	/**
	 * CLASS Builder
	 * @private
	 * @class
	 */
	function Builder() {
		
		var ds, root = null;
		
		function createDataSource (source, type) {
			var ret = null;
			if (type == Builder.XML) { ret = new XMLDataSource ( source ) };
			if (type == Builder.JSON) { ret = new JSONDataSource( source )};
			return ret;
		}
		
		function buildMenu() {
	    	var w = new Walker(ds, buildItem);
	    	w.walk ();
	    }
		
	    function buildItem (item) {
			
			// Get & parse info about the item to be built:
	    	var summary = ds.getSummary (item);
    		var isFirstLevel = (!ds.getParent(item));
	    	var isItemDisabled = (!summary[FieldsRegistry.ENABLED]);
    		var hasChildren = ds.hasChildren(item);
    		var isItemSeparator = (summary [FieldsRegistry.TYPE] == 
    			FieldsRegistry.SEPARATOR);
	    	var isItemAToggle = (summary [FieldsRegistry.TYPE] == 
	    		FieldsRegistry.CHECK);
	    	
	    	// Build the NativeMenuItem to represent this item:
	    	var ret = parseLabelForMnemonic (summary [FieldsRegistry.LABEL]);
			var nmi = new NativeMenuItem ( ret[0], isItemSeparator );
			
			
			// Attach features for this item:
	    	var parsedMnemonicIndex = ret[1];
	    	if (parsedMnemonicIndex >= 0) {
	    		summary [FieldsRegistry.MNEMONIC_INDEX] = parsedMnemonicIndex;
	    	};
			var mnemonicIndex = summary [FieldsRegistry.MNEMONIC_INDEX];
			if (mnemonicIndex != -1) { nmi.mnemonicIndex = mnemonicIndex };
			if (isItemAToggle) {
				var toggler = function (event) {
					var val = !ds.getProperty (item,
						FieldsRegistry.TOGGLED);
					ds.setProperty (item, FieldsRegistry.TOGGLED, val);
					nmi.checked = val;
				}
				nmi.addEventListener (SELECT, toggler);
				nmi.checked = summary [FieldsRegistry.TOGGLED];
			}
			if (summary [FieldsRegistry.ON_SELECT]) {
				var f = function (event) {
					var target = event.target;
					summary [FieldsRegistry.ON_SELECT].call (
						window, event, summary
					);
				}
				nmi.addEventListener (SELECT, f);
			}
			attachKeyEquivalentHandler (nmi, summary);
	    	if ( isItemDisabled ) { nmi.enabled = false };
	    	
			// Attach our item within the menu structure:
			item['_widget_'] =  nmi;
			if (hasChildren) { nmi.submenu = new NativeMenu() };
			var data = nmi.data || (nmi.data = {});
			data['item'] = item;
			var parMnu = null;
	    	var parItem = ds.getParent(item);
	    	if (parItem) {
		    	var parWidget = parItem['_widget_'];
		    	parMnu = parWidget.submenu;
		    	if (!parMnu) { return };
	    	} else {
	    		parMnu = root || ( root = new NativeMenu() );
	    	}
			parMnu.addItem(nmi);
	    }

		function qReplace (tStr, searchStr , replaceStr) {
			var index;
			while ((index = tStr.indexOf (searchStr)) >= 0) {
				var arr = tStr.split('');
				arr.splice (index, searchStr.length,
					replaceStr);
				tStr = arr.join('');
			}
			return tStr;
		}

		function parseLabelForMnemonic (label) {
			var l = label;
			if (l) {
				l = qReplace(l, '__', '[UNDERSCORE]');
				l = qReplace(l, '_', '[MNEMONIC]');
				l = qReplace(l, '[UNDERSCORE]', '_');
				var mi = l.indexOf ('[MNEMONIC]');
				l = qReplace(l, '[MNEMONIC]', '');
				if (mi >= 0) { return [l, mi] };
			}
			return [l, -1];
		}

		function attachKeyEquivalentHandler (nativeItem, summary) {
			if (summary[FieldsRegistry.DEFAULT_KEY]) {
				// Linux implementation needs this check:
				var def = nativeItem.keyEquivalentModifiers &&
					nativeItem.keyEquivalentModifiers[0]?
					nativeItem.keyEquivalentModifiers[0] : null;
				if (def && typeof def != "undefined") {
					if (summary[FieldsRegistry.CTRL_KEY] === false) {
						if (def == KEYBOARD.CONTROL) { def = null };
					}
					if (summary[FieldsRegistry.CMD_KEY] === false) {
						if (def == KEYBOARD.COMMAND) { def = null };
					}
				}
			}
			var key;
			if (key = summary[FieldsRegistry.KEY_EQUIVALENT]) {
				var mods = [];
				if (def) { mods.push(def) };
				if (summary[FieldsRegistry.CTRL_KEY]) {
					mods.push (KEYBOARD.CONTROL);
				}
				if (summary[FieldsRegistry.CMD_KEY]) {
					mods.push (KEYBOARD.COMMAND);
				}
				if (summary[FieldsRegistry.ALT_KEY]) {
					mods.push (KEYBOARD.ALTERNATE);
				}
				key = (summary[FieldsRegistry.SHIFT_KEY])? 
					key.toUpperCase() : key.toLowerCase();
				nativeItem.keyEquivalent = key;
				nativeItem.keyEquivalentModifiers = mods;
			}
		}

		this.loadData = function (source, type) {
			if (source) { ds = createDataSource (source, type) }
			else { throw new Error([
				"Cannot create menu. ",
				"Provided data source is null"
			].join('')) }
		}
		
		this.build = function() {
			if(ds) {buildMenu()};
			return root;
		}
	}
	Builder.XML = 0x10;
	Builder.JSON = 0x20;

	/**
	 * CLASS NIConnector
	 * @private
	 * @class
	 */
	function NIConnector () {
		
		var that = this;
		
		var LAST = 0x1;
		var BEFORE_LAST = 0x2;
		
		var ni;
		var nativeMenu;
		var overwrite;
		var allSet;
		var isMac;
		
		function $NIConnector (oNi, oNewNativeMenu, bOverwriteExisting) {
			if (oNi && oNewNativeMenu) {
				allSet = true;
				ni = oNi;
				nativeMenu = oNewNativeMenu;
				overwrite = bOverwriteExisting;
				isMac = currentOS.indexOf('Mac') >= 0;
				if (typeof NIConnector.defaultMenu == "undefined") {
					var app = NativeApplication.nativeApplication;
					NIConnector.defaultMenu = app.menu;
				}
			}
		}

		
		function isDefaultApplicationMenu () {
			var app = NativeApplication.nativeApplication;
			return (app.menu == NIConnector.defaultMenu);
		}
		
		function purge () {
			while (ni.menu.numItems) { ni.menu.removeItemAt (0) }
		}
		
		function add ( style ) {
			if (!ni.menu) { 
				replace();
				return;
			}
			var addFunction = (style == LAST)? 
				ni.menu.addItem : function (item) {
					ni.menu.addItemAt (item, ni.menu.numItems-1);
				}
			var item;
			while (nativeMenu.numItems && (item = nativeMenu.removeItemAt(0))) {
				if(isMac && !item.submenu) { continue };
				addFunction.call (that, item);
			}
		}

		function replace () {
			ni.menu = nativeMenu;
		}
		
		this.doConnect = function () {
			if (allSet) {
				if (overwrite) {
					if (isMac) {
						purge ();
						add (LAST);
					}
					else { replace() };
				}
				else {
					if (isMac) {
						if (isDefaultApplicationMenu()) { add (BEFORE_LAST) }
						else { add (LAST) };
					} else { add (LAST) };
				}
			}
		}
		
		$NIConnector.apply (this, arguments);
	}
	NIConnector.defaultMenu;
	
	
	/**
	 * CLASS Shell
	 * @private
	 * @class
	 */
	function Shell() {
		
		function $Shell(){}
		
		var that = this;
		
		var CONTEXT_MENU = 'contextmenu';
	    var app = NativeApplication.nativeApplication;
	    
	    var uidSeed = 0;
	    var DEFAULT_ID = "DEFAULT_ID";
	    
	    var isMac = currentOS.indexOf('Mac') >= 0;
	    
	    var isBitmapData = function(obj) {
	    	return obj &&
	    		obj.constructor &&
	    		obj.constructor === (new BitmapData (1, 1));
	    }
	    
		var resolveDomEl = function (obj) {
			var ret = null;
			if (obj) {
				if (typeof obj == 'object' && obj.nodeType == 1) { ret = obj };
				if (typeof obj == 'string') {
					var el;
					if (el = document.getElementById(obj)) { ret = el };
				}
			}
			return ret;
		}
		
		var checkUserIcon = function (obj) {
			var icon = app.icon;
			return icon.bitmaps.length > 0;
		}
		
		var getIcons = function (userIcons) {
			var ret = [];
			var entries = [];
			if (userIcons && userIcons.length) { 
				entries = userIcons;
			} else {
				var p = new DOMParser();
				var descr = String(app.applicationDescriptor);
				var descrDoc = p.parseFromString(descr, "text/xml");
				var appEl = descrDoc.getElementsByTagName('application')[0];
				var iconEl = appEl.getElementsByTagName('icon')[0];
				if (iconEl) {
					var iconEntries = iconEl.getElementsByTagName('*');
					for (var i=0; i<iconEntries.length; i++) {
						if (iconEntries[i].firstChild) {
							var path = iconEntries[i].firstChild.nodeValue;
							entries.push (path);
						}
					}
				}
			}
			for (var i=0; i<entries.length; i++) {
				var entry = entries[i];
				if (isBitmapData(entry)) {
					ret.push (entry)
				} else {
					var file = Shell.resolve(entry);
					if (!file.exists) { 
						throw (new Error([
						'Could not set icon(s) for the iconMenu.',
						'Could not resolve this path:', file.url
						].join('\n')));
					};
					ret.push (file);
				}
			}
			return ret;
		}
		
		var loadDefaultBitmaps = function (icons, callback) {
			var bmpDataObjects = [];
			var completeHandler = function (event){
				var bitmap = event.target.loader.content;
				bmpDataObjects.push(bitmap.bitmapData);
				loadNext();
			}
			var ioErrorHandler = function(event){};
			var loadNext = function(){
				var icon = icons.pop();
				if (icon) {
					if (icon.url) {
						var iconURL = icon.url;
						var request = new URLRequest(iconURL);
						loader.load(request);
					} else { bmpDataObjects.push(icon) };
				} else {
					if (typeof callback == 'function') {
						callback.call(this, bmpDataObjects);
					}
				}
			}
			var loader = new Loader();
	        loader.contentLoaderInfo.addEventListener(COMPLETE,completeHandler);
	        loader.contentLoaderInfo.addEventListener(IO_ERROR,ioErrorHandler);
	        loadNext();
		}
		
		var setBitmaps = function (bitmaps) {
			var icon = app.icon;
			icon.bitmaps = bitmaps;
		}
		
		var linkMenu = function (menu, doOverwrite) {
			var target = NativeWindow.supportsMenu? window.nativeWindow:
				NativeApplication.supportsMenu? 
					NativeApplication.nativeApplication:
					null;
			var nic = new NIConnector(target, menu, doOverwrite);
			return nic.doConnect();
		}
		
		var generateUID = function () { return ['el', ++uidSeed].join('_') };

        var linkContextMenu = function (menu, domEl) {
              var stage = window.htmlLoader.stage;
              var listener = function (e) {
                    if (e.returnValue && menu) { menu.display(stage, e.x, e.y)};
                    e.preventDefault();
                    e.stopPropagation();
              }
              var target = (domEl == Shell.UNSPECIFIED)? window : 
              		resolveDomEl(domEl);
              if (!target) {
              	throw (new Error ([
              		"Cannot set contextual menu.",
              		"The DOM element that you specified was not found."
              	].join('\n')));
              }
              target.addEventListener (CONTEXT_MENU, listener, false);
        }
		
		var linkIconMenu = function (menu, userIcons) {
			var haveCustomIcons = (typeof userIcons != "undefined" &&
				userIcons && userIcons.length);
			var haveIcon = checkUserIcon();
			if (!haveCustomIcons && haveIcon) { app.icon.menu = menu }
			else {
				var defaultIcons = getIcons (userIcons);
				var haveDefaultIcons = defaultIcons.length > 0;
				if (!haveDefaultIcons) {
					if (!isMac) {
						throw (new Error([
						"Cannot set the icon menu.",
						"On operating systems that do not provide a default",
						"tray icon, you must specify one before calling",
						"setAsIconMenu().",
						"Alternativelly, you can specify default icons in the",
						"application's XML descriptor."
						].join('\n')));
					}
				}
				var doAttach = function(bitmaps){
					setBitmaps (bitmaps);
					app.icon.menu = menu;
				}
				if (defaultIcons) {
					loadDefaultBitmaps(defaultIcons, doAttach);
				}
			}
		}
		
		this.link = function (oMenu, style, target, icons) {
			if (Shell.MENU & style) {
				var bOverwrite = style & Shell.OVERWRITE;
				return linkMenu(oMenu, bOverwrite);
			}
			if (Shell.CONTEXT & style) {return linkContextMenu(oMenu, target)};
			if (Shell.ICON & style) { return linkIconMenu(oMenu, icons) };
		}
		
		$Shell.apply (this, arguments);
	}
	Shell.UNSPECIFIED = -1;
	Shell.MENU 	      = 1;
	Shell.CONTEXT     = 2;
	Shell.ICON	      = 4;
	Shell.OVERWRITE   = 8;
	
	Shell.resolve = function (pathOrFile) {
		var file = null;
		try {
		    file = File(pathOrFile);
		} catch(e) {
		    file = File.applicationDirectory.resolvePath (pathOrFile);
		    if (!file.exists) {
		    	try {
			        file = new File (pathOrFile);
		    	} catch(e) {
		    		// must be a path, both 'relative' AND 'non-existing'.
		    	}
		    }
		}
		return file;
	}
	
	/**
	 * CLASS Walker
	 * @class
	 * @private
	 */
	function Walker() {
		var t, c, currentItem, allSet, item;
		
		function $Walker (target, callback) {
	    	if (target && target instanceof DataSource) {
	    		t = target;
	    	}
	    	if (callback && typeof callback == "function") {
	    		c = callback; 
	    	}
	    	if (t && c) { allSet = true };
		}
		
		function getNearestAncestorSibling(node) {
			while (node) {
				node = t.getParent(node);
				if(node) {
					var s = t.getNextSibling(node);
					if (s) { return s };
				}
			}
			return null;
		}
		
		function getFirstChildOfRoot() {
			return t.getChildren(t.getRoot())[0] || null;
		}
		
		function doTraverse() {
			if (allSet) {
				while (item = getNext()) { c.call (window, item) };
				
			} else {
				throw (new Error([
				'Cannot traverse data tree.',
				'Please check the arguments you provided to the Walker class.',
				].join('\n')));
			}
		}
		
		function getNext() {
			if (currentItem === null) { return null };
			
			if (typeof currentItem == 'undefined') {
				currentItem = getFirstChildOfRoot();
			}
			
			if (t.hasChildren(currentItem)) {
				var parentNode = currentItem;
				currentItem = t.getChildren(currentItem)[0];
				return parentNode;
			}
			
			if(t.getNextSibling(currentItem)) {
				var current = currentItem;
				currentItem = t.getNextSibling(currentItem);
				return current;
			}
			
			var ci = currentItem;
			currentItem = getNearestAncestorSibling(currentItem);
			return ci;
		}
	
	    this.walk = function (callback) {
	    	doTraverse();
	    	if (typeof callback == "function") { callback.call (this) };
	    }
		
		$Walker.apply (this, arguments);
	}
	
	constructor.apply (this, arguments);
})();