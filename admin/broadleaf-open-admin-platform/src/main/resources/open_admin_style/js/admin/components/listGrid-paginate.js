/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
(function($, BLCAdmin) {

    var recordLoadHandlers = [];

    var LISTGRID_AJAX_LOCK = 0;
    var fetchDebounce = 200;
    var updateUrlDebounce = 700;
    var lockDebounce = 100;
    var maxSubCollectionListGridHeight = 360;
    var treeListGridHeight = 400;

    var SCROLLBAR_WIDTH = 15;
    var TREE_COLUMN_WIDTH = 320;

    var topRowVisiblePart = 0.55; // part of top row visibility to consider full row as visible
    var bottomRowVisiblePart = 0.6; // part of bottom row visibility to consider full row as visible

    var MIN_WIDTH = 60;
    var CONTROL_WIDTH = 45;

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

        getFirstId : function($tbody) {
            return $tbody.data('firstid');
        },

        getLastId : function($tbody) {
            return $tbody.data('lastid');
        },

        getUpperCount : function($tbody) {
            return $tbody.data('uppercount');
        },

        getLowerCount : function($tbody) {
            return $tbody.data('lowercount');
        },

        getTotalCountLessThanPageSize : function($tbody) {
            return $tbody.data('lessthanpagesize');
        },
        
        getRange : function(rangeDescription) {
            var seperator = rangeDescription.indexOf('-');
            var lo = Math.max(rangeDescription.substring(0, seperator), 0);
            var hi = Math.max(rangeDescription.substring(seperator + 1), 0);
            rangeObj = {lo : parseInt(lo), hi : parseInt(hi)};
            return rangeObj;
        },
        
        getLoadedRecordRanges : function($tbody) {
            var rangeDescriptions;
            if($tbody.data('recordranges')) {
                rangeDescriptions = $tbody.data('recordranges').split(',');
            } else {
                rangeDescriptions = [];
            }
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
            var totalRecords = this.getTotalRecords($tbody);

            // If the index is larger than the total number of records, then technically the record is loaded
            // Add 1 since index is 0-indexed & total records is 1-indexed
            if ((index + 1) > totalRecords) {
                return true;
            }
            
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

            // If there is no padding, these rows were probably already loaded.
            if (padRange == null) {
                return;
            }

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

                    var currentHeader = $(tableResizing.headerTable.find('thead tr th')[index])[0];
                    var $currentHeaderText = $(currentHeader).find('.listgrid-title span');

                    var nextHeader = $(tableResizing.headerTable.find('thead tr th')[index+1])[0];
                    var $nextHeaderText = $(nextHeader).find('.listgrid-title span');

                    // Calculate the new column widths
                    var currentNewWidth = tableResizing.startWidths[index] + widthDifference;
                    var nextNewWidth = tableResizing.startWidths[index+1] - widthDifference;

                    // Check the column widths
                    if ((currentNewWidth < MIN_WIDTH) || (nextNewWidth < MIN_WIDTH)) {
                        return false;
                    }

                    // Resize the selected column to its new width
                    $(tableResizing.headerTable.find('thead tr th')[index]).outerWidth(currentNewWidth);
                    $(tableResizing.bodyTable.find('thead tr th')[index]).outerWidth(currentNewWidth);
                    $currentHeaderText.outerWidth(currentNewWidth - CONTROL_WIDTH);

                    // Resize the next column to its new width
                    $(tableResizing.headerTable.find('thead tr th')[index+1]).outerWidth(nextNewWidth);
                    $(tableResizing.bodyTable.find('thead tr th')[index+1]).outerWidth(nextNewWidth);
                    $nextHeaderText.outerWidth(nextNewWidth - CONTROL_WIDTH);
                }
            });
            
            $(document).mouseup(function() {
                if (tableResizing.active) {
                    tableResizing.active = false;
                    $(document).enableSelection();
                }
            });

            BLCAdmin.listGrid.paginate.initializeHeaderWidths($headerTable);
        },

        initializeHeaderWidths : function($headerTable) {
            var $columnHeaders = $headerTable.find('thead tr th');
            $columnHeaders.each(function(index, header) {
                var currentHeader = $($columnHeaders[index])[0];
                var $currentHeaderText = $(currentHeader).find('.listgrid-title span');

                var nextHeader = $($columnHeaders[index+1])[0];
                var $nextHeaderText = $(nextHeader).find('.listgrid-title span');

                var newCurrentHeaderWidth = ($(currentHeader).outerWidth() - CONTROL_WIDTH);
                var newNextHeaderWidth = ($(nextHeader).outerWidth() - CONTROL_WIDTH);

                newCurrentHeaderWidth = newCurrentHeaderWidth > MIN_WIDTH ? newCurrentHeaderWidth : MIN_WIDTH;
                newNextHeaderWidth = newNextHeaderWidth > MIN_WIDTH ? newNextHeaderWidth : MIN_WIDTH;

                $currentHeaderText.outerWidth(newCurrentHeaderWidth);
                $nextHeaderText.outerWidth(newNextHeaderWidth);
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
                    potentialMax = Math.max(totalRecords - 1, 0);
                } else {
                    potentialMax = Math.max(potentialMax - 1, 0);
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
                maxIndex = Math.max(this.findFirstLoadedIndex($tbody, topIndex) - 1, 0);
                
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
                var spinnerOffset = $tbody.closest('.mCustomScrollBox').position().top + 3;
                BLCAdmin.listGrid.showLoadingSpinner($tbody, spinnerOffset);

                var params =  BLCAdmin.history.getUrlParameters();
                for (var param in params) {
                    baseUrl = BLCAdmin.history.getUrlWithParameter(param, params[param], null, baseUrl);
                }

                var url = BLCAdmin.history.getUrlWithParameter('startIndex', startIndex, null, baseUrl);
                url = BLCAdmin.history.getUrlWithParameter('maxIndex', maxIndex, null, url);

                // also grab the sorts and ensure those inputs are also serialized
                var $sorts = $tbody.closest('.listgrid-container').find('input.sort-direction.active, input.sort-property.active');
                $sorts.each(function(index, input) {
                    //only submit fields that have a value set and are not a sort field. Sort fields will be added separately
                    if ($(input).val()) {
                        url = BLCAdmin.history.getUrlWithParameter($(input).data('name'), $(input).val(), null, url);
                    }
                });

                //console.log('Loading more records -- ' + url);
                
                BLC.ajax({ url: url, type: 'GET' }, function(data) {
                    var $newTbody;
                    if ($tbody.closest('.tree-column-wrapper').length) {
                        var treeColumnParentId = $tbody.closest('.select-column').data('parentid');
                        if (treeColumnParentId === undefined) {
                            $newTbody = $(data).find(".tree-column-wrapper .select-column").find('tbody');
                        } else {
                            $newTbody = $(data).find(".tree-column-wrapper .select-column[data-parentid='" + treeColumnParentId + "']").find('tbody');
                        }
                    } else {
                        var listGridId = $tbody.closest('table').attr('id');
                        $newTbody = $(data).find('table#' + listGridId).find('tbody');
                    }
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

                    if( $tbody.closest('.listgrid-container').find('.listgrid-header-wrapper').find('input[type=checkbox].multiselect-checkbox')) {
                        BLCAdmin.listGrid.paginate.updateSelectedRecords($tbody)
                    }
                    // Run any additionally configured initialization handlers
                    for (var i = 0; i < recordLoadHandlers.length; i++) {
                        recordLoadHandlers[i]($tbody.closest("table"));
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
            return $tbody.find('td:not(.blank-padding):first').outerHeight();
        },

        getActualRowIndex : function($tr) {
            var trPlacementTop = $tr.position().top;
            var rowHeight = this.getRowHeight($tr.closest('tbody'));
            // The division should produce an integer, assuming the row heights are consistent.
            return trPlacementTop / rowHeight;
        },
        
        getTopVisibleIndex : function($tbody) {
            if (typeof mcs === 'undefined') {
                return 0;
            }
            
            var scrollOffset = $tbody.closest('.mCSB_container').position().top * -1;
            var trHeight = this.getRowHeight($tbody);
            var topVisibleIndex = scrollOffset / trHeight;
            var hiddenRowRemainder = topVisibleIndex % 1;
            topVisibleIndex = hiddenRowRemainder > (1 - topRowVisiblePart) ? Math.ceil(topVisibleIndex) : Math.floor(topVisibleIndex);
            return Math.max(topVisibleIndex, 0);
        },
        
        getBottomVisibleIndex : function($tbody) {
            var scrollOffset = $tbody.closest('.mCSB_container').position().top * -1;
            var trHeight = this.getRowHeight($tbody);
            // Updated the code here to use the exact value (possibly float value) of
            // the listgrid body wrapper. Previously it would round this value which
            // led to inaccurate math.
            var boundingRectHeight = $tbody.closest('.listgrid-body-wrapper')[0].getBoundingClientRect().height;
            var bottomVisibleIndex = (scrollOffset + boundingRectHeight - trHeight) / trHeight;
            var visibleRowRemainder = bottomVisibleIndex % 1;
            return visibleRowRemainder > bottomRowVisiblePart ? Math.ceil(bottomVisibleIndex) : Math.floor(bottomVisibleIndex);
        },
        
        scrollToIndex : function($tbody, index) {
            var offset = index * this.getRowHeight($tbody);
            if (offset > 0) {
                // make sure to account for the top boarder on each row other than the first
                offset += 1;
            }
            //console.log('scrolling to ' + offset);
            $tbody.closest('.listgrid-body-wrapper').find('.mCSB_container').css('top', '-' + offset + 'px');
        },

        /**
         * If the "select-all" checkbox is checked, then make sure all rows are checked
         * @param $tbody
         */
        updateSelectedRecords : function($tbody) {
            // if the "select-all" button is checked then make sure the newly loaded rows/records are checked as well
            if($tbody.closest('.listgrid-container').find('.listgrid-header-wrapper').find('input[type=checkbox].multiselect-checkbox').length) {
                var $listgridBody = $tbody.closest(".listgrid-header-wrapper").next();
                var $checkbox = $tbody.closest('.listgrid-container').find('.listgrid-header-wrapper').find('input[type=checkbox].multiselect-checkbox');
                if ($checkbox.prop('checked')) {
                    $listgridBody.find(".listgrid-checkbox").prop('checked', true);
                    BLCAdmin.listGrid.inlineRowSelected(null, $tbody.find("tr:not(.selected)"), null, null, null, true);
                }
            }
        },

        updateTableFooter : function($tbody) {
            var topIndex = this.getTopVisibleIndex($tbody) + 1;
            var botIndex = this.getBottomVisibleIndex($tbody) + 1;
            var totalRecords = this.getTotalRecords($tbody);
            var pageSize = this.getPageSize($tbody);
            var lowerCount = this.getLowerCount($tbody);
            var upperCount = this.getUpperCount($tbody);
            var totalCountLessThanPageSize = this.getTotalCountLessThanPageSize($tbody);
            var $footer = $tbody.closest('.listgrid-container').find('.listgrid-table-footer');

            if (totalRecords > 0){
                $footer.find('.low-index').text(topIndex);
                $footer.find('.high-index').text(botIndex);
            } else {
                $footer.find('.low-index').text("0");
                $footer.find('.high-index').text("0");
            }
            $footer.find('.total-records').text(totalRecords);
            if (upperCount - totalRecords > 1) {
                $footer.find('.previous-page').css('display', 'inline');
            } else {
                $footer.find('.previous-page').css('display', 'none');
            }
            if (totalRecords >= pageSize || (!totalCountLessThanPageSize && totalRecords - upperCount === 0)) {
                $footer.find('.next-page').css('display', 'inline');
            } else {
                $footer.find('.next-page').css('display', 'none');
            }
            $footer.find('.first-index').text(lowerCount);
            $footer.find('.last-index').text(upperCount);

            //expose either the paging control or the standard scroll counter
            var $header = $tbody.closest('div.listgrid-body-wrapper').siblings('div.listgrid-header-wrapper');
            var $headerTable = $header.find("table");
            var params = BLCAdmin.history.getUrlParameters();
            if (!params) {
                params = $headerTable.data('currentparams');
            }
            var foundFilterOrSort = false;
            if (params) {
                $.each(params, function(key, value) {
                    var $criteriaInput = $header.find("input[data-name='" + key + "']");
                    if (!$criteriaInput || $criteriaInput.length <= 0) {
                        $criteriaInput = $header.find("select[data-name='" + key + "']");
                    }
                    if ($criteriaInput && $criteriaInput.length > 0) {
                        foundFilterOrSort = true;
                    }
                });
            }
            var fetchType = $headerTable.data("fetchtype");
            if (!foundFilterOrSort && fetchType === 'LARGERESULTSET') {
                $footer.find('.page-results').css('display', 'inline');
                $footer.find('.scroll-results').css('display', 'none');
            } else {
                $footer.find('.page-results').css('display', 'none');
                $footer.find('.scroll-results').css('display', 'inline');
            }
        },
        
        updateGridSize : function($tbody) {
            var $table = $tbody.closest('table.list-grid-table');
            var $headerTable = $table.closest('.listgrid-container').find('.listgrid-header-wrapper table');
            var rowHeight = BLCAdmin.listGrid.paginate.getRowHeight($tbody);
            var thWidths = [];
            var $modalBody = $tbody.closest('.modal-body');

            if ($modalBody.length > 0) {
                $modalBody.css('overflow-y', 'hidden');
            }

            if ($table.data('listgridtype') == 'asset_grid' && $table.closest('.select-group').find('.select-column:visible').length > 0) {
                // For Asset Grids, the folder selector collection has a width of TREE_COLUMN_WIDTH.
                // To get the remaining width available for displaying assets, we must subtract TREE_COLUMN_WIDTH from the total width.
                var totalWidth = $table.closest('.select-group').width();
                var remainingWidth = totalWidth - TREE_COLUMN_WIDTH;

                $headerTable.css('width', '');
                $table.css('width', '');
                $table.css('table-layout', 'fixed');

                // Ensure that the new width accounts for the width of the scrollbar (i.e. SCROLLBAR_WIDTH)
                var newWidth = remainingWidth - SCROLLBAR_WIDTH + 'px';
                $headerTable.css('width', newWidth);
                $table.css('width', newWidth);
            } else if ($table.data('listgridtype') == 'tree' &&
                        $table.closest('.select-group').find('.select-column:visible').length === 0 &&
                        $modalBody.length > 0) {
                $headerTable.css('width', '');
                $table.css('width', '');
                $table.css('table-layout', 'fixed');

                // Ensure that the new width accounts for the width of the scrollbar (i.e. SCROLLBAR_WIDTH)
                var newWidth = $table.closest('.select-group').width() - SCROLLBAR_WIDTH + 'px';
                $headerTable.css('width', newWidth);
                $table.css('width', newWidth);
            } else {
                // Clear out widths
                $headerTable.css('width', '');
                $table.css('width', '');
                $table.css('table-layout', 'fixed');
                //$headerTable.closest('.listgrid-container').find('th').css('width', '');

                // Ensure that the new width accounts for the width of the scrollbar (i.e. SCROLLBAR_WIDTH)
                var newWidth = $headerTable.width() - SCROLLBAR_WIDTH + 'px';
                $headerTable.css('width', newWidth);
                $table.css('width', newWidth);
            }
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
            if (listGridsCount == 1 && $wrapper.parents('.entity-form').length == 0 &&
                $table.data('listgridtype') !== 'tree' &&
                ($table.data('listgridtype') !== 'asset_grid' && $table.data('listgridtype') !== 'asset_grid_folder') ||
                (BLCAdmin.assetGrid == undefined && ($table.data('listgridtype') === 'asset_grid' || $table.data('listgridtype') === 'asset_grid_folder'))) {

                var $window = $(window);
                
                var wrapperHeight = $window.height() - $wrapper.offset().top - 50;
                wrapperHeight = BLCAdmin.listGrid.paginate.computeActualMaxHeight($tbody, wrapperHeight);

                // Add an extra 2px to the maxHeight to avoid exposing the scroll bar if there is only one record
                if (wrapperHeight <= rowHeight) {
                    wrapperHeight = rowHeight + 2;
                }

                $wrapper.css('max-height', wrapperHeight);
                $wrapper.find('.mCustomScrollBox').css('max-height', wrapperHeight);
                
                $wrapper.mCustomScrollbar('update');
                
                // If we are showing all records from the single grid page, ensure the url is updated
                if ($wrapper.find('.mCS_no_scrollbar').length > 0 && $modalBody.length === 0) {
                    BLCAdmin.listGrid.paginate.updateUrlFromScroll($wrapper.find('tbody'));
                }
            } else if (BLCAdmin.assetGrid != undefined
                && ($table.data('listgridtype') === 'asset_grid'
                || $table.data('listgridtype') === 'asset_grid_folder'
                || $table.data('listgridtype') === 'tree')) {
                var $window = $(window);
                var wrapperHeight = $window.height() - $wrapper.offset().top - 50;

                if ($modalBody.length > 0) {
                    wrapperHeight = $tbody.closest('.select-group').outerHeight();
                }

                wrapperHeight -= $wrapper.next('.listgrid-table-footer:visible').outerHeight();
                wrapperHeight = BLCAdmin.listGrid.paginate.computeActualMaxHeight($tbody, wrapperHeight);

                $wrapper.css('max-height', wrapperHeight);
                $wrapper.find('.mCustomScrollBox').css('max-height', wrapperHeight);

                $wrapper.css('height', wrapperHeight);
                $wrapper.find('.mCustomScrollBox').css('height', wrapperHeight);
                $modalBody.css('overflow-y', 'auto');

                $wrapper.mCustomScrollbar('update');
            } else if ($modalBody.length > 0) {
                var maxHeight;
                if ($wrapper.outerHeight(true) > $modalBody.height()) {
                    var $window = $(window);
                    var newModalHeight = Math.min($window.height() - 300, $wrapper.outerHeight(true));
                    $modalBody.css('height', newModalHeight);
                    maxHeight = newModalHeight - $wrapper.prev().outerHeight(true) - $wrapper.next().outerHeight(true) - 140;
                } else {
                    maxHeight = $modalBody.height() - $wrapper.prev().outerHeight(true) - $wrapper.next().outerHeight(true) - 140;
                }

                // If this is inside of a modal, the max height should be the size of the modal
                $wrapper.closest('.adorned-select-wrapper').find('.fieldset-card').each(function(index, fieldset) {
                    if (!$wrapper.closest(fieldset).length) {
                        maxHeight -= $(fieldset).height();
                    }
                });
                
                if ($wrapper.parent().find('label').length > 0) {
                    maxHeight -= $wrapper.parent().find('label').outerHeight(true);
                    maxHeight -= 5;
                }
                
                if ($wrapper.closest('.listgrid-container').find('.listgrid-toolbar').length > 0) {
                    maxHeight -= $wrapper.parent().find('.listgrid-toolbar').outerHeight(true);
                }
                
                var minHeight = Math.max($wrapper.find('table tr:not(.width-control-header)').outerHeight() + 1, maxSubCollectionListGridHeight);;
                if (maxHeight < minHeight) {
                    maxHeight = minHeight;
                }
                
                //maxHeight = BLCAdmin.listGrid.paginate.computeActualMaxHeight($tbody, maxHeight);
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
            BLCAdmin.listGrid.updateGridTitleBarSize($table.closest('.listgrid-container').find('.fieldgroup-listgrid-wrapper-header'));
        },
        
        computeActualMaxHeight : function($tbody, desiredMaxHeight) {
            // what is the height of the visible rows?
            var rowHeight = BLCAdmin.listGrid.paginate.getRowHeight($tbody);
            var loadedRecordRange = BLCAdmin.listGrid.paginate.getLoadedRecordRanges($tbody)[0];
            // This gives me back a 0-indexed range, I need the row count so add 1
            var numLoadedRows = loadedRecordRange.hi - loadedRecordRange.lo + 1;
            var numPaddedRows = Math.max(0, BLCAdmin.listGrid.paginate.getTotalRecords($tbody) - numLoadedRows);

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
            var $container = $table.closest('.listgrid-container');
            var thWidths = [];
            var $modalBody = $container.closest('.modal-body');

            // If we're in a modal, we need to hide overflow in the modal to calculate sizes correclty. We'll restore this.
            $modalBody.css('overflow-y', 'hidden');

            // We want to remove the padding on the right side
            $table.css('padding-right', '0');

            // First, we'll adjust the size of the table to be 15px less, since this is the margin we need
            // for our scrollbar. This will ensure the widths are correct once we draw the scrollbar
            if ($table.width() == $container.width() - 2) {
                $table.css('width', ($table.width() - 15) + 'px');

                // Figure out what the currently drawn widths are for each row
                // This is effectively the same for all rows for both the head and the body for now
                // Also, set the width we determined directly on the element
                $table.find('th').each(function (index, thElement) {
                    var $th = $(thElement);
                    var width = $th.width();
                    $th.css('width', width);
                    thWidths[index] = width;
                });
            }
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

            // Get the first tr's height
            var trHeight = parseInt(this.getRowHeight($tbody), 10);

            // Set up the mCustomScrollbar on the table body. Also bind the necessary events to enable infinite scrolling
            $wrapper.mCustomScrollbar({
                theme: 'dark',
                scrollEasing: "linear",
                scrollInertia: 500,
                mouseWheelPixels: trHeight,
                advanced:{
                    autoScrollOnFocus: false,
                },
                callbacks: {
                    onScroll: function() {
                        var singleGrid = BLCAdmin.listGrid.getListGridCount($) == 1;
                        var inModal = $tbody.closest('.modal-body').length === 1;
                        var listGridType = $table.data('listgridtype');

                        // Update the currently visible range
                        BLCAdmin.listGrid.paginate.updateTableFooter($tbody);
                        
                        // Fetch records if necessary
                        $.doTimeout('fetch', fetchDebounce, function() {
                        	var url = $tbody.closest('table').data('path');
                            if ($container.data('parentid')) {
                                url += "?parentId=" + $container.data('parentid');
                                url += "&inModal=" + inModal;
                            } else {
                                url += "?inModal=" + inModal;
                            }

                            var sectionCrumbs = $tbody.closest('table').data('sectioncrumbs');
                            if (typeof sectionCrumbs !== 'undefined') {
                                url += "&sectionCrumbs=" + sectionCrumbs;
                            }

                            BLCAdmin.listGrid.paginate.loadRecords($tbody, url);
                        });
                        
                        // Also update the URL if this is the only grid on the page
                        if (singleGrid && !inModal && listGridType !== 'tree') {
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
            var recordsBelow = Math.max(this.getTotalRecords($tbody) - 1 - range.hi, 0);
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
        },

        addRecordLoadHandler : function(fn) {
            recordLoadHandlers.push(fn);
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
        $.doTimeout('resizeListGrid', 0, function() {
            if ($('.oms').length == 0) {
                BLCAdmin.getActiveTab().find('tbody:not(.no-update)').each(function (index, element) {
                    if ($(element).closest('.oms-tab').length) {
                        return;
                    }

                    if ($(element).is(':visible')) {
                        if ($(element).parents('#listGrid-main.org\\.broadleafcommerce\\.core\\.order\\.domain\\.Order').length > 0) {
                            fillUnusedPageBottomSpace($(element));
                        } else {
                            BLCAdmin.listGrid.paginate.updateGridSize($(element));
                        }
                    } else {
                        $(element).addClass('needsupdate');
                    }
                });
                BLCAdmin.getActiveTab().find('.fieldgroup-listgrid-wrapper-header').each(function (index, element) {
                    BLCAdmin.listGrid.updateGridTitleBarSize($(element));
                });
            }
        });
    });

    function fillUnusedPageBottomSpace($tbody) {
        var gridRowHeight = BLCAdmin.listGrid.paginate.getRowHeight($tbody)
        var minGridRowCount = 9;
        var smallestGridMaxHeight = gridRowHeight * minGridRowCount;

        var $wrapper = $tbody.closest('.listgrid-body-wrapper');
        if ($wrapper) {
            var maxHeight = calcMaxHeight($wrapper.height());
            $wrapper.css('max-height', maxHeight);
            $wrapper.find('.mCustomScrollBox').css('max-height', maxHeight);
            $wrapper.mCustomScrollbar('update');
            BLCAdmin.listGrid.paginate.updateTableFooter($tbody);
        }

        function calcMaxHeight(wrapperHeight) {
            var maxHeight = wrapperHeight + calcUnusedHeight();
            maxHeight = Math.floor(maxHeight / gridRowHeight) * gridRowHeight;
            return Math.max(smallestGridMaxHeight, maxHeight);
        }

        function calcUnusedHeight() {
            var hClient = document.body.clientHeight;
            var hSiteBar = $('.site-bar').height();
            var hSendboxRibbon = $('#sandbox-ribbon').height();
            var hStickyContainer = $('.sticky-container').height();
            var hContentYield = $('.content-yield').outerHeight();
            return hClient - hSiteBar - hSendboxRibbon - hStickyContainer - hContentYield;
        }
    }

    $('body').on('click', 'a.previous-page', function(event) {
        var $pageLink = $(this);
        var $parentSpan = $pageLink.closest('div.listgrid-table-footer');
        var $headerWrapper = $parentSpan.siblings('div.listgrid-header-wrapper');
        var $bodyWrapper = $parentSpan.siblings('div.listgrid-body-wrapper');
        var $tbody = $bodyWrapper.find('table.list-grid-table').find('tbody');
        var currentUrl = $tbody.closest('table').data('path');
        if (BLCAdmin.listGrid.isLoading($tbody)) {
            return false;
        }
        var firstId = BLCAdmin.listGrid.paginate.getFirstId($tbody);
        currentUrl = BLCAdmin.history.getUrlWithParameter('firstId', firstId, null, currentUrl);
        var lowerCount = BLCAdmin.listGrid.paginate.getLowerCount($tbody);
        var upperCount = BLCAdmin.listGrid.paginate.getUpperCount($tbody);
        currentUrl = BLCAdmin.history.getUrlWithParameter('upperCount', upperCount, null, currentUrl);
        currentUrl = BLCAdmin.history.getUrlWithParameter('lowerCount', lowerCount, null, currentUrl);
        var pageSize = $tbody.closest('.listgrid-container').find('.listgrid-table-footer').find('.result-page-size-input').val();
        currentUrl = BLCAdmin.history.getUrlWithParameter('pageSize', pageSize, null, currentUrl);
        var spinnerOffset = $tbody.closest('.mCustomScrollBox').position().top + 3 + (BLCAdmin.listGrid.paginate.getRowHeight($tbody));
        BLCAdmin.listGrid.showLoadingSpinner($tbody, spinnerOffset);
        BLC.ajax({
            url: currentUrl,
            type: "GET"
        }, function(data) {
            BLCAdmin.listGrid.hideLoadingSpinner($tbody);
            BLCAdmin.listGrid.replaceRelatedListGrid($(data), null, { isRefresh : false});
        });
        return false;
    });

    $('body').on('click', 'a.next-page', function(event) {
        var $pageLink = $(this);
        var $parentSpan = $pageLink.closest('div.listgrid-table-footer');
        var $headerWrapper = $parentSpan.siblings('div.listgrid-header-wrapper');
        var $bodyWrapper = $parentSpan.siblings('div.listgrid-body-wrapper');
        var $tbody = $bodyWrapper.find('table.list-grid-table').find('tbody');
        var currentUrl = $tbody.closest('table').data('path');
        if (BLCAdmin.listGrid.isLoading($tbody)) {
            return false;
        }
        var lastId = BLCAdmin.listGrid.paginate.getLastId($tbody);
        currentUrl = BLCAdmin.history.getUrlWithParameter('lastId', lastId, null, currentUrl);
        var lowerCount = BLCAdmin.listGrid.paginate.getLowerCount($tbody);
        var upperCount = BLCAdmin.listGrid.paginate.getUpperCount($tbody);
        currentUrl = BLCAdmin.history.getUrlWithParameter('upperCount', upperCount, null, currentUrl);
        currentUrl = BLCAdmin.history.getUrlWithParameter('lowerCount', lowerCount, null, currentUrl);
        var pageSize = $tbody.closest('.listgrid-container').find('.listgrid-table-footer').find('.result-page-size-input').val();
        currentUrl = BLCAdmin.history.getUrlWithParameter('pageSize', pageSize, null, currentUrl);
        var spinnerOffset = $tbody.closest('.mCustomScrollBox').position().top + 3 + (BLCAdmin.listGrid.paginate.getRowHeight($tbody));
        BLCAdmin.listGrid.showLoadingSpinner($tbody, spinnerOffset);
        BLC.ajax({
            url: currentUrl,
            type: "GET"
        }, function(data) {
            BLCAdmin.listGrid.hideLoadingSpinner($tbody);
            var $newTBody = $(data).find('tbody');
            var totalRecords = BLCAdmin.listGrid.paginate.getTotalRecords($newTBody);
            var $footer = $tbody.closest('.listgrid-container').find('.listgrid-table-footer');
            if (totalRecords === 0) {
                $footer.find('.next-page').css('display', 'none');
            } else {
                $footer.find('.next-page').css('display', 'inline');
                BLCAdmin.listGrid.replaceRelatedListGrid($(data), null, { isRefresh : false});
            }
        });
        return false;
    });
});

