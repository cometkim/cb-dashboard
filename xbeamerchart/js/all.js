function replaceAll(str, searchStr, replaceStr) {
    return str.split(searchStr).join(replaceStr);
}

var DEFAULT_TRUNCATE_CHARS = 60;

function truncateText(text, numberOfChars) {
    numberOfChars = numberOfChars || DEFAULT_TRUNCATE_CHARS;
    if (text.length > numberOfChars) {
        return (text.substring(0, numberOfChars) + "...");
    }
    return text;
}

String.prototype.includes = String.prototype.includes || function(chars){
    return this.indexOf(chars) != -1;
};

(function($){
    $(function(){
        $('.XBeamerWidget select').each(function(){
            $(this).multiselect({
                classes: 'queryConditionSelector',
                multiple: $(this).is('[multiple]'),
                selectedText: function(numChecked, numTotal, checkedItems) {
                    var value = [];
                    var $checkedItems = $(checkedItems);

                    var target = $checkedItems.first().attr('name').split('_')[1];
                    var $target = $('#' + target).closest('.select');

                    console.log(target);
                    $checkedItems.each(function(){
                        var valueString = $(this).next().html();
                        if ( $target.hasClass('user') && valueString.includes(' (')){
                            valueString = valueString.substring(0, valueString.indexOf(' ('));
                        }
                        value.push(valueString);
                    });
                    var joinedText = value.join(", ");
                    return truncateText(joinedText);
                }
            });
            if($(this).hasClass('with-filter')){
                $(this).multiselectfilter();
            }
        });

        $('.XBeamerWidget > .help').tooltip();
    });

})(jQuery);