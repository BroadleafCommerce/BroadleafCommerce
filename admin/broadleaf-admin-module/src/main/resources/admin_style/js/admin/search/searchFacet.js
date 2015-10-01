/**
 * Created by ncrum on 10/1/15.
 */
(function($, BLCAdmin) {

    var searchFacetClassName = 'org.broadleafcommerce.core.search.domain.SearchFacet';

    var useFacetRangesSelector = '#field-useFacetRanges';
    var searchFacetRangesSelector = '#searchFacetRanges';

    console.log("TEST ONE");


    BLCAdmin.addDependentFieldHandler(
        searchFacetClassName,
        useFacetRangesSelector,
        searchFacetRangesSelector,
        function showIfValue(parentValue) {
            console.log("TEST TWO");
            return (parentValue === 'true');
        }
        ,
        {
            'additionalChangeAction-runOnInitialization' : true
        }
    );


})(jQuery, BLCAdmin);