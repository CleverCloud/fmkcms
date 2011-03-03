#{set 'moreScripts'}
<script type="text/javascript">
   $(document).ready(function(){
      $('#langSwitcher option').each(function(){
         var newLang = $(this).attr('value');
         $(this).select(function(){
            alert(newLang);
            $.post("@{I18nController.changeLang}", {lang: newLang});
            location.reload();
         });
      });
   });
</script>
#{/set}
<select id="langSwitcher">
   #{list items:play.Play.langs, as:'l'}
   <option value="${l}" ${play.i18n.Lang.get().equals(l) ? 'selected="selected"'.raw() : ''}>
      ${l}
   </option>
   #{/list}
</select>
