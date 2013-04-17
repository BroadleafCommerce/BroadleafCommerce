(function($, BLCAdmin) {
    
    var LISTGRID_AJAX_LOCK = 0;
    
    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.listGrid.paginate = {
            
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
            
        showLoadingSpinner : function($tr) {
            var $loadingMessage = $('<i>', { 'class' : 'icon-spin icon-spinner' });
            
            $tr.find('td')
                .html('Loading &nbsp;')
                .append($loadingMessage)
        },
        
        loadMorePreviousRecords : function($tbody) {
            console.log('Attemping to load more previous');
            var url = $tbody.data('prevpage');
            
            if (url) {
                if (BLCAdmin.listGrid.paginate.acquireLock()) {
                    console.log('Lock acquired for load more previous');
                    BLCAdmin.listGrid.paginate.showLoadingSpinner($tbody.find('tr.blank-row-top'));
                    
                    BLC.ajax({
                        url: url,
                        type: 'GET'
                    }, function(data) {
                        var $newTbody = $(data.trim()).find('tbody');
                        
                        // Remove any extraneous blank rows
                        $tbody.find('tr.blank-row-top').remove();
                        $newTbody.find('tr.blank-row-bottom').remove();
                        
                        // Add the new rows above the current row
                        var $previousFirstRow = $tbody.find('tr:first');
                        $previousFirstRow.before($newTbody.find('tr'));
                        
                        // If we still have more previous pages, update the data to reflect thtat
                        var newPrevPage = $newTbody.data('prevpage') || '';
                        $tbody.data('prevpage', newPrevPage);
                        
                        // Unhide the new blank top row if present
                        $tbody.find('tr.blank-row-top').removeClass('hidden');
                        
                        // Update the height so that the user doesn't see a scroll action
                        var newOffset = $previousFirstRow.prev().position().top;
                        $tbody.find('.mCSB_container').css('top', '-' + newOffset + 'px');
                        $tbody.mCustomScrollbar('update');
                        
                        BLCAdmin.listGrid.paginate.releaseLock();
                        console.log('Lock released for load more previous');
                    });
                }
            }
        },
        
        loadMoreNextRecords : function($tbody) {
            console.log('Attemping to load more next');
            var url = $tbody.data('nextpage');
            
            if (url) {
                if (BLCAdmin.listGrid.paginate.acquireLock()) {
                    console.log('Lock acquired for load more next');
                    BLCAdmin.listGrid.paginate.showLoadingSpinner($tbody.find('tr.blank-row-bottom'));
                    
                    BLC.ajax({
                        url: url,
                        type: 'GET'
                    }, function(data) {
                        var $newTbody = $(data.trim()).find('tbody');
                        
                        // Remove any extraneous blank rows
                        $tbody.find('tr.blank-row-bottom').remove();
                        $newTbody.find('tr.blank-row-top').remove();
                        
                        // Add the new rows below the current row
                        var $previousLastRow = $tbody.find('tr:last');
                        $previousLastRow.after($newTbody.find('tr'));
                        
                        // If we still have more next pages, update the data to reflect thtat
                        var newNextPage = $newTbody.data('nextpage') || '';
                        $tbody.data('nextpage', newNextPage);
                        
                        // Unhide the new blank top row if present
                        $tbody.find('tr.blank-row-bottom').removeClass('hidden');
                        
                        // No need to manually update the height, but we need to update the scrollbar height
                        $tbody.mCustomScrollbar('update');
                        BLCAdmin.listGrid.paginate.releaseLock();
                        console.log('Lock released for load more next');
                    });
                }
            }
        },
        
        updateUrlFromScroll : function($tbody) {
            var items        = $tbody.find('tr'),
                scrollOffset = mcs.top,
                startIndex   = 0,
                stopIndex    = items.length - 1,
                middle       = Math.floor((stopIndex + startIndex) / 2),
                searchWindow = 30;
            
            while(startIndex < stopIndex) {
                var topElementPosition = $(items[middle]).position().top;
                var elementDistanceToTop = topElementPosition - (-1 * scrollOffset);
                
                if (elementDistanceToTop >= 0 && elementDistanceToTop < searchWindow) {
                    break;
                }
                
                //adjust search area
                if (elementDistanceToTop > 0) {
                    stopIndex = middle - 1;
                } else {
                    startIndex = middle + 1;
                }
    
                //recalculate middle
                middle = Math.floor((stopIndex + startIndex) / 2);
            }
            
            var $topRow = $(items[middle]);
            
            BLCAdmin.history.replaceUrlParameter('startIndex', $topRow.data('pagestart'));
        },
        
        initialize : function($container) {
            var $table = $container.find('table.list-grid-table');
            var thWidths = [];
            
            // Figure out what the currently drawn widths are for each row
            // This is effectively the same for all rows for both the head and the body for now
            // Also, set the width we determined directly on the element
            $table.find('th').each(function(index, thElement) {
                var $th = $(thElement);
                var width = $th.width();
                $th.css('width', width);
                thWidths[index] = width;
            });
            
            // Set the same widths we used for the head elements to the second row. The other rows will follow,
            // but they don't explcitly need the width set
            var $tr = $table.find('tr:nth-child(2)');
            $tr.find('td').each(function(index, tdElement) {
                var $td = $(tdElement);
                $td.css('width', thWidths[index]);
            });
            
            // Set the display to block on the table, the thead, and the tbody.
            // This will allow us to keep the header static and scroll the body. As a result of this change,
            // the head and the body will no longer be in sync width-wise. However, since we explicitly set the width
            // previously for the head and at least one body row, everything will continue to render as we would expect.
            $table.css('display', 'block');
            $table.find('thead, tbody').css('display', 'block');
            
            // Set up the mCustomScrollbar on the table body. Also bind the necessary events to enable infinite scrolling
            var $tbody = $table.find('tbody');
            $tbody.mCustomScrollbar({
                theme: 'dark',
                scrollInertia: 70,
                callbacks: {
                    //onTotalScrollOffset: 10,
                    onTotalScroll: function() {
                        BLCAdmin.listGrid.paginate.loadMoreNextRecords($tbody);
                    },
                    
                    //onTotalScrollBackOffset: 10,
                    onTotalScrollBack: function() {
                        BLCAdmin.listGrid.paginate.loadMorePreviousRecords($tbody);
                    },
                    
                    onScroll: function() {
                        if ($tbody.closest('table').data('listgridtype') == 'main') {
                            $.doTimeout( 'updateurl', 500, function(){
                                BLCAdmin.listGrid.paginate.updateUrlFromScroll($tbody);
                            });
                        }
                    }
                }
            });
            
            // After we've established the scrolling inner body, we have to do some additional tweaks to ensure
            // the user experience is as clean as possible
            
            if ($table.find('tr.blank-row-top').length) {
                // Unhide the top row since there is more previous content
                $tbody.find('tr.blank-row-top').removeClass('hidden');
                
                // Update the height so that the user doesn't see a scroll action
                var newOffset = $tbody.find('tr:nth-child(2)').position().top;
                $tbody.find('.mCSB_container').css('top', '-' + newOffset + 'px');
                $tbody.mCustomScrollbar('update');
            }
            
            if ($table.find('tr.blank-row-bottom').length) {
                // Unhide the bottom row since there is more previous content
                $tbody.find('tr.blank-row-bottom').removeClass('hidden');
            }
            
            // Render the table
            $table.find('tbody').css('visibility', 'visible');
        }
    };
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    /**
     * Performs pagination on the given list grid. 
     */
    $('body').on('click', 'ul.pagination-links a', function(event) {
        var $toReplace = $(this).closest('.listgrid-container').find('tbody');
        var $this = $(this);
        
        BLC.ajax({
            url: $(this).attr('href'),
            type: 'GET'
        }, function(data) {
            $this.closest('ul').find('a.active').removeClass('active');
            $this.addClass('active')
            $toReplace.replaceWith($(data.trim()).find('tbody'));
        });
        
        return false;
    });
    
    $('.listgrid-container').each(function(index, element) {
        BLCAdmin.listGrid.initialize($(element));
    });
    
});

