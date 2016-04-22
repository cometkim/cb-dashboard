function replaceAll(str, searchStr, replaceStr) {
    return str.split(searchStr).join(replaceStr);
}

function truncateText(text, numberOfChars) {
    numberOfChars = numberOfChars || DEFAULT_TRUNCATE_CHARS;
    if (text.length > numberOfChars) {
        return (text.substring(0, numberOfChars) + "...");
    }
    return text;
}

$(function(){
    $('.XBeamerWidget-select').each(function(){
        $(this).multiselect({multiple: false});
        if($(this).hasClass('with-filter')){
            $(this).multiselectfilter();
        }
    });

    $('.XBeamerWidget-multiselect').each(function(){
        $(this).multiselect({multiple: true});
        if($(this).hasClass('with-filter')){
            $(this).multiselectfilter();
        }
    });

    $('.XBeamerWidget > .help').tooltip();

    $('.ui-icon-minusthick').click(function() {
        var icon = $( this );
        icon.toggleClass('ui-icon-minusthick ui-icon-plusthick');
        icon.parent().parent().find('.content').toggle();
    });
});