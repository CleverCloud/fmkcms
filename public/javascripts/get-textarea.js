/**
 *
 * @author cnivolle
 */

//TODO: Making the HTML cleaner. <br> is diry.

GENTICS.Aloha.settings = {
    errorhandling : false,
    ribbon: false,
    "i18n": {
        "current": "en"
    }
};

jQuery(document).ready(function() {
    // Here we hiding the texarea if JS is enabled
    jQuery('#object_content').css('display','none');

    //Putting a div where we can edit rich-text
    jQuery("#object_content").after('<div id="editable2"></div>');

    //Copying content of the textarea into the div
    jQuery('#editable2').html(jQuery('#object_content').val());
    //Making the div editable by aloha-editor
    jQuery('#editable2').aloha();
    /**
     * When submitting, this function copy the content of 
     * the div in the textarea.
    */
    jQuery("#page_form").submit(function(){
    jQuery('#object_content').val(jQuery('#editable2').html());

  });
});
