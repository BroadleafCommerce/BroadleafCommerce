/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
(function($, BLCAdmin) {
    
    var LISTGRID_AJAX_LOCK = 0;
    var fetchDebounce = 200;
    var updateUrlDebounce = 700;
    var lockDebounce = 100;
    var maxSubCollectionListGridHeight = 360;
    
    var tableResizing = {
        active : false,
        headerTable : undefined,
        bodyTable : undefined,
        startX : undefined,
        startWidths : undefined,
        totalWidth : 0,
        index : undefined
    };
    
    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.listGrid.paginate = {
            
        // ********************** *
        // LOCK RELATED FUNCTIONS *
        // ********************** *
            
        acquireLock : function() {
            if (LISTGRID_AJAX_LOCK == 0) {
                LISTGRID_AJAX_LOCK = 1;
                return true;
            }
            return false;
        },
        
        releaseLock : function() {
            LISTGRID_AJAX_LOCK = 0;
        },
                
        // ****************************** *
        // RECORD RANGE RELATED FUNCTIONS *
        // ****************************** *
        
        getPageSize : function($tbody) {
            return $tbody.data('pagesize');
        },
        
        getTotalRecords : function($tbody) {
            return $tbody.data('totalrecords');
        },
        
        getRange : function(rangeDescription) {
            var range = rangeDescription.split('-');
            rangeObj = {lo : parseInt(range[0]), hi : parseInt(range[1])};
            return rangeObj;
        },
        
        getLoadedRecordRanges : function($tbody) {
            var rangeDescriptions = $tbody.data('recordranges').split(',');
            var ranges = [];
            
            for (var i = 0; i < rangeDescriptions.length; i++) {
                ranges[i] = this.getRange(rangeDescriptions[i]);
            }
            
            return ranges;
        },
        
        addLoadedRange : function($tbody, lo, hi) {
            var loadedRanges = this.getLoadedRecordRanges($tbody);
            
            // Add the new range
            loadedRanges.push({ lo : parseInt(lo), hi : parseInt(hi) });
            
            // Sort the ranges
            loadedRanges.sort(function(a, b) {
                return a.lo - b.lo;
            });
            
            // Merge any ranges that were "bridged" by the new range
            for (var i = loadedRanges.length - 1; i > 0; i--) {
                if (loadedRanges[i].lo - 1 == loadedRanges[i - 1].hi) {
                    loadedRanges[i - 1].hi = loadedRanges[i].hi;
                    loadedRanges.splice(i, 1);
                }
            }
            
            // Save the new record ranges
            var s = "";
            for (var i = 0; i < loadedRanges.length; i++) {
                s += loadedRanges[i].lo + '-' + loadedRanges[i].hi + ',';
            }
            s = s.substring(0, s.length - 1);
            $tbody.data('recordranges', s);
        },
        
        isIndexLoaded : function($tbody, index) {
            var loadedRanges = this.getLoadedRecordRanges($tbody);
            
            for (var i = 0; i < loadedRanges.length; i++) {
                if (loadedRanges[i].lo <= index && loadedRanges[i].hi >= index) {
                    return true;
                }
            }
            
            return false;
        },
        
        findFirstLoadedIndex : function($tbody, startIndex) {
            var loadedRanges = this.getLoadedRecordRanges($tbody);
            
            var firstIndex = null;
            for (var i = loadedRanges.length - 1; i >= 0; i--) {
                if (loadedRanges[i].lo > startIndex) {
                    firstIndex = loadedRanges[i].lo;
                }
            }
            
            return firstIndex;
        },
        
        findLastLoadedIndex : function($tbody, endIndex) {
            var loadedRanges = this.getLoadedRecordRanges($tbody);
            
            var lastIndex = null;
            for (var i = 0; i < loadedRanges.length; i++) {
                if (loadedRanges[i].hi < endIndex) {
                    lastIndex = loadedRanges[i].hi;
                }
            }
                
            return lastIndex;
        },
        
        // ************************** *
        // DOM MANIPULATION FUNCTIONS *
        // ************************** *
        
        injectRecords : function($tbody, $newTbody) {
            // Find the padding row that will be split (or potentially replaced)
            var _this = this;
            var newRange = this.getRange($newTbody.data('recordranges'));
            var $pad = null;
            var padRange = null;
            $tbody.find('tr.blank-padding').each(function(index, element) {
                var $e = $(element);
                var pr = _this.getRange($e.data('range'));
                
                if ((padRange == null || padRange.lo < pr.lo)  && pr.lo <= newRange.lo) {
                    $pad = $e;
                    padRange = pr;
                }
            });
            // Create the top split (potentially nothing)
            var blankRangeAbove = {
                lo : padRange.lo,
                hi : newRange.lo - 1
            };
            if (blankRangeAbove.lo != (blankRangeAbove.hi + 1)) {
                var $topPad = this.createPadding($tbody, blankRangeAbove.lo, blankRangeAbove.hi);
            }
            
            // Create the bottom split (potentially nothing)
            var blankRangeBelow = {
                lo : newRange.hi + 1,
                hi : padRange.hi
            };
            if (blankRangeBelow.lo != blankRangeBelow.hi + 1) {
                var $bottomPad = this.createPadding($tbody, blankRangeBelow.lo, blankRangeBelow.hi);
            }
            
            // Extract the new rows
            var $newTrs = $newTbody.find('tr');
            
            // Replace the old padding row with the top split, new rows, and bottom split
            if ($topPad != null) {
                $pad.before($topPad);
            }
            if ($bottomPad != null) {
                $pad.after($bottomPad);
            }
            $pad.replaceWith($newTrs);
            
            // Update the loaded record ranges to reflect this new chunk
            this.addLoadedRange($tbody, newRange.lo, newRange.hi);
        },
        
        initializeTableResizing : function($headerTable, $bodyTable) {
            
            $headerTable.find('th div.resizer').mousedown(function(e) {
                var $this = $(this).closest('th');
                
                tableResizing.active = true;
                tableResizing.headerTable = $this.closest('table');
                tableResizing.bodyTable = $this.closest('.listgrid-header-wrapper').next().find('table'); 
                tableResizing.startX = e.pageX;
                tableResizing.startWidths = [];
                tableResizing.index = $this.index();
                tableResizing.totalWidth = 0;
                
                tableResizing.headerTable.find('th').each(function(index, element) {
                    tableResizing.startWidths.push($(this).outerWidth());
                    tableResizing.totalWidth += $(this).outerWidth();
                });
                
                $(document).disableSelection();
            });
            
            $(document).mousemove(function(e) {
                if (tableResizing.active) {
                    var index = tableResizing.index;
                    var widthDifference = (e.pageX - tableResizing.startX);
                    
                    // Resize the selected column to its new width
                    var newWidth = tableResizing.startWidths[index] + widthDifference;
                    
                    if ((newWidth > tableResizing.totalWidth - 30) || (newWidth < 30)) {
                        return false;
                    }
                    
                    $(tableResizing.headerTable.find('thead tr th')[index]).outerWidth(newWidth);
                    $(tableResizing.bodyTable.find('thead tr th')[index]).outerWidth(newWidth);
                    
                    // This represents the width of the table cells other than the one we're resizing
                    var remainingWidth = tableResizing.totalWidth - tableResizing.startWidths[index];
                    
                    for (var i = 0; i < tableResizing.startWidths.length; i++) {
                        if (i != index) {
                            var percentage = tableResizing.startWidths[i] / remainingWidth;
                            var delta = widthDifference * percentage;
                            
                            newWidth = tableResizing.startWidths[i] - delta;
                            
                            $(tableResizing.headerTable.find('thead tr th')[i]).outerWidth(newWidth);
                            $(tableResizing.bodyTable.find('thead tr th')[i]).outerWidth(newWidth);
                        }
                    }
                }
            });
            
            $(document).mouseup(function() {
                if (tableResizing.active) {
                    tableResizing.active = false;
                    $(document).enableSelection();
                }
            });
            
        },
        
        // ********************** *
        // AJAX CALLING FUNCTIONS *
        // ********************** *
        
        loadRecords : function($tbody, baseUrl) {
            while (!this.acquireLock()) {
                var _this = this;
                //console.log("Couldn't acquire lock. Will try again in " + lockDebounce + "ms");
                $.doTimeout('acquirelock', lockDebounce, function() {
                    _this.loadRecords($tbody, baseUrl);
                });
                return false;
            }
            
            // If we can't see the list grid at all, don't load anything
            if (!$tbody.is(':visible')) {
                this.releaseLock();
                return false;
            }
            
            var topIndex = this.getTopVisibleIndex($tbody);
            var botIndex = this.getBottomVisibleIndex($tbody);
            var ranges = this.getLoadedRecordRanges($tbody);
            var pageSize = this.getPageSize($tbody);
            var totalRecords = this.getTotalRecords($tbody);
            
            var topIndexLoaded = this.isIndexLoaded($tbody, topIndex);
            var botIndexLoaded = this.isIndexLoaded($tbody, botIndex);
            
            var startIndex = null;
            var maxIndex = null;
            
            // This is responsible for determining which range of records to load, considering what the currently
            // visible viewport is, what ranges are already loaded near the viewport, and the maximum page size
            if (!topIndexLoaded && !botIndexLoaded) {
                var potentialStart = this.findLastLoadedIndex($tbody, topIndex);
                var potentialMax = this.findFirstLoadedIndex($tbody, botIndex);
                
                if (potentialStart == null) {
                    potentialStart = 0;
                } else {
                    potentialStart = potentialStart + 1;
                }
                
                if (potentialMax == null) {
                    potentialMax = totalRecords - 1;
                } else {
                    potentialMax = potentialMax - 1;
                }
                
                if (pageSize < potentialMax - topIndex) {
                    startIndex = topIndex;
                    maxIndex = topIndex + pageSize - 1;
                } else if (pageSize > potentialMax - potentialStart) {
                    startIndex = potentialStart;
                    maxIndex = potentialMax;
                } else if (pageSize > potentialMax - topIndex) {
                    startIndex = potentialMax - pageSize + 1;
                    maxIndex = potentialMax;
                } else {
                    startIndex = topIndex;
                    maxIndex = topIndex + pageSize - 1;
                }
            } else if (!topIndexLoaded && botIndexLoaded) {
                maxIndex = this.findFirstLoadedIndex($tbody, topIndex) - 1;
                
                var potentialStart = this.findLastLoadedIndex($tbody, topIndex);
                
                if (potentialStart == null) {
                    potentialStart = 0;
                } else {
                    potentialStart = potentialStart + 1;
                }
                
                if (pageSize > maxIndex - potentialStart) {
                    startIndex = potentialStart;
                } else {
                    startIndex = maxIndex - pageSize + 1;
                }
            } else if (topIndexLoaded && !botIndexLoaded) {
                startIndex = this.findLastLoadedIndex($tbody, botIndex) + 1;
                
                var potentialMax = this.findFirstLoadedIndex($tbody, startIndex);
                if (potentialMax == null) {
                    potentialMax = totalRecords - 1;
                } else {
                    potentialMax = potentialMax - 1;
                }
                
                if (pageSize > potentialMax - startIndex) {
                    maxIndex = potentialMax;
                } else {
                    maxIndex = startIndex + pageSize - 1;
                }
            } else {
                for (var i = topIndex + 1; i < botIndex; i++) {
                    if (!this.isIndexLoaded($tbody, i)) {
                        startIndex = i;
                        maxIndex = this.findFirstLoadedIndex($tbody, i) - 1;
                        break;
                    }
                }
            }
            
            if (startIndex != null && maxIndex != null) {
                var delta;
                if (startIndex <= topIndex && maxIndex <= botIndex) {
                    // Top range missing - show in the middle of the top and max
                    delta = (topIndex + maxIndex) / 2;
                } else if (startIndex > topIndex && maxIndex < botIndex) {
                    // Mid range missing - show in the middle of the start and max
                    delta = (startIndex + maxIndex) / 2;
                } else if (startIndex > topIndex && maxIndex >= botIndex) {
                    // Bottom range missing - show in the middle of the start and bot
                    delta = (startIndex + botIndex) / 2;
                } else {
                    // Full range missing - show in the middle of the top and bot
                    delta = (topIndex + botIndex) / 2;
                }
                delta = delta - topIndex;
                spinnerOffset = $tbody.closest('.mCustomScrollBox').position().top + 3 + (this.getRowHeight($tbody) * delta);
                BLCAdmin.listGrid.showLoadingSpinner($tbody, spinnerOffset);
                
                var url = BLCAdmin.history.getUrlWithParameter('startIndex', startIndex, null, baseUrl);
                url = BLCAdmin.history.getUrlWithParameter('maxIndex', maxIndex, null, url);
                
                var params = $tbody.closest('.listgrid-container').find('.listgrid-header-wrapper table').data('currentparams');
                for (var param in params) {
                    url = BLCAdmin.history.getUrlWithParameter(param, params[param], null, url);
                }
                
                //console.log('Loading more records -- ' + url);
                
                BLC.ajax({ url: url, type: 'GET' }, function(data) {
                    var $newTbody = data.find('tbody');
                    BLCAdmin.listGrid.paginate.injectRecords($tbody, $newTbody);
                    BLCAdmin.listGrid.paginate.releaseLock();
                    
                    // now that I've loaded records, see if I need to do it again
                    var topIndex = BLCAdmin.listGrid.paginate.getTopVisibleIndex($tbody);
                    var topIndexLoaded = BLCAdmin.listGrid.paginate.isIndexLoaded($tbody, topIndex);
                    var botIndex = BLCAdmin.listGrid.paginate.getBottomVisibleIndex($tbody);
                    var botIndexLoaded = BLCAdmin.listGrid.paginate.isIndexLoaded($tbody, botIndex);
                    if (!botIndexLoaded || !topIndexLoaded) {
                        BLCAdmin.listGrid.paginate.loadRecords($tbody, baseUrl);
                    } else {
                        BLCAdmin.listGrid.hideLoadingSpinner($tbody);
                    }
                });
            } else {
                BLCAdmin.listGrid.paginate.releaseLock();
            }
        },
        
        // ************************* *
        // CUSTOM SCROLLER FUNCTIONS *
        // ************************* *
        
        getRowHeight : function($tbody) {
            return $tbody.find('tr:not(.blank-padding):first').height();
        },
        
        getTopVisibleIndex : function($tbody) {
            if (typeof mcs === 'undefined') {
                return 0;
            }
            
            var scrollOffset = $tbody.closest('.mCSB_container').position().top;
            var trHeight = this.getRowHeight($tbody);
            var topVisibleIndex = Math.floor(scrollOffset * -1 / trHeight);
            return topVisibleIndex;
        },
        
        getBottomVisibleIndex : function($tbody) {
            var scrollOffset = $tbody.closest('.mCSB_container').position().top;
            var trHeight = this.getRowHeight($tbody);
            var bottomVisibleIndex = Math.floor((scrollOffset * -1 + $tbody.closest('.listgrid-body-wrapper').height() - 4) / trHeight);
            return bottomVisibleIndex;
        },
        
        scrollToIndex : function($tbody, index) {
            var offset = index * this.getRowHeight($tbody);
            //console.log('scrolling to ' + offset);
            $tbody.closest('.listgrid-body-wrapper').find('.mCSB_container').css('top', '-' + offset + 'px');
        },
        
        updateTableFooter : function($tbody) {
            var topIndex = this.getTopVisibleIndex($tbody) + 1;
            var botIndex = this.getBottomVisibleIndex($tbody) + 1;
            var totalRecords = this.getTotalRecords($tbody);
            var $footer = $tbody.closest('.listgrid-container').find('.listgrid-table-footer');

            if (totalRecords > 0){
                $footer.find('.low-index').text(topIndex);
                $footer.find('.high-index').text(botIndex);
            } else {
                $footer.find('.low-index').text("0");
                $footer.find('.high-index').text("0");
            }
            $footer.find('.total-records').text(totalRecords);
        },
        
        updateGridSize : function($tbody) {
            var $table = $tbody.closest('table.list-grid-table');
            var $headerTable = $table.closest('div.listgrid-container').find('.listgrid-header-wrapper table');
            var thWidths = [];
            var $modalBody = $tbody.closest('.modal-body');
            
            if ($modalBody.length > 0) {
                $modalBody.css('overflow-y', 'hidden');
            }
            
            // Clear out widths
            $headerTable.css('width', '');
            $table.css('width', '');
            $headerTable.closest('.listgrid-container').find('th').css('width', '');
            
            // Figure out what the new table width will be
            var newWidth = ($headerTable.width() - 15) + 'px';
            $headerTable.css('width', newWidth);
            $table.css('width', newWidth);
            
            // Determine if we need to ignore any explicitly set column widths
            var $explicitSizeThs = $headerTable.closest('.listgrid-container').find('th.explicit-size');
            if (($table.data('listgridtype') == 'main' && $table.outerWidth() < 960) || 
                ($table.data('listgridtype') != 'main' && $table.outerWidth() < 680)) {
                $explicitSizeThs.each(function(index, element) {
                    $(element).addClass('width-ignored');
                });
            } else {
                $explicitSizeThs.each(function(index, element) {
                    $(element).removeClass('width-ignored');
                });
            }
            
            // Set back any specified widths if appropriate
            $headerTable.closest('.listgrid-container').find('th').filter(function() {
                return $(this).hasClass('explicit-size') && !$(this).hasClass('width-ignored');
            }).each(function(index, thElement) {
                $(thElement).css('width', $(thElement).data('columnwidth'));
            });
            
            // Set the new widths
            $headerTable.find('th').each(function(index, thElement) {
                var $th = $(thElement);
                var width = $th.outerWidth();
                $th.css('width', width);
                thWidths[index] = width;
            });
            $table.find('th').each(function(index, thElement) {
                $(thElement).css('width', thWidths[index]);
                var columnNo = $(thElement).index();
                $(thElement).closest("table")
                    .find("tr td:nth-child(" + (columnNo+1) + ")")
                    .css("max-width", thWidths[index]);
            });
            
            var $wrapper = $tbody.closest('.listgrid-body-wrapper');
            
            // If we're the only grid on the page, we should stretch to the bottom of the screen if we are not encapsulated
            // inside of an entity-form
            var listGridsCount = BLCAdmin.listGrid.getListGridCount($);
            if (listGridsCount == 1 && $wrapper.parents('.entity-form').length == 0) {
                var $window = $(window);
                
                var wrapperHeight = $window.height() - $wrapper.offset().top - 50;
                wrapperHeight = BLCAdmin.listGrid.paginate.computeActualMaxHeight($tbody, wrapperHeight);
                
                $wrapper.css('max-height', wrapperHeight);
                $wrapper.find('.mCustomScrollBox').css('max-height', wrapperHeight);
                
                $wrapper.mCustomScrollbar('update');
                
                // If we are showing all records from the single grid page, ensure the url is updated
                if ($wrapper.find('.mCS_no_scrollbar').length > 0) {
                    BLCAdmin.listGrid.paginate.updateUrlFromScroll($wrapper.find('tbody'));
                }
            } else if ($modalBody.length > 0) {
                // If this is inside of a modal, the max height should be the size of the modal
                
                var maxHeight = $modalBody.height() - $wrapper.prev().height() - $wrapper.next().height() - 51;

                $wrapper.closest('.adorned-select-wrapper').find('fieldset').each(function(index, fieldset) {
                    maxHeight -= $(fieldset).height();
                });
                
                if ($wrapper.parent().find('label').length > 0) {
                    maxHeight -= $wrapper.parent().find('label').outerHeight();
                    maxHeight -= 5;
                }
                
                if ($wrapper.parent().find('.listgrid-toolbar').length > 0) {
                    maxHeight -= $wrapper.parent().find('.listgrid-toolbar').outerHeight();
                }
                
                var minHeight = $wrapper.find('table tr:not(.width-control-header)').outerHeight() + 1;
                if (maxHeight < minHeight) {
                    maxHeight = minHeight;
                }
                
                maxHeight = BLCAdmin.listGrid.paginate.computeActualMaxHeight($tbody, maxHeight);
                $wrapper.css('max-height', maxHeight);
                $wrapper.find('.mCustomScrollBox').css('max-height', maxHeight);
                $modalBody.css('overflow-y', 'auto');
            } else {
                // not in a modal, not the only grid on the screen, my size should be equal to max size of a grid
                // There is a possibility, if pagination is limited on the packed, that 
                
                var maxHeight = BLCAdmin.listGrid.paginate.computeActualMaxHeight($tbody, maxSubCollectionListGridHeight);
                $wrapper.css('max-height', maxHeight);
                $wrapper.find('.mCustomScrollBox').css('max-height', maxHeight);

                $wrapper.mCustomScrollbar('update');
            }
            
            // after all the heights have been calculated, update the table footer with the correct record shown count
            BLCAdmin.listGrid.paginate.updateTableFooter($wrapper.find('tbody'));
        },
        
        computeActualMaxHeight : function($tbody, desiredMaxHeight) {
            // what is the height of the visible rows?
            var rowHeight = BLCAdmin.listGrid.paginate.getRowHeight($tbody);
            var loadedRecordRange = BLCAdmin.listGrid.paginate.getLoadedRecordRanges($tbody)[0]
            // This gives me back a 0-indexed range, I need the row count so add 1
            var numLoadedRows = loadedRecordRange.hi - loadedRecordRange.lo + 1;
            var numPaddedRows = BLCAdmin.listGrid.paginate.getTotalRecords($tbody) - numLoadedRows;

            // How much of the visible viewport is actual loaded rows and how much is padding? 
            var visibleRowsHeight = rowHeight * numLoadedRows;
            var paddedRowsHeight = rowHeight * numPaddedRows;
            
            var maxHeight = desiredMaxHeight;
            
            // If we added visible padding and there isn't enough rows to cover the entire viewport that we want
            // (maxSubCollectionListGridHeight), then we need to shrink the size such that scrolling occurs. Otherwise,
            // we end up in a scenario in which you have some visible rows, padding is there, but no scrolling will
            // ever take place and new records will never be loaded. This will only occur if the size of the pages from
            // the server multiplied by the row height is less than desiredMaxHeight
            if (paddedRowsHeight != 0 && visibleRowsHeight <= desiredMaxHeight) {
                // shrink the size of the grid by just enough so that scrolling is activated
                maxHeight = visibleRowsHeight + paddedRowsHeight - 3;
            }

            if (maxHeight < rowHeight) {
                maxHeight = rowHeight;
            }
            
            return maxHeight;
        },
        
        updateUrlFromScroll : function($tbody) {
            var topIndex = this.getTopVisibleIndex($tbody);
            if (topIndex > 0) {
                BLCAdmin.history.replaceUrlParameter('startIndex', topIndex);
            } else {
                BLCAdmin.history.replaceUrlParameter('startIndex');
            }
        },
        
        createPadding : function($tbody, startRange, endRange) {
            var rowHeight = this.getRowHeight($tbody);
            var recordsCount = endRange - startRange + 1;
            
            var $pad = $('<tr>', { 
                'class' : 'blank-padding', 
                'css' : { 
                    'height' : recordsCount * rowHeight
                },
                'data-range' : startRange + '-' + endRange
            });
            
            return $pad;
        },
        
        // ********** *
        // INITIALIZE *
        // ********** *
        
        initialize : function($container) {
            var $table = $container.find('table.list-grid-table');
            var $tbody = $table.children('tbody');
            var thWidths = [];
            var $modalBody = $container.closest('.modal-body');
            
            // If we're in a modal, we need to hide overflow in the modal to calculate sizes correclty. We'll restore this.
            $modalBody.css('overflow-y', 'hidden');
            
            // First, we'll adjust the size of the table to be 15px less, since this is the margin we need
            // for our scrollbar. This will ensure the widths are correct once we draw the scrollbar
            $table.css('width', ($table.width() - 15) + 'px');
            
            // Figure out what the currently drawn widths are for each row
            // This is effectively the same for all rows for both the head and the body for now
            // Also, set the width we determined directly on the element
            $table.find('th').each(function(index, thElement) {
                var $th = $(thElement);
                var width = $th.width();
                $th.css('width', width);
                thWidths[index] = width;
            });
            
            $tbody.remove();
            var $clonedTable = $table.clone();
            $table.parent().after($clonedTable);
            if ($table.attr('id').indexOf("-header") === -1) {
                $table.attr('id', $table.attr('id') + '-header');
            }
            
            $clonedTable.wrap($('<div>', { 'class' : 'listgrid-body-wrapper' }));
            var $wrapper = $clonedTable.parent();
            
            $clonedTable.find('thead').find('tr').addClass('width-control-header').find('th').empty().css("padding",0,"height",0);
            $clonedTable.append($tbody);
            $tbody = $clonedTable.find('tbody');
            $clonedTable.attr('id', $clonedTable.attr('id').replace('-header', ''));
            
            // Set up the mCustomScrollbar on the table body. Also bind the necessary events to enable infinite scrolling
            $wrapper.mCustomScrollbar({
                theme: 'dark',
                callbacks: {
                    onScroll: function() {
                        var singleGrid = BLCAdmin.listGrid.getListGridCount($) == 1;
                        var isAssetGrid = $tbody.closest('table').data('listgridtype') == 'asset';
            
                        // Update the currently visible range
                        BLCAdmin.listGrid.paginate.updateTableFooter($tbody);
                        
                        // Fetch records if necessary
                        $.doTimeout('fetch', fetchDebounce, function() {
                            if (singleGrid) {
                                var url = null;
                            } else if (isAssetGrid) {
                                var url = $tbody.closest('table').data('currenturl');
                            } else {
                                var url = $tbody.closest('table').data('path');
                            }
                            
                            BLCAdmin.listGrid.paginate.loadRecords($tbody, url);
                        });
                        
                        // Also update the URL if this is the only grid on the page
                        if (singleGrid) {
                            $.doTimeout('updateurl', updateUrlDebounce, function(){
                                BLCAdmin.listGrid.paginate.updateUrlFromScroll($tbody);
                            });
                        }
                    }
                }
            });
            
            // Figure out how large to make the top and bottom paddings
            var range = this.getLoadedRecordRanges($tbody)[0];
            var recordsAbove = range.lo;
            var recordsBelow = this.getTotalRecords($tbody) - 1 - range.hi;
            var rowHeight = this.getRowHeight($tbody);
            
            if (recordsAbove) {
                // Create the appropriate padding above
                var $pad = this.createPadding($tbody, 0, range.lo - 1);
                $tbody.find('tr:first').before($pad);
                
                // Update the height so that the user doesn't see a scroll action
                this.scrollToIndex($tbody, range.lo);
            }
            
            if (recordsBelow) {
                var $pad = this.createPadding($tbody, range.hi + 1, this.getTotalRecords($tbody) - 1);
                $tbody.find('tr:last').after($pad);
            }
            
            BLCAdmin.listGrid.paginate.updateGridSize($tbody);
            
            // Render the table
            $wrapper.mCustomScrollbar('update');
            $clonedTable.find('tbody').css('visibility', 'visible');
            $modalBody.css('overflow-y', 'auto');
            
            this.initializeTableResizing($table, $clonedTable);
        }
    };
    
    BLCAdmin.addUpdateHandler(function($container) {
        $container.find('.needsupdate').each(function(index, element) {
            BLCAdmin.listGrid.paginate.updateGridSize($(element));
            $(element).removeClass('needsupdate');
        });
    });
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    $(window).resize(function() {
        $.doTimeout('resizeListGrid', 150, function() {
            $('tbody').each(function(index, element) {
                if ($(element).is(':visible')) {
                    BLCAdmin.listGrid.paginate.updateGridSize($(element));
                } else {
                    $(element).addClass('needsupdate');
                }
            });
        });
    });
    
});

