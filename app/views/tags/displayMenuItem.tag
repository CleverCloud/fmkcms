<li class="menuItem ${request.url.equals(_menuItem.getLink()) ? ' active' : ''} ${_liCss}">
   #{if _menuItem.getLink()}
   <a href="${_menuItem.getLink()}" #{if _menuItem.cssLinkClass}class="${_menuItem.cssLinkClass}"#{/if}>&{_menuItem.displayStr}</a>
   #{/if}
   #{else}
   <h3>&{_menuItem.displayStr}</h3>
   #{/else}
   #{if _menuItem.menu}
   #{displaySubMenu menu:_menuItem.menu /}
   #{/if}
</li>
