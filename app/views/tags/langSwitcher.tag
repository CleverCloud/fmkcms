<script type="text/javascript">
   $(document).ready(function(){
      $("#langSwitcher").children().map(function(){
         $(this).select(function(){
            var newLang = $(this).attr('value');
            alert(newLang);
            $.post("@{I18nController.changeLang}", {lang: newLang}, function(){
               location.reload();
            });
         });
      });
   });
</script>
<select id="langSwitcher">
   #{list items:play.Play.langs, as:'l'}
   <option value="${l}" ${play.i18n.Lang.get().equals(l) ? 'selected="selected"'.raw() : ''}>
      ${l}
   </option>
   #{/list}
</select>
