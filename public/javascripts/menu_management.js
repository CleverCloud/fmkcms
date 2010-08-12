
jQuery(document).ready(function() {
    // Here we hiding the texarea if JS is enabled
    jQuery('#form_entrie div.forms').css('display','none');
    jQuery('#add_page_btn').click(function(){
        jQuery('#form_entrie_page').show();
    });
    jQuery('#add_page_btn_submit').click(function(){
        jQuery.ajax({
            type:"get",
            url: '/CRUD4MenuEntryPages/quickAdd',
            data:{
                'urlId':jQuery('#urlId').val()
            },
            success: function(data) {
                jQuery('#hereEntries').append('<input type="hidden" value="'+data.id+'" name="object.entries.id" ></input>');
                jQuery('#hereEntries ul').append('<li>'+data.urlid+'</li>');
            }
        });
    });
});
