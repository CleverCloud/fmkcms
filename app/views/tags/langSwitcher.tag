#{set 'moreScripts'}
<script type="text/javascript">
   $(document).ready(function(){
      $('#langSwitcher option').each(function(){
         $(this).select(function(){
            $.post("@{I18nController.changeLang}", {lang: $(this).attr('value')});
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
