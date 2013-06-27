$(document).ready(function() {
    
    $('body').on('click', 'button.show-category-list-view', function() {
        $('.category-list').show();
        $('.category-tree').hide();
        $('button.show-category-list-view').addClass('active');
        $('button.show-category-tree-view').removeClass('active');
    });
    
    $('body').on('click', 'button.show-category-tree-view', function() {
        $('.category-list').hide();
        $('.category-tree').show();
        $('button.show-category-list-view').removeClass('active');
        $('button.show-category-tree-view').addClass('active');
    });
    
});