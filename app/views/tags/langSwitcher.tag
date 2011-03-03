<script type="text/javascript">
   $(document).ready(function(){
      $("#langSwitcher").change(function(){
         var newLang = $(this).val();
         alert(newLang);
         $.post("@{I18nController.changeLang}", {lang: newLang}, function(){
            location.reload();
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
